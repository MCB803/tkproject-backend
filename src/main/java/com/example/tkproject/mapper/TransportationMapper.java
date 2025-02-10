package com.example.tkproject.mapper;

import com.example.tkproject.dto.TransportationDTO;
import com.example.tkproject.model.Location;
import com.example.tkproject.model.Transportation;
import com.example.tkproject.model.TransportationType;
import com.example.tkproject.repository.LocationRepository;
import org.springframework.stereotype.Component;

@Component
public class TransportationMapper {

    private final LocationRepository locationRepository;

    public TransportationMapper(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public TransportationDTO toDTO(Transportation transportation) {
        TransportationDTO dto = new TransportationDTO();
        dto.setId(transportation.getId());
        dto.setOriginId(transportation.getOrigin().getId());
        dto.setDestinationId(transportation.getDestination().getId());
        dto.setType(transportation.getType().name());
        dto.setOperatingDays(transportation.getOperatingDays());
        return dto;
    }

    public Transportation toEntity(TransportationDTO dto) {
        Transportation transportation = new Transportation();
        // If an ID is provided, set it (for update operations)
        if (dto.getId() != null) {
            transportation.setId(dto.getId());
        }
        // Convert originId to a Location entity
        Location origin = locationRepository.findById(dto.getOriginId())
                .orElseThrow(() -> new RuntimeException("Origin location not found with id: " + dto.getOriginId()));
        transportation.setOrigin(origin);

        // Convert destinationId to a Location entity
        Location destination = locationRepository.findById(dto.getDestinationId())
                .orElseThrow(() -> new RuntimeException("Destination location not found with id: " + dto.getDestinationId()));
        transportation.setDestination(destination);

        // Convert type string to TransportationType enum (forcing uppercase)
        try {
            transportation.setType(TransportationType.valueOf(dto.getType().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid transportation type: " + dto.getType());
        }

        transportation.setOperatingDays(dto.getOperatingDays());
        return transportation;
    }
}
