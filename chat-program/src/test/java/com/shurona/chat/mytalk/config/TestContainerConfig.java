package com.shurona.chat.mytalk.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfig implements BeforeAllCallback {

    public static GenericContainer<?> redis;
    public final String REDIS_IMAGE = "redis:7.4.0-alpine";

    public final Integer port = 6379;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (redis == null) {
            redis = new GenericContainer(
                DockerImageName.parse(REDIS_IMAGE)).withExposedPorts(port).withReuse(true);

            redis.start();
            System.setProperty("spring.data.redis.host", redis.getHost());
            System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(port)));
        }
    }
}
