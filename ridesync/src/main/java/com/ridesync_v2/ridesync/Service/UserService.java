
package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Repo.UserRepo;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setGoogleSubject(null);

        return userRepo.save(user);
    }

    public Authentication verify(@Valid User user) {
        if (!StringUtils.hasText(user.getEmail())
                || !StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException(
                    "Email and password are required"
            );
        }

        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        user.getEmail().trim(),
                        user.getPassword()
                )
        );
    }
}
