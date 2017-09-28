package xl.net.http;

import xl.net.http.HttpRequest;
import xl.net.http.HttpResponse;
import xl.net.http.HttpServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * HTTP Server Test. (Helper functions are not commented.)
 */
public class Main {

    private static HttpServer server;


    public static void main(String[] args) throws IOException {
        server = new HttpServer(new InetSocketAddress("127.0.0.1", 9000));
        server.handleGetOn("\\/.*", Main::handleAllGet);
        server.handlePostOn("\\/.*", Main::handleAllPost);
        server.handlePutOn("\\/.*", Main::handleAllPut);
        server.handleDeleteOn("\\/.*", Main::handleAllDelete);
        try {
        	server.start();
        	Thread.currentThread().join();
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        } 
        finally {
        	 server.shutdown();
        }
    }

    private static String readTextResponse(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static void writeTextResponse(HttpResponse response, String content) {
        ByteBuffer responseBody = ByteBuffer.wrap(content.getBytes());
        response.getHeaders().put("Content-Type", "text/plain");
        response.getHeaders().put("Content-Length", String.valueOf(responseBody.limit()));
        response.setEntity(responseBody);
    }

    private static void handleAllGet(HttpRequest request, HttpResponse response) {
        String name = request.getUrlParams().get("name");
        String greet = String.format("Hello, %s!", (name == null) ? "world" : name);
        writeTextResponse(response, greet);
    }

    private static void handleAllPost(HttpRequest request, HttpResponse response) {
        String name = request.getBodyParams().get("name");
        String greet = String.format("Hello, %s!", (name == null) ? "world" : name);
        ByteBuffer responseBody = ByteBuffer.wrap(greet.getBytes());
        writeTextResponse(response, greet);
    }

    private static void handleAllPut(HttpRequest request, HttpResponse response) {
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            ByteBuffer requstBody = request.getBody();
            String name = (requstBody != null) ? decoder.decode(requstBody).toString() : null;
            String greet = String.format("Hello, %s!", (name == null) ? "world" : name);
            writeTextResponse(response, greet);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
    }

    private static void handleAllDelete(HttpRequest request, HttpResponse response) {
        String greet = "Hello, world! is deleted";
        writeTextResponse(response, greet);
    }
}