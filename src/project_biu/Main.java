package project_biu;

import project_biu.server.HTTPServer;
import project_biu.server.MyHTTPServer;
import project_biu.servlets.ConfLoader;
import project_biu.servlets.FeaturesHandler;
import project_biu.servlets.HtmlLoader;
import project_biu.servlets.TopicDisplayer;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        HTTPServer server=new MyHTTPServer(8080,5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET","/features", new FeaturesHandler());
        server.addServlet("GET", "/app/", new HtmlLoader("AdvancedProgrammingProject/html_files/"));
        server.start();

        // Let server get start
        Thread.sleep(5000);

        try {
            // URL to open
            URI uri = new URI("http://localhost:8080/app/index.html");

            // Check if Desktop is supported
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    // Open URL in the default browser
                    desktop.browse(uri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.in.read();
        server.close();
        System.out.println("done");
    }
}
