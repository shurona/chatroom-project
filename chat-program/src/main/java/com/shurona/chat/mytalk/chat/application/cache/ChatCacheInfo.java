package com.shurona.chat.mytalk.chat.application.cache;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import java.util.List;
import java.util.Map;

public interface ChatCacheInfo {

    /**
     * 채팅방의 최근 메시지를 캐시해서 갖고 올 수 있도록 한다.
     */
    public boolean checkLastMessageUpdate(ChatRoom room, ChatLog chatLog);

    /**
     * 채팅 내용 안 읽은 유저의 숫자를 계산해 두기 위한 함수
     */
    public void calculateUnreadCount(
        ChatRoom chatRoom, List<ChatLog> chatLogList, List<ChatUser> chatUserList);

    /**
     * 채팅 방을 기준으로 조회 한다.
     */
    public Map<Long, Integer> getUnreadCountByChatRoomId(Long chatRoomId);

    /**
     * 채팅 방 및 로그의 범위로 조회 한다.
     */
    public Map<Long, Integer> getUnreadCountByChatRoomIdAndLogRange(
        Long chatRoomId, Long startLogId, Long endLogId);

}
