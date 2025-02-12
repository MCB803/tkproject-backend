package com.example.tkproject.service;

import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.exception.TransportationServiceException;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.TransportationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportationServiceImpl implements TransportationService {

    private static final Logger logger = LoggerFactory.getLogger(TransportationServiceImpl.class);
    private final TransportationRepository transportationRepository;

    public TransportationServiceImpl(TransportationRepository transportationRepository) {
        this.transportationRepository = transportationRepository;
    }

    @Cacheable(value = "transportationsCache")
    @Override
    public List<Transportation> findAll() {
        try {
            logger.info("Fetching all transportations from the database.");
            List<Transportation> transports = transportationRepository.findAll();
            logger.debug("Number of transportations retrieved: {}", transports.size());
            return transports;
        } catch (Exception ex) {
            logger.error("Error fetching transportations: {}", ex.getMessage(), ex);
            throw new TransportationServiceException("Error fetching transportations", ex);
        }
    }

    @Cacheable(value = "transportationCache", key = "#id")
    @Override
    public Optional<Transportation> findById(Long id) {
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("User {} fetching transportation with id: {}", currentUser, id);
            Optional<Transportation> transportation = transportationRepository.findById(id);
            if (transportation.isPresent()) {
                logger.debug("Transportation found: {}", transportation.get());
            } else {
                logger.warn("No transportation found with id: {}", id);
            }
            return transportation;
        } catch (Exception ex) {
            logger.error("Error fetching transportation with id {}: {}", id, ex.getMessage(), ex);
            throw new TransportationServiceException("Error fetching transportation with id " + id, ex);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "transportationsCache", allEntries = true)
    })
    @Override
    public Transportation create(Transportation transportation) {
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("User {} is creating new transportation: {}", currentUser, transportation);
            Transportation created = transportationRepository.save(transportation);
            logger.info("Transportation created with id {} by user {}", created.getId(), currentUser);
            return created;
        } catch (Exception ex) {
            logger.error("Error creating transportation: {}", ex.getMessage(), ex);
            throw new TransportationServiceException("Error creating transportation", ex);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "transportationsCache", allEntries = true),
            @CacheEvict(value = "transportationCache", key = "#id")
    })
    @Override
    public Transportation update(Long id, Transportation transportation) {
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("User {} is updating transportation with id: {}", currentUser, id);
            Transportation existing = transportationRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Transportation not found with id: {}", id);
                        return new ResourceNotFoundException("Transportation not found with id: " + id);
                    });
            existing.setType(transportation.getType());
            existing.setOrigin(transportation.getOrigin());
            existing.setDestination(transportation.getDestination());
            existing.setOperatingDays(transportation.getOperatingDays());
            Transportation updated = transportationRepository.save(existing);
            logger.debug("User {} updated Transportation: {}", currentUser, updated);
            return updated;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating transportation with id {}: {}", id, ex.getMessage(), ex);
            throw new TransportationServiceException("Error updating transportation with id " + id, ex);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "transportationsCache", allEntries = true),
            @CacheEvict(value = "transportationCache", key = "#id")
    })
    @Override
    public void delete(Long id) {
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("User {} is deleting transportation with id: {}", currentUser, id);
            transportationRepository.deleteById(id);
            logger.debug("User {} deleted Transportation with id {}.", currentUser, id);
        } catch (Exception ex) {
            logger.error("Error deleting transportation with id {}: {}", id, ex.getMessage(), ex);
            throw new TransportationServiceException("Error deleting transportation with id " + id, ex);
        }
    }
}
