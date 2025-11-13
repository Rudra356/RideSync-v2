package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import com.ridesync_v2.ridesync.Service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // allow React app
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    // ✅ Get paginated records (REST alternative)
    @GetMapping("/{pageNumber}/{pageSize}")
    public ResponseEntity<List<Maintenance>> getAll(
            @PathVariable int pageNumber,
            @PathVariable int pageSize) {
        try {
            List<Maintenance> records = maintenanceService.getAll(pageNumber, pageSize);
            if (!records.isEmpty()) {
                return ResponseEntity.ok(records);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching records", e);
        }
    }

    // ✅ Create Record (POST)
    @PostMapping
    public ResponseEntity<Maintenance> createRecord(@RequestBody Maintenance maintenance) {
        try {
            Maintenance saved = maintenanceService.createRecord(maintenance);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            throw new RuntimeException("Record not created", e);
        }
    }

    // ✅ Update
    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> updateRecord(
            @PathVariable String id,
            @RequestBody Maintenance maintenance) {
        Maintenance updated = maintenanceService.updateRecord(maintenance, id);
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String id) {
        maintenanceService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
