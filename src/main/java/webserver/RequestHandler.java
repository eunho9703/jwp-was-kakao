package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        RequestMapper requestMapper = new RequestMapper();
        HttpResponse response = requestMapper.processRequest(request);

        sendResponse(dos, response);

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
