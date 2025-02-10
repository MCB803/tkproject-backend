package com.example.tkproject.controller;

import com.example.tkproject.model.Location;
import com.example.tkproject.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return locationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location created = locationService.create(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        Location updated = locationService.update(id, location);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
