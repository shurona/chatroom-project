package com.shurona.chat.mytalk.chat.application.cache;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;

public interface ChatCacheInfo {

    public boolean checkLastMessageUpdate(ChatRoom room, ChatLog chatLog);

}
