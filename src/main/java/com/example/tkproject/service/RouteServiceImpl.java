package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;

    public RouteServiceImpl(LocationRepository locationRepository, TransportationRepository transportationRepository) {
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
    }

    /**
     * Finds all valid routes from origin to destination on the given trip date.
     * The result is cached using a key composed of originCode, destinationCode, and tripDate.
     */
    @Override
    @Cacheable(value = "routesCache", key = "#originCode + '_' + #destinationCode + '_' + #tripDate")
    public List<List<TransportationDTO>> findRoutes(String originCode, String destinationCode, LocalDate tripDate) {
        try {
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("User {} is searching routes from {} to {} on {}", currentUser, originCode, destinationCode, tripDate);

            int dayOfWeek = tripDate.getDayOfWeek().getValue();

            // Fetch locations and validate
            Location origin = locationRepository.findByLocationCode(originCode)
                    .orElseThrow(() -> new RouteServiceException("Origin not found: " + originCode));

            Location destination = locationRepository.findByLocationCode(destinationCode)
                    .orElseThrow(() -> new RouteServiceException("Destination not found: " + destinationCode));

            // Fetch all transportation options
            List<Transportation> allTransports = transportationRepository.findAll();
            logger.debug("Total transportation records found: {}", allTransports.size());

            // Ensure lazy fields are loaded
            allTransports.forEach(t -> Hibernate.initialize(t.getOperatingDays()));

            // Define filter predicate for valid operating days
            Predicate<Transportation> availableOnDay = t -> t.getOperatingDays().contains(dayOfWeek);

            List<List<Transportation>> validRoutes = new ArrayList<>();

            // 🚀 **Optimized Route Search**
            for (Transportation t1 : allTransports) {
                if (!availableOnDay.test(t1) || !t1.getOrigin().equals(origin)) continue;

                if (t1.getDestination().equals(destination)) {
                    // Case 1: Direct Flight
                    if (t1.getType() == TransportationType.FLIGHT) {
                        logger.debug("Direct flight found: {}", t1);
                        validRoutes.add(Collections.singletonList(t1));
                    }
                } else {
                    for (Transportation t2 : allTransports) {
                        if (!availableOnDay.test(t2) || !t2.getOrigin().equals(t1.getDestination())) continue;

                        if (t2.getDestination().equals(destination)) {
                            // Case 2: Pre-flight Transfer + Flight
                            if (t1.getType() != TransportationType.FLIGHT && t2.getType() == TransportationType.FLIGHT) {
                                logger.debug("Pre-flight transfer + Flight route found: {} then {}", t1, t2);
                                validRoutes.add(List.of(t1, t2));
                            }
                            // Case 3: Flight + Post-flight Transfer
                            else if (t1.getType() == TransportationType.FLIGHT && t2.getType() != TransportationType.FLIGHT) {
                                logger.debug("Flight + Post-flight transfer route found: {} then {}", t1, t2);
                                validRoutes.add(List.of(t1, t2));
                            }
                        } else {
                            for (Transportation t3 : allTransports) {
                                if (!availableOnDay.test(t3) || !t3.getOrigin().equals(t2.getDestination()) || !t3.getDestination().equals(destination)) continue;

                                // Case 4: Pre-flight + Flight + Post-flight Transfer
                                if (t1.getType() != TransportationType.FLIGHT && t2.getType() == TransportationType.FLIGHT && t3.getType() != TransportationType.FLIGHT) {
                                    logger.debug("Pre-flight + Flight + Post-flight transfer route found: {} then {} then {}", t1, t2, t3);
                                    validRoutes.add(List.of(t1, t2, t3));
                                }
                            }
                        }
                    }
                }
            }

            // Enforce rule: Each route must contain exactly **one** flight
            validRoutes.removeIf(route -> route.stream().filter(t -> t.getType() == TransportationType.FLIGHT).count() != 1);
            logger.info("Total valid routes found: {}", validRoutes.size());

            // Convert to DTOs
            return validRoutes.stream()
                    .map(route -> route.stream().map(TransportationDTO::fromEntity).collect(Collectors.toList()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error finding routes: {}", ex.getMessage(), ex);
            throw new RouteServiceException("Error finding routes", ex);
        }
    }
}
