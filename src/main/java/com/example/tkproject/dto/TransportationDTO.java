package com.example.tkproject.dto;

import com.example.tkproject.model.Transportation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportationDTO {

    private Long id;

    @NotNull(message = "Origin must not be null")
    private LocationDTO origin;

    @NotNull(message = "Destination must not be null")
    private LocationDTO destination;

    /**
     * The type of transportation. Allowed values: "BUS", "FLIGHT", "UBER", "SUBWAY".
     */
    @NotNull(message = "Transportation type is required")
    @Pattern(regexp = "(?i)BUS|FLIGHT|UBER|SUBWAY", message = "Type must be BUS, FLIGHT, UBER or SUBWAY")
    private String type;

    /**
     * A set of integers representing the operating days.
     * Example: [1, 3, 5]. At least one day is required.
     */
    @NotEmpty(message = "Operating days must not be empty")
    private Set<Integer> operatingDays;

    /**
     * Converts a `Transportation` entity into a `TransportationDTO`
     */
    public static TransportationDTO fromEntity(Transportation transportation) {
        return new TransportationDTO(
                transportation.getId(),
                LocationDTO.fromEntity(transportation.getOrigin()),  // Convert origin to DTO
                LocationDTO.fromEntity(transportation.getDestination()),  // Convert destination to DTO
                transportation.getType().toString(),
                transportation.getOperatingDays() != null ? Set.copyOf(transportation.getOperatingDays()) : Set.of()
        );
    }
}
