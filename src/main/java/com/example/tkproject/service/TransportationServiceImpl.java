package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationRequestDTO;
import com.example.tkproject.dto.TransportationResponseDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.enums.TransportationType;
import com.example.tkproject.repository.TransportationRepository;
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
public class TransportationServiceImpl implements TransportationService {

    private static final Logger logger = LoggerFactory.getLogger(TransportationServiceImpl.class);
    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;

    public TransportationServiceImpl(TransportationRepository transportationRepository, LocationRepository locationRepository) {
        this.transportationRepository = transportationRepository;
        this.locationRepository = locationRepository;
    }

    @Cacheable(value = "transportationsCache")
    @Override
    public List<TransportationResponseDTO> findAll() {
        logger.info("Fetching all transportations from the database.");
        return transportationRepository.findAll()
                .stream()
                .map(TransportationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "transportationCache", key = "#id")
    @Override
    public TransportationResponseDTO findById(Long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} fetching transportation with id: {}", currentUser, id);
        return transportationRepository.findById(id)
                .map(TransportationResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation not found with id: " + id));
    }

    @CacheEvict(value = "transportationsCache", allEntries = true)
    @Override
    public TransportationResponseDTO create(TransportationRequestDTO transportationDTO) {
        if (transportationDTO.getOriginId().equals(transportationDTO.getDestinationId())) {
            throw new RouteServiceException("Origin and destination must be different!");
        }

        Transportation transportation = new Transportation();
        transportation.setType(TransportationType.valueOf(transportationDTO.getType()));
        transportation.setOperatingDays(transportationDTO.getOperatingDays());

        Location origin = locationRepository.findById(transportationDTO.getOriginId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin location not found with id: " + transportationDTO.getOriginId()));

        Location destination = locationRepository.findById(transportationDTO.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found with id: " + transportationDTO.getDestinationId()));

        transportation.setOrigin(origin);
        transportation.setDestination(destination);

        Transportation savedTransportation = transportationRepository.save(transportation);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} created new transportation: {}", currentUser, savedTransportation);

        return TransportationResponseDTO.fromEntity(savedTransportation);
    }

    @Override
    @CacheEvict(value = "transportationsCache", allEntries = true)
    public TransportationResponseDTO update(Long id, TransportationRequestDTO transportationDTO) {
        if (transportationDTO.getOriginId().equals(transportationDTO.getDestinationId())) {
            throw new RouteServiceException("Origin and destination must be different!");
        }

        Transportation existing = transportationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation not found with id: " + id));

        Location origin = locationRepository.findById(transportationDTO.getOriginId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin location not found with id: " + transportationDTO.getOriginId()));

        Location destination = locationRepository.findById(transportationDTO.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found with id: " + transportationDTO.getDestinationId()));

        existing.setType(TransportationType.valueOf(transportationDTO.getType()));
        existing.setOperatingDays(transportationDTO.getOperatingDays());
        existing.setOrigin(origin);
        existing.setDestination(destination);

        Transportation updatedTransportation = transportationRepository.save(existing);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} updated transportation with id: {}", currentUser, id);

        return TransportationResponseDTO.fromEntity(updatedTransportation);
    }

    @Override
    @CacheEvict(value = "transportationsCache", allEntries = true)
    public void delete(Long id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        transportationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation not found with id: " + id));

        transportationRepository.deleteById(id);
        logger.info("User {} deleted transportation with id: {}", currentUser, id);
    }
}
