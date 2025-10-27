package com.example.kafka.controller;

import com.example.kafka.service.KafkaProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        producer.sendMessage("test-topic", message);
        return "Сообщение отправлено: " + message;
    }

    @GetMapping("/test")
    public String test() {
        return "Kafka сервис работает!";
    }
}
