package org.example.socets;

import org.example.files.Pages;
import org.example.home.HttpStatusImageDownloader;
import org.example.request.HttpRequest;
import org.example.response.HttpResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.example.request.RequestMapper.mapToHttpRequest;

public class NewConnections {

    private static final int TIMEOUT = 1000;

    public void createConnection(int port) throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(10);

        ServerSocket socket = new ServerSocket(port);

        while(true) {
            System.out.println("Wait for connection...");
            Socket connection = socket.accept();
            System.out.println("Client connection successfully created!");

            connectionHandler(connection);
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

    private String requestWorker(Socket connection) throws Exception {
        InputStream is = connection.getInputStream();
        String requestText = read(is);
        if(!requestText.isEmpty()) {
            HttpRequest request = mapToHttpRequest(requestText);
            System.out.println("-------------------------------");
            System.out.println("REQUEST => " + request);
            System.out.println("-------------------------------");
            return request.getPath();
        } else {
            return "";
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
            HttpResponse response = new HttpResponse();
            response.setProtocol("HTTP/1.1");
            try {
                // get URI at format (/100 or /404 or /ololololololo)
                String path = requestWorker(connection);
                // change URI from /100 to 100
                String uri = path.substring(1);
                // check URI format (if 100 or 200 or 404 - ok and if ololololololo - throw pageNotFound.html)
                if(isInteger(uri)) {
                    download(Integer.parseInt(uri), response);
                } else {
                    String body = Pages.getPageByPath("/notFound.html");
                    response.setStatusCode(404);
                    response.setStatusText("Not Found");
                    response.setBody(body);
                }

                String responseText = response.makeResponseText();
                byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
                connection.getOutputStream().write(responseBytes);
            } catch(Exception e) {
                String body = Pages.getPageByPath("/notFound.html");
                response.setStatusCode(404);
                response.setStatusText("Not Found");
                response.setBody(body);

                String responseText = response.makeResponseText();
                byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
                connection.getOutputStream().write(responseBytes);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch(Exception ex) {
                System.out.println("Can not close connection. Reason: " + ex.getMessage());
            }
        }

    }


    public boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void download(Integer uri,  HttpResponse response) throws Exception {
        HttpStatusImageDownloader downloader = new HttpStatusImageDownloader();
        downloader.downloadStatusImage(uri);

        String body = Pages.getPageByPath("/test.html");
        response.setStatusCode(200);
        response.setStatusText("OK");
        response.setBody(body);
    }
}
