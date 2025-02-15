package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.dto.TransportationResponseDTO;
import com.example.tkproject.service.LocationService;
import com.example.tkproject.service.RouteService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Validated
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
     * Returns a CompletableFuture for non-blocking behavior.
     */
    @GetMapping
    public CompletableFuture<ResponseEntity<ApiResponse<List<List<TransportationResponseDTO>>>>> getRoutes(
            @RequestParam @NotNull(message = "Origin id must not be blank") Long originId,
            @RequestParam @NotNull(message = "Destination id must not be blank") Long destinationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tripDate) {

        logger.info("Fetching routes from {} to {} for date {}", originId, destinationId, tripDate);
        return routeService.findRoutes(originId, destinationId, tripDate)
                .thenApply(routes -> {
                    logger.debug("Found {} routes", routes.size());
                    ApiResponse<List<List<TransportationResponseDTO>>> response =
                            new ApiResponse<>(HttpStatus.OK.value(), "Routes fetched successfully", routes);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    logger.error("Error fetching routes: {}", ex.getMessage(), ex);
                    ApiResponse<List<List<TransportationResponseDTO>>> errorResponse =
                            new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    "Error fetching routes: " + ex.getMessage(), null);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAllLocations() {
        try {
            logger.info("Fetching all locations for routes");
            List<LocationDTO> locations = locationService.findAll();
            logger.debug("Found {} locations", locations.size());
            ApiResponse<List<LocationDTO>> response =
                    new ApiResponse<>(HttpStatus.OK.value(), "Locations fetched successfully", locations);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching locations: {}", ex.getMessage(), ex);
            ApiResponse<List<LocationDTO>> errorResponse =
                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error fetching locations: " + ex.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
