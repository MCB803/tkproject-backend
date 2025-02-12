package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.mapper.TransportationMapper;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.service.TransportationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transportations")
public class TransportationController {

    private static final Logger logger = LoggerFactory.getLogger(TransportationController.class);

    private final TransportationService transportationService;
    private final TransportationMapper transportationMapper;

    public TransportationController(TransportationService transportationService,
                                    TransportationMapper transportationMapper) {
        this.transportationService = transportationService;
        this.transportationMapper = transportationMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransportationDTO>>> getAllTransportations() {
        logger.info("Fetching all transportations");
        List<TransportationDTO> dtos = transportationService.findAll().stream()
                .map(transportationMapper::toDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} transportations", dtos.size());
        ApiResponse<List<TransportationDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportations fetched successfully",
                dtos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationDTO>> getTransportationById(@PathVariable Long id) {
        logger.info("Fetching transportation with id {}", id);
        Transportation transportation = transportationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation not found with id: " + id));
        TransportationDTO dto = transportationMapper.toDTO(transportation);
        logger.debug("Transportation found: {}", dto);
        ApiResponse<TransportationDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportation fetched successfully",
                dto
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransportationDTO>> createTransportation(@RequestBody @Valid TransportationDTO dto) {
        logger.info("Creating new transportation with data: {}", dto);
        Transportation transportation = transportationMapper.toEntity(dto);
        Transportation created = transportationService.create(transportation);
        TransportationDTO createdDto = transportationMapper.toDTO(created);
        logger.info("Transportation created with id {}", createdDto.getId());
        ApiResponse<TransportationDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Transportation created successfully",
                createdDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportationDTO>> updateTransportation(
            @PathVariable Long id,
            @RequestBody @Valid TransportationDTO dto) {
        logger.info("Updating transportation with id {} using data: {}", id, dto);
        Transportation transportation = transportationMapper.toEntity(dto);
        Transportation updated = transportationService.update(id, transportation);
        TransportationDTO updatedDto = transportationMapper.toDTO(updated);
        logger.info("Transportation updated with id {}", updatedDto.getId());
        ApiResponse<TransportationDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transportation updated successfully",
                updatedDto
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransportation(@PathVariable Long id) {
        logger.info("Deleting transportation with id {}", id);
        transportationService.delete(id);
        logger.info("Transportation deleted with id {}", id);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(),
                "Transportation deleted successfully",
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
