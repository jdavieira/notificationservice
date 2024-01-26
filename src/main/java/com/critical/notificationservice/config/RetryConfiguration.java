package com.critical.notificationservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Configuration
public class RetryConfiguration {

    @Autowired
    private RetryRegistry retryRegistry;

    @Bean
    public Retry retryWithCustomConfig() {
        RetryConfig customConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(JsonProcessingException.class, ExecutionException.class, InterruptedException.class)
                .build();

        return retryRegistry.retry("unstableKafkaService", customConfig);
    }

    @PostConstruct
    public void postConstruct() {
        Retry.EventPublisher eventPublisher = retryRegistry.retry("unstableKafkaService").getEventPublisher();
        eventPublisher.onError(event -> System.out.println("Simple Retry - On Error. Event Details: " + event));
        eventPublisher.onRetry(event -> System.out.println("Simple Retry - On Retry. Event Details: " + event));
    }
}