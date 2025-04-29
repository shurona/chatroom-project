package com.shurona.chat.mytalk.chat.domain.model;

import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "chat_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "content_type")
    private ChatContentType type;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "log")
    private List<ReadReceipt> readReceiptList = new ArrayList<>();

    /*
        static method pattern
     */
    public static ChatLog createLog(ChatRoom room, User user, String content,
        ChatContentType type) {
        ChatLog chatLog = new ChatLog();
        chatLog.room = room;
        chatLog.user = user;
        chatLog.content = content;
        chatLog.type = type;

        return chatLog;
    }


}
