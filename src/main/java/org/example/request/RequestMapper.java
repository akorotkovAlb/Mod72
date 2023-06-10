package org.example.request;

import org.example.props.Method;

public class RequestMapper {

    public static HttpRequest mapToHttpRequest(String text) {
        HttpRequest request = new HttpRequest();
        String[] lines = text.replace("\r", "").split("\n");

        String[] firstLineParts = lines[0].split(" ");
        request.setMethod(Method.valueOf(firstLineParts[0]));
        request.setPath(firstLineParts[1]);
        request.setProtocol(firstLineParts[2]);

        int bodyStartLineNumber = -1;
        for(int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if(line.equals("")) {
                bodyStartLineNumber = i + 1;
                break;
            } else {
                String[] header = line.split(": ");
                request.getHeaders().put(header[0], header[1]);
            }
        }

        if (bodyStartLineNumber > 0) {
            StringBuilder sb = new StringBuilder();
            for(int i = bodyStartLineNumber; i < lines.length; i++) {
                sb.append(lines[i]).append("\n");
            }
            request.setBody(sb.toString());
        } else {
            request.setBody("");
        }

        return request;
    }
}
