package com.example.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "test-topic", groupId = "demo-group")
    public void listen(String message) {
        System.out.println("Получено из топика [test-topic]: " + message);
    }
}
