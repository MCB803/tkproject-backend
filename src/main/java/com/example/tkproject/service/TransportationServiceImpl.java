package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.TransportationRepository;
import com.example.tkproject.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportationServiceImpl implements TransportationService {

    private static final Logger logger = LoggerFactory.getLogger(TransportationServiceImpl.class);
    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;

    public TransportationServiceImpl(TransportationRepository transportationRepository, LocationRepository locationRepository) {
        this.transportationRepository = transportationRepository;
        this.locationRepository = locationRepository;
    }

    // Fetch all transportations as DTOs
    @Cacheable(value = "transportationsCache")
    @Override
    public List<TransportationDTO> findAll() {
        logger.info("Fetching all transportations from the database.");
        return transportationRepository.findAll()
                .stream()
                .map(TransportationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Fetch transportation by ID
    @Cacheable(value = "transportationCache", key = "#id")
    @Override
    public TransportationDTO findById(Long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} fetching transportation with id: {}", currentUser, id);
        return transportationRepository.findById(id)
                .map(TransportationDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation not found with id: " + id));
    }

    // Create transportation (Convert DTO to Entity)
    @CacheEvict(value = "transportationsCache", allEntries = true)
    @Override
    public TransportationDTO create(TransportationDTO transportationDTO) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is creating new transportation: {}", currentUser, transportationDTO);

        // Convert DTO to Entity
        Transportation transportation = new Transportation();
        transportation.setType(TransportationType.valueOf(transportationDTO.getType()));
        transportation.setOperatingDays(transportationDTO.getOperatingDays());

        // Fetch origin & destination from DB
        Location origin = locationRepository.findById(transportationDTO.getOrigin().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin location not found with id: " + transportationDTO.getOrigin()));

        Location destination = locationRepository.findById(transportationDTO.getDestination().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found with id: " + transportationDTO.getDestination()));

        transportation.setOrigin(origin);
        transportation.setDestination(destination);

        Transportation savedTransportation = transportationRepository.save(transportation);
        return TransportationDTO.fromEntity(savedTransportation);
    }

    // Update transportation (Convert DTO to Entity)
    @Caching(evict = {
            @CacheEvict(value = "transportationsCache", allEntries = true),
            @CacheEvict(value = "transportationCache", key = "#id")
    })
    @Override
    public TransportationDTO update(Long id, TransportationDTO transportationDTO) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is updating transportation with id: {}", currentUser, id);

        Transportation existing = transportationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation not found with id: " + id));

        // Fetch origin & destination from DB
        Location origin = locationRepository.findById(transportationDTO.getOrigin().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin location not found with id: " + transportationDTO.getOrigin().getId()));

        Location destination = locationRepository.findById(transportationDTO.getDestination().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found with id: " + transportationDTO.getDestination().getId()));

        // Update fields from DTO
        existing.setType(TransportationType.valueOf(transportationDTO.getType()));
        existing.setOperatingDays(transportationDTO.getOperatingDays());
        existing.setOrigin(origin);
        existing.setDestination(destination);

        Transportation updatedTransportation = transportationRepository.save(existing);
        return TransportationDTO.fromEntity(updatedTransportation);
    }

    // Delete transportation
    @Caching(evict = {
            @CacheEvict(value = "transportationsCache", allEntries = true),
            @CacheEvict(value = "transportationCache", key = "#id")
    })
    @Override
    public void delete(Long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is deleting transportation with id: {}", currentUser, id);
        transportationRepository.deleteById(id);
    }
}
