package com.shurona.chat.mytalk.infrastructure;

import com.shurona.chat.mytalk.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String username);

}
