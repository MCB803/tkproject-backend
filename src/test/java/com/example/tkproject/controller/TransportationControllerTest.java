package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;


import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransportationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransportationRepository transportationRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Location origin;
    private Location destination;
    private Transportation transportation;

    @BeforeEach
    public void setup() {
        transportationRepository.deleteAll();
        locationRepository.deleteAll();

        origin = new Location("Origin Airport", "CountryA", "CityA", "ORG");
        destination = new Location("Destination Airport", "CountryB", "CityB", "DST");
        origin = locationRepository.save(origin);
        destination = locationRepository.save(destination);

        transportation = new Transportation(origin, destination, TransportationType.UBER, Set.of(1, 2, 3));
        transportation = transportationRepository.save(transportation);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllTransportations() throws Exception {
        mockMvc.perform(get("/api/transportations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(transportation.getId().intValue())))
                .andExpect(jsonPath("$.data[0].type", is(transportation.getType().name())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetTransportationById() throws Exception {
        mockMvc.perform(get("/api/transportations/{id}", transportation.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(transportation.getId().intValue())))
                .andExpect(jsonPath("$.data.type", is(transportation.getType().name())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateTransportation() throws Exception {
        TransportationDTO dto = new TransportationDTO();
        dto.setOriginId(origin.getId());
        dto.setDestinationId(destination.getId());
        dto.setType("FLIGHT");
        dto.setOperatingDays(Set.of(1, 2, 3));

        mockMvc.perform(post("/api/transportations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type", is("FLIGHT")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateTransportation() throws Exception {
        TransportationDTO dto = new TransportationDTO();
        dto.setOriginId(origin.getId());
        dto.setDestinationId(destination.getId());
        dto.setType("BUS");
        dto.setOperatingDays(Set.of(4, 5, 6));

        mockMvc.perform(put("/api/transportations/{id}", transportation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type", is("BUS")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteTransportation() throws Exception {
        mockMvc.perform(delete("/api/transportations/{id}", transportation.getId()))
                .andExpect(status().isNoContent());
    }
}
