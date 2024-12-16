package com.bluehawana.smrtmart.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/uploads")
@Slf4j
public class ImageController {
    private static final String IMAGES_PATH = "static/uploads/";

    @GetMapping("/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        log.info("Requesting image: {}", filename);

        try {
            Resource resource = new ClassPathResource(IMAGES_PATH + filename);
            if (!resource.exists()) {
                log.error("Image not found: {}", filename);
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (IOException e) {
            log.error("Error loading image: {}", filename, e);
            return ResponseEntity.internalServerError().body("Error loading image");
        }
    }

    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}