
package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/password")
public class AccountPasswordController {

    private final UserService userService;

    public AccountPasswordController(UserService userService) {
        this.userService = userService;
    }

    public record SetPasswordRequest(
            @NotBlank String newPassword,
            @NotBlank String confirmPassword
    ) {}

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank String newPassword,
            @NotBlank String confirmPassword
    ) {}

    public record PasswordResponse(String message) {}

    @PostMapping
    public ResponseEntity<PasswordResponse> setPassword(
            @Valid @RequestBody SetPasswordRequest request
    ) {
        userService.setPassword(
                request.newPassword(),
                request.confirmPassword()
        );

        return ResponseEntity.ok(
                new PasswordResponse("Password set successfully")
        );
    }

    @PutMapping
    public ResponseEntity<PasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(
                request.currentPassword(),
                request.newPassword(),
                request.confirmPassword()
        );

        return ResponseEntity.ok(
                new PasswordResponse("Password changed successfully")
        );
    }
}
