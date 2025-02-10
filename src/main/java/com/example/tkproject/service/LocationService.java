package com.example.tkproject.service;

import com.example.tkproject.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<Location> findAll();
    Optional<Location> findById(Long id);
    Location create(Location location);
    Location update(Long id, Location location);
    void delete(Long id);
}
