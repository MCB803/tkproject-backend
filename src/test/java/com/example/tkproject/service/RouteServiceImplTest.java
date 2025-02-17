package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationResponseDTO;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.enums.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDate;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TransportationRepository transportationRepository;

    private static class DummyTransactionManager implements PlatformTransactionManager {
        @Override
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            return new TransactionStatus() {
                @Override public boolean isNewTransaction() { return true; }
                @Override public boolean hasSavepoint() { return false; }
                @Override public void setRollbackOnly() {}
                @Override public boolean isRollbackOnly() { return false; }
                @Override public void flush() {}
                @Override public boolean isCompleted() { return false; }
                @Override public Object createSavepoint() { throw new UnsupportedOperationException("Not supported"); }
                @Override public void rollbackToSavepoint(Object savepoint) { throw new UnsupportedOperationException("Not supported"); }
                @Override public void releaseSavepoint(Object savepoint) { throw new UnsupportedOperationException("Not supported"); }
            };
        }
        @Override public void commit(TransactionStatus status) {}
        @Override public void rollback(TransactionStatus status) {}
    }

    private PlatformTransactionManager transactionManager;
    private RouteServiceImpl routeService;

    private Location origin;
    private Location destination;
    private Location stopover1;
    private Location stopover2;
    private Transportation directFlight;
    private Transportation busOriginToStopover1;
    private Transportation flightStopover1ToStopover2;
    private Transportation subwayStopover2ToDestination;
    private Transportation flightOriginToStopover1;
    private Transportation flightStopover1ToDestination;
    private Transportation busOriginToStopover1_NF;
    private Transportation subwayStopover1ToDestination_NF;
    private Transportation flightOriginToStopover1_Invalid;
    private Transportation busStopover1ToStopover2_Invalid;
    private Transportation subwayStopover2ToDestination_Invalid;
    private Transportation busOriginToStopover1_Invalid;
    private Transportation flightStopover1ToStopover2_Invalid;
    private Transportation flightStopover2ToDestination_Invalid;

    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        origin = new Location();
        origin.setId(1L);
        origin.setName("Istanbul Airport");

        stopover1 = new Location();
        stopover1.setId(2L);
        stopover1.setName("Munich Airport");

        stopover2 = new Location();
        stopover2.setId(4L);
        stopover2.setName("Paris Charles de Gaulle");

        destination = new Location();
        destination.setId(3L);
        destination.setName("London Heathrow");

        directFlight = new Transportation();
        directFlight.setId(10L);
        directFlight.setType(TransportationType.FLIGHT);
        directFlight.setOperatingDays(Set.of(3));
        directFlight.setOrigin(origin);
        directFlight.setDestination(destination);

        busOriginToStopover1 = new Transportation();
        busOriginToStopover1.setId(20L);
        busOriginToStopover1.setType(TransportationType.BUS);
        busOriginToStopover1.setOperatingDays(Set.of(3));
        busOriginToStopover1.setOrigin(origin);
        busOriginToStopover1.setDestination(stopover1);

        flightStopover1ToStopover2 = new Transportation();
        flightStopover1ToStopover2.setId(30L);
        flightStopover1ToStopover2.setType(TransportationType.FLIGHT);
        flightStopover1ToStopover2.setOperatingDays(Set.of(3));
        flightStopover1ToStopover2.setOrigin(stopover1);
        flightStopover1ToStopover2.setDestination(stopover2);

        subwayStopover2ToDestination = new Transportation();
        subwayStopover2ToDestination.setId(40L);
        subwayStopover2ToDestination.setType(TransportationType.SUBWAY);
        subwayStopover2ToDestination.setOperatingDays(Set.of(3));
        subwayStopover2ToDestination.setOrigin(stopover2);
        subwayStopover2ToDestination.setDestination(destination);

        flightOriginToStopover1 = new Transportation();
        flightOriginToStopover1.setId(50L);
        flightOriginToStopover1.setType(TransportationType.FLIGHT);
        flightOriginToStopover1.setOperatingDays(Set.of(3));
        flightOriginToStopover1.setOrigin(origin);
        flightOriginToStopover1.setDestination(stopover1);

        flightStopover1ToDestination = new Transportation();
        flightStopover1ToDestination.setId(60L);
        flightStopover1ToDestination.setType(TransportationType.FLIGHT);
        flightStopover1ToDestination.setOperatingDays(Set.of(3));
        flightStopover1ToDestination.setOrigin(stopover1);
        flightStopover1ToDestination.setDestination(destination);

        busOriginToStopover1_NF = new Transportation();
        busOriginToStopover1_NF.setId(70L);
        busOriginToStopover1_NF.setType(TransportationType.BUS);
        busOriginToStopover1_NF.setOperatingDays(Set.of(3));
        busOriginToStopover1_NF.setOrigin(origin);
        busOriginToStopover1_NF.setDestination(stopover1);

        subwayStopover1ToDestination_NF = new Transportation();
        subwayStopover1ToDestination_NF.setId(80L);
        subwayStopover1ToDestination_NF.setType(TransportationType.SUBWAY);
        subwayStopover1ToDestination_NF.setOperatingDays(Set.of(3));
        subwayStopover1ToDestination_NF.setOrigin(stopover1);
        subwayStopover1ToDestination_NF.setDestination(destination);

        flightOriginToStopover1_Invalid = new Transportation();
        flightOriginToStopover1_Invalid.setId(90L);
        flightOriginToStopover1_Invalid.setType(TransportationType.FLIGHT);
        flightOriginToStopover1_Invalid.setOperatingDays(Set.of(3));
        flightOriginToStopover1_Invalid.setOrigin(origin);
        flightOriginToStopover1_Invalid.setDestination(stopover1);

        busStopover1ToStopover2_Invalid = new Transportation();
        busStopover1ToStopover2_Invalid.setId(100L);
        busStopover1ToStopover2_Invalid.setType(TransportationType.BUS);
        busStopover1ToStopover2_Invalid.setOperatingDays(Set.of(3));
        busStopover1ToStopover2_Invalid.setOrigin(stopover1);
        busStopover1ToStopover2_Invalid.setDestination(stopover2);

        subwayStopover2ToDestination_Invalid = new Transportation();
        subwayStopover2ToDestination_Invalid.setId(110L);
        subwayStopover2ToDestination_Invalid.setType(TransportationType.SUBWAY);
        subwayStopover2ToDestination_Invalid.setOperatingDays(Set.of(3));
        subwayStopover2ToDestination_Invalid.setOrigin(stopover2);
        subwayStopover2ToDestination_Invalid.setDestination(destination);

        busOriginToStopover1_Invalid = new Transportation();
        busOriginToStopover1_Invalid.setId(120L);
        busOriginToStopover1_Invalid.setType(TransportationType.BUS);
        busOriginToStopover1_Invalid.setOperatingDays(Set.of(3));
        busOriginToStopover1_Invalid.setOrigin(origin);
        busOriginToStopover1_Invalid.setDestination(stopover1);

        flightStopover1ToStopover2_Invalid = new Transportation();
        flightStopover1ToStopover2_Invalid.setId(130L);
        flightStopover1ToStopover2_Invalid.setType(TransportationType.FLIGHT);
        flightStopover1ToStopover2_Invalid.setOperatingDays(Set.of(3));
        flightStopover1ToStopover2_Invalid.setOrigin(stopover1);
        flightStopover1ToStopover2_Invalid.setDestination(stopover2);

        flightStopover2ToDestination_Invalid = new Transportation();
        flightStopover2ToDestination_Invalid.setId(140L);
        flightStopover2ToDestination_Invalid.setType(TransportationType.FLIGHT);
        flightStopover2ToDestination_Invalid.setOperatingDays(Set.of(3));
        flightStopover2ToDestination_Invalid.setOrigin(stopover2);
        flightStopover2ToDestination_Invalid.setDestination(destination);

        testDate = LocalDate.of(2025, 2, 19);

        transactionManager = new DummyTransactionManager();

        routeService = new RouteServiceImpl(locationRepository, transportationRepository, transactionManager);
    }

    @Test
    void findRoutes_ShouldReturnDirectFlight() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findByOperatingDaysContaining(3))
                .thenReturn(List.of(directFlight));

        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate).join();
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Expected one valid route");
        List<TransportationResponseDTO> routeDto = result.getFirst();
        assertEquals(1, routeDto.size(), "Expected one segment in the direct flight route");
        assertEquals("FLIGHT", routeDto.getFirst().getType(), "Expected a FLIGHT type for direct flight");

        verify(transportationRepository, atLeastOnce()).findByOperatingDaysContaining(3);
    }

    @Test
    void findRoutes_ShouldReturnEmptyList_ForInvalidRoute_FlightFlight() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findByOperatingDaysContaining(3))
                .thenReturn(List.of(flightOriginToStopover1, flightStopover1ToDestination));
        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate).join();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Expected no valid routes for flight -> flight pattern");
    }

    @Test
    void findRoutes_ShouldReturnEmptyList_ForInvalidRoute_NonFlightNonFlight() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findByOperatingDaysContaining(3))
                .thenReturn(List.of(busOriginToStopover1_NF, subwayStopover1ToDestination_NF));
        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate).join();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Expected no valid routes for non-flight -> non-flight pattern");
    }

    @Test
    void findRoutes_ShouldReturnEmptyList_ForInvalidRoute_FlightNonFlightNonFlight() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findByOperatingDaysContaining(3))
                .thenReturn(List.of(flightOriginToStopover1_Invalid, busStopover1ToStopover2_Invalid, subwayStopover2ToDestination_Invalid));
        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate).join();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Expected no valid routes for flight -> non-flight -> non-flight pattern");
    }

    @Test
    void findRoutes_ShouldReturnEmptyList_ForInvalidRoute_NonFlightFlightFlight() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        lenient().when(locationRepository.findById(2L)).thenReturn(Optional.of(stopover1));
        lenient().when(locationRepository.findById(4L)).thenReturn(Optional.of(stopover2));
        when(transportationRepository.findByOperatingDaysContaining(3))
                .thenReturn(List.of(busOriginToStopover1_Invalid, flightStopover1ToStopover2_Invalid, flightStopover2ToDestination_Invalid));
        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate).join();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Expected no valid routes for non-flight -> flight -> flight pattern");
    }

    @Test
    void findRoutes_ShouldThrowException_WhenOriginAndDestinationAreSame() {
        RouteServiceException exception = assertThrows(RouteServiceException.class,
                () -> routeService.findRoutes(1L, 1L, testDate),
                "Expected exception when origin equals destination");
        assertEquals("Origin and destination must be different!", exception.getMessage());
    }

    @Test
    void findRoutes_ShouldThrowException_WhenOriginNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        lenient().when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));

        RouteServiceException exception = assertThrows(RouteServiceException.class,
                () -> routeService.findRoutes(1L, 3L, testDate),
                "Expected exception when origin is not found");
        assertEquals("Origin not found with ID: 1", exception.getMessage());
    }

    @Test
    void findRoutes_ShouldThrowException_WhenDestinationNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.empty());

        RouteServiceException exception = assertThrows(RouteServiceException.class,
                () -> routeService.findRoutes(1L, 3L, testDate),
                "Expected exception when destination is not found");
        assertEquals("Destination not found with ID: 3", exception.getMessage());
    }

    @Test
    void findRoutes_ShouldReturnEmptyList_WhenNoAvailableRoutes() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(destination));
        when(transportationRepository.findByOperatingDaysContaining(3)).thenReturn(List.of());

        List<List<TransportationResponseDTO>> result = routeService.findRoutes(1L, 3L, testDate).join();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Expected empty list when no routes are available");
    }
}
