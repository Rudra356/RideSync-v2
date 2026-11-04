
package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import com.ridesync_v2.ridesync.Entity.UserPrincipal;
import com.ridesync_v2.ridesync.Repo.MaintenanceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MaintenanceService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public List<Maintenance> getAll(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and " + MAX_PAGE_SIZE
            );
        }

        return maintenanceRepository.findAllByUser_UserId(
                currentUserId(),
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(Sort.Direction.DESC, "replacingDate")
                )
        ).getContent();
    }

    @Transactional
    public Maintenance createRecord(Maintenance maintenance) {
        maintenance.setId(null);
        maintenance.setUser(currentUser());
        return maintenanceRepository.save(maintenance);
    }

    @Transactional
    public Maintenance updateRecord(String id, Maintenance updatedMaintenance) {
        Maintenance existing = findRecordOrThrow(id);

        existing.setSpareName(updatedMaintenance.getSpareName());
        existing.setRC(updatedMaintenance.getRC());
        existing.setIssue(updatedMaintenance.getIssue());
        existing.setBrandModel(updatedMaintenance.getBrandModel());
        existing.setPrice(updatedMaintenance.getPrice());
        existing.setCurrentKM(updatedMaintenance.getCurrentKM());
        existing.setReplacingDate(updatedMaintenance.getReplacingDate());
        existing.setUpcomingCheckUpKM(
                updatedMaintenance.getUpcomingCheckUpKM()
        );
        existing.setExtraNotes(updatedMaintenance.getExtraNotes());

        // Never accept an owner supplied in the request body.
        return maintenanceRepository.save(existing);
    }

    @Transactional
    public Maintenance deleteRecord(String id) {
        Maintenance existing = findRecordOrThrow(id);
        maintenanceRepository.delete(existing);
        return existing;
    }

    public Maintenance getById(String id) {
        return findRecordOrThrow(id);
    }

    private Maintenance findRecordOrThrow(String id) {
        return maintenanceRepository.findByIdAndUser_UserId(
                id,
                currentUserId()
        ).orElseThrow(() ->
                new MaintenanceNotFoundException(
                        "Maintenance record not found"
                )
        );
    }

    private UserPrincipal currentPrincipal() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Authentication required"
            );
        }

        return principal;
    }

    private String currentUserId() {
        return currentPrincipal().getUser().getUserId();
    }

    private com.ridesync_v2.ridesync.Entity.User currentUser() {
        return currentPrincipal().getUser();
    }

    public static class MaintenanceNotFoundException
            extends RuntimeException {
        public MaintenanceNotFoundException(String message) {
            super(message);
        }
    }
}
