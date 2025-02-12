package com.example.tkproject.service;

import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RouteServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TransportationRepository transportationRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Location origin;
    private Location destination;
    private Transportation directFlight;
    private LocalDate tripDate;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        "testPassword",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        MockitoAnnotations.openMocks(this);

        // Set up test data
        origin = new Location("Origin Airport", "USA", "CityA", "OAP");
        destination = new Location("Destination Airport", "USA", "CityB", "DAP");
        // For the sake of the test, assume the tripDate is such that the day of week is 3 (e.g. Wednesday)
        tripDate = LocalDate.of(2025, 3, 12); // Check what day of week this is; for example, let’s assume it equals 3.

        // Create a direct flight available on that day
        directFlight = new Transportation(origin, destination, TransportationType.FLIGHT, new HashSet<>(Collections.singletonList(tripDate.getDayOfWeek().getValue())));
    }

    @Test
    public void testFindRoutes_DirectFlight() {
        // Set up repository behavior
        when(locationRepository.findByLocationCode("OAP")).thenReturn(Optional.of(origin));
        when(locationRepository.findByLocationCode("DAP")).thenReturn(Optional.of(destination));
        when(transportationRepository.findAll()).thenReturn(Collections.singletonList(directFlight));

        // Call the service
        List<List<Transportation>> routes = routeService.findRoutes("OAP", "DAP", tripDate);

        // Verify results
        assertNotNull(routes);
        assertEquals(1, routes.size());
        assertEquals(1, routes.get(0).size());
        assertEquals(TransportationType.FLIGHT, routes.get(0).get(0).getType());
        verify(locationRepository, times(1)).findByLocationCode("OAP");
        verify(locationRepository, times(1)).findByLocationCode("DAP");
    }

    @Test
    public void testFindRoutes_OriginNotFound() {
        when(locationRepository.findByLocationCode("OAP")).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routeService.findRoutes("OAP", "DAP", tripDate);
        });
        assertTrue(exception.getMessage().contains("Origin not found"));
    }
}
