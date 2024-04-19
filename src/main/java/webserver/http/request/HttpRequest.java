package webserver.http.request;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpRequestHeader header;
    private String body;
    private final Map<String, String> queryParams;

    public HttpRequest(RequestLine requestLine, HttpRequestHeader header, String body, Map<String, String> queryParams) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
        this.queryParams = queryParams;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

}
