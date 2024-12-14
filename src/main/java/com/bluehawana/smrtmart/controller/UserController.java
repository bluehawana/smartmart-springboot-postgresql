package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<String> getAllUsers() {
        log.info("Received request to get all users");
        try {
            var users = userService.getAllUsers();
            log.info("Found {} users", users.size());
            return ResponseEntity.ok(users.toString());
        } catch (Exception e) {
            log.error("Error getting users", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}