package com.ridesync_v2.ridesync.Repo;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends MongoRepository<Maintenance,String> {
}
