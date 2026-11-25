package com.allobank.splitbill.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("service", "Split Bill REST API");
        info.put("status", "UP");
        info.put("version", "1.0.0");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("groups", "/api/v1/groups");
        endpoints.put("h2_console", "/h2-console");
        info.put("endpoints", endpoints);

        return ResponseEntity.ok(info);
    }
}
