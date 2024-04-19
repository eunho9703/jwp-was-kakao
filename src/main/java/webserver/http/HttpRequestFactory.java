package webserver.http;

import utils.HttpRequestParser;
import utils.IOUtils;
import webserver.http.request.HttpRequest;
import webserver.http.request.HttpRequestHeader;
import webserver.http.request.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequestFactory {

    public static HttpRequest createRequest(BufferedReader reader) throws Exception {
        RequestLine requestLine = HttpRequestParser.parseRequestLine(reader.readLine());

        String queryString = requestLine.getQueryString();
        Map<String, String> queryParams = HttpRequestParser.parseQueryParams(queryString);

        HttpRequestHeader header = HttpRequestParser.parseHeaders(getHeaderLines(reader));

        int contentLength = header.getContentLength();
        String body = IOUtils.readData(reader, contentLength);

        return new HttpRequest(requestLine, header, body, queryParams);
    }

    private static List<String> getHeaderLines(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }

        return headerLines;
    }
}
