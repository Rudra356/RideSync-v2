package com.ridesync_v2.ridesync.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrentUserController {

    public record CurrentUserResponse(
            boolean authenticated,
            String email
    ) {}

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                new CurrentUserResponse(
                        true,
                        authentication.getName()
                )
        );
    }
}