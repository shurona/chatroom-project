package com.shurona.chat.mytalk.chat.common.config;

import static com.shurona.chat.mytalk.chat.common.variable.StaticVariable.KAFKA_CHAT_WRITTEN_TOPIC_ID;

import com.shurona.chat.mytalk.chat.presentation.kafka.ChatDeadLetterRecoverer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.EndpointHandlerMethod;

//@Configuration
//@EnableKafkaRetryTopic
public class KafkaRetryConfig {


    //    @Bean
    public RetryTopicConfiguration chatRetryTopicConfiguration(
        KafkaTemplate<String, String> kafkaTemplate
    ) {

        return RetryTopicConfigurationBuilder
            .newInstance()
            .maxAttempts(2)
            .fixedBackOff(1000L)
            .dltSuffix("-dlt")
//            .autoCreateTopics(false, null, null)
//            .retryOn(Exception.class) // 다시 시작할 에러
//            .notRetryOn(Exception.class) // 다시 시작하지 않을 에러
            .dltHandlerMethod(
                new EndpointHandlerMethod(ChatDeadLetterRecoverer.class, "handleRetryTopicDlt"))
            .includeTopic(KAFKA_CHAT_WRITTEN_TOPIC_ID)
            .create(kafkaTemplate);
    }

}
