package com.example.tkproject.service;

import com.example.tkproject.model.Transportation;
import java.time.LocalDate;
import java.util.List;

public interface RouteService {
    /**
     * Finds all valid routes from the given origin to destination on the specified trip date.
     * A valid route is a list of Transportation segments that meet the following criteria:
     * - Exactly one segment is a FLIGHT (the rest are transfers).
     * - Each segment is available on the given trip day.
     *
     * @param originCode      the location code of the origin
     * @param destinationCode the location code of the destination
     * @param tripDate        the date of travel
     * @return a list of routes, where each route is a list of Transportation segments
     */
    List<List<Transportation>> findRoutes(String originCode, String destinationCode, LocalDate tripDate);
}
