package com.shurona.chat.mytalk.chat.domain.model;

import com.shurona.chat.mytalk.chat.domain.type.RoomRole;
import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "chat_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column
    private RoomRole role;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    /*
        static method pattern
     */
    public static ChatUser createChatUser(User user, ChatRoom room, RoomRole role) {
        ChatUser chatUser = new ChatUser();
        chatUser.user = user;
        chatUser.role = role;
        chatUser.room = room;
        chatUser.lastReadMessageId = 0L;
        return chatUser;
    }


    /**
     * 최근 읽은 메시지 정보를 업데이트 했는 지 확인한다.
     */
    public boolean updateRecentRead(Long logId) {
        // 최근 읽은 메시지가 최근이면 업데이트 해준다.
        if (logId > this.lastReadMessageId) {
            this.lastReadMessageId = logId;
            return true;
        }
        return false;
    }
}
