package com.shurona.chat.mytalk.chat.presentation.kafka;

import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.KAFKA_CHAT_GROUP_ID;
import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.KAFKA_CHAT_READ_TOPIC_DLT_ID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatDeadLetterRecoverer {

    @KafkaListener(
        groupId = KAFKA_CHAT_GROUP_ID,
        topics = KAFKA_CHAT_READ_TOPIC_DLT_ID
    )
    public void handleDltMessage(String message,
        @Header("kafka_dlt-exception-message") String errorMessage,
        @Header("kafka_dlt-original-topic") String originalTopic
        , Acknowledgment ack
    ) {

        log.error("DLT 메시지 수신 - 원본 토픽: {}, 메시지: {}, 에러: {}",
            originalTopic, message, errorMessage);

        // DLT 메시지 처리 로직 처리 후 ack 처리
        ack.acknowledge();
    }


    public void handleRetryTopicDlt(
        String message,
        @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
        @Header(value = KafkaHeaders.OFFSET, required = false) Long offset,
        @Header(value = KafkaHeaders.GROUP_ID, required = false) String groupId,
        @Header(value = "kafka_dlt-exception-message", required = false) String errorMessage,
        @Header(value = "kafka_dlt-exception-stacktrace", required = false) String stackTrace
        , Acknowledgment ack
    ) {
        log.error("DLT 메시지 수신 - 토픽: {}, 오프셋: {}, 그룹: {}, 메시지: {}, 에러: {}",
            topic, offset, groupId, message, errorMessage);

        // DLT 메시지 처리 로직 처리 후 ack 처리
        ack.acknowledge();
    }

}
