package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationResponseDTO;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    private Location origin, destination, stopover;
    private Transportation flight, bus, subway;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Sample Locations
        origin = new Location();
        origin.setId(1L);
        origin.setName("Istanbul Airport");

        stopover = new Location();
        stopover.setId(2L);
        stopover.setName("Munich Airport");

        destination = new Location();
        destination.setId(3L);
        destination.setName("London Heathrow");

        // Sample Transportations
        flight = new Transportation();
        flight.setId(10L);
        flight.setType(TransportationType.FLIGHT);
        flight.setOperatingDays(Set.of(3));
        flight.setOrigin(origin);
        flight.setDestination(destination);

        bus = new Transportation();
        bus.setId(20L);
        bus.setType(TransportationType.BUS);
        bus.setOperatingDays(Set.of(3));
        bus.setOrigin(origin);
        bus.setDestination(stopover);

        subway = new Transportation();
        subway.setId(30L);
        subway.setType(TransportationType.SUBWAY);
        subway.setOperatingDays(Set.of(3));
        subway.setOrigin(stopover);
        subway.setDestination(destination);

        testDate = LocalDate.of(2025, 2, 19); // A Wednesday
    }

    @Test
    void findRoutes_ShouldReturnDirectFlight() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findAll()).thenReturn(List.of(flight));

        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("FLIGHT", result.get(0).get(0).getType());

        verify(transportationRepository, times(1)).findAll();
    }

    @Test
    void findRoutes_ShouldReturnMultiStepRoute() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findAll()).thenReturn(List.of(flight, bus, subway));

        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).size()); // Two-step journey

        verify(transportationRepository, times(1)).findAll();
    }

    @Test
    void findRoutes_ShouldThrowException_WhenOriginAndDestinationAreSame() {
        assertThrows(RouteServiceException.class, () -> routeService.findRoutes(1L, 1L, testDate));
    }

    @Test
    void findRoutes_ShouldThrowException_WhenOriginNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));

        assertThrows(RouteServiceException.class, () -> routeService.findRoutes(1L, 3L, testDate));
    }

    @Test
    void findRoutes_ShouldThrowException_WhenDestinationNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(RouteServiceException.class, () -> routeService.findRoutes(1L, 3L, testDate));
    }

    @Test
    void findRoutes_ShouldReturnEmptyList_WhenNoAvailableRoutes() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findAll()).thenReturn(List.of());

        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate);

        assertTrue(result.isEmpty());
    }
}
