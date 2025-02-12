package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.ErrorResponse;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.service.LocationService;
import com.example.tkproject.service.RouteService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated // <-- Add this annotation to enable method-level validation
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    private final RouteService routeService;
    private final LocationService locationService;

    public RouteController(RouteService routeService, LocationService locationService) {
        this.routeService = routeService;
        this.locationService = locationService;
    }

    /**
     * GET endpoint to retrieve valid routes.
     * Example request: /api/routes?originCode=XXX&destinationCode=YYY&tripDate=2025-03-12
     */
    @GetMapping
    public ResponseEntity<?> getRoutes(
            @RequestParam @NotBlank(message = "Origin code must not be blank") String originCode,
            @RequestParam @NotBlank(message = "Destination code must not be blank") String destinationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tripDate) {
        try {
            logger.info("Fetching routes from {} to {} for date {}", originCode, destinationCode, tripDate);
            List<List<TransportationDTO>> routes = routeService.findRoutes(originCode, destinationCode, tripDate);
            logger.debug("Found {} routes", routes.size());
            ApiResponse<List<List<TransportationDTO>>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Routes fetched successfully",
                    routes
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching routes: {}", ex.getMessage(), ex);
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching routes",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/locations")
    public ResponseEntity<?> getAllLocations() {
        try {
            logger.info("Fetching all locations for routes");
            List<LocationDTO> locations = locationService.findAll();
            logger.debug("Found {} locations", locations.size());
            ApiResponse<List<LocationDTO>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Locations fetched successfully",
                    locations
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching locations: {}", ex.getMessage(), ex);
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching locations",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
