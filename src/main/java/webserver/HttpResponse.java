package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final String httpVersion;
    private final int statusCode;
    private final String statusText;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(String httpVersion, int statusCode, String statusText, Map<String, String> headers, byte[] body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.body = body;
    }

    public byte[] getBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion).append(" ").append(statusCode).append(" ").append(statusText).append(" \r\n");

        headers.keySet().forEach(key -> {
            String value = headers.get(key);
            sb.append(key).append(": ").append(value).append("\r\n");
        });

        sb.append("\r\n");
        byte[] header = sb.toString().getBytes();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(header);
            out.write(body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return out.toByteArray();
    }
}
