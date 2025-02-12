package com.example.tkproject.service;

import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
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
    public List<List<Transportation>> findRoutes(String originCode, String destinationCode, LocalDate tripDate) {
        try {
            // Log the start of the route search, including the current user
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("User {} is searching routes from {} to {} on {}", currentUser, originCode, destinationCode, tripDate);
            int dayOfWeek = tripDate.getDayOfWeek().getValue(); // Monday = 1, ..., Sunday = 7

            // Lookup origin and destination locations; if not found, log and throw an exception.
            Location origin = locationRepository.findByLocationCode(originCode)
                    .orElseThrow(() -> {
                        String msg = "Origin not found: " + originCode;
                        logger.error(msg);
                        return new RouteServiceException(msg);
                    });

            Location destination = locationRepository.findByLocationCode(destinationCode)
                    .orElseThrow(() -> {
                        String msg = "Destination not found: " + destinationCode;
                        logger.error(msg);
                        return new RouteServiceException(msg);
                    });

            // Retrieve all transportation records
            List<Transportation> allTransports = transportationRepository.findAll();
            logger.debug("Total transportation records found: {}", allTransports.size());

            List<List<Transportation>> validRoutes = new ArrayList<>();
            Predicate<Transportation> availableOnDay = t -> t.getOperatingDays().contains(dayOfWeek);

            // 1. Direct Flight: [FLIGHT]
            for (Transportation t : allTransports) {
                if (availableOnDay.test(t)
                        && t.getOrigin().equals(origin)
                        && t.getDestination().equals(destination)
                        && t.getType() == TransportationType.FLIGHT) {
                    logger.debug("Direct flight found: {}", t);
                    validRoutes.add(Collections.singletonList(t));
                }
            }

            // 2. Pre-flight transfer + Flight: [non-FLIGHT, FLIGHT]
            for (Transportation t1 : allTransports) {
                if (availableOnDay.test(t1)
                        && t1.getOrigin().equals(origin)
                        && t1.getType() != TransportationType.FLIGHT) {

                    Location intermediate = t1.getDestination();
                    if (intermediate.equals(destination)) continue;

                    for (Transportation t2 : allTransports) {
                        if (availableOnDay.test(t2)
                                && t2.getOrigin().equals(intermediate)
                                && t2.getDestination().equals(destination)
                                && t2.getType() == TransportationType.FLIGHT) {
                            logger.debug("Pre-flight transfer + Flight route found: {} then {}", t1, t2);
                            List<Transportation> route = new ArrayList<>();
                            route.add(t1);
                            route.add(t2);
                            validRoutes.add(route);
                        }
                    }
                }
            }

            // 3. Flight + Post-flight transfer: [FLIGHT, non-FLIGHT]
            for (Transportation t1 : allTransports) {
                if (availableOnDay.test(t1)
                        && t1.getOrigin().equals(origin)
                        && t1.getType() == TransportationType.FLIGHT) {

                    Location intermediate = t1.getDestination();
                    if (intermediate.equals(destination)) continue;

                    for (Transportation t2 : allTransports) {
                        if (availableOnDay.test(t2)
                                && t2.getOrigin().equals(intermediate)
                                && t2.getDestination().equals(destination)
                                && t2.getType() != TransportationType.FLIGHT) {
                            logger.debug("Flight + Post-flight transfer route found: {} then {}", t1, t2);
                            List<Transportation> route = new ArrayList<>();
                            route.add(t1);
                            route.add(t2);
                            validRoutes.add(route);
                        }
                    }
                }
            }

            // 4. Pre-flight + Flight + Post-flight transfer: [non-FLIGHT, FLIGHT, non-FLIGHT]
            for (Transportation t1 : allTransports) {
                if (availableOnDay.test(t1)
                        && t1.getOrigin().equals(origin)
                        && t1.getType() != TransportationType.FLIGHT) {

                    Location intermediate1 = t1.getDestination();
                    if (intermediate1.equals(destination)) continue;

                    for (Transportation t2 : allTransports) {
                        if (availableOnDay.test(t2)
                                && t2.getOrigin().equals(intermediate1)
                                && t2.getType() == TransportationType.FLIGHT) {

                            Location intermediate2 = t2.getDestination();
                            if (intermediate2.equals(destination)) continue;

                            for (Transportation t3 : allTransports) {
                                if (availableOnDay.test(t3)
                                        && t3.getOrigin().equals(intermediate2)
                                        && t3.getDestination().equals(destination)
                                        && t3.getType() != TransportationType.FLIGHT) {
                                    logger.debug("Pre-flight + Flight + Post-flight route found: {} then {} then {}", t1, t2, t3);
                                    List<Transportation> route = new ArrayList<>();
                                    route.add(t1);
                                    route.add(t2);
                                    route.add(t3);
                                    validRoutes.add(route);
                                }
                            }
                        }
                    }
                }
            }

            // Enforce the rule: each valid route must contain exactly one flight.
            validRoutes.removeIf(route -> route.stream().filter(t -> t.getType() == TransportationType.FLIGHT).count() != 1);
            logger.info("Total valid routes found: {}", validRoutes.size());
            return validRoutes;
        } catch (Exception ex) {
            logger.error("Error finding routes: {}", ex.getMessage(), ex);
            throw new RouteServiceException("Error finding routes", ex);
        }
    }
}
