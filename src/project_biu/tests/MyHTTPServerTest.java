package project_biu.tests;

import project_biu.servlets.ConfLoader;
import project_biu.servlets.HtmlLoader;
import project_biu.servlets.TopicDisplayer;
import project_biu.server.MyHTTPServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MyHTTPServerTest {
    public static void runTests() throws InterruptedException {
        // Step 1: Create and register the servlets
        MyHTTPServer server = new MyHTTPServer(8080, 5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
        System.out.println("Servlets registered.");

        // Step 2: Start the server
        server.start();
        System.out.println("Server started.");

        // Step 3: Connect a client to the server and send test requests
        try {
            // Ensure only one thread is started
            System.out.println("Thread count before test: " + Thread.activeCount());

            // Test GET /publish
            String publishRequest = "GET /publish HTTP/1.1\r\nHost: localhost\r\n\r\n";
            String publishExpectedResponse = "HTTP/1.1 200 OK\r\n\r\nTopic Displayed";
            testClientRequest("localhost", 8080, publishRequest, publishExpectedResponse);

            // Test POST /upload
            String uploadRequest = "POST /upload HTTP/1.1\r\nHost: localhost\r\n\r\n";
            String uploadExpectedResponse = "HTTP/1.1 200 OK\r\n\r\nConfiguration Loaded";
            testClientRequest("localhost", 8080, uploadRequest, uploadExpectedResponse);

            // Test GET /app/
            String appRequest = "GET /app/index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
            String appExpectedResponse = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html>...</html>"; // Modify as needed
            testClientRequest("localhost", 8080, appRequest, appExpectedResponse);
            // Step 6: Wait for 2 seconds

            // Check thread count after test to ensure no additional threads were left hanging
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Step 5: Clean up resources
            server.close();
            System.out.println("Server closed.");
            Thread.sleep(2000);
            System.out.println("Thread count after test: " + Thread.activeCount());
            System.out.println("Thread count after test: " + Thread.activeCount());
            System.out.println("Thread count after test: " + Thread.activeCount());
            System.out.println("Thread count after test: " + Thread.activeCount());

        }
    }

    private static void testClientRequest(String host, int port, String request, String expectedResponse) throws Exception {
        try (Socket clientSocket = new Socket(host, port)) {
            System.out.println("Client connected to server.");

            // Send request to the server
            OutputStream out = clientSocket.getOutputStream();
            out.write(request.getBytes());
            System.out.println("Request sent to server.");

            // Read and construct the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine).append("\n");
                if (responseLine.isEmpty()) {
                    break; // End of headers
                }
            }

            // Read the response body
            while (in.ready()) {
                response.append((char) in.read());
            }

            // Normalize the responses for comparison
            String actualResponse = response.toString().trim().replaceAll("\r", "").replaceAll("\n", " ");
            String normalizedExpectedResponse = expectedResponse.trim().replaceAll("\r", "").replaceAll("\n", " ");

            // Print the actual response and expected response for comparison
            System.out.println("Actual Response: " + actualResponse);
            System.out.println("Expected Response: " + normalizedExpectedResponse);

            // Verify the response
            if (actualResponse.equals(normalizedExpectedResponse)) {
                System.out.println("Test passed!\n");
            } else {
                System.out.println("Test failed!\n");
            }

            System.out.println("Client disconnected.");
        }
    }
}
