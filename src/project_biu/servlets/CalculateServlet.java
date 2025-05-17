package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import project_biu.server.RequestParser.RequestInfo;

/**
 * CalculateServlet is responsible for handling HTTP requests that involve basic arithmetic operations.
 * It extracts parameters from the request and returns the calculated result in the response.
 */
public class CalculateServlet implements Servlet {

    /**
     * Processes the incoming request and sends the computed result to the client.
     *
     * @param requestInfo contains metadata and parameter details from the HTTP request
     * @param response the stream used to send the HTTP response back to the client
     * @throws IOException if an error occurs during reading or writing
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream response) throws IOException {
        Map<String, String> parameters = requestInfo.getParameters();
        int a = Integer.parseInt(parameters.getOrDefault("a", "0"));
        int b = Integer.parseInt(parameters.getOrDefault("b", "0"));
        String operation = parameters.getOrDefault("op", "subtract");
        int result;

        // Execute the requested arithmetic operation
        switch (operation) {
            case "add":
                result = a + b;
                break;
            case "multiply":
                result = a * b;
                break;
            case "divide":
                if (b != 0) {
                    result = a / b;
                } else {
                    result = 0; // Fallback for division by zero
                }
                break;
            case "subtract":
            default:
                result = a - b;
                break;
        }

        // Construct and return an HTTP 200 OK response with the result
        String resultString = "Result: " + result;
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + resultString.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                resultString;
        response.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        response.flush();
    }

    /**
     * Performs cleanup when the servlet is no longer needed.
     * In this case, there are no resources to release.
     *
     * @throws IOException if an I/O error occurs during closing
     */
    @Override
    public void close() throws IOException {
        // No cleanup needed for this servlet implementation
    }
}
