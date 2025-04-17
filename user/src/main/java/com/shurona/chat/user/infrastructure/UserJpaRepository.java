package com.shurona.chat.user.infrastructure;

import com.shurona.chat.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
