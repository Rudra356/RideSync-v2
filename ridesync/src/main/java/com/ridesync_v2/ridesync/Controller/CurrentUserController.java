
package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Entity.UserPrincipal;
import com.ridesync_v2.ridesync.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrentUserController {

    private final UserService userService;

    public CurrentUserController(UserService userService) {
        this.userService = userService;
    }

    public record CurrentUserResponse(
            boolean authenticated,
            String email,
            boolean hasPassword,
            boolean googleLinked,
            boolean githubLinked
    ) {}

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            Authentication authentication
    ) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.getAuthenticatedUser();

        return ResponseEntity.ok(
                new CurrentUserResponse(
                        true,
                        user.getEmail(),
                        StringUtils.hasText(user.getPassword()),
                        StringUtils.hasText(user.getGoogleSubject()),
                        StringUtils.hasText(user.getGithubSubject())
                )
        );
    }
}
