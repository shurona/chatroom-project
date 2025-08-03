package com.shurona.chat.mytalk.chat.infrastructure.jpa;

import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatUserJpaRepository extends JpaRepository<ChatUser, Long> {

    Optional<ChatUser> findByUserAndRoom(User user, ChatRoom room);

    List<ChatUser> findByRoom(ChatRoom room);
}
