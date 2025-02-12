package com.example.tkproject.dto;

import com.example.tkproject.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    private Long id;

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Country is required")
    @NotBlank(message = "Country cannot be blank")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    @NotNull(message = "City is required")
    @NotBlank(message = "City cannot be blank")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @NotNull(message = "Location code is required")
    @NotBlank(message = "Location code cannot be blank")
    @Pattern(regexp = "^[A-Z0-9]{3,5}$", message = "Location code must be 3-5 uppercase letters or numbers")
    private String locationCode;
    /**
     * Converts a `Location` entity into a `LocationDTO`
     */
    public static LocationDTO fromEntity(Location location) {
        return new LocationDTO(
                location.getId(),
                location.getName(),
                location.getCountry(),
                location.getCity(),
                location.getLocationCode()
        );
    }
}

