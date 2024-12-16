package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Slf4j
public class PhotoController {


    private static final String UPLOADS_PATH = "static/uploads/";

    @GetMapping
    public ResponseEntity<?> getAllPhotos() {
        try {
            Resource resource = new ClassPathResource(UPLOADS_PATH);
            File directory = resource.getFile();

            List<String> photoUrls = new ArrayList<>();

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jpg") ||
                                name.toLowerCase().endsWith(".jpeg") ||
                                name.toLowerCase().endsWith(".png")
                );

                if (files != null) {
                    for (File file : files) {
                        photoUrls.add("/api/uploads/" + file.getName());
                    }
                }
            }

            return ResponseEntity.ok(new ApiResponse("Photos retrieved successfully", true, photoUrls));

        } catch (IOException e) {
            log.error("Error reading photos directory", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Error retrieving photos", false, null));
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> getPhoto(@PathVariable String filename) {
        try {
            Resource resource = new ClassPathResource(UPLOADS_PATH + filename);
            if (!resource.exists()) {
                return ResponseEntity.notFound()
                        .build();
            }

            byte[] photoBytes = resource.getInputStream().readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(photoBytes);

        } catch (IOException e) {
            log.error("Error loading photo: {}", filename, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Error loading photo", false, null));
        }
    }
}

