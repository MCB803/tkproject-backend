package com.example.tkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;
    private String city;

    @Column(name = "location_code", nullable = false, unique = true)
    private String locationCode;

    // Constructors, getters, and setters
    public Location() {
    }

    public Location(String name, String country, String city, String locationCode) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.locationCode = locationCode;
    }

    // Getters and setters

    public Long getId() {
        return id;
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

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    // For simplicity in comparisons in our route logic,
    // we assume that two Location objects are equal if their locationCode is equal.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return locationCode != null && locationCode.equals(location.locationCode);
    }

    @Override
    public int hashCode() {
        return locationCode != null ? locationCode.hashCode() : 0;
    }
}
