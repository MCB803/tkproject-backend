package com.example.tkproject.service;

import com.example.tkproject.model.Location;
import com.example.tkproject.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

    @Override
    public Location create(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public Location update(Long id, Location location) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        existing.setName(location.getName());
        existing.setCountry(location.getCountry());
        existing.setCity(location.getCity());
        existing.setLocationCode(location.getLocationCode());
        return locationRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        locationRepository.deleteById(id);
    }
}
