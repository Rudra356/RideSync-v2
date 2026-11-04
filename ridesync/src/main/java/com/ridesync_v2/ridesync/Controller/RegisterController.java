
package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    public record RegistrationResponse(
            String userId,
            String email,
            String userName,
            String message
    ) {}

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody User user
    ) {
        User registeredUser = userService.register(user);

        RegistrationResponse response = new RegistrationResponse(
                registeredUser.getUserId(),
                registeredUser.getEmail(),
                registeredUser.getUserName(),
                "Registration successful"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
