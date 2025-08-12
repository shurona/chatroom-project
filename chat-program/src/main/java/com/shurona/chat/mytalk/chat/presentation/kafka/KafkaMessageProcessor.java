package com.shurona.chat.mytalk.chat.presentation.kafka;

import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.KAFKA_CHAT_GROUP_ID;
import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.KAFKA_CHAT_READ_TOPIC_ID;
import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.KAFKA_CHAT_WRITTEN_TOPIC_ID;
import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.chatRoomDestinationPrefix;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode;
import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.chat.infrastructure.redis.dto.BaseMessage;
import com.shurona.chat.mytalk.chat.infrastructure.redis.dto.ChatMessageDto;
import com.shurona.chat.mytalk.chat.infrastructure.redis.dto.ReadNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaMessageProcessor {


    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    // 어노테이션을 활용한 Retryable topic 설정
    //    @RetryableTopic(
//        attempts = "2",
//        backoff = @Backoff(delay = 1000),
//        dltStrategy = DltStrategy.FAIL_ON_ERROR,
//
//        include = {Exception.class}
//        // exclude = {SomeSpecificException.class} // notRetryOn에 해당
//    )
    @KafkaListener(
        groupId = KAFKA_CHAT_GROUP_ID,
        topics = KAFKA_CHAT_WRITTEN_TOPIC_ID
    )
    public void chatWrittenProcess(
        String message, @Headers MessageHeaders messageHeaders
    ) {

        try {
            BaseMessage<ChatMessageDto> chatMessageDtoBaseMessage = objectMapper.readValue(message,
                new TypeReference<BaseMessage<ChatMessageDto>>() {
                });

            log.info("룸 아이디 : {}  내용 : {}", chatMessageDtoBaseMessage.roomId(),
                chatMessageDtoBaseMessage.payload().content()
            );

            deliverWriteChatLog(chatMessageDtoBaseMessage);
        } catch (Exception e) {
            log.error("[채팅 작성 전달 에러 발생] {}", message, e);
            throw new ChatException(ChatErrorCode.USER_NOT_INCLUDE_ROOM);
        }
    }

    @KafkaListener(
        groupId = KAFKA_CHAT_GROUP_ID,
        topics = KAFKA_CHAT_READ_TOPIC_ID
    )
    public void notificationWhenReading(
        String message, @Headers MessageHeaders messageHeaders
//        , Acknowledgment ack
    ) {

        try {
            BaseMessage<ReadNotificationDto> readNotificationDto = objectMapper.readValue(
                message,
                new TypeReference<BaseMessage<ReadNotificationDto>>() {
                });

            log.info("룸 아이디 : {}  읽은 시간 : {}", readNotificationDto.roomId(),
                readNotificationDto.payload().readAt());

            notificationReading(readNotificationDto);

            // 승인한다.
//            ack.acknowledge();
        } catch (Exception e) {
            log.error("[읽는 중 에러 발생] {}", message, e);
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
