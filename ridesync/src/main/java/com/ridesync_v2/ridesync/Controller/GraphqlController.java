package com.ridesync_v2.ridesync.Controller;

import com.ridesync_v2.ridesync.Entity.Maintenance;
import com.ridesync_v2.ridesync.Service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@Controller
public class GraphqlController {

    @Autowired
    private MaintenanceService maintenanceService;

    // ✅ Paginated Fetch
    @QueryMapping("getAllPaginated")
    public List<Maintenance> getAllPaginated(@Argument int page, @Argument int size) {
        return maintenanceService.getAll(page, size);
    }

    // ✅ Find by ID
    @QueryMapping("getById")
    public Maintenance getById(@Argument String id) {
        return maintenanceService.getById(id);
    }
}
