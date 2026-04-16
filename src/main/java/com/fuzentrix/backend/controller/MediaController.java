package com.fuzentrix.backend.controller;

import com.fuzentrix.backend.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MinioService minioService;

    @GetMapping("/stream-url")
    @PreAuthorize("hasAuthority('STREAM_VIDEO') or hasAuthority('VIEW_COURSE')")
    public ResponseEntity<Map<String, String>> getStreamUrl(@RequestParam String fileKey) {
        // In a real scenario, you'd want to check if the user is enrolled in the course that this fileKey belongs to.
        // For demonstration, we simply require the 'STREAM_VIDEO' or 'VIEW_COURSE' authority.
        
        String preSignedUrl = minioService.getPresignedUrl(fileKey);
        return ResponseEntity.ok(Map.of("url", preSignedUrl));
    }
}
