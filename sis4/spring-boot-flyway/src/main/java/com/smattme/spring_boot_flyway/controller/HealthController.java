package com.smattme.spring_boot_flyway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Order Management System");
        response.put("version", "1.0.0");
        return response;
    }

    /**
     * Welcome endpoint
     */
    @GetMapping("/welcome")
    public Map<String, String> welcome() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Order Management System API!");
        response.put("documentation", "Available endpoints: /api/users, /api/orders");
        return response;
    }
}
