package com.example.tkproject.model;

import com.example.tkproject.model.enums.TransportationType;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "transportations")
public class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Transportation() {
    }

    public Transportation(Location origin, Location destination, TransportationType type, Set<Integer> operatingDays) {
        this.origin = origin;
        this.destination = destination;
        this.type = type;
        this.operatingDays = operatingDays;
    }

}