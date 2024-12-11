package com.bluehawana.smrtmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(new ApiResponse("Welcome to Smart Mart API", true));
    }

    private record ApiResponse(String message, boolean success) {}
}