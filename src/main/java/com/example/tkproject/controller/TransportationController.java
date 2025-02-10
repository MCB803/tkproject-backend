package com.example.tkproject.controller;

import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.mapper.TransportationMapper;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.service.TransportationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transportations")
public class TransportationController {

    private final TransportationService transportationService;
    private final TransportationMapper transportationMapper;

    public TransportationController(TransportationService transportationService,
                                    TransportationMapper transportationMapper) {
        this.transportationService = transportationService;
        this.transportationMapper = transportationMapper;
    }

    @GetMapping
    public List<Transportation> getAllTransportations() {
        return  transportationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportation> getTransportationById(@PathVariable Long id) {
        return transportationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransportationDTO> createTransportation(@RequestBody TransportationDTO dto) {
        Transportation transportation = transportationMapper.toEntity(dto);
        Transportation created = transportationService.create(transportation);
        return ResponseEntity.status(HttpStatus.CREATED).body(transportationMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportationDTO> updateTransportation(@PathVariable Long id, @RequestBody TransportationDTO dto) {
        Transportation transportation = transportationMapper.toEntity(dto);
        Transportation updated = transportationService.update(id, transportation);
        return ResponseEntity.ok(transportationMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransportation(@PathVariable Long id) {
        transportationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
