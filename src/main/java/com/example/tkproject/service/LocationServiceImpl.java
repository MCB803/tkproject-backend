package com.example.tkproject.service;

import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.model.Location;
import com.example.tkproject.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // Retrieve all locations with caching
    @Cacheable(value = "locationsCache")
    @Override
    public List<Location> findAll() {
        logger.info("Fetching all locations from the database");
        try {
            return locationRepository.findAll();
        } catch (Exception ex) {
            logger.error("Error fetching all locations: {}", ex.getMessage(), ex);
            throw ex; // Optionally, wrap in a custom exception if needed
        }
    }

    // Retrieve one location by id, caching the result
    @Cacheable(value = "locationsCache", key = "#id")
    @Override
    public Optional<Location> findById(Long id) {
        logger.info("Fetching location with id: {}", id);
        try {
            return locationRepository.findById(id);
        } catch (Exception ex) {
            logger.error("Error fetching location with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    // Create a new location. Evict the cache to ensure fresh data.
    @CacheEvict(value = "locationsCache", allEntries = true)
    @Override
    public Location create(Location location) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is creating a new location: {}", currentUser, location);
        try {
            return locationRepository.save(location);
        } catch (Exception ex) {
            logger.error("Error creating location {}: {}", location, ex.getMessage(), ex);
            throw ex;
        }
    }

    // Update a location. Use a custom exception if the location is not found.
    @CacheEvict(value = "locationsCache", allEntries = true)
    @Override
    public Location update(Long id, Location location) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is updating location with id: {}", currentUser, id);
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = "Location not found with id: " + id;
                    logger.error(msg);
                    return new ResourceNotFoundException(msg);
                });
        try {
            existing.setName(location.getName());
            existing.setCountry(location.getCountry());
            existing.setCity(location.getCity());
            existing.setLocationCode(location.getLocationCode());
            Location updated = locationRepository.save(existing);
            logger.info("Location updated with id: {}", updated.getId());
            return updated;
        } catch (Exception ex) {
            logger.error("Error updating location with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    // Delete a location, evicting the cache afterwards.
    @CacheEvict(value = "locationsCache", allEntries = true)
    @Override
    public void delete(Long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is deleting location with id: {}", currentUser, id);
        try {
            locationRepository.deleteById(id);
            logger.info("Location deleted with id: {}", id);
        } catch (Exception ex) {
            logger.error("Error deleting location with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }
}
