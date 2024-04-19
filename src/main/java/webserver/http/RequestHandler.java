package webserver.http;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.request.HttpCookie;
import webserver.http.request.HttpRequest;

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
                handleGetRequest(request, dos);
            }

            if ("POST".equals(request.getMethod())) {
                handlePostRequest(request, dos);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleGetRequest(HttpRequest request, DataOutputStream dos) {
        HttpCookie.RequestHandlerUtils.doGet(request, dos);
    }

    private void handlePostRequest(HttpRequest request, DataOutputStream dos) {
        HttpCookie.RequestHandlerUtils.doPost(request, dos);
    }
}
