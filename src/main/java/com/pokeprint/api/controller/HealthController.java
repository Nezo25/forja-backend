package com.pokeprint.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        long start = System.currentTimeMillis();
        try {
            jdbcTemplate.execute("SELECT 1");
            long latency = System.currentTimeMillis() - start;
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("timestamp", System.currentTimeMillis());
            response.put("latency", latency);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
