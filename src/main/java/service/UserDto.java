package service;

import java.util.Arrays;

public class UserDto {

    private final String userId;
    private final String password;
    private final String name;
    private final String email;

    public UserDto(String userId, String password, String name, String email) {
        validate(userId, password, name, email);
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    private void validate(String... inputValues) {
        Arrays.stream(inputValues)
                .forEach(this::validateString);
    }

    private void validateString(String input) {
        if (input == null |input.isBlank() || input.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] input validation failed");
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
