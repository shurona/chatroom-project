package com.shurona.chat.mytalk.user.application;

import com.shurona.chat.mytalk.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserServiceImplTest {

    @Autowired
    private UserService userService;


    @Test
    public void 저장_조회_테스트() {

        String username = "username";
        String password = "password";
        String description = "description";
        String phoneNumber = "010-202-2020";

        Long userId = userService.saveUser(username, password, description, phoneNumber);

        User userA = userService.findUserById(userId);

        Assertions.assertThat(username).isEqualTo(userA.getLoginId());
    }

}