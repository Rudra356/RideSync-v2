package com.ridesync_v2.ridesync.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(name = "email", unique = true, length = 254)
    private String email;

    @Size(max = 100)
    @Column(name = "username", length = 100)
    private String userName;

    /*
     * Null for accounts that authenticate exclusively through Google.
     * Store only a BCrypt hash for password-based accounts.
     */
    @Column(name = "password_hash", length = 100)
    private String password;

    @Column(name = "google_subject", unique = true, length = 255)
    private String googleSubject;

    @Column(name = "github_subject", unique = true, length = 255)
    private String githubSubject;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
