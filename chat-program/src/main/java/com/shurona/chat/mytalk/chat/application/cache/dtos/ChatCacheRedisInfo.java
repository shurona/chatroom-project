package com.shurona.chat.mytalk.chat.application.cache.dtos;

import com.shurona.chat.mytalk.chat.application.cache.ChatCacheInfo;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatCacheRedisInfo implements ChatCacheInfo {

    private static final String LAST_MESSAGE_PREFIX = "last-message:";  //  사용자 ID로 토큰 조회
    private static final String UNREAD_COUNT = "unread-count:"; // unreadCount
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, LastMessageOfChatDto> chatRoomLastTimeTemplate;


    @Override
    public boolean checkLastMessageUpdate(ChatRoom room, ChatLog chatLog) {
        boolean isUpdate = false;
        String key = LAST_MESSAGE_PREFIX + room.getId();

        LastMessageOfChatDto chatDto = chatRoomLastTimeTemplate.opsForValue().get(key);

        // 현재 저장되어 있는 데이터가 없거나 현재 들어온 것이 더 최근이면 업데이트 해준다.
        if (chatDto == null || chatDto.lastTime().isBefore(chatLog.getCreatedAt())) {
            chatRoomLastTimeTemplate.opsForValue()
                .set(key, new LastMessageOfChatDto(
                    chatLog.getContent(), chatLog.getCreatedAt()), Duration.ofDays(14));
            isUpdate = true;
        }

        return isUpdate;
    }

    @Override
    public void calculateUnreadCount(
        ChatRoom chatRoom, List<ChatLog> chatLogList, List<ChatUser> chatUserList) {

        String key = UNREAD_COUNT + chatRoom.getId();

        for (ChatLog chatLog : chatLogList) {
            // 로그 아이디와 유저가 최근 읽은 메시지 아이디 숫자를 기준으로 읽지않은 유저의 수를 계산한다.
            int unreadCount = (int) chatUserList.stream()
                .filter(user -> chatLog.getId() > user.getLastReadMessageId())
                .count();

            // 중복 값을 미리 삭제해준다.
            redisTemplate.opsForZSet()
                .removeRangeByScore(key, chatLog.getId(), chatLog.getId());
            // 계산한 읽지 않은 유저의 값을 저장해준다.
            redisTemplate.opsForZSet()
                .add(key, chatLog.getId() + ":" + unreadCount, chatLog.getId());

        }

        // 기한은 30일로 유지해준다.
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    @Override
    public Map<Long, Integer> getUnreadCountByChatRoomId(Long chatRoomId) {

        String key = UNREAD_COUNT + chatRoomId;

        Set<ZSetOperations.TypedTuple<String>> results =
            redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

        if (results == null) {
            return Map.of();
        }

        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.getScore().longValue(),
                tuple -> extractUnreadCountFromValue(tuple.getValue())
            ));
    }

    @Override
    public Map<Long, Integer> getUnreadCountByChatRoomIdAndLogRange(Long chatRoomId,
        Long startLogId, Long endLogId) {

        String key = UNREAD_COUNT + chatRoomId;

        Set<TypedTuple<String>> results =
            redisTemplate.opsForZSet().rangeByScoreWithScores(
                key, startLogId.doubleValue(), endLogId.doubleValue());

        if (results == null) {
            return Map.of();
        }

        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.getScore().longValue(),
                tuple -> extractUnreadCountFromValue(tuple.getValue())
            ));
    }

    /**
     * zSet을 활용하기 위해서 추가한 String으로 부터 readCount 추출
     */
    private int extractUnreadCountFromValue(String value) {
        String[] split = value.split(":");
        return Integer.parseInt(split[1]);

    }
}
