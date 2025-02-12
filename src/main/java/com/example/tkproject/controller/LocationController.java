package com.example.tkproject.controller;

import com.example.tkproject.exception.ErrorResponse;
import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.model.Location;
import com.example.tkproject.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        try {
            logger.info("Fetching all locations");
            List<Location> locations = locationService.findAll();
            logger.debug("Found {} locations", locations.size());
            return ResponseEntity.ok(locations);
        } catch (Exception ex) {
            logger.error("Error fetching locations: {}", ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching locations",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        try {
            logger.info("Fetching location with id {}", id);
            Optional<Location> locationOpt = locationService.findById(id);
            if (locationOpt.isPresent()) {
                logger.debug("Location found: {}", locationOpt.get());
                return ResponseEntity.ok(locationOpt.get());
            } else {
                logger.warn("Location with id {} not found", id);
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Location not found",
                        "No location found with id " + id
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception ex) {
            logger.error("Error fetching location with id {}: {}", id, ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching location",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> createLocation(@Valid @RequestBody LocationDTO locationDTO) {
        try {
            logger.info("Creating new location with data: {}", locationDTO);
            // Convert DTO to entity
            Location location = new Location(
                    locationDTO.getName(),
                    locationDTO.getCountry(),
                    locationDTO.getCity(),
                    locationDTO.getLocationCode()
            );
            Location created = locationService.create(location);
            logger.info("Location created with id {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            logger.error("Error creating location: {}", ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error creating location",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationDTO locationDTO) {
        try {
            logger.info("Updating location with id {} using data: {}", id, locationDTO);
            // Optionally convert DTO to entity
            Location location = new Location(
                    locationDTO.getName(),
                    locationDTO.getCountry(),
                    locationDTO.getCity(),
                    locationDTO.getLocationCode()
            );
            Location updated = locationService.update(id, location);
            logger.info("Location updated with id {}", updated.getId());
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating location with id {}: {}", id, ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error updating location",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        try {
            logger.info("Deleting location with id {}", id);
            locationService.delete(id);
            logger.info("Deleted location with id {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting location with id {}: {}", id, ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error deleting location",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
