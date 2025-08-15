package com.shurona.chat.mytalk.common.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@RequiredArgsConstructor
@EnableKafka
@Configuration
public class KafkaConfig {

    /**
     * Kafka Template
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
        ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(
            producerFactory);

        kafkaTemplate.setObservationEnabled(true);

        return kafkaTemplate;
    }

    /**
     * 카프카 리스너 설정
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory,
        KafkaTemplate<String, String> template
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

//        factory.setConcurrency();

        // Manual immediate ack mode
//        factory.getContainerProperties().setAckMode(MANUAL_IMMEDIATE);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            template);
        factory.setCommonErrorHandler(createChatErrorHandler(recoverer));

        return factory;
    }


    /**
     * 채팅 메시지 처리를 위한 에러 핸들러를 생성합니다.
     * 재시도 횟수를 제한하여 빠른 처리를 보장합니다.
     */
    private DefaultErrorHandler createChatErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        // 채팅 메시지는 빠른 처리가 중요하므로 재시도 횟수를 제한
        return new DefaultErrorHandler(
            recoverer,
            new FixedBackOff(1000L, 2L)
        );
    }

}
