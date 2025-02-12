package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private LocationDTO locationDTO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();

        locationDTO = new LocationDTO(1L, "Istanbul Airport", "Turkey", "Istanbul", "IST");
    }

    @Test
    void getAllLocations_ShouldReturnLocations() throws Exception {
        List<LocationDTO> locations = List.of(locationDTO);
        when(locationService.findAll()).thenReturn(locations);

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Istanbul Airport"));

        verify(locationService, times(1)).findAll();
    }

    @Test
    void getLocationById_ShouldReturnLocation() throws Exception {
        when(locationService.findById(1L)).thenReturn(locationDTO);

        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Istanbul Airport"));

        verify(locationService, times(1)).findById(1L);
    }

    @Test
    void getLocationById_ShouldReturnNotFound_WhenLocationDoesNotExist() throws Exception {
        when(locationService.findById(2L)).thenThrow(new ResourceNotFoundException("Location not found"));

        mockMvc.perform(get("/api/locations/2"))
                .andExpect(status().isNotFound());

        verify(locationService, times(1)).findById(2L);
    }

    @Test
    void createLocation_ShouldReturnCreatedLocation() throws Exception {
        when(locationService.create(any(LocationDTO.class))).thenReturn(locationDTO);

        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Istanbul Airport"));

        verify(locationService, times(1)).create(any(LocationDTO.class));
    }

    @Test
    void updateLocation_ShouldReturnUpdatedLocation() throws Exception {
        when(locationService.update(eq(1L), any(LocationDTO.class))).thenReturn(locationDTO);

        mockMvc.perform(put("/api/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Istanbul Airport"));

        verify(locationService, times(1)).update(eq(1L), any(LocationDTO.class));
    }

    @Test
    void deleteLocation_ShouldReturnNoContent() throws Exception {
        doNothing().when(locationService).delete(1L);

        mockMvc.perform(delete("/api/locations/1"))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).delete(1L);
    }
}
