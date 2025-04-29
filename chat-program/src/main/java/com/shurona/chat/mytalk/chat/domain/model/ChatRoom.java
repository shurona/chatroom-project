package com.shurona.chat.mytalk.chat.domain.model;

import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomRole;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.user.domain.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType type;

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ChatUser> chatUserList = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ChatLog> chatLogList = new ArrayList<>();

    /*
        static method pattern
     */
    public static ChatRoom createChatRoom(String name, RoomType type, User user,
        List<User> userList) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.name = name;
        chatRoom.type = type;

        chatRoom.getChatUserList()
            .add(ChatUser.createChatUser(user, chatRoom, RoomRole.ADMIN));

        for (User userInfo : userList) {
            // 롤을 먼저 분리 한다.
            RoomRole role;
            if (RoomType.PRIVATE.equals(type)) {
                role = RoomRole.ADMIN;
            } else {
                role = RoomRole.MEMBER;
            }
            chatRoom.getChatUserList()
                .add(ChatUser.createChatUser(userInfo, chatRoom, role));
        }

        return chatRoom;
    }

    public void addChatLog(User user, String chatData, ChatContentType type) {
        this.chatLogList.add(ChatLog.createLog(this, user, chatData, type));
    }


}
