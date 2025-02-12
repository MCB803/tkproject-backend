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
        location = new Location("Istanbul Airport", "Turkey", "Istanbul", "IST");
        location.setId(1L);

        locationDTO = new LocationDTO(1L, "Istanbul Airport", "Turkey", "Istanbul", "IST");
    }

    @Test
    void findAll_ShouldReturnLocationList() {
        when(locationRepository.findAll()).thenReturn(List.of(location));

        List<LocationDTO> result = locationService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("IST", result.get(0).getLocationCode());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnLocation() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        LocationDTO result = locationService.findById(1L);

        assertNotNull(result);
        assertEquals("IST", result.getLocationCode());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(locationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationService.findById(2L));
    }

    @Test
    void create_ShouldSaveLocation() {
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDTO result = locationService.create(locationDTO);

        assertNotNull(result);
        assertEquals("IST", result.getLocationCode());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void update_ShouldUpdateLocation() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDTO result = locationService.update(1L, locationDTO);

        assertNotNull(result);
        assertEquals("IST", result.getLocationCode());
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void delete_ShouldRemoveLocation() {
        when(locationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(1L);

        assertDoesNotThrow(() -> locationService.delete(1L));
        verify(locationRepository, times(1)).deleteById(1L);
    }
}
