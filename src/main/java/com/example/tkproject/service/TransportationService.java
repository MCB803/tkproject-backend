package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationRequestDTO;
import com.example.tkproject.dto.TransportationResponseDTO;

import java.util.List;

public interface TransportationService {
    List<TransportationResponseDTO> findAll();

    TransportationResponseDTO findById(Long id);

    TransportationResponseDTO create(TransportationRequestDTO transportationDTO);

    TransportationResponseDTO update(Long id, TransportationRequestDTO transportationDTO);

    void delete(Long id);
}
