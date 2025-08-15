package com.shurona.chat.mytalk;

import com.shurona.chat.mytalk.config.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@ExtendWith(TestContainerConfig.class)
@EmbeddedKafka
@SpringBootTest
class MyTalkApplicationTests {

    @Test
    void contextLoads() {
    }

}
