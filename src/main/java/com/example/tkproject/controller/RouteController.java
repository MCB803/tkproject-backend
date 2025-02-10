package com.example.tkproject.controller;

import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.service.LocationService;
import com.example.tkproject.service.RouteService;
import jodd.io.findfile.FindFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")  // This line enables CORS for requests from localhost:3000
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private LocationService locationService;

    /**
     * GET endpoint to retrieve valid routes.
     * Example request: /api/routes?originCode=ORIG&destinationCode=DEST&tripDate=2025-03-12
     */
    @GetMapping
    public List<List<Transportation>> getRoutes(
            @RequestParam String originCode,
            @RequestParam String destinationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tripDate) {

        return routeService.findRoutes(originCode, destinationCode, tripDate);
    }

    @GetMapping("/locations")
    public List<Location> getAllLocations() {
        return locationService.findAll();
    }
}