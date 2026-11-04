package com.ridesync_v2.ridesync.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/health")
public class HealthController {

    public record HealthResponse(
            String status,
            String timestamp
    ) {
    }

    @GetMapping
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse(
                "UP",
                Instant.now().toString()
        );

        return ResponseEntity.ok(response);
    }
}
