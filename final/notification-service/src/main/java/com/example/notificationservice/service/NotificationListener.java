package com.example.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    // Слушаем топик "project-events"
    // groupId должен совпадать с тем, что в application.properties
    @KafkaListener(topics = "project-events", groupId = "notification-group")
    public void handleProjectCreated(String message) {
        log.info("📨 [NOTIFICATION SERVICE] Получено сообщение из Kafka!");
        log.info("📦 Тело сообщения: {}", message);

        // Здесь имитация отправки email
        log.info("🚀 Отправляем письмо администратору: 'Создан новый проект...'");
        log.info("✅ Уведомление успешно обработано.");
    }
}