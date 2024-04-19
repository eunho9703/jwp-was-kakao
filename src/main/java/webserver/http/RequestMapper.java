package webserver.http;

import db.DataBase;
import model.User;
import service.UserService;
import utils.HttpRequestParser;
import webserver.Session;
import webserver.SessionManager;
import webserver.http.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static webserver.http.request.HttpCookie.processCookie;

public class RequestMapper {
    private static final Map<String, Function<HttpRequest, HttpResponse>> REQUEST_HANDLERS = Map.of(
            "/user/create", RequestMapper::handleUserCreate,
            "/user/login", RequestMapper::handleValidUserLogin
    );

    public HttpResponse processRequest(HttpRequest request) {
        String requestUri = request.getRequestUri();
        Function<HttpRequest, HttpResponse> handler = REQUEST_HANDLERS.get(requestUri);
        if (handler != null) {
            return handler.apply(request);
        }
        return null;
    }

    private static HttpResponse handleUserCreate(HttpRequest request) {
        UserService service = new UserService();
        service.createUser(request);
        return service.performHttpRedirect(request, new HashMap<>(), "/index.html");
    }

    static HttpResponse handleValidUserLogin(HttpRequest request) {
        UserService service = new UserService();
        if (service.isValidUser(request)) {
            return proceedLogin(service, request);
        }

        return service.performLoginFailure(request);
    }

    static HttpResponse proceedLogin(UserService service, HttpRequest request) {
        String body = request.getBody();
        Map<String, String> params = HttpRequestParser.parseQueryParams(body);
        String userId = params.get("userId");

        User user = DataBase.findUserById(userId);
        Map<String, String> cookieHeader = new HashMap<>();
        String uuid = processCookie(request, cookieHeader);

        Session session = new Session(uuid);
        session.setAttribute(uuid, user);
        SessionManager.add(session);

        return service.performHttpRedirect(request, cookieHeader, "/index.html");
    }
}