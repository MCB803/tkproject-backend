package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationRequestDTO;
import com.example.tkproject.dto.TransportationResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface RouteService {

    List<List<TransportationResponseDTO>> findRoutes(Long originId, Long destinationId, LocalDate tripDate);
}
