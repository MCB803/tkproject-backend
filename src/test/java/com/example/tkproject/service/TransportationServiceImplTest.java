package com.example.tkproject.service;

import com.example.tkproject.dto.TransportationRequestDTO;
import com.example.tkproject.dto.TransportationResponseDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.exception.RouteServiceException;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.enums.TransportationType;
import com.example.tkproject.repository.TransportationRepository;
import com.example.tkproject.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransportationServiceImplTest {

    @Mock
    private TransportationRepository transportationRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private TransportationServiceImpl transportationService;

    private Transportation transportation;
    private Location origin, destination;
    private TransportationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        origin = new Location();
        origin.setId(1L);
        origin.setName("Istanbul Airport");

        destination = new Location();
        destination.setId(2L);
        destination.setName("London Heathrow");

        transportation = new Transportation();
        transportation.setId(10L);
        transportation.setType(TransportationType.FLIGHT);
        transportation.setOperatingDays(Set.of(1, 2, 3));
        transportation.setOrigin(origin);
        transportation.setDestination(destination);

        requestDTO = new TransportationRequestDTO();
        requestDTO.setOriginId(1L);
        requestDTO.setDestinationId(2L);
        requestDTO.setType("FLIGHT");
        requestDTO.setOperatingDays(Set.of(1, 2, 3));
    }

    @Test
    void findAll_ShouldReturnListOfTransportations() {
        when(transportationRepository.findAll()).thenReturn(List.of(transportation));

        List<TransportationResponseDTO> result = transportationService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Istanbul Airport", result.getFirst().getOrigin().getName());

        verify(transportationRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnTransportation_WhenFound() {
        when(transportationRepository.findById(10L)).thenReturn(Optional.of(transportation));

        TransportationResponseDTO result = transportationService.findById(10L);

        assertNotNull(result);
        assertEquals("Istanbul Airport", result.getOrigin().getName());

        verify(transportationRepository, times(1)).findById(10L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(transportationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transportationService.findById(99L));

        verify(transportationRepository, times(1)).findById(99L);
    }

    @Test
    void create_ShouldSaveAndReturnNewTransportation() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(transportationRepository.save(any())).thenReturn(transportation);

        TransportationResponseDTO result = transportationService.create(requestDTO);

        assertNotNull(result);
        assertEquals("FLIGHT", result.getType());
        assertEquals("Istanbul Airport", result.getOrigin().getName());

        verify(transportationRepository, times(1)).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenOriginAndDestinationAreSame() {
        requestDTO.setDestinationId(1L);

        assertThrows(RouteServiceException.class, () -> transportationService.create(requestDTO));
    }

    @Test
    void update_ShouldModifyExistingTransportation() {
        when(transportationRepository.findById(10L)).thenReturn(Optional.of(transportation));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(transportationRepository.save(any())).thenReturn(transportation);

        TransportationResponseDTO result = transportationService.update(10L, requestDTO);

        assertNotNull(result);
        assertEquals("FLIGHT", result.getType());
        assertEquals("Istanbul Airport", result.getOrigin().getName());

        verify(transportationRepository, times(1)).save(any());
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {
        when(transportationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transportationService.update(99L, requestDTO));

        verify(transportationRepository, times(1)).findById(99L);
    }

    @Test
    void delete_ShouldRemoveTransportation() {
        when(transportationRepository.findById(10L)).thenReturn(Optional.of(transportation));
        doNothing().when(transportationRepository).deleteById(10L);

        transportationService.delete(10L);

        verify(transportationRepository, times(1)).deleteById(10L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(transportationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transportationService.delete(99L));

        verify(transportationRepository, times(1)).findById(99L);
    }
}
