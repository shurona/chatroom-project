package com.shurona.chat.mytalk.chat.application;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

    /**
     * 채팅방 생성
     */
    public ChatRoom createChatRoom(
        User user, List<User> invitedUserList, RoomType type, String name);

    /**
     * 개인톡을 유저와 파트너로 조회하기
     */
    public ChatRoom findPrivateChatRoomByUser(User user, User partner);

    /**
     * id를 기준으로 채팅방 단일 조회
     */
    public ChatRoom findChatRoomById(Long id);

    /**
     * 내가 속한 채팅방 목록을 갖고 온다.
     */
    public List<ChatRoom> findChatRoomListByUser(User user);

    /**
     * 채팅 입력
     */
    public ChatLog writeChat(ChatRoom room, User user, String chatData, ChatContentType type);

    /**
     * 채팅 목록 조회
     */
    public Page<ChatLog> readChatLog(User user, ChatRoom room, Pageable pageable);
    
    /**
     * 채팅의 유저가 최근까지 읽은 아이디의 목록을 갖고 온다.
     */
    public Map<Long, Long> findUserRecentReadMap(ChatRoom room);

    /*
    ------------------------------------------------------------------------------
    ****************   그룹 채팅 방 관련  메소드   **********************
    ------------------------------------------------------------------------------
     */

    /*
        채팅 창에 초대
     */
    public ChatRoom inviteUser(User user, User friend, ChatRoom room);

    /*
        채팅 방에서 친구 밴
     */
//    public ChatRoom bannedUser(User user, User friend, ChatRoom room);

}
