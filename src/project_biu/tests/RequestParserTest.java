package project_biu.tests;
import project_biu.server.RequestParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParserTest {

    public static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: 5\n"+
                "\n" +
                "filename=\"hello_world.txt\"\n"+
                "\n" +
                "hello world!\n"+
                "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);
            System.out.println("httpCommand:"+requestInfo.getHttpCommand());
            System.out.println("uri:"+requestInfo.getUri());
            System.out.println("uriSegment:"+java.util.Arrays.toString(requestInfo.getUriSegments()));
           // System.out.println("uriParams:"+java.util.Arrays.toString(requestInfo.getParameters()));
            String contentString = new String(requestInfo.getContent());
            System.out.println("content string-----------"+contentString+"-----------");

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }

    public static void testNoContent() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n"+
        "Host: example.com\n"+
        "User-Agent: Mozilla/5.0\n"+
        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n";

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);
            System.out.println("httpCommand:"+requestInfo.getHttpCommand());
            System.out.println("uri:"+requestInfo.getUri());
            System.out.println("uriSegment:"+java.util.Arrays.toString(requestInfo.getUriSegments()));
            // System.out.println("uriParams:"+java.util.Arrays.toString(requestInfo.getParameters()));
            if(requestInfo.getContent()!=null) {
                String contentString = new String(requestInfo.getContent());
                System.out.println("content:"+contentString);
            }
            else {
                System.out.println("content: null");
            }
            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            if (requestInfo.getContent()!=null) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }

    public static void testNoAdditionalParam() {
        // Test data
        String request = "POST /api/upload HTTP/1.1\n"+
                "Host: example.com\n"+
                "Content-Type: text/plain\n"+
                "Content-Length: 11\n"+
                "User-Agent: Mozilla/5.0\n\n"+
                "\n"+
                "Hello World\n";

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);
            System.out.println("httpCommand:"+requestInfo.getHttpCommand());
            System.out.println("uri:"+requestInfo.getUri());
            System.out.println("uriSegment:"+java.util.Arrays.toString(requestInfo.getUriSegments()));
            System.out.println("uriSegment:"+java.util.Arrays.toString(requestInfo.getUriSegments()));
            if(requestInfo.getParameters()!=null) {
                System.out.println("uriParams:"+requestInfo.getParameters());;
            }
            else {
                System.out.println("uriParams: null");
            }

            //
            if(requestInfo.getContent()!=null) {
                String contentString = new String(requestInfo.getContent());
                System.out.println("content:"+contentString);
            }
            else {
                System.out.println("content: null");
            }
            // Test URI
            if (!requestInfo.getUri().equals("/api/upload")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "upload"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            if (!requestInfo.getParameters().isEmpty()) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            if (requestInfo.getContent().toString().equals("Hello World\n")) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }



}
