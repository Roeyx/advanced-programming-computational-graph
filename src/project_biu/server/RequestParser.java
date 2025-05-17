package project_biu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * RequestParser is responsible for reading and parsing raw HTTP request data from a BufferedReader.
 * It breaks down the HTTP request into structured components such as the command, URI, headers, parameters, and body content.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from the provided BufferedReader.
     * This method handles parsing the request line, headers, query parameters, and optionally the request body.
     *
     * @param reader the BufferedReader connected to the client input stream
     * @return a fully populated RequestInfo object with the parsed request data
     * @throws IOException if the request is malformed or cannot be read properly
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String requestLine = null;
        try {
            requestLine = reader.readLine(); // Read the first line of the HTTP request
        } catch (IOException e) {
            return null;
        }

        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new IOException("Invalid/Empty request line");
        }

        // Extract HTTP command and URI from the request line
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            throw new IOException("Invalid request missing Command/ URI");
        }
        String command = requestParts[0];
        String uri = requestParts[1];

        // Split URI into path and query string (e.g., /path?key=value)
        String[] uriComponents = uri.split("\\?", 2);
        String path = uriComponents[0];
        String[] pathSegments = Arrays.stream(path.split("/"))
                .filter(segment -> !segment.isEmpty())
                .toArray(String[]::new);

        // Extract query parameters if present in the URI
        Map<String, String> queryParams = new LinkedHashMap<>();
        if (uriComponents.length > 1) {
            parseQueryParams(uriComponents[1], queryParams);
        }

        // Read HTTP headers (key: value format)
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        // -------- ADDED SECTION --------
        /*
         * Attempt to parse any custom parameters after headers using "=" format,
         * commonly used in form submissions. We mark the current reader position
         * to restore it later in case no key=value lines are found.
         */
        reader.mark(2048); // Arbitrary mark limit; assumes no extra parameters exceed this size
        boolean extraParameters = false;
        try {
            while ((line = reader.readLine()) != null && line.trim().contains("=")) {
                extraParameters = true;
                String[] headerParts = line.split("=", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
            }
        } catch (IOException e) {
            // If no extra parameters were found, reset to start of content
            if (!extraParameters) {
                reader.reset();
            }
        }
        // -------- END ADDED SECTION --------

        // Parse content body (if any) based on Content-Length header
        int contentLength = headers.containsKey("Content-Length")
                ? Integer.parseInt(headers.get("Content-Length"))
                : 0;

        StringBuilder bodyBuilder = new StringBuilder();

        if (contentLength > 0) {
            char[] bodyBuffer = new char[contentLength];
            int bytesRead = reader.read(bodyBuffer, 0, contentLength);
            if (bytesRead == -1) {
                throw new IOException("Content body is empty.");
            }
            bodyBuilder.append(bodyBuffer);
        }

        if (reader.ready()) {
            System.err.println("Warning: Actual content length may be longer than declared Content-Length.");
        }

        byte[] contentBytes = bodyBuilder.toString().getBytes(StandardCharsets.UTF_8);

        // Uncomment for debugging
        // debugPrint(command, uri, pathSegments, queryParams, contentBytes);

        return new RequestInfo(command, uri, pathSegments, queryParams, headers, contentBytes);
    }

    /**
     * Parses key-value pairs from a query string (e.g., key1=val1&key2=val2).
     *
     * @param queryString the raw query string from the URI
     * @param queryParams a map where parsed parameters will be stored
     */
    private static void parseQueryParams(String queryString, Map<String, String> queryParams) {
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue.length > 1 ? keyValue[1].trim() : "";
            queryParams.put(key, value);
        }
    }

    /**
     * Prints all parsed components of the request for debugging purposes.
     * Useful for development and troubleshooting request parsing issues.
     */
    private static void debugPrint(String command, String uri, String[] pathSegments, Map<String, String> queryParams, byte[] contentBytes) {
        System.out.println("Command: " + command);
        System.out.println("URI: " + uri);
        System.out.println("Path Segments: " + Arrays.toString(pathSegments));
        System.out.println("Query Parameters: " + queryParams);
        System.out.println("---");
        System.out.println("Content: " + new String(contentBytes, StandardCharsets.UTF_8));
        System.out.println("---");
    }

    /**
     * RequestInfo is a structured container for the parsed components of an HTTP request.
     * It includes command type, URI, path segments, query parameters, headers, and body content.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final Map<String, String> headers;
        private final byte[] content;

        /**
         * Constructs a new RequestInfo instance from parsed HTTP data.
         *
         * @param httpCommand the request method (e.g., GET, POST)
         * @param uri the requested URI
         * @param uriSegments segments of the URI path
         * @param parameters the query parameters
         * @param headers the HTTP headers
         * @param content the body content in byte format
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments,
                           Map<String, String> parameters, Map<String, String> headers, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.headers = headers;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public byte[] getContent() {
            return content;
        }

        /**
         * Prints the request's parsed components to the console.
         * Intended for debugging and inspection.
         */
        public void printRequest() {
            System.out.println("HTTP Command: " + httpCommand);
            System.out.println("URI: " + uri);
            System.out.println("URI Segments: " + Arrays.toString(uriSegments));
            System.out.println("Parameters: " + parameters);
            System.out.println("Headers: " + headers);
            System.out.println("Content: " + new String(content, StandardCharsets.UTF_8));
        }
    }
}
