package com.shurona.chat.mytalk.chat.infrastructure.redis.subscriber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shurona.chat.mytalk.chat.infrastructure.redis.dto.BaseMessage;
import com.shurona.chat.mytalk.chat.infrastructure.redis.dto.ChatMessageDto;
import com.shurona.chat.mytalk.chat.infrastructure.redis.dto.ReadNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
//public class RedisSubscriber {
public class RedisSubscriber {

    private static String chatRoomDestinationPrefix = "/topic/room/";

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    // MessageListenerAdapter가 이 메소드를 호출함
    public void onMessage(String message, String channel) {
        try {
            BaseMessage<?> baseMessageDto = objectMapper.readValue(message,
                BaseMessage.class);

            switch (baseMessageDto.type()) {
                case CHAT -> {
                    deliverWriteChatLog(objectMapper.readValue(message,
                        new TypeReference<BaseMessage<ChatMessageDto>>() {
                        }));
                }
                case READ_NOTIFICATION -> {
                    notificationReading(objectMapper.readValue(message,
                        new TypeReference<BaseMessage<ReadNotificationDto>>() {
                        }));
                }
                default -> {
                    log.error("Unsupported Type");
                }
            }


        } catch (Exception e) {
            log.error("메시지 처리 실패: channel={}, message={}", channel, message, e);
        }
    }

    private void deliverWriteChatLog(BaseMessage<ChatMessageDto> baseMessage) {
        // 해당 채팅룸으로 구독한 유저들에게 전달을 해준다..
        messagingTemplate.convertAndSend(
            chatRoomDestinationPrefix + baseMessage.roomId(), baseMessage.payload());
    }

    private void notificationReading(BaseMessage<ReadNotificationDto> baseMessage) {

        // 읽었음 표시해준다. 여기서는 추가되는 Message가 없으므로 null로 전송한다.
        messagingTemplate.convertAndSend(
            chatRoomDestinationPrefix + baseMessage.roomId() + "/read-notifications",
            baseMessage.payload());
    }
}
