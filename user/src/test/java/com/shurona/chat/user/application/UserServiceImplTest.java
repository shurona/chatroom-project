package com.shurona.chat.user.application;

import static org.junit.jupiter.api.Assertions.*;

import com.shurona.chat.user.config.JpaConfig;
import com.shurona.chat.user.domain.model.User;
import com.shurona.chat.user.infrastructure.UserJpaRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

        Assertions.assertThat(username).isEqualTo(userA.getUsername());
    }

}