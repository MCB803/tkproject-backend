package com.example.tkproject.service;

import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import com.example.tkproject.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TransportationRepository transportationRepository;

    @InjectMocks
    private RouteService routeService;

    private Location origin;
    private Location destination;
    private Location intermediate1;
    private Location intermediate2;

    private LocalDate tripDate;
    private int tripDay;

    @BeforeEach
    public void setup() {
        // Create sample locations
        origin = new Location("Origin Airport", "CountryA", "CityA", "ORIG");
        destination = new Location("Destination Airport", "CountryB", "CityB", "DEST");
        intermediate1 = new Location("Intermediate Hub 1", "CountryA", "CityC", "INT1");
        intermediate2 = new Location("Intermediate Hub 2", "CountryB", "CityD", "INT2");

        tripDate = LocalDate.of(2025, 3, 12); // for example, a Wednesday
        tripDay = tripDate.getDayOfWeek().getValue();

        // Stub locationRepository to return these objects based on location code.
        when(locationRepository.findByLocationCode("ORIG")).thenReturn(Optional.of(origin));
        when(locationRepository.findByLocationCode("DEST")).thenReturn(Optional.of(destination));
    }

    @Test
    public void testDirectFlightRoute() {
        // Create a direct flight from origin to destination available on tripDay.
        Transportation directFlight = new Transportation(
                origin,
                destination,
                TransportationType.FLIGHT,
                new HashSet<>(Arrays.asList(tripDay))
        );

        when(transportationRepository.findAll()).thenReturn(Arrays.asList(directFlight));

        List<List<Transportation>> routes = routeService.findRoutes("ORIG", "DEST", tripDate);
        assertEquals(1, routes.size());
        assertEquals(1, routes.get(0).size());
        assertEquals(TransportationType.FLIGHT, routes.get(0).get(0).getType());
    }

    @Test
    public void testPreFlightTransferRoute() {
        // Create a non-flight transfer from origin to intermediate1.
        Transportation transfer = new Transportation(
                origin,
                intermediate1,
                TransportationType.BUS,
                new HashSet<>(Arrays.asList(tripDay))
        );
        // Create a flight from intermediate1 to destination.
        Transportation flight = new Transportation(
                intermediate1,
                destination,
                TransportationType.FLIGHT,
                new HashSet<>(Arrays.asList(tripDay))
        );

        when(transportationRepository.findAll()).thenReturn(Arrays.asList(transfer, flight));

        List<List<Transportation>> routes = routeService.findRoutes("ORIG", "DEST", tripDate);
        assertEquals(1, routes.size());
        assertEquals(2, routes.get(0).size());
        long flightCount = routes.get(0).stream()
                .filter(t -> t.getType() == TransportationType.FLIGHT)
                .count();
        assertEquals(1, flightCount);
    }

    @Test
    public void testPostFlightTransferRoute() {
        // Create a flight from origin to intermediate1.
        Transportation flight = new Transportation(
                origin,
                intermediate1,
                TransportationType.FLIGHT,
                new HashSet<>(Arrays.asList(tripDay))
        );
        // Create a non-flight transfer from intermediate1 to destination.
        Transportation transfer = new Transportation(
                intermediate1,
                destination,
                TransportationType.UBER,
                new HashSet<>(Arrays.asList(tripDay))
        );

        when(transportationRepository.findAll()).thenReturn(Arrays.asList(flight, transfer));

        List<List<Transportation>> routes = routeService.findRoutes("ORIG", "DEST", tripDate);
        assertEquals(1, routes.size());
        assertEquals(2, routes.get(0).size());
        long flightCount = routes.get(0).stream()
                .filter(t -> t.getType() == TransportationType.FLIGHT)
                .count();
        assertEquals(1, flightCount);
    }

    @Test
    public void testPreAndPostFlightTransferRoute() {
        // Create pre-flight transfer.
        Transportation transfer1 = new Transportation(
                origin,
                intermediate1,
                TransportationType.SUBWAY,
                new HashSet<>(Arrays.asList(tripDay))
        );
        // Create flight from intermediate1 to intermediate2.
        Transportation flight = new Transportation(
                intermediate1,
                intermediate2,
                TransportationType.FLIGHT,
                new HashSet<>(Arrays.asList(tripDay))
        );
        // Create post-flight transfer.
        Transportation transfer2 = new Transportation(
                intermediate2,
                destination,
                TransportationType.BUS,
                new HashSet<>(Arrays.asList(tripDay))
        );

        when(transportationRepository.findAll()).thenReturn(Arrays.asList(transfer1, flight, transfer2));

        List<List<Transportation>> routes = routeService.findRoutes("ORIG", "DEST", tripDate);
        assertEquals(1, routes.size());
        assertEquals(3, routes.get(0).size());
        long flightCount = routes.get(0).stream()
                .filter(t -> t.getType() == TransportationType.FLIGHT)
                .count();
        assertEquals(1, flightCount);
    }
}
