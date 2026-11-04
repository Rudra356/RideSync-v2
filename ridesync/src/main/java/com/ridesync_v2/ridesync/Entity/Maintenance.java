
package com.ridesync_v2.ridesync.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.ridesync_v2.ridesync.Entity.User;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records")
@Getter
@Setter
@NoArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "spare_name", nullable = false, length = 150)
    private String spareName;

    @NotBlank
    @Size(max = 30)
    @Column(name = "registration_number", nullable = false, length = 30)
    private String RC;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "issue", nullable = false, length = 1000)
    private String issue;

    @NotBlank
    @Size(max = 150)
    @Column(name = "brand_model", nullable = false, length = 150)
    private String brandModel;

    @NotNull
    @Min(0)
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull
    @Min(0)
    @Column(name = "current_km", nullable = false)
    private Long currentKM;

    @NotNull
    @Column(name = "replacing_date", nullable = false)
    private LocalDate replacingDate;

    @NotNull
    @Min(0)
    @Column(name = "upcoming_checkup_km", nullable = false)
    private Long upcomingCheckUpKM;

    @Size(max = 2000)
    @Column(name = "extra_notes", length = 2000)
    private String extraNotes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
