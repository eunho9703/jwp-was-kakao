package service;

import db.DataBase;
import model.User;
import utils.HttpRequestParser;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    public User save(UserDto userDto) {
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
        UserService service = new UserService();
        service.save(userDto);
    }

    public HttpResponse performHttpRedirect(HttpRequest request) {
        String host = request.getHeader().getHost();
        Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Location", "http://" + host + "/index.html");

        HttpResponse response = new HttpResponse("HTTP/1.1", 302, "FOUND",
                responseHeader, "".getBytes());
        return response;
    }
}
