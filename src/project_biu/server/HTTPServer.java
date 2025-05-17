package project_biu.server;

import project_biu.servlets.Servlet;

// Defines an HTTP server capable of managing servlets and running in its own thread
public interface HTTPServer extends Runnable {

    // Registers a servlet for a specific HTTP method and URI
    public void addServlet(String httpCommanmd, String uri, Servlet s);

    // Removes a servlet associated with the given HTTP method and URI
    public void removeServlet(String httpCommanmd, String uri);

    // Launches the HTTP server and begins listening for requests
    public void start();

    // Stops the server and releases any allocated resources
    public void close();
}
