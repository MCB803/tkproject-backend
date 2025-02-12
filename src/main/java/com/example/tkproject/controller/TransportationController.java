package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.TransportationDTO;
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
    public ResponseEntity<ApiResponse<List<TransportationDTO>>> getAllTransportations() {
        logger.info("Fetching all transportations");
        List<TransportationDTO> transportations = transportationService.findAll();
        logger.debug("Found {} transportations", transportations.size());

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportations fetched successfully",
                transportations
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationDTO>> getTransportationById(@PathVariable Long id) {
        logger.info("Fetching transportation with id {}", id);
        TransportationDTO transportation = transportationService.findById(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportation fetched successfully",
                transportation
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransportationDTO>> createTransportation(@RequestBody @Valid TransportationDTO transportationDTO) {
        logger.info("Creating new transportation: {}", transportationDTO);
        TransportationDTO createdTransportation = transportationService.create(transportationDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Transportation created successfully",
                createdTransportation
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationDTO>> updateTransportation(
            @PathVariable Long id,
            @RequestBody @Valid TransportationDTO transportationDTO) {
        logger.info("Updating transportation with id {}", id);
        TransportationDTO updatedTransportation = transportationService.update(id, transportationDTO);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportation updated successfully",
                updatedTransportation
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransportation(@PathVariable Long id) {
        logger.info("Deleting transportation with id {}", id);
        transportationService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(),
                "Transportation deleted successfully",
                null
        ));
    }
}
