package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationRequestDTO;
import com.example.tkproject.dto.TransportationResponseDTO;

import java.util.List;

public interface TransportationService {
    /**
     * Retrieves all transportations as DTOs.
     * @return list of TransportationDTOs
     */
    List<TransportationResponseDTO> findAll();

    /**
     * Finds a transportation by ID.
     * @param id the transportation ID
     * @return TransportationDTO if found
     */
    TransportationResponseDTO findById(Long id);

    /**
     * Creates a new transportation entry.
     * @param transportationDTO the transportation details
     * @return the created TransportationDTO
     */
    TransportationResponseDTO create(TransportationRequestDTO transportationDTO);

    /**
     * Updates an existing transportation entry.
     * @param id                 the transportation ID
     * @param transportationDTO  the updated transportation details
     * @return the updated TransportationDTO
     */
    TransportationResponseDTO update(Long id, TransportationRequestDTO transportationDTO);

    /**
     * Deletes a transportation by ID.
     * @param id the transportation ID
     */
    void delete(Long id);
}
