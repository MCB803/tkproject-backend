package com.example.tkproject.controller;

import com.example.tkproject.dto.*;
import com.example.tkproject.service.TransportationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportations")
public class TransportationController {

    private static final Logger logger = LoggerFactory.getLogger(TransportationController.class);
    private final TransportationService transportationService;

    public TransportationController(TransportationService transportationService) {
        this.transportationService = transportationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransportationResponseDTO>>> getAllTransportations() {
        logger.info("Fetching all transportations from the database.");
        List<TransportationResponseDTO> transportations = transportationService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportations fetched successfully",
                transportations
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationResponseDTO>> getTransportationById(@PathVariable Long id) {
        logger.info("Fetching transportation with ID: {}", id);
        TransportationResponseDTO transportation = transportationService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportation fetched successfully",
                transportation
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransportationResponseDTO>> createTransportation(
            @Valid @RequestBody TransportationRequestDTO transportationDTO) {
        logger.info("Creating new transportation: {}", transportationDTO);
        TransportationResponseDTO createdTransportation = transportationService.create(transportationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Transportation created successfully",
                createdTransportation
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationResponseDTO>> updateTransportation(
            @PathVariable Long id, @Valid @RequestBody TransportationRequestDTO transportationDTO) {
        logger.info("Updating transportation with ID: {}", id);
        TransportationResponseDTO updatedTransportation = transportationService.update(id, transportationDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportation updated successfully",
                updatedTransportation
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransportation(@PathVariable Long id) {
        logger.info("Deleting transportation with ID: {}", id);
        transportationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
