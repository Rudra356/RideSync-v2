package com.ridesync_v2.ridesync.Service;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import com.ridesync_v2.ridesync.Repo.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    protected static int totalRecords;

    //Get All Method
    public List<Maintenance> getAll(int pageNumber, int pageSize) {
        if(pageNumber<0){
            pageNumber=0;
        }
        if(pageSize<1){
            pageSize=1;
        }
        try{
           Pageable pages = PageRequest.of(pageNumber,pageSize);
           return maintenanceRepository.findAll(pages).getContent();
       } catch (Exception e) {
           throw new RuntimeException("ERROR IN GET ALL SERVICE");
       }
    }

    //Post Method
    public Maintenance createRecord(Maintenance maintenance) {
        try{
            maintenance.setId(UUID.randomUUID().toString());
            maintenance.setReplacingDate(LocalDate.now());
            return maintenanceRepository.save(maintenance);
        } catch (Exception e) {
            throw new RuntimeException("ERROR IN createRecord SERVICE");
        }
    }

    // Put/Update Method
    public Maintenance updateRecord(Maintenance maintenance, String id) {
        Maintenance existing = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        // Preserving ID
        maintenance.setId(existing.getId());

        // Update only fields you actually want to allow changing
        existing.setSpareName(maintenance.getSpareName());
        existing.setRC(maintenance.getRC());
        existing.setIssue(maintenance.getIssue());
        existing.setBrandModel(maintenance.getBrandModel());
        existing.setPrice(maintenance.getPrice());
        existing.setCurrentKM(maintenance.getCurrentKM());
        existing.setReplacingDate(maintenance.getReplacingDate());
        existing.setUpcomingCheckUpKM(maintenance.getUpcomingCheckUpKM());
        existing.setExtraNotes(maintenance.getExtraNotes());

        return maintenanceRepository.save(existing);
    }


    //Delete Method
    public Maintenance deleteRecord(String id) {
        return maintenanceRepository.findById(id)
                .map(existing -> {
                    maintenanceRepository.deleteById(id);
                    return existing; // return the deleted record details
                })
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
    }

    //Find By Id
    public Maintenance getById(String id) {
        try {
            return maintenanceRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new RuntimeException("Record not found with id: " + id);
        }
    }
}
