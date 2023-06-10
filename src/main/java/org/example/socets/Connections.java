package org.example.socets;

import org.example.files.Pages;
import org.example.request.HttpRequest;
import org.example.response.HttpResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.request.RequestMapper.mapToHttpRequest;

public class Connections {

    private static final int TIMEOUT = 1000;

    public void createConnection(int port) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        ServerSocket socket = new ServerSocket(port);

        while(true) {
            System.out.println("Wait for connection...");
            Socket connection = socket.accept();
            System.out.println("Client connection successfully created!");

            executor.submit(() -> connectionHandler(connection));
        }
    }

    /* Helpers */

    private String read(InputStream is) throws Exception {
        Thread.sleep(TIMEOUT);
        byte[] buffer = new byte[1024 * 20];
        int length = 0;
        while(is.available() > 0) {
            int read = is.read(buffer, length, is.available());
            length += read;

            Thread.sleep(TIMEOUT);
        }

        return new String(buffer, 0, length);
    }

    private Optional<HttpRequest> requestWorker(Socket connection) throws Exception {
        InputStream is = connection.getInputStream();
        String requestText = read(is);
        if(!requestText.isEmpty()) {
            HttpRequest request = mapToHttpRequest(requestText);
            System.out.println("-------------------------------");
            System.out.println("REQUEST => " + request);
            System.out.println("-------------------------------");
            return Optional.of(request);
        } else {
            return Optional.empty();
        }
    }

    private void responseWorker(Socket connection, HttpRequest request) throws Exception {

        HttpResponse response = new HttpResponse();
        response.setProtocol(request.getProtocol());
        try {
            String body = Pages.getPageByPath(request.getPath());
            response.setStatusCode(200);
            response.setStatusText("OK");
            response.setBody(body);
        } catch(FileNotFoundException e) {
            String body = Pages.getPageByPath("/notFound.html");
            response.setStatusCode(404);
            response.setStatusText("Not Found");
            response.setBody(body);
        }
        String responseText = response.makeResponseText();
        System.out.println("+++++++++++++++++++++++++++++++");
        System.out.println("RESPONSE => " + responseText);
        System.out.println("+++++++++++++++++++++++++++++++");
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);

        connection.getOutputStream().write(responseBytes);
    }

    private void connectionHandler(Socket connection) {
        try {
            Optional<HttpRequest> optionalHttpRequest = requestWorker(connection);
            if(optionalHttpRequest.isPresent()) {
                HttpRequest request = optionalHttpRequest.get();
                responseWorker(connection, request);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
