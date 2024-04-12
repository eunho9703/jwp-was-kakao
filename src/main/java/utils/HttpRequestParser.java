package utils;

import webserver.HttpRequestHeader;
import webserver.RequestLine;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequestHeader parseHeaders(List<String> headerString) {
        HashMap<String, String> headers = new HashMap<>();

        for (String headerLine : headerString) {
            String[] splitHeader = headerLine.split(": ");
            headers.put(splitHeader[0], splitHeader[1]);
        }

        return new HttpRequestHeader(headers);
    }

    public static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] splitParams = queryString.split("\\&");

        for (String splitParam : splitParams) {
            String[] splitParamElements = splitParam.split("=");
            String key = splitParamElements[0];
            String value = splitParamElements.length < 2 ? "" : splitParamElements[1];

            queryParams.put(key, value);
        }

        return queryParams;
    }

    public static String parseExt(String requestUri) {
        String[] splitResource = requestUri.split("\\.");
        return splitResource.length < 2 ? null : splitResource[splitResource.length - 1];
    }

    public static RequestLine parseRequestLine(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");

        String method = splitRequestLine[0];

        String requestUri = URLDecoder.decode(splitRequestLine[1], StandardCharsets.UTF_8);
        String queryString = "";
        if (requestUri.contains("?")) {
            String[] splitQueryString = requestUri.split("\\?");
            requestUri = splitQueryString[0];
            queryString = splitQueryString[1];
        }

        String httpVersion = splitRequestLine[2];

        return new RequestLine(method, requestUri, queryString, httpVersion);
    }
}
