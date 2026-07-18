package com.ridesync_v2.ridesync.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "User Id", updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @NotBlank
    @Column(name = "Username", updatable = false)
    private String userName;

    @NotBlank
    @Column(name = "Password", updatable = true)
    private String password;

    @CreationTimestamp
    @Column(name = "Created on")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "Updated on")
    private LocalDateTime updatedAt;

}
