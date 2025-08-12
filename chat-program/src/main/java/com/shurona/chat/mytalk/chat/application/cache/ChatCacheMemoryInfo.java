package com.shurona.chat.mytalk.chat.application.cache;

import com.shurona.chat.mytalk.chat.application.cache.dtos.LastMessageOfChatDto;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//@Component
public class ChatCacheMemoryInfo implements ChatCacheInfo {

    // 채팅방의 최근 메시지를 캐시해서 갖고 온다.
    private Map<Long, LastMessageOfChatDto> chatRoomLastTime = new HashMap<>();

    // 채팅방의 유저별로 안 읽은 메시지의 숫자를 캐시해서 갖고 온다.
    private Map<Long, TreeMap<Long, Integer>> unreadCountCache = new HashMap<>();

    @Override
    public boolean checkLastMessageUpdate(ChatRoom room, ChatLog chatLog) {
        boolean isUpdate = false;
        // 채팅 방마다의 Last Message인지 확인하고 Boolean을 return 해준다.
        LastMessageOfChatDto roomLastTimeChat = chatRoomLastTime.getOrDefault(room.getId(), null);
        if (roomLastTimeChat == null
            || roomLastTimeChat.lastTime().isBefore(chatLog.getCreatedAt())) {
            chatRoomLastTime.put(
                room.getId(),
                new LastMessageOfChatDto(chatLog.getContent(), chatLog.getCreatedAt()));
            isUpdate = true;
        }

        return isUpdate;
    }

    @Override
    public void calculateUnreadCount(
        ChatRoom chatRoom, List<ChatLog> chatLogList, List<ChatUser> chatUserList) {
        // ChatRoom의 ChatLog를 조회하고 없으면 초기화 한다.
        Map<Long, Integer> chatLogUnreadCounts = unreadCountCache
            .computeIfAbsent(chatRoom.getId(), k -> new TreeMap<>());

        for (ChatLog chatLog : chatLogList) {
            int unreadCount = (int) chatUserList.stream()
                .filter(user -> chatLog.getId() > user.getLastReadMessageId())
                .count();
            chatLogUnreadCounts.put(chatLog.getId(), unreadCount);
        }
    }

    @Override
    public Map<Long, Integer> getUnreadCountByChatRoomId(Long chatRoomId) {
        // 채팅방 ID로 안 읽은 메시지의 숫자를 가져온다.
        return unreadCountCache.getOrDefault(chatRoomId, new TreeMap<>());
    }


    @Override
    public Map<Long, Integer> getUnreadCountByChatRoomIdAndLogRange(Long chatRoomId,
        Long startLogId, Long endLogId) {

        TreeMap<Long, Integer> unreadCountMap = unreadCountCache.getOrDefault(chatRoomId,
            new TreeMap<>());

        // startLogId와 endLogId 사이의 안 읽은 메시지 숫자를 필터링한다.
        return unreadCountMap.subMap(startLogId, true, endLogId, true);
    }
}
