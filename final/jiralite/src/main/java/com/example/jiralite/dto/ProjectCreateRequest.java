package com.example.jiralite.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectCreateRequest(
        @NotBlank(message = "Name cannot be empty")
        String name,

        String description
) {}