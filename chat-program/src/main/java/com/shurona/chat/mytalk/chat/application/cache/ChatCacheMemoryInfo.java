package com.shurona.chat.mytalk.chat.application.cache;

import com.shurona.chat.mytalk.chat.application.cache.dtos.LastMessageOfChatDto;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ChatCacheMemoryInfo implements ChatCacheInfo {

    private Map<Long, LastMessageOfChatDto> chatRoomLastTime = new HashMap<>();

    public boolean checkLastMessageUpdate(ChatRoom room, ChatLog chatLog) {
        boolean isUpdate = false;
        // 채팅 방마다의 Last Message인지 확인하고 Boolean을 return 해준다.
        LastMessageOfChatDto roomLastTimeChat = chatRoomLastTime.getOrDefault(room.getId(), null);
        if (roomLastTimeChat == null || roomLastTimeChat.lastTime()
            .isBefore(chatLog.getCreatedAt())) {
            chatRoomLastTime.put(
                room.getId(),
                new LastMessageOfChatDto(chatLog.getContent(), chatLog.getCreatedAt()));
            isUpdate = true;
        }

        return isUpdate;
    }

}
