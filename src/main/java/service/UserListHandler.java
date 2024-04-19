package service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import model.User;
import webserver.SessionManager;
import webserver.http.request.HttpCookie;
import webserver.http.request.HttpRequest;
import webserver.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserListHandler {
    public static HttpResponse handleUserList(HttpRequest request) throws IOException {
        System.out.println("Login result: " + isUserLoggedIn(request));
        if (isUserLoggedIn(request)) {
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/templates");
            loader.setSuffix(".html");
            Handlebars handlebars = new Handlebars(loader);
            byte[] body = generateUserListHtml(handlebars);

            Map<String, String> responseHeader = new HashMap<>();
            responseHeader.put("Content-Type", "text/html;charset=utf-8");
            responseHeader.put("Content-Length", String.valueOf(body.length));

            return new HttpResponse("HTTP/1.1", 200, "OK", responseHeader, body);
        } else {
            UserService service = new UserService();
            return service.performLogin(request);
        }
    }

    public static byte[] generateUserListHtml(Handlebars handlebars) throws IOException {
        Template template = handlebars.compile("user/list");
        List<User> users = new ArrayList<>(DataBase.findAll());
        List<Map<String, Object>> indexedUsers = IntStream.range(0, users.size())
                .mapToObj(index -> Map.of("index", index + 1, "user", users.get(index)))
                .collect(Collectors.toList());
        byte[] body = template.apply(Map.of("users", indexedUsers)).getBytes();
        return body;
    }

    public static boolean isUserLoggedIn(HttpRequest httpRequest) {
        String cookies = httpRequest.getHeader().getCookie();

        String sessionId = HttpCookie.parseSessionId(cookies);
        return SessionManager.findSession(sessionId);
    }
}
