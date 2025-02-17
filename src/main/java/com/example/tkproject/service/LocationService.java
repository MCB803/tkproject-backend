package com.example.tkproject.service;

import com.example.tkproject.dto.LocationDTO;
import java.util.List;

public interface LocationService {

    List<LocationDTO> findAll();

    LocationDTO findById(Long id);

    LocationDTO create(LocationDTO locationDTO);

    LocationDTO update(Long id, LocationDTO locationDTO);

    void delete(Long id);
}
