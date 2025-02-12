package com.example.tkproject.controller;

import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.exception.ResourceNotFoundException;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.service.TransportationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransportationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransportationService transportationService;


    @InjectMocks
    private TransportationController transportationController;

    private TransportationDTO transportationDTO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transportationController).build();

        LocationDTO origin = new LocationDTO(1L, "Istanbul Airport", "Turkey", "Istanbul", "IST");
        LocationDTO destination = new LocationDTO(2L, "John F. Kennedy Airport", "USA", "New York", "JFK");

        transportationDTO = new TransportationDTO(1L, origin, destination, "FLIGHT", Set.of(1, 3, 5));
    }

    @Test
    void getAllTransportations_ShouldReturnTransportations() throws Exception {
        when(transportationService.findAll()).thenReturn(List.of(transportationDTO));

        mockMvc.perform(get("/api/transportations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value("FLIGHT"));

        verify(transportationService, times(1)).findAll();
    }

    @Test
    void getTransportationById_ShouldReturnTransportation() throws Exception {
        when(transportationService.findById(1L)).thenReturn(transportationDTO);

        mockMvc.perform(get("/api/transportations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("FLIGHT"));

        verify(transportationService, times(1)).findById(1L);
    }

    @Test
    void getTransportationById_ShouldReturnNotFound_WhenTransportationDoesNotExist() throws Exception {
        when(transportationService.findById(2L)).thenThrow(new ResourceNotFoundException("Transportation not found"));

        mockMvc.perform(get("/api/transportations/2"))
                .andExpect(status().isNotFound());

        verify(transportationService, times(1)).findById(2L);
    }

    @Test
    void createTransportation_ShouldReturnCreatedTransportation() throws Exception {
        when(transportationService.create(any(TransportationDTO.class))).thenReturn(transportationDTO);

        mockMvc.perform(post("/api/transportations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transportationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("FLIGHT"));

        verify(transportationService, times(1)).create(any(TransportationDTO.class));
    }

    @Test
    void updateTransportation_ShouldReturnUpdatedTransportation() throws Exception {
        when(transportationService.update(eq(1L), any(TransportationDTO.class))).thenReturn(transportationDTO);

        mockMvc.perform(put("/api/transportations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transportationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("FLIGHT"));

        verify(transportationService, times(1)).update(eq(1L), any(TransportationDTO.class));
    }

    @Test
    void deleteTransportation_ShouldReturnNoContent() throws Exception {
        doNothing().when(transportationService).delete(1L);

        mockMvc.perform(delete("/api/transportations/1"))
                .andExpect(status().isNoContent());

        verify(transportationService, times(1)).delete(1L);
    }
}
