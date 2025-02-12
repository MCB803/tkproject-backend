package com.example.tkproject.service;

import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransportationServiceImplTest {

    @Mock
    private TransportationRepository transportationRepository;

    @InjectMocks
    private TransportationServiceImpl transportationService;

    private Transportation transportation;
    private Location origin;
    private Location destination;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        "testPassword",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ));
        MockitoAnnotations.openMocks(this);
        origin = new Location("Origin Airport", "USA", "CityA", "OAP");
        destination = new Location("Destination Airport", "USA", "CityB", "DAP");

        transportation = new Transportation(origin, destination, TransportationType.FLIGHT, new HashSet<>(List.of(1, 2, 3)));
    }

    @Test
    public void testFindAll() {
        when(transportationRepository.findAll()).thenReturn(Arrays.asList(transportation));
        List<Transportation> transports = transportationService.findAll();
        assertNotNull(transports);
        assertEquals(1, transports.size());
        verify(transportationRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(transportationRepository.findById(1L)).thenReturn(Optional.of(transportation));
        Optional<Transportation> result = transportationService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(TransportationType.FLIGHT, result.get().getType());
    }

    @Test
    public void testCreate() {
        when(transportationRepository.save(any(Transportation.class))).thenReturn(transportation);
        Transportation created = transportationService.create(transportation);
        assertNotNull(created);
        assertEquals(origin, created.getOrigin());
    }

    @Test
    public void testUpdate() {
        when(transportationRepository.findById(1L)).thenReturn(Optional.of(transportation));
        when(transportationRepository.save(any(Transportation.class))).thenReturn(transportation);
        transportation.setType(TransportationType.UBER);
        Transportation updated = transportationService.update(1L, transportation);
        assertEquals(TransportationType.UBER, updated.getType());
    }

    @Test
    public void testDelete() {
        doNothing().when(transportationRepository).deleteById(1L);
        assertDoesNotThrow(() -> transportationService.delete(1L));
        verify(transportationRepository, times(1)).deleteById(1L);
    }
}
