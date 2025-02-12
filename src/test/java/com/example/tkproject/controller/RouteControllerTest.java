package com.example.tkproject.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TransportationRepository transportationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Location origin;
    private Location destination;
    private Transportation flight;

    @BeforeEach
    public void setup() {
        transportationRepository.deleteAll();
        locationRepository.deleteAll();

        origin = new Location("Origin Airport", "CountryA", "CityA", "ORG");
        destination = new Location("Destination Airport", "CountryB", "CityB", "DST");
        origin = locationRepository.save(origin);
        destination = locationRepository.save(destination);

        int day = LocalDate.now().getDayOfWeek().getValue();
        flight = new Transportation(origin, destination, TransportationType.FLIGHT, Set.of(day));
        transportationRepository.save(flight);
    }



    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetRoutesSuccess() throws Exception {
        mockMvc.perform(get("/api/routes")
                        .param("originCode", "ORG")
                        .param("destinationCode", "DST")
                        .param("tripDate", LocalDate.now().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", isA(List.class)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetRoutesNotFound() throws Exception {
        // Using a non-existent origin code to force an error
        mockMvc.perform(get("/api/routes")
                        .param("originCode", "XXX")
                        .param("destinationCode", "DST")
                        .param("tripDate", LocalDate.now().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllLocationsForRoutes() throws Exception {
        mockMvc.perform(get("/api/routes/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].locationCode", is("ORG")));
    }
}
