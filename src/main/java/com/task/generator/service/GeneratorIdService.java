package com.task.generator.service;

import com.task.generator.model.Id;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GeneratorIdService {
    @KafkaListener(topics = "${spring.task.kafka.generator-topic}")
    public void listener(
            final @Payload String message,
            final @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
            final @Header(KafkaHeaders.OFFSET) Long offset
    ) {
        if (!"skip".equals(message)) {
            bufferList.add(offset);
        }
    }

    public Id generateId() throws ExecutionException, InterruptedException {
        //second variant UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE - version 2 not time base, but maybe collision
        final Long offset;
        if (bufferList.isEmpty()) {
            offset = kafkaTemplate.send(generatorTopic, "skip").get().getRecordMetadata().offset();
        } else {
            offset = bufferList.remove(0);
        }
        return new Id(offset);
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 100)
    private void generateIdInBuffer() {
        if (bufferList.size() < (capacity - 1)) {
            kafkaTemplate.send(generatorTopic, "", "");
        }
    }

    private final int capacity = 1_000_000;
    private final List<Long> bufferList = new ArrayList<>(capacity);
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.task.kafka.generator-topic}")
    private String generatorTopic;

    public GeneratorIdService(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
