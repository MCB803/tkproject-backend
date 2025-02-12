package com.example.tkproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Location code is required")
    private String locationCode;
}
