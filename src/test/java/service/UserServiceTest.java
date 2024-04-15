package service;

import db.DataBase;
import model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    @Test
    void 사용자_정보를_DB에_저장한다() {
        //given
        UserService service = new UserService();

        String userId = "cu";
        String password = "password";
        String name = "이동규";
        String email = "brainbackdoor@gmail.com";
        UserDto userDto = new UserDto(userId, password, name, email);

        //when
        User user = service.save(userDto);

        //then
        Assertions.assertThat(DataBase.findUserById(userId)).isEqualTo(user);
    }
}
