package com.shurona.chat.mytalk.chat.infrastructure;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatLogJpaRepository extends JpaRepository<ChatLog, Long> {

    @Query("select log from chat_log log where log.room = :room order by log.createdAt desc")
    List<ChatLog> findByRoom(ChatRoom room, Pageable pageable);
}
