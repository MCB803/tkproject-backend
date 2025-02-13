package com.example.tkproject.dto;

import com.example.tkproject.model.Location;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LocationDTO {

    private Long id;

    @NotNull(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Country cannot be blank")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    @NotNull(message = "City cannot be blank")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    private String locationCode;

    private Double latitude;

    private Double longitude;

    public LocationDTO() {
    }

    public LocationDTO(Long id, String name, String country, String city, String locationCode, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
        this.locationCode = locationCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry (String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode (String locationCode) {
        this.locationCode = locationCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public static LocationDTO fromEntity(Location location) {
        return new LocationDTO(
                location.getId(),
                location.getName(),
                location.getCountry(),
                location.getCity(),
                location.getLocationCode(),
                location.getLatitude(),
                location.getLongitude()
        );
    }
}

