package com.masantello.bookstoremanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class TestController {

    @GetMapping
    public ResponseEntity<String> getText() {
        return ResponseEntity.ok("Hello, World!");
    }
}
