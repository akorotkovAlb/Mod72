package org.example.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

    private static final String SPACE = " ";
    private static final String HEADER_SPLITTER = ": ";
    private static final String NEW_LINE = "\n";

    private String protocol;
    private int statusCode;
    private String statusText;
    private String body;
    private Map<String, String> headers = new LinkedHashMap<>();

    public String makeResponseText() {
        StringBuffer result = new StringBuffer();
        // start line
        result.append(this.protocol).append(SPACE).append(this.statusCode).append(SPACE)
                .append(statusText).append(NEW_LINE);

        // headers
        this.headers.forEach((key, value) -> {
            result.append(key).append(HEADER_SPLITTER).append(value).append(NEW_LINE);
        });
        // add content length header
        result.append("Content-Length: ").append(this.body.getBytes(StandardCharsets.UTF_8).length).append(NEW_LINE);
        // add content type header
        result.append("Content-Type: text/html; charset=utf-8").append(NEW_LINE);

        // empty line between headers and body
        result.append(NEW_LINE);

        //body
        result.append(this.body);

        return result.toString();
    }
}
