package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserDto;
import service.UserService;
import utils.FileIoUtils;
import utils.HttpRequestParser;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in));
             DataOutputStream dos = new DataOutputStream(out)) {

            HttpRequest request = HttpRequestFactory.createRequest(reader);

            if ("GET".equals(request.getMethod())) {
                doGet(request, dos);
                return;
            }

            if ("POST".equals(request.getMethod())) {
                doPost(request, dos);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                logger.error("Error while closing socket: " + e.getMessage());
            }
        }
    }

    private void doGet(HttpRequest request, DataOutputStream dos) throws IOException, URISyntaxException {
        Map<String, String> responseHeader = new HashMap<>();

        String requestUri = request.getRequestUri();
        if ("/".equals(requestUri)) {
            requestUri = "/index.html";
        }
        String extension = HttpRequestParser.parseExt(requestUri);

        String pathPrefix = "html".equals(extension) ? "./templates" : "./static";
        byte[] body = FileIoUtils.loadFileFromClasspath(pathPrefix + requestUri);

        responseHeader.put("Content-Type", "text/" + extension + ";charset=utf-8");
        responseHeader.put("Content-Length", String.valueOf(body.length));

        HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK", responseHeader, body);

        sendResponse(dos, response);
    }

    private void doPost(HttpRequest request, DataOutputStream dos) {
        Map<String, String> responseHeader = new HashMap<>();

        if ("/user/create".equals(request.getRequestUri())) {
            String body = request.getBody();
            Map<String, String> params = HttpRequestParser.parseQueryParams(body);

            UserDto userDto = new UserDto(
                    params.getOrDefault("userId", ""),
                    params.getOrDefault("password", ""),
                    params.getOrDefault("name", ""),
                    params.getOrDefault("email", "")
            );
            UserService service = new UserService();
            service.save(userDto);

            String host = request.getHeader().getHost();
            responseHeader.put("Location", "http://" + host + "/index.html");

            HttpResponse response = new HttpResponse("HTTP/1.1", 302, "FOUND",
                    responseHeader, "".getBytes());
            sendResponse(dos, response);
        }
    }

    private void sendResponse(DataOutputStream dos, HttpResponse response) {
        try {
            dos.write(response.getBytes());
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
