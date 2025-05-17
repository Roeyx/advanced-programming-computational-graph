package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ArrayBlockingQueue;

/*
 * This class acts as a concurrency-enabled proxy for an Agent.
 * It allows messages to be processed independently on a dedicated thread,
 * using a blocking queue to manage workload asynchronously.
 *
 * Components:
 * - agent: The actual agent doing the processing work.
 * - queue: Stores incoming messages until they're handled.
 * - stop: A boolean used to shut down the background thread cleanly.
 */
public class ParallelAgent implements Agent {

    Agent agent;
    ArrayBlockingQueue<Message> queue;
    private boolean stop = false;

    /*
     * Constructor for initializing a ParallelAgent with a defined capacity.
     * Launches a background worker thread that processes messages pulled from the queue.
     *
     * @param agent The core agent that handles message logic.
     * @param capacity Maximum number of messages the queue can hold simultaneously.
     */
    public ParallelAgent(Agent agent , int capacity){
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<Message>(capacity);
        new Thread(new Runnable() {
            public void run() {
                while(!stop){
                    try{
                        Message message = queue.take(); // Blocks when queue is empty

                        // Try to extract topic and content using regular expression
                        Pattern pattern = Pattern.compile("Topic:(.*?) Message:(.*)");
                        Matcher matcher = pattern.matcher(message.asText);
                        String topic;
                        String originalMessage;
                        if (matcher.matches()) {
                            topic = matcher.group(1).trim();
                            originalMessage = matcher.group(2).trim();
                            agent.callback(topic,new Message(originalMessage));
                        }
                        else{
                            topic = message.asText;
                            if(topic.matches("stop")){
                                stop=true;
                            }
                        }
                    }
                    catch(InterruptedException e){
                        // Swallow the interruption and retry
                    }
                }
            }
        }).start();
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }

    /*
     * Called when a message is received. Prepares it by embedding topic info
     * and submits it to the processing queue for deferred handling.
     *
     * @param topic The subject or label associated with the message.
     * @param msg The message body to be forwarded for processing.
     */
    @Override
    public void callback(String topic, Message msg) {
        String newmsgString = "Topic:" + topic + " Message:" + msg.asText;
        Message newMessage = new Message(newmsgString);
        try {
            queue.put(newMessage);
        }
        catch (InterruptedException e) {
            // Silently ignore interruption
        }
    }

    /*
     * Gracefully shuts down this agent by sending a termination signal to the thread.
     * Waits for the processing loop to acknowledge shutdown before releasing the wrapped agent.
     */
    @Override
    public void close() {
        try {
            queue.put(new Message("stop"));
        }
        catch (InterruptedException e) {
            // Interruption ignored during shutdown
        }
        while(!stop){}

        agent.close();
        agent.close(); // Called twice intentionally or possibly by mistake?
    }

    public Agent getAgent(){return agent;}
}
