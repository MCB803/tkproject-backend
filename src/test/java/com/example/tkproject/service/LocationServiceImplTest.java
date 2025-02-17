package com.example.tkproject.service;

import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.model.Location;
import com.example.tkproject.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    private Location location;
    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin", "adminpass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        location = new Location("Istanbul Airport", "Turkey", "Istanbul", "IST");
        location.setId(1L);
        location.setLatitude(41.275);
        location.setLongitude(28.751);

        locationDTO = new LocationDTO(1L, "Istanbul Airport", "Turkey", "Istanbul", "IST", 41.275, 28.751);
    }

    @Test
    void findAll_ShouldReturnLocations() {
        when(locationRepository.findAll()).thenReturn(List.of(location));

        List<LocationDTO> result = locationService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("IST", result.getFirst().getLocationCode());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldThrowException_OnDatabaseError() {
        when(locationRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(RuntimeException.class, locationService::findAll);
        assertTrue(exception.getMessage().contains("Error fetching locations"));

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnLocation_WhenExists() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        LocationDTO result = locationService.findById(1L);

        assertNotNull(result);
        assertEquals("IST", result.getLocationCode());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationService.findById(99L));
    }

    @Test
    void create_ShouldSaveLocation() {
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDTO result = locationService.create(locationDTO);

        assertNotNull(result);
        assertEquals("IST", result.getLocationCode());
        assertEquals(41.275, result.getLatitude());
        assertEquals(28.751, result.getLongitude());

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void create_ShouldHandleNullLatLong() {
        LocationDTO newDTO = new LocationDTO(null, "Ankara Airport", "Turkey", "Ankara", "ANK", null, null);
        Location newLocation = new Location("Ankara Airport", "Turkey", "Ankara", "ANK");

        when(locationRepository.save(any(Location.class))).thenReturn(newLocation);

        LocationDTO result = locationService.create(newDTO);

        assertNotNull(result);
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void update_ShouldUpdateExistingLocation() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDTO updatedDTO = new LocationDTO(1L, "New Istanbul Airport", "Turkey", "Istanbul", "IST2", 41.300, 28.800);

        LocationDTO result = locationService.update(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("IST2", result.getLocationCode());
        assertEquals(41.300, result.getLatitude());
        assertEquals(28.800, result.getLongitude());

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void update_ShouldThrowException_WhenLocationNotFound() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        LocationDTO updatedDTO = new LocationDTO(99L, "Nonexistent Airport", "Unknown", "Nowhere", "N/A", null, null);

        assertThrows(ResourceNotFoundException.class, () -> locationService.update(99L, updatedDTO));
    }

    @Test
    void delete_ShouldRemoveLocation_WhenExists() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        doNothing().when(locationRepository).deleteById(1L);
        assertDoesNotThrow(() -> locationService.delete(1L));

        verify(locationRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenLocationNotFound() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationService.delete(99L));
    }
}
