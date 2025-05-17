package project_biu.servlets;

import project_biu.configs.*;
import project_biu.graph.Agent;
import project_biu.graph.TopicManagerSingleton;
import project_biu.server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * This servlet processes HTTP requests related to feature evaluations.
 * Specifically responds to "GET" requests directed at the "/features" endpoint.
 */
public class FeaturesHandler implements Servlet {

    /**
     * Handles HTTP GET requests for features.
     * If the URI contains the keyword "eval", it composes an HTML response showing evaluated results.
     *
     * @param ri       the parsed request data including URI segments and headers
     * @param toClient the stream used to send data back to the HTTP client
     * @throws IOException if sending the response fails
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        if (uriContainsString(ri.getUriSegments(), "eval")) {
            String httpResponse = showEvaluationsHandle();
            toClient.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            toClient.flush();
        }
    }

    /**
     * Invoked when the servlet is shut down.
     * Used to clean up resources if needed — currently not required here.
     *
     * @throws IOException if closing fails
     */
    @Override
    public void close() throws IOException {
        // No resource management needed here
    }

    /**
     * Builds an HTML page that presents each agent's formula and its most recent calculated value.
     * The information is gathered from the system's configuration and topic manager.
     *
     * @return a string containing the full HTTP response with HTML content
     */
    private String showEvaluationsHandle() {
        GenericConfig gc = ConfLoader.gc;
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        List<Agent> agents = gc.getAgents();

        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<html>\n");
        htmlTable.append("<body>\n");
        htmlTable.append("<h1> Topics Values </h1>\n");
        htmlTable.append("<table border='1'>");
        htmlTable.append("<tr><th>Agent</th><th>Equation</th><th>Result</th></tr>"); // Table columns

        for (Agent agent : agents) {
            if (agent instanceof ParallelAgent) {
                Agent a = ((ParallelAgent) agent).getAgent();

                switch (a) {
                    case IncAgent incAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>")
                                .append(incAgent.getOutputTopicName()).append(" = ")
                                .append(incAgent.getInputTopicName()).append(" + 1")
                                .append("</td>");
                        String lastMessage = tm.getTopic(incAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case PlusAgent plusAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>")
                                .append(plusAgent.getOutputTopicName()).append(" = ")
                                .append(plusAgent.getFirstTopicName()).append(" + ")
                                .append(plusAgent.getSecondTopicName())
                                .append("</td>");
                        String lastMessage = tm.getTopic(plusAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case MulAgent mulAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>")
                                .append(mulAgent.getOutputTopicName()).append(" = ")
                                .append(mulAgent.getFirstTopicName()).append(" * ")
                                .append(mulAgent.getSecondTopicName())
                                .append("</td>");
                        String lastMessage = tm.getTopic(mulAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case DivAgent divAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>")
                                .append(divAgent.getOutputTopicName()).append(" = ")
                                .append(divAgent.getFirstTopicName()).append(" / ")
                                .append(divAgent.getSecondTopicName())
                                .append("</td>");
                        String lastMessage = tm.getTopic(divAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case ExponnentAgent exponnentAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>")
                                .append(exponnentAgent.getOutputTopicName()).append(" = ")
                                .append(exponnentAgent.getFirstTopicName()).append("^")
                                .append(exponnentAgent.getSecondTopicName())
                                .append("</td>");
                        String lastMessage = tm.getTopic(exponnentAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    default -> {
                        // Skip unknown agent types
                    }
                }
            }
        }

        htmlTable.append("</table>\n");
        htmlTable.append("</body>\n");

        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + htmlTable.toString().getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + htmlTable.toString();

        return httpResponse;
    }

    /**
     * Checks whether a specific keyword exists within the URI segments of the incoming request.
     *
     * @param uriSegment the array of URI segments to inspect
     * @param target     the string to search for within the URI
     * @return true if the target is found, otherwise false
     */
    private boolean uriContainsString(String[] uriSegment, String target) {
        return Arrays.asList(uriSegment).contains(target);
    }
}
