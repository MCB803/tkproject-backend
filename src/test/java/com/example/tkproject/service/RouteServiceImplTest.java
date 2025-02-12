package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TransportationRepository transportationRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Location origin;
    private Location destination;
    private Transportation flight;

    @BeforeEach
    void setUp() {
        origin = new Location("Istanbul Airport", "Turkey", "Istanbul", "IST");
        destination = new Location("New York JFK", "USA", "New York", "JFK");

        flight = new Transportation();
        flight.setType(TransportationType.FLIGHT);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setOperatingDays(Set.of(1, 2, 3, 4, 5, 6, 7)); // All days
    }

    @Test
    void findRoutes_ShouldReturnRoutes() {
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationRepository.findByLocationCode("JFK")).thenReturn(Optional.of(destination));
        when(transportationRepository.findAll()).thenReturn(List.of(flight));

        List<List<TransportationDTO>> routes = routeService.findRoutes("IST", "JFK", LocalDate.of(2025, 3, 12));

        assertNotNull(routes);
        assertFalse(routes.isEmpty());
        assertEquals(1, routes.size());
        verify(locationRepository, times(1)).findByLocationCode("IST");
        verify(locationRepository, times(1)).findByLocationCode("JFK");
        verify(transportationRepository, times(1)).findAll();
    }

    @Test
    void findRoutes_ShouldThrowException_WhenOriginNotFound() {
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.empty());

        assertThrows(RouteServiceException.class, () -> routeService.findRoutes("IST", "JFK", LocalDate.of(2025, 3, 12)));
    }

    @Test
    void findRoutes_ShouldThrowException_WhenDestinationNotFound() {
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationRepository.findByLocationCode("JFK")).thenReturn(Optional.empty());

        assertThrows(RouteServiceException.class, () -> routeService.findRoutes("IST", "JFK", LocalDate.of(2025, 3, 12)));
    }
}
