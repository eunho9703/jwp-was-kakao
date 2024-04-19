package webserver.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;
import utils.HttpRequestParser;
import webserver.http.HttpResponse;
import webserver.http.RequestMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static service.UserListHandler.handleUserList;
import static service.UserService.handleUserLogin;

public class HttpCookie {
    public static String processCookie(HttpRequest request, Map<String, String> responseHeader) {
        String cookies = request.getHeader().getCookie();
        if (cookies == null || !cookies.contains("JSESSIONID")) {
            String sessionId = UUID.randomUUID().toString();
            responseHeader.put("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
            return sessionId;
        }

        return parseSessionId(cookies);
    }

    public static String parseSessionId(String cookies) {
        for (String split : cookies.split("; ")) {
            if (split.contains("JSESSIONID")) {
                String[] keyValue = split.split("=");
                return keyValue[1];
            }
        }

        return "";
    }

    public static class RequestHandlerUtils {
        private static final Logger logger = LoggerFactory.getLogger(RequestHandlerUtils.class);

        public static void doGet(HttpRequest request, DataOutputStream dos) {
            try {
                String requestUri = resolveRequestUri(request.getRequestUri());
                HttpResponse response = resolveGetResponse(request, requestUri);
                sendResponse(dos, response);
            } catch (IOException | URISyntaxException e) {
                System.out.println("Error!");
                logger.error("Error occurred while handling GET request: {}", e.getMessage());
            }
        }

        public static void doPost(HttpRequest request, DataOutputStream dos) {
            HttpResponse response = new RequestMapper().processRequest(request);
            sendResponse(dos, response);
        }

        public static HttpResponse resolveGetResponse(HttpRequest request, String requestUri) throws IOException, URISyntaxException {
            switch (requestUri) {
                case "/user/list":
                case "/user/list.html":
                    System.out.println(request.getHeader());
                    System.out.println(request.getBody());
                    System.out.println("here");
                    return handleUserList(request);
                case "/user/login":
                    //System.out.println("here");
                    return handleUserLogin(request);
                default:
                    return handleDefaultGetRequest(requestUri);
            }
        }

        private static String resolveRequestUri(String requestUri) {
            return "/".equals(requestUri) ? "/index.html" : requestUri;
        }

        private static HttpResponse handleDefaultGetRequest(String requestUri) throws IOException, URISyntaxException {
            String extension = HttpRequestParser.parseExt(requestUri);
            String pathPrefix = "html".equals(extension) ? "./templates" : "./static";
            byte[] body = FileIoUtils.loadFileFromClasspath(pathPrefix + requestUri);

            Map<String, String> responseHeader = new HashMap<>();
            responseHeader.put("Content-Type", "text/" + extension + ";charset=utf-8");
            responseHeader.put("Content-Length", String.valueOf(body.length));

            return new HttpResponse("HTTP/1.1", 200, "OK", responseHeader, body);
        }

        private static void sendResponse(DataOutputStream dos, HttpResponse response) {
            try {
                dos.write(response.getBytes());
                dos.flush();
            } catch (IOException e) {
                logger.error("Error occurred while sending response: {}", e.getMessage());
            }
        }
    }
}

