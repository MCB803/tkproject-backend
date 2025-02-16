package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationResponseDTO;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.enums.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;
    private final TransactionTemplate transactionTemplate;

    public RouteServiceImpl(LocationRepository locationRepository,
                            TransportationRepository transportationRepository,
                            PlatformTransactionManager transactionManager) {
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // State for the Dijkstra-style search.
    private static class RouteState implements Comparable<RouteState> {
        final Long locationId;
        final double distance; // cumulative distance
        final List<Transportation> route;
        final int flightCount;

        RouteState(Long locationId, double distance, List<Transportation> route, int flightCount) {
            this.locationId = locationId;
            this.distance = distance;
            this.route = route;
            this.flightCount = flightCount;
        }

        @Override
        public int compareTo(RouteState other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * Caches the list of available transports for the given dayOfWeek.
     */
    @Cacheable(value = "availableTransportsCache", key = "#dayOfWeek")
    public List<Transportation> getAvailableTransports(int dayOfWeek) {
        logger.debug("Fetching available transports for day {}", dayOfWeek);
        return transportationRepository.findByOperatingDaysContaining(dayOfWeek);
    }

    /**
     * Caches the adjacency list for the given dayOfWeek.
     */
    @Cacheable(value = "adjacencyListCache", key = "#dayOfWeek")
    public Map<Long, List<Transportation>> getAdjacencyList(int dayOfWeek) {
        List<Transportation> availableTransports = getAvailableTransports(dayOfWeek);
        logger.debug("Building adjacency list from {} transports", availableTransports.size());
        return availableTransports.stream()
                .collect(Collectors.groupingBy(t -> t.getOrigin().getId()));
    }

    /**
     * Caches the location map for the given dayOfWeek.
     */
    @Cacheable(value = "locationMapCache", key = "#dayOfWeek")
    public Map<Long, Location> getLocationMap(int dayOfWeek) {
        List<Transportation> availableTransports = getAvailableTransports(dayOfWeek);
        Map<Long, Location> locationMap = new HashMap<>();
        availableTransports.forEach(t -> {
            locationMap.put(t.getOrigin().getId(), t.getOrigin());
            locationMap.put(t.getDestination().getId(), t.getDestination());
        });
        return locationMap;
    }

    /**
     * Synchronous method that performs route finding and converts the results to DTOs.
     * All processing—including lazy initialization and DTO conversion—is performed inside
     * a programmatic transaction to guarantee that the Hibernate session remains active.
     */
    @Override
    @Cacheable(value = "routesCache", key = "#originId + '_' + #destinationId + '_' + #tripDate")
    public List<List<TransportationResponseDTO>> findRoutesSync(Long originId, Long destinationId, LocalDate tripDate) {
        return transactionTemplate.execute(status -> {
            try {
                String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
                logger.info("User {} is searching routes from {} to {} on {}", currentUser, originId, destinationId, tripDate);

                if (originId.equals(destinationId)) {
                    throw new RouteServiceException("Origin and destination must be different!");
                }

                int dayOfWeek = tripDate.getDayOfWeek().getValue();

                // Fetch and validate origin and destination locations.
                Location origin = locationRepository.findById(originId)
                        .orElseThrow(() -> new RouteServiceException("Origin not found with ID: " + originId));
                Location destination = locationRepository.findById(destinationId)
                        .orElseThrow(() -> new RouteServiceException("Destination not found with ID: " + destinationId));

                // Use cached structures.
                List<Transportation> availableTransports = getAvailableTransports(dayOfWeek);
                logger.debug("Total transportation records found for day {}: {}", dayOfWeek, availableTransports.size());

                Map<Long, List<Transportation>> adjacencyList = getAdjacencyList(dayOfWeek);
                Map<Long, Location> locationMap = getLocationMap(dayOfWeek);
                // Ensure origin and destination are present.
                locationMap.putIfAbsent(origin.getId(), origin);
                locationMap.putIfAbsent(destination.getId(), destination);

                // Dijkstra's algorithm–style search initialization.
                PriorityQueue<RouteState> pq = new PriorityQueue<>();
                List<List<Transportation>> validRoutes = new ArrayList<>();
                pq.offer(new RouteState(originId, 0.0, new ArrayList<>(), 0));

                // Explore possible routes (allowing up to 3 segments).
                while (!pq.isEmpty()) {
                    RouteState current = pq.poll();

                    // If destination reached, check route validity.
                    if (current.locationId.equals(destinationId)) {
                        if (isValidRoute(current.route)) {
                            validRoutes.add(new ArrayList<>(current.route));
                        }
                        continue;
                    }

                    // Limit routes to at most 3 segments.
                    if (current.route.size() >= 3) {
                        continue;
                    }

                    List<Transportation> nextTransports = adjacencyList.getOrDefault(current.locationId, Collections.emptyList());
                    for (Transportation t : nextTransports) {
                        // Do not exceed one flight in a route.
                        int newFlightCount = current.flightCount + (t.getType() == TransportationType.FLIGHT ? 1 : 0);
                        if (newFlightCount > 1) {
                            continue;
                        }
                        double segmentDistance = calculateDistance(t.getOrigin(), t.getDestination());
                        double newDistance = current.distance + segmentDistance;
                        List<Transportation> newRoute = new ArrayList<>(current.route);
                        newRoute.add(t);
                        pq.offer(new RouteState(t.getDestination().getId(), newDistance, newRoute, newFlightCount));
                    }
                }

                // Sort routes by total distance.
                validRoutes.sort((r1, r2) -> {
                    double d1 = calculateTotalDistance(r1, locationMap);
                    double d2 = calculateTotalDistance(r2, locationMap);
                    return Double.compare(d1, d2);
                });
                logger.info("Total valid routes found: {}", validRoutes.size());

                // Force initialization of lazy collections while the transaction is active.
                validRoutes.forEach(route -> route.forEach(t -> {
                    if (t.getOperatingDays() != null) {
                        Hibernate.initialize(t.getOperatingDays());
                    }
                }));

                // *** Perform DTO conversion inside the transaction ***
                // Optionally, you could use parallel streams here if DTO conversion becomes CPU-bound.
                List<List<TransportationResponseDTO>> dtoRoutes = validRoutes.stream()
                        .map(route -> route.stream()
                                .map(TransportationResponseDTO::fromEntity)
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());

                return dtoRoutes;
            } catch (Exception ex) {
                logger.error("Error finding routes: {}", ex.getMessage(), ex);

                if (ex instanceof RouteServiceException) {
                    throw (RouteServiceException) ex;
                }
                throw new RouteServiceException("Error finding routes", ex);
            }
        });
    }

    /**
     * Asynchronous wrapper for route-finding.
     * Since findRoutesSync fully handles DTO conversion inside a transaction,
     * this method simply delegates to it.
     */
    @Override
    @Async("asyncExecutor")
    public CompletableFuture<List<List<TransportationResponseDTO>>> findRoutes(Long originId, Long destinationId, LocalDate tripDate) {
        logger.debug("Executing asynchronous findRoutes on thread: {}", Thread.currentThread().getName());
        List<List<TransportationResponseDTO>> result = findRoutesSync(originId, destinationId, tripDate);
        return CompletableFuture.completedFuture(result);
    }

    private boolean isValidRoute(List<Transportation> route) {
        if (route.isEmpty()) {
            return false;
        }
        long flightCount = route.stream().filter(t -> t.getType() == TransportationType.FLIGHT).count();
        int size = route.size();
        if (size == 1) {
            return flightCount == 1;
        } else if (size == 2) {
            return flightCount == 1;
        } else if (size == 3) {
            return flightCount == 1 &&
                    route.get(0).getType() != TransportationType.FLIGHT &&
                    route.get(1).getType() == TransportationType.FLIGHT &&
                    route.get(2).getType() != TransportationType.FLIGHT;
        }
        return false;
    }

    private double calculateDistance(Location start, Location end) {
        if (start == null || end == null ||
                start.getLatitude() == null || start.getLongitude() == null ||
                end.getLatitude() == null || end.getLongitude() == null) {
            return Double.MAX_VALUE;
        }
        final int R = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(end.getLatitude() - start.getLatitude());
        double dLon = Math.toRadians(end.getLongitude() - start.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(start.getLatitude())) *
                        Math.cos(Math.toRadians(end.getLatitude())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double calculateTotalDistance(List<Transportation> route, Map<Long, Location> locationMap) {
        double total = 0.0;
        for (Transportation t : route) {
            Location start = locationMap.get(t.getOrigin().getId());
            Location end = locationMap.get(t.getDestination().getId());
            double d = calculateDistance(start, end);
            if (d == Double.MAX_VALUE) {
                return Double.MAX_VALUE;
            }
            total += d;
        }
        return total;
    }
}
