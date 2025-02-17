package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RouteService {
    List<List<TransportationResponseDTO>> findRoutesSync(Long originId, Long destinationId, LocalDate tripDate);
    CompletableFuture<List<List<TransportationResponseDTO>>> findRoutes(Long originId, Long destinationId, LocalDate tripDate);
}
