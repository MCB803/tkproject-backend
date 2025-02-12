package com.example.tkproject.service;

import com.example.tkproject.model.Location;
import com.example.tkproject.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        "testPassword",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ));
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Location> expected = Arrays.asList(
                new Location("Location A", "CountryA", "CityA", "CODEA"),
                new Location("Location B", "CountryB", "CityB", "CODEB")
        );
        when(locationRepository.findAll()).thenReturn(expected);

        // Act
        List<Location> locations = locationService.findAll();

        // Assert
        assertThat(locations).hasSize(2).containsExactlyElementsOf(expected);
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdFound() {
        // Arrange
        Location location = new Location("Location A", "CountryA", "CityA", "CODEA");
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        // Act
        Optional<Location> result = locationService.findById(1L);

        // Assert
        assertThat(result).isPresent().contains(location);
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateNotFound() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                locationService.update(1L, new Location("New", "NewCountry", "NewCity", "NEWCODE"))
        );
        assertThat(exception.getMessage()).contains("Location not found with id");
        verify(locationRepository, times(1)).findById(1L);
    }
}
