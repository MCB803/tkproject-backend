package com.example.tkproject.dto;

import com.example.tkproject.model.Transportation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportationResponseDTO {
    private Long id;
    private LocationDTO origin;
    private LocationDTO destination;
    private String type;
    private Set<Integer> operatingDays;

    public static TransportationResponseDTO fromEntity(Transportation transportation) {
        if (transportation == null) {
            return null;
        }

        return new TransportationResponseDTO(
                transportation.getId(),
                LocationDTO.fromEntity(transportation.getOrigin()),
                LocationDTO.fromEntity(transportation.getDestination()),
                transportation.getType().toString(),
                transportation.getOperatingDays() != null ? new HashSet<>(transportation.getOperatingDays()) : Collections.emptySet()
        );
    }


}
