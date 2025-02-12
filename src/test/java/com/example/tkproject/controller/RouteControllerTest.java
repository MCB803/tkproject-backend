package com.example.tkproject.controller;

import com.example.tkproject.dto.ApiResponse;
import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.service.LocationService;
import com.example.tkproject.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RouteService routeService;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private RouteController routeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
    }

    @Test
    void getRoutes_ShouldReturnRoutes() throws Exception {
        String originCode = "IST";
        String destinationCode = "JFK";
        LocalDate tripDate = LocalDate.of(2025, 3, 12);

        when(routeService.findRoutes(originCode, destinationCode, tripDate)).thenReturn(List.of(List.of(new TransportationDTO())));

        mockMvc.perform(get("/api/routes")
                        .param("originCode", originCode)
                        .param("destinationCode", destinationCode)
                        .param("tripDate", tripDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Routes fetched successfully"));

        verify(routeService, times(1)).findRoutes(originCode, destinationCode, tripDate);
    }

    @Test
    void getRoutes_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        String originCode = "IST";
        String destinationCode = "JFK";
        LocalDate tripDate = LocalDate.of(2025, 3, 12);

        when(routeService.findRoutes(originCode, destinationCode, tripDate))
                .thenThrow(new RuntimeException("Error fetching routes"));

        mockMvc.perform(get("/api/routes")
                        .param("originCode", originCode)
                        .param("destinationCode", destinationCode)
                        .param("tripDate", tripDate.toString()))
                .andExpect(status().isInternalServerError());

        verify(routeService, times(1)).findRoutes(originCode, destinationCode, tripDate);
    }
}
