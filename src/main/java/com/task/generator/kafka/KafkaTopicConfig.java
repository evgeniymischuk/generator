package com.task.generator.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class KafkaTopicConfig {

    @Value("${spring.task.kafka.generator-topic}")
    private String generatorTopic;

    @Bean
    public NewTopic generatorTopic() {
        return TopicBuilder.name(generatorTopic).build();
    }
}
