package com.example.tkproject.controller;

import com.example.tkproject.dto.LocationDTO;
import com.example.tkproject.model.Location;
import com.example.tkproject.repository.LocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Location testLocation;

    @BeforeEach
    public void setup() {
        locationRepository.deleteAll();
        testLocation = new Location("Test Airport", "TestCountry", "TestCity", "TST");
        testLocation = locationRepository.save(testLocation);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllLocations() throws Exception {
        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].locationCode", is("TST")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetLocationById() throws Exception {
        mockMvc.perform(get("/api/locations/{id}", testLocation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationCode", is("TST")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateLocation() throws Exception {
        LocationDTO dto = new LocationDTO();
        dto.setName("New Airport");
        dto.setCountry("NewCountry");
        dto.setCity("NewCity");
        dto.setLocationCode("NEW");

        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.locationCode", is("NEW")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateLocation() throws Exception {
        LocationDTO dto = new LocationDTO();
        dto.setName("Updated Airport");
        dto.setCountry("UpdatedCountry");
        dto.setCity("UpdatedCity");
        dto.setLocationCode("UPD");

        mockMvc.perform(put("/api/locations/{id}", testLocation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationCode", is("UPD")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteLocation() throws Exception {
        mockMvc.perform(delete("/api/locations/{id}", testLocation.getId()))
                .andExpect(status().isNoContent());
    }
}
