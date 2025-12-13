package com.example.jiralite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "jira-group")
    public void listen(String message) {
        log.info("CONSUMER received payload: {}", message);

    }
}