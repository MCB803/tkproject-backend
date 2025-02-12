package com.example.tkproject.service;

import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransportationServiceImplTest {

    @Mock
    private TransportationRepository transportationRepository;

    @InjectMocks
    private TransportationServiceImpl transportationService;

    private Transportation transportation;
    private TransportationDTO transportationDTO;
    private LocationDTO originDTO;
    private LocationDTO destinationDTO;

    @BeforeEach
    void setUp() {
        originDTO = new LocationDTO(1L, "Istanbul Airport", "Turkey", "Istanbul", "IST");
        destinationDTO = new LocationDTO(2L, "New York JFK", "USA", "New York", "JFK");

        transportation = new Transportation();
        transportation.setId(1L);
        transportation.setType(TransportationType.FLIGHT);
        transportation.setOperatingDays(Set.of(1, 3, 5));

        transportationDTO = new TransportationDTO(1L, originDTO, destinationDTO, "FLIGHT", Set.of(1, 3, 5));
    }

    @Test
    void findAll_ShouldReturnTransportationList() {
        when(transportationRepository.findAll()).thenReturn(List.of(transportation));

        List<TransportationDTO> result = transportationService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("FLIGHT", result.get(0).getType());
        verify(transportationRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnTransportation() {
        when(transportationRepository.findById(1L)).thenReturn(Optional.of(transportation));

        TransportationDTO result = transportationService.findById(1L);

        assertNotNull(result);
        assertEquals("FLIGHT", result.getType());
        verify(transportationRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(transportationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transportationService.findById(2L));
    }

    @Test
    void create_ShouldSaveTransportation() {
        when(transportationRepository.save(any(Transportation.class))).thenReturn(transportation);

        TransportationDTO result = transportationService.create(transportationDTO);

        assertNotNull(result);
        assertEquals("FLIGHT", result.getType());
        verify(transportationRepository, times(1)).save(any(Transportation.class));
    }

    @Test
    void update_ShouldModifyTransportation() {
        when(transportationRepository.findById(1L)).thenReturn(Optional.of(transportation));
        when(transportationRepository.save(any(Transportation.class))).thenReturn(transportation);

        TransportationDTO result = transportationService.update(1L, transportationDTO);

        assertNotNull(result);
        assertEquals("FLIGHT", result.getType());
        verify(transportationRepository, times(1)).findById(1L);
        verify(transportationRepository, times(1)).save(any(Transportation.class));
    }

    @Test
    void delete_ShouldRemoveTransportation() {
        when(transportationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transportationRepository).deleteById(1L);

        assertDoesNotThrow(() -> transportationService.delete(1L));
        verify(transportationRepository, times(1)).deleteById(1L);
    }
}
