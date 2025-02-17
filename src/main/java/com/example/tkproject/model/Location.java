package com.example.tkproject.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;
    private String city;

    @Column(name = "location_code")
    private String locationCode;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    public Location() {
    }

    public Location(String name, String country, String city, String locationCode) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.locationCode = locationCode;
    }


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
