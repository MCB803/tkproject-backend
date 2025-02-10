package com.example.tkproject.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transportations")
public class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One relationship for origin and destination
    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_id", nullable = false)
    private Location origin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Location destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransportationType type;

    @ElementCollection
    @CollectionTable(name = "transportation_operating_days", joinColumns = @JoinColumn(name = "transportation_id"))
    @Column(name = "operating_day")
    private Set<Integer> operatingDays = new HashSet<>();

    // Constructors, getters, and setters
    public Transportation() {
    }

    public Transportation(Location origin, Location destination, TransportationType type, Set<Integer> operatingDays) {
        this.origin = origin;
        this.destination = destination;
        this.type = type;
        this.operatingDays = operatingDays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public TransportationType getType() {
        return type;
    }

    public void setType(TransportationType type) {
        this.type = type;
    }

    public Set<Integer> getOperatingDays() {
        return operatingDays;
    }

    public void setOperatingDays(Set<Integer> operatingDays) {
        this.operatingDays = operatingDays;
    }
}