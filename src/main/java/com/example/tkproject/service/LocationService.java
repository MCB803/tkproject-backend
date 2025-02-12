package com.example.tkproject.service;

import com.example.tkproject.dto.LocationDTO;
import java.util.List;

public interface LocationService {
    /**
     * Retrieves all locations as DTOs.
     * @return list of LocationDTOs
     */
    List<LocationDTO> findAll();

    /**
     * Finds a location by ID.
     * @param id the location ID
     * @return LocationDTO if found
     */
    LocationDTO findById(Long id);

    /**
     * Creates a new location.
     * @param locationDTO the location details
     * @return the created LocationDTO
     */
    LocationDTO create(LocationDTO locationDTO);

    /**
     * Updates an existing location.
     * @param id          the location ID
     * @param locationDTO the updated location details
     * @return the updated LocationDTO
     */
    LocationDTO update(Long id, LocationDTO locationDTO);

    /**
     * Deletes a location by ID.
     * @param id the location ID
     */
    void delete(Long id);
}
