package com.example.tkproject.service;

import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RouteService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TransportationRepository transportationRepository;

    /**
     * Finds all valid routes from origin to destination on the given trip date.
     * Valid routes are:
     * 1. Direct Flight: [FLIGHT]
     * 2. Pre-flight transfer + Flight: [transfer, FLIGHT]
     * 3. Flight + Post-flight transfer: [FLIGHT, transfer]
     * 4. Pre-flight transfer + Flight + Post-flight transfer: [transfer, FLIGHT, transfer]
     *
     * Each transportation must be available on the given day.
     *
     * @param originCode      Location code for the origin.
     * @param destinationCode Location code for the destination.
     * @param tripDate        Date of travel.
     * @return List of routes, each route is represented as a List of Transportation.
     */
    public List<List<Transportation>> findRoutes(String originCode, String destinationCode, LocalDate tripDate) {
        int dayOfWeek = tripDate.getDayOfWeek().getValue();

        Location origin = locationRepository.findByLocationCode(originCode)
                .orElseThrow(() -> new RuntimeException("Origin not found"));
        Location destination = locationRepository.findByLocationCode(destinationCode)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        List<Transportation> allTransports = transportationRepository.findAll();
        List<List<Transportation>> validRoutes = new ArrayList<>();

        // Helper: check if a transportation is available on the trip day.
        // (We assume operatingDays is a Set<Integer> where 1=Monday,...,7=Sunday)
        java.util.function.Predicate<Transportation> availableOnDay = t -> t.getOperatingDays().contains(dayOfWeek);

        // 1. Direct Flight: [FLIGHT]
        for (Transportation t : allTransports) {
            if (availableOnDay.test(t) &&
                    t.getOrigin().equals(origin) &&
                    t.getDestination().equals(destination) &&
                    t.getType() == TransportationType.FLIGHT) {
                validRoutes.add(Collections.singletonList(t));
            }
        }

        // 2. Pre-flight transfer + Flight: [non-FLIGHT, FLIGHT]
        for (Transportation t1 : allTransports) {
            if (availableOnDay.test(t1) &&
                    t1.getOrigin().equals(origin) &&
                    t1.getType() != TransportationType.FLIGHT) {

                Location intermediate = t1.getDestination();
                // Ensure intermediate is not the final destination (if a direct transfer exists, case 1 would have caught it)
                if (intermediate.equals(destination)) continue;

                for (Transportation t2 : allTransports) {
                    if (availableOnDay.test(t2) &&
                            t2.getOrigin().equals(intermediate) &&
                            t2.getDestination().equals(destination) &&
                            t2.getType() == TransportationType.FLIGHT) {
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
            if (availableOnDay.test(t1) &&
                    t1.getOrigin().equals(origin) &&
                    t1.getType() == TransportationType.FLIGHT) {

                Location intermediate = t1.getDestination();
                if (intermediate.equals(destination)) continue;

                for (Transportation t2 : allTransports) {
                    if (availableOnDay.test(t2) &&
                            t2.getOrigin().equals(intermediate) &&
                            t2.getDestination().equals(destination) &&
                            t2.getType() != TransportationType.FLIGHT) {
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
            if (availableOnDay.test(t1) &&
                    t1.getOrigin().equals(origin) &&
                    t1.getType() != TransportationType.FLIGHT) {

                Location intermediate1 = t1.getDestination();
                // t1 should not directly go to destination (that case is handled above)
                if (intermediate1.equals(destination)) continue;

                for (Transportation t2 : allTransports) {
                    if (availableOnDay.test(t2) &&
                            t2.getOrigin().equals(intermediate1) &&
                            t2.getType() == TransportationType.FLIGHT) {

                        Location intermediate2 = t2.getDestination();
                        if (intermediate2.equals(destination)) {
                            // This case is already covered in case 2 or 3.
                            continue;
                        }
                        for (Transportation t3 : allTransports) {
                            if (availableOnDay.test(t3) &&
                                    t3.getOrigin().equals(intermediate2) &&
                                    t3.getDestination().equals(destination) &&
                                    t3.getType() != TransportationType.FLIGHT) {
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

        return validRoutes;
    }
}
