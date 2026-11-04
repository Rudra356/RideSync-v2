
package com.ridesync_v2.ridesync.Repo;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaintenanceRepository extends JpaRepository<Maintenance, String> {

    Page<Maintenance> findAllByUser_UserId(String userId, Pageable pageable);

    Optional<Maintenance> findByIdAndUser_UserId(String id, String userId);

    boolean existsByIdAndUser_UserId(String id, String userId);
}
