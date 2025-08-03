package com.shurona.chat.mytalk.chat.infrastructure.jpa;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatLogJpaRepository extends JpaRepository<ChatLog, Long> {

    @Query("select log from chat_log log where log.room = :room order by log.createdAt desc")
    Page<ChatLog> findByRoom(ChatRoom room, Pageable pageable);
}
