package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import project_biu.server.RequestParser.RequestInfo;

/**
 * HtmlLoader is a servlet designed to respond to requests by delivering HTML or JavaScript files.
 * It serves responses to "GET" requests targeting the "/app/" route.
 */
public class HtmlLoader implements Servlet {

    private final String htmlFolder;
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("js", "application/javascript");
    }

    // Initializes the loader with a directory containing HTML resources
    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

    /**
     * Responds to a client's HTTP request by locating and returning an HTML or JS file.
     * If the requested file isn't found or is inaccessible, an error response is sent instead.
     *
     * @param requestInfo contains URI and header information for the client's request
     * @param toClient    the output stream used to deliver the server's response
     * @throws IOException if an error occurs reading from or writing to streams
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        String modifiedPath = htmlFolder + requestInfo.getUri().substring(4); // Removes "/app" prefix
        Path filePath = Paths.get(modifiedPath).normalize();

        // Prevent access to directories outside the allowed base directory
        if (!filePath.startsWith(Paths.get(htmlFolder))) {
            String response = "HTTP/1.1 403 Forbidden\r\n\r\nAccess Denied";
            toClient.write(response.getBytes());
            return;
        }

        try {
            byte[] content = Files.readAllBytes(filePath);
            String contentType = MIME_TYPES.getOrDefault(getFileExtension(filePath), "text/plain");
            String response = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\n\r\n";
            toClient.write(response.getBytes());
            toClient.write(content);
        } catch (IOException e) {
            // Handle case when the file cannot be found or read
            String response = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
            toClient.write(response.getBytes());
        }
    }

    /**
     * Extracts and returns the file type extension from a given path.
     * Used to determine how to classify the file's MIME type.
     *
     * @param path the path pointing to the file
     * @return a string representing the file extension, or empty if none found
     */
    private String getFileExtension(Path path) {
        String name = path.toString();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1);
    }

    /**
     * Shuts down the servlet instance. Currently, there's no specific cleanup required.
     *
     * @throws IOException if something goes wrong during resource release
     */
    @Override
    public void close() throws IOException {
        // No teardown logic needed for this servlet
    }
}
