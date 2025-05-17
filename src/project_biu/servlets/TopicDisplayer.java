package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;
import project_biu.server.RequestParser.RequestInfo;

/**
 * TopicDisplayer is a servlet that processes GET requests to "/publish".
 * It publishes a message to a topic if the topic exists and displays a table of all topics and their latest values.
 */
public class TopicDisplayer implements Servlet {

    /**
     * Handles the HTTP GET request for "/publish".
     * It extracts the topic and message from the query parameters, posts the message to the topic,
     * and sends an HTML table displaying the current values of all topics.
     *
     * If the configuration file hasn't been loaded or the topic is invalid, an error HTML response is returned.
     *
     * @param requestInfo Contains URI, headers, and parameters of the HTTP request
     * @param toClient    The stream used to write the HTTP response
     * @throws IOException if there's an issue writing to the client
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {

        // Extract parameters: topic name and message content
        String topic = requestInfo.getParameters().get("topic");
        String message = requestInfo.getParameters().get("message");

        // Access the singleton topic manager
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        // Check if the configuration was loaded
        if (ConfLoader.gc != null) {
            // Validate topic existence
            if (topicExists(topic)) {
                // Publish the message to the corresponding topic
                tm.getTopic(topic).publish(new Message(message));

                // Build HTML table response showing all topics and their last published message
                StringBuilder htmlResponse = new StringBuilder();
                htmlResponse.append("<html>\n");
                htmlResponse.append("<body>\n");
                htmlResponse.append("<h1> Topics Values </h1>\n");
                htmlResponse.append("<table border=\"1\">\n");
                htmlResponse.append("  <tr><th>Topic</th><th>Last Value</th></tr>\n");

                for (Topic t : tm.getTopics()) {
                    String topicName = t.name;
                    String topicMessage = t.getLastMessage();
                    if (topicMessage == null) topicMessage = "";
                    htmlResponse.append("  <tr>\n");
                    htmlResponse.append("    <td>").append(topicName).append("</td>\n");
                    htmlResponse.append("    <td>").append(topicMessage).append("</td>\n");
                    htmlResponse.append("  </tr>\n");
                }

                htmlResponse.append("</table>\n");
                htmlResponse.append("</body>\n");

                // Build and send the HTTP success response
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + htmlResponse.toString().getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" + htmlResponse.toString();

                toClient.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                toClient.flush();
            } else {
                // Topic not found in the system
                String httpErrorResponse = handleError("Error: Topic Not Found", "The specified topic does not exist.");
                toClient.write(httpErrorResponse.getBytes(StandardCharsets.UTF_8));
                toClient.flush();
            }
        } else {
            // No configuration has been loaded yet
            String httpErrorResponse = handleError("Error: No Configuration Loaded", "Please upload a configuration file first.");
            toClient.write(httpErrorResponse.getBytes(StandardCharsets.UTF_8));
            toClient.flush();
        }
    }

    /**
     * Releases any resources associated with this servlet.
     * Currently, there's nothing to clean up.
     *
     * @throws IOException never thrown in this implementation
     */
    @Override
    public void close() throws IOException {
        // Nothing to clean up
    }

    /**
     * Generates a complete HTTP error response containing an HTML error message.
     *
     * @param errorHeader  Title text to display in the HTML error page
     * @param errorMessage Description to show under the error title
     * @return A string containing the full HTTP 400 response with embedded HTML
     */
    private String handleError(String errorHeader, String errorMessage) {
        String htmlResponse = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Error Page</title>\n" +
                "    <style>\n" +
                "        body { display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial, sans-serif; background-color: #f2f2f2; }\n" +
                "        .error-container { background-color: #fff; padding: 20px; border: 1px solid #ccc; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center; }\n" +
                "        .error-container h1 { font-size: 24px; color: red; }\n" +
                "        .error-container p { font-size: 16px; color: #333; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"error-container\">\n" +
                "    <h1>" + errorHeader + "</h1>\n" +
                "    <p>" + errorMessage + "</p>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";

        return "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + htmlResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + htmlResponse;
    }

    /**
     * Verifies if the given topic name exists in the current topic manager.
     *
     * @param topic The name of the topic to look up
     * @return true if the topic exists, false otherwise
     */
    private boolean topicExists(String topic) {
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        for (Topic t : topics) {
            if (t.name.equals(topic)) {
                return true;
            }
        }
        return false;
    }
}
