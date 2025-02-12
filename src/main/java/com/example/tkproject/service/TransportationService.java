package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationDTO;
import java.util.List;

public interface TransportationService {
    /**
     * Retrieves all transportations as DTOs.
     * @return list of TransportationDTOs
     */
    List<TransportationDTO> findAll();

    /**
     * Finds a transportation by ID.
     * @param id the transportation ID
     * @return TransportationDTO if found
     */
    TransportationDTO findById(Long id);

    /**
     * Creates a new transportation entry.
     * @param transportationDTO the transportation details
     * @return the created TransportationDTO
     */
    TransportationDTO create(TransportationDTO transportationDTO);

    /**
     * Updates an existing transportation entry.
     * @param id                 the transportation ID
     * @param transportationDTO  the updated transportation details
     * @return the updated TransportationDTO
     */
    TransportationDTO update(Long id, TransportationDTO transportationDTO);

    /**
     * Deletes a transportation by ID.
     * @param id the transportation ID
     */
    void delete(Long id);
}
