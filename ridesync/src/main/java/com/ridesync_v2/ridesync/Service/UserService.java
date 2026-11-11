
package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Entity.UserPrincipal;
import com.ridesync_v2.ridesync.Repo.UserRepo;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User register(@Valid User user) {

        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }

        String email = user.getEmail().trim();

        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        validatePassword(user.getPassword());

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setGoogleSubject(null);

        return userRepo.save(user);
    }

    public Authentication verify(@Valid User user) {

        if (!StringUtils.hasText(user.getEmail())
                || !StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Email and password are required");
        }

        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        user.getEmail().trim(),
                        user.getPassword()
                )
        );
    }

    @Transactional
    public void setPassword(String newPassword, String confirmPassword) {

        User user = getAuthenticatedUser();

        if (user.getPassword() != null) {
            throw new IllegalStateException(
                    "Password already exists. Use change password instead."
            );
        }

        validateMatchingPasswords(newPassword, confirmPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Transactional
    public void changePassword(
            String currentPassword,
            String newPassword,
            String confirmPassword
    ) {

        User user = getAuthenticatedUser();

        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalStateException(
                    "No password exists. Use set password instead."
            );
        }

        if (!StringUtils.hasText(currentPassword)
                || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        validateMatchingPasswords(newPassword, confirmPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    // Public so CurrentUserController can access the authenticated user's details.
    public User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("User is not authenticated");
        }

        return userRepo.findById(principal.getUser().getUserId())
                .orElseThrow(() -> new IllegalStateException("User account not found"));
    }

    private void validateMatchingPasswords(
            String newPassword,
            String confirmPassword
    ) {

        validatePassword(newPassword);

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }

    private void validatePassword(String password) {

        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password is required");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "Password must not exceed 72 UTF-8 bytes"
            );
        }
    }
}
