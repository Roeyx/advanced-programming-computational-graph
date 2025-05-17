package project_biu.server;

import project_biu.servlets.Servlet;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MyHTTPServer is a multithreaded HTTP server that supports basic HTTP methods (GET, POST, DELETE).
 * It uses a thread pool to handle multiple client connections concurrently and manages servlet mappings by URI.
 * This class implements the HTTPServer interface and extends Thread, allowing the server to run in its own thread.
 */
public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port; // Port the server listens on
    private final ExecutorService threadPool; // Thread pool for handling client requests
    private final ConcurrentHashMap<String, Servlet> getServlets;    // Maps URIs to GET servlets
    private final ConcurrentHashMap<String, Servlet> postServlets;   // Maps URIs to POST servlets
    private final ConcurrentHashMap<String, Servlet> deleteServlets; // Maps URIs to DELETE servlets
    private final Lock lock; // Lock for thread-safe servlet operations
    private volatile boolean running = true; // Server running state

    /**
     * Constructs the HTTP server with a specific port and thread pool size.
     * Initializes servlet maps for each HTTP method and prepares the thread pool.
     *
     * @param port     The port number for incoming HTTP requests
     * @param nThreads The number of worker threads in the thread pool
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
        this.getServlets = new ConcurrentHashMap<>();
        this.postServlets = new ConcurrentHashMap<>();
        this.deleteServlets = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Registers a servlet to handle requests for a given HTTP method and URI.
     * Thread-safe using a lock to ensure consistent state when updating servlet maps.
     *
     * @param httpCommand The HTTP method (GET, POST, DELETE)
     * @param uri         The URI to associate with the servlet
     * @param s           The servlet that will handle requests
     * @throws IllegalArgumentException If the HTTP method is not supported
     */
    public void addServlet(String httpCommand, String uri, Servlet s) {
        lock.lock();
        try {
            switch (httpCommand.toUpperCase()) {
                case "GET":
                    getServlets.put(uri, s);
                    break;
                case "POST":
                    postServlets.put(uri, s);
                    break;
                case "DELETE":
                    deleteServlets.put(uri, s);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a servlet associated with a given HTTP method and URI.
     * Uses a lock to ensure thread-safe modification of the servlet maps.
     *
     * @param httpCommand The HTTP method (GET, POST, DELETE)
     * @param uri         The URI for which to remove the servlet
     */
    @Override
    public void removeServlet(String httpCommand, String uri) {
        lock.lock();
        try {
            switch (httpCommand.toUpperCase()) {
                case "GET":
                    getServlets.remove(uri);
                    break;
                case "POST":
                    postServlets.remove(uri);
                    break;
                case "DELETE":
                    deleteServlets.remove(uri);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Starts the HTTP server in a new thread.
     * This allows the server to begin accepting and processing connections asynchronously.
     */
    @Override
    public void start() {
        new Thread(this).start();
    }

    /**
     * The main server loop that listens for incoming connections and handles them using worker threads.
     * Uses a timeout to periodically check if the server should stop running.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(1000); // Check for shutdown every 1 second
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Accept new client connection
                    clientSocket.setSoTimeout(6000); // Set read timeout for client
                    threadPool.submit(() -> handleClient(clientSocket)); // Handle in thread pool
                } catch (SocketTimeoutException e) {
                    // Timeout reached, recheck running condition
                } catch (IOException e) {
                    if (!running) break; // Stop accepting if server is shutting down
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Error during server startup
        }
    }

    /**
     * Gracefully stops the HTTP server by updating the running flag and shutting down the thread pool.
     * After calling this method, the server will no longer accept new connections.
     */
    public void close() {
        running = false;
        threadPool.shutdown();
    }

    /**
     * Handles a single client's request. Parses the request and delegates to the appropriate servlet.
     * Sends back HTTP 404 if no matching servlet is found or 405 if the method is unsupported.
     *
     * @param clientSocket The socket representing the client connection
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(in);
            String httpCommand = requestInfo.getHttpCommand();
            String uri = requestInfo.getUri();

            Servlet servlet = null;
            switch (httpCommand.toUpperCase()) {
                case "GET":
                    servlet = findLongestMatchingServlet(uri, getServlets);
                    break;
                case "POST":
                    servlet = findLongestMatchingServlet(uri, postServlets);
                    break;
                case "DELETE":
                    servlet = findLongestMatchingServlet(uri, deleteServlets);
                    break;
                default:
                    out.write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes());
                    return;
            }

            if (servlet != null) {
                servlet.handle(requestInfo, out); // Let servlet handle the request
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes()); // No matching servlet
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Always close the socket after handling
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Searches for the servlet with the longest matching prefix of the requested URI.
     * This allows support for wildcard-like URI handling where more specific matches are prioritized.
     *
     * @param uri      The requested URI
     * @param servlets A map of registered servlets by URI
     * @return The best-matching servlet or null if none match
     */
    private Servlet findLongestMatchingServlet(String uri, ConcurrentHashMap<String, Servlet> servlets) {
        Servlet matchedServlet = null;
        int maxMatchLength = -1;

        for (String keyUri : servlets.keySet()) {
            if (uri.startsWith(keyUri) && keyUri.length() > maxMatchLength) {
                matchedServlet = servlets.get(keyUri);
                maxMatchLength = keyUri.length(); // Track best match by longest prefix
            }
        }

        return matchedServlet;
    }
}
