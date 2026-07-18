package com.ridesync_v2.ridesync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank
    private String spareName;
    @NotBlank
    private String RC;
    @NotBlank
    private String issue;
    @NotBlank
    private String brandModel;
    @NotBlank
    private int price;
    @NotBlank
    private Long currentKM;
    @NotBlank
    private LocalDate replacingDate;
    @NotBlank
    private Long upcomingCheckUpKM;
    private String extraNotes;
}
