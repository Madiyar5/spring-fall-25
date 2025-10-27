package com.example.kafka;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
}
