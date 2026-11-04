
package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import com.ridesync_v2.ridesync.Service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public record MaintenanceResponse(
            String id,
            String spareName,
            String RC,
            String issue,
            String brandModel,
            Integer price,
            Long currentKM,
            LocalDate replacingDate,
            Long upcomingCheckUpKM,
            String extraNotes
    ) {
        public static MaintenanceResponse from(Maintenance maintenance) {
            return new MaintenanceResponse(
                    maintenance.getId(),
                    maintenance.getSpareName(),
                    maintenance.getRC(),
                    maintenance.getIssue(),
                    maintenance.getBrandModel(),
                    maintenance.getPrice(),
                    maintenance.getCurrentKM(),
                    maintenance.getReplacingDate(),
                    maintenance.getUpcomingCheckUpKM(),
                    maintenance.getExtraNotes()
            );
        }
    }

    @GetMapping("/{pageNumber}/{pageSize}")
    public ResponseEntity<List<MaintenanceResponse>> getAll(
            @PathVariable int pageNumber,
            @PathVariable int pageSize) {

        List<MaintenanceResponse> records =
                maintenanceService.getAll(pageNumber, pageSize)
                        .stream()
                        .map(MaintenanceResponse::from)
                        .toList();

        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<MaintenanceResponse> createRecord(
            @Valid @RequestBody Maintenance maintenance) {

        Maintenance saved = maintenanceService.createRecord(maintenance);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MaintenanceResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> updateRecord(
            @PathVariable String id,
            @Valid @RequestBody Maintenance maintenance) {

        Maintenance updated =
                maintenanceService.updateRecord(id, maintenance);

        return ResponseEntity.ok(MaintenanceResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String id) {
        maintenanceService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
