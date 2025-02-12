package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.exception.ErrorResponse;
import com.example.tkproject.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Validated
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAllLocations() {
        logger.info("Fetching all locations");
        List<LocationDTO> locations = locationService.findAll();
        logger.debug("Found {} locations", locations.size());

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Locations fetched successfully",
                locations
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> getLocationById(@PathVariable Long id) {
        logger.info("Fetching location with id {}", id);
        LocationDTO locationDTO = locationService.findById(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Location fetched successfully",
                locationDTO
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LocationDTO>> createLocation(@Valid @RequestBody LocationDTO locationDTO) {
        logger.info("Creating new location: {}", locationDTO);
        LocationDTO createdLocation = locationService.create(locationDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Location created successfully",
                createdLocation
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationDTO locationDTO) {
        logger.info("Updating location with id {}", id);
        LocationDTO updatedLocation = locationService.update(id, locationDTO);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Location updated successfully",
                updatedLocation
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        logger.info("Deleting location with id {}", id);
        locationService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(),
                "Location deleted successfully",
                null
        ));
    }
}
