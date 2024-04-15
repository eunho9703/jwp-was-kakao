package service;

import db.DataBase;
import model.User;
import utils.HttpRequestParser;
import webserver.HttpRequest;

import java.io.DataOutputStream;
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

    private void createUser(HttpRequest request, DataOutputStream dos) {
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
}
