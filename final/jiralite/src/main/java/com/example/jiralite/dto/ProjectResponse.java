package com.example.jiralite.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        String status,
        LocalDateTime createdAt
) {}