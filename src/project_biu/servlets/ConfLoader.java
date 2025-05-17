package project_biu.servlets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import project_biu.graph.TopicManagerSingleton;
import project_biu.view.HtmlGraphWriter;

import project_biu.configs.GenericConfig;
import project_biu.graph.Graph;
import project_biu.server.RequestParser.RequestInfo;

/**
 * This servlet handles the uploading and processing of configuration files used to build the system graph.
 * Specifically responds to POST requests sent to the "/upload" endpoint.
 */
public class ConfLoader implements Servlet {
    static public GenericConfig gc = null;

    /**
     * Processes an incoming POST request containing a configuration file.
     * Parses file content, updates internal state, creates graph structure, and sends a visual HTML response.
     *
     * @param requestInfo request metadata and raw content
     * @param toClient    output stream used to send the HTTP response
     * @throws IOException if reading input or writing output fails
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        String data = readFile(requestInfo);
        BufferedReader reader = new BufferedReader(new StringReader(data));
        Map<String, String> contentParams = new HashMap<>(Map.of());
        String line;
        String filePath = null;

        // Parse headers or inline parameters before file content
        while ((line = reader.readLine()) != null && line.trim().contains(":")) {
            if (line.contains(";")) {
                String[] paramParts = line.split(";");
                for (String paramPart : paramParts) {
                    String delimiter = paramPart.contains(":") ? ":" : "=";
                    String[] keyVal = paramPart.split(delimiter, 2);
                    contentParams.put(keyVal[0], keyVal[1]);
                    if (keyVal[0].contains("filename")) {
                        filePath = keyVal[1];
                    }
                }
            } else {
                String[] keyVal = line.split(":", 2);
                contentParams.put(keyVal[0], keyVal[1]);
            }
        }

        // Accumulate actual file contents after header parsing
        StringBuilder confData = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            confData.append(line).append("\n");
        }

        // Store file temporarily on disk for configuration loading
        Path tempFilePath = Paths.get("temp_config");
        try {
            Files.writeString(tempFilePath, confData.toString());
        } catch (IOException e) {
            System.err.println("Failed to save temporary config file: " + e.getMessage());
        }

        if (filePath != null) {
            // Reset environment and create a new configuration instance
            gc = new GenericConfig();
            TopicManagerSingleton.get().clear();
            gc.setConfFile("temp_config");

            try {
                gc.create(); // Apply the configuration to build agents/topics
                Graph configGraph = new Graph();
                configGraph.createFromTopics(); // Generate the graph from system structure
                String graphHtml = HtmlGraphWriter.getGraphHTML(configGraph);

                // Prepare and send an HTTP response containing the graph visualization
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + graphHtml.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "Content-Disposition: inline; filename=\"graph.html\"; target=\"graphFrame\"\r\n" +
                        "\r\n" +
                        graphHtml;
                toClient.write(httpResponse.getBytes());
                Files.delete(tempFilePath); // Clean up temporary config file
            } catch (Exception e) {
                String errorResponse = handleError(e.getMessage());
                toClient.write(errorResponse.getBytes(StandardCharsets.UTF_8));
                toClient.flush();
            }
        } else {
            // Return an error page if no file was uploaded
            String errorHtml = handleError("Unable to read configuration file.");
            String response = "HTTP/1.1 404 Bad Request\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + errorHtml.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Content-Disposition: inline; filename=\"graph.html\"; target=\"graphFrame\"\r\n" +
                    "\r\n" +
                    errorHtml;
            toClient.write(response.getBytes(StandardCharsets.UTF_8));
            toClient.flush();
        }
    }

    /**
     * Releases any internal resources held by the servlet.
     * Currently unused since this implementation does not hold any open resources.
     *
     * @throws IOException if any I/O errors occur while closing
     */
    @Override
    public void close() throws IOException {
        // Nothing to release
    }

    /**
     * Builds an HTTP error response containing an embedded HTML page.
     *
     * @param errorMessage message to be shown to the user
     * @return the full HTTP response with error page HTML
     */
    private String handleError(String errorMessage) {
        String htmlResponse = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Error</title>\n" +
                "    <style>\n" +
                "        body { display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial; background-color: #f2f2f2; }\n" +
                "        .error-container { background-color: #fff; padding: 20px; border: 1px solid #ccc; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center; }\n" +
                "        .error-container h1 { color: red; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"error-container\">\n" +
                "    <h1>Error in configuration</h1>\n" +
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
     * Extracts uploaded file content from the multipart form data contained in the request body.
     *
     * @param requestInfo object containing headers and content stream of the request
     * @return the parsed content from the uploaded file
     * @throws IOException if the request does not contain valid multipart data
     */
    public String readFile(RequestInfo requestInfo) throws IOException {
        // Ensure request is multipart/form-data
        String contentType = requestInfo.getHeaders().get("Content-Type");
        if (contentType == null || !contentType.contains("multipart/form-data")) {
            throw new IOException("Expected multipart/form-data in Content-Type");
        }

        String boundary = contentType.split("boundary=")[1];

        // Use BufferedReader to extract the file content following the boundary
        ByteArrayInputStream contentStream = new ByteArrayInputStream(requestInfo.getContent());
        BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8));
        String line;
        StringBuilder filteredContent = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.contains(boundary)) {
                break; // Skip multipart headers
            }
            filteredContent.append(line).append("\n");
        }

        return filteredContent.toString();
    }
}
