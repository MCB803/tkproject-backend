package com.example.tkproject.service;

import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.model.Location;
import com.example.tkproject.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // Retrieve all locations as DTOs with caching
    @Cacheable(value = "locationsCache")
    @Override
    public List<LocationDTO> findAll() {
        logger.info("Fetching all locations from the database");
        try {
            return locationRepository.findAll()
                    .stream()
                    .map(LocationDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error fetching locations: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error fetching locations", ex);
        }
    }

    // Retrieve a single location by ID as DTO
    @Cacheable(value = "locationsCache", key = "#id")
    @Override
    public LocationDTO findById(Long id) {
        logger.info("Fetching location with id: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return LocationDTO.fromEntity(location);
    }

    // Create a new location
    @CacheEvict(value = "locationsCache", allEntries = true)
    @Override
    public LocationDTO create(LocationDTO locationDTO) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is creating a new location: {}", currentUser, locationDTO);

        // Convert DTO to entity
        Location location = new Location();
        location.setName(locationDTO.getName());
        location.setCountry(locationDTO.getCountry());
        location.setCity(locationDTO.getCity());
        location.setLocationCode(locationDTO.getLocationCode());

        Location savedLocation = locationRepository.save(location);
        return LocationDTO.fromEntity(savedLocation);
    }

    // Update an existing location
    @CacheEvict(value = "locationsCache", allEntries = true)
    @Override
    public LocationDTO update(Long id, LocationDTO locationDTO) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is updating location with id: {}", currentUser, id);

        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));

        // Update fields from DTO
        existing.setName(locationDTO.getName());
        existing.setCountry(locationDTO.getCountry());
        existing.setCity(locationDTO.getCity());
        existing.setLocationCode(locationDTO.getLocationCode());

        Location updatedLocation = locationRepository.save(existing);
        return LocationDTO.fromEntity(updatedLocation);
    }

    // Delete a location
    @CacheEvict(value = "locationsCache", allEntries = true)
    @Override
    public void delete(Long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is deleting location with id: {}", currentUser, id);
        locationRepository.deleteById(id);
    }
}
