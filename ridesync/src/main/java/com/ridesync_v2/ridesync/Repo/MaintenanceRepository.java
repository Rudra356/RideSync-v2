package com.ridesync_v2.ridesync.Repo;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, String> {

}
