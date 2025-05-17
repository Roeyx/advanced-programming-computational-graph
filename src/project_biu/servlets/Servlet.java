package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import project_biu.server.RequestParser.RequestInfo;

/**
 * This interface defines the contract for any servlet that handles HTTP requests in the system.
 * Implementing classes are responsible for processing requests and generating appropriate responses.
 *
 * Servlets are used by the HTTP server to delegate request handling logic, allowing separation of concerns
 * and support for dynamic behavior (e.g., returning HTML pages, computing results, etc.).
 */
public interface Servlet {

    /**
     * Processes an HTTP request and writes the corresponding response to the client.
     *
     * This method is called by the server when a request matches the servlet's endpoint. The implementation
     * should parse the request, perform any required operations (e.g., read data, compute results), and
     * write the HTTP response to the output stream.
     *
     * @param ri       contains metadata and content extracted from the HTTP request
     * @param toClient the output stream used to send the HTTP response to the client
     * @throws IOException if an I/O error occurs during request processing or while writing the response
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Releases any held resources or performs cleanup when the servlet is no longer needed.
     *
     * This method may be used to close files, sockets, or free other system resources. For stateless servlets,
     * this method may be left empty.
     *
     * @throws IOException if an I/O error occurs during cleanup
     */
    void close() throws IOException;
}
