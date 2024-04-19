package service;

import db.DataBase;
import model.User;
import utils.HttpRequestParser;
import webserver.http.request.HttpRequest;
import webserver.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static service.UserListHandler.isUserLoggedIn;

public class UserService {
    public User saveUser(UserDto userDto) {
        User user = new User(userDto.getUserId(),
                userDto.getPassword(),
                userDto.getName(),
                userDto.getEmail());

        DataBase.addUser(user);
        return user;
    }

    public void createUser(HttpRequest request) {
        String body = request.getBody();
        Map<String, String> params = HttpRequestParser.parseQueryParams(body);

        UserDto userDto = new UserDto(
                params.getOrDefault("userId", ""),
                params.getOrDefault("password", ""),
                params.getOrDefault("name", ""),
                params.getOrDefault("email", "")
        );
        saveUser(userDto);
    }

    public boolean isValidUser(HttpRequest request) {
        String body = request.getBody();
        Map<String, String> params = HttpRequestParser.parseQueryParams(body);
        String userId = params.get("userId");
        String password = params.get("password");

        return Optional.ofNullable(DataBase.findUserById(userId))
                .map(User::getPassword)
                .filter(password::equals)
                .isPresent();
    }

    public static HttpResponse handleUserLogin(HttpRequest request) {
        System.out.println(isUserLoggedIn(request));
        return isUserLoggedIn(request) ?
                performHttpRedirect(request, new HashMap<>(), "/index.html") :
                performLogin(request);
    }

    public static HttpResponse performHttpRedirect(HttpRequest request, Map<String, String> responseHeader, String location) {
        responseHeader.put("Location", "http://" + request.getHeader().getHost() + location);
        return createRedirectResponse(responseHeader);
    }

    private static HttpResponse createRedirectResponse(Map<String, String> headers) {
        return new HttpResponse("HTTP/1.1", 302, "FOUND", headers, new byte[0]);
    }

    public HttpResponse performLoginFailure(HttpRequest request) {
        return performHttpRedirect(request, new HashMap<>(), "/user/login_failed.html");
    }

    public static HttpResponse performLogin(HttpRequest request) {
        return performHttpRedirect(request, new HashMap<>(), "/user/login.html");
    }
}
