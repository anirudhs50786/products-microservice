package com.motocart.products_microservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic productsTopic() {
        return TopicBuilder.name("operation-status-events").config(TopicConfig.RETENTION_MS_CONFIG, "3600000").replicas(1).build();
    }
}