package project_biu.graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

/**
 * Provides access to a singleton instance of TopicManager, which handles the creation and retrieval
 * of Topic objects used in a publish-subscribe messaging architecture.
 *
 * The singleton pattern ensures that there is only one centralized manager of all topics
 * throughout the application's lifecycle, promoting consistency and ease of access.
 */
public class TopicManagerSingleton {

    /**
     * Returns the globally shared instance of the TopicManager.
     * This method provides the main entry point for interacting with the topic management system.
     *
     * @return the single TopicManager instance used throughout the application
     */
    public static TopicManager get() {
        return TopicManager.instance;
    }

    /**
     * TopicManager is a static inner class responsible for managing a collection of Topic objects.
     * It uses a thread-safe data structure (ConcurrentHashMap) to allow concurrent access and updates,
     * ensuring that the system behaves correctly even when accessed by multiple threads.
     */
    public static class TopicManager {

        // The single instance of TopicManager, initialized eagerly
        private static final TopicManager instance = new TopicManager();

        // A thread-safe map storing topics by their names
        ConcurrentHashMap<String, Topic> topicMap;

        /**
         * Private constructor to enforce the singleton pattern.
         * Prevents external classes from creating multiple instances of TopicManager.
         * Initializes the topic map to hold topic objects.
         */
        private TopicManager() {
            topicMap = new ConcurrentHashMap<>();
        }

        /**
         * Retrieves an existing topic by its name. If the topic doesn't already exist in the map,
         * a new Topic object is created, added to the map, and then returned.
         * This lazy initialization ensures that topics are only created when actually needed.
         *
         * @param topicName the unique name of the topic to retrieve or create
         * @return the Topic instance associated with the specified name
         */
        public Topic getTopic(String topicName) {
            Topic topic = topicMap.get(topicName);
            if (topic == null) {
                topic = new Topic(topicName);
                topicMap.put(topicName, topic);
            }
            return topic;
        }

        /**
         * Provides access to all topics currently managed by this TopicManager.
         * This method returns a collection view of the topics, which can be used for
         * inspection, visualization, or operations involving all topics.
         *
         * @return a collection containing all Topic instances in the manager
         */
        public Collection<Topic> getTopics() {
            return topicMap.values();
        }

        /**
         * Removes all topics from the TopicManager.
         * This can be useful for resetting the system state during testing or reinitialization.
         */
        public void clear() {
            topicMap.clear();
        }
    }
}
