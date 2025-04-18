package com.shurona.chat.mytalk.user.infrastructure;

import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.vo.UserPhoneNumber;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String username);

    Optional<User> findByPhoneNumber(UserPhoneNumber userPhoneNumber);
}
