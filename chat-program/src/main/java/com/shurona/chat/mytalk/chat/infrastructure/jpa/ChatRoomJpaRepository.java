package com.shurona.chat.mytalk.chat.infrastructure.jpa;

import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByChatUserList_User(User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE chat_room c SET c.lastMessage = :content, c.lastTime = :lastTime WHERE c.id = :id AND c.lastTime < :lastTime")
    void updateLastMessageAndTime(Long id, String content, LocalDateTime lastTime);

    @Query("""
        SELECT cr
        FROM chat_room cr
        JOIN cr.chatUserList cu
        WHERE cr.type = :roomType
        AND cu.user IN :users
        GROUP BY cr.id
        HAVING COUNT(DISTINCT cu.user.id) = :userCount
        """)
    List<ChatRoom> findPrivateRoomContainingExactUsers(
        List<User> users,
        RoomType roomType,
        int userCount
    );

}
