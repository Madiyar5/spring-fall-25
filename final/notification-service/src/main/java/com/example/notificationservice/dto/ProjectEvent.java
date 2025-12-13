package com.example.notificationservice.dto;

import java.time.LocalDateTime;


// Поля должны называться так же, как в JSON
public record ProjectEvent(
        Long id,
        String name,
        String description,
        String status,
        LocalDateTime createdAt
) {}