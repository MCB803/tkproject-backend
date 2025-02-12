package com.example.tkproject.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.Set;

@Data
public class TransportationDTO {

    private Long id;

    @NotNull(message = "Origin id must not be null")
    private Long originId;

    @NotNull(message = "Destination id must not be null")
    private Long destinationId;

    /**
     * The type of transportation. For example: "BUS", "FLIGHT", etc.
     * You can validate against a regular expression that only allows specific values.
     * For instance, if you want only "BUS", "FLIGHT", "UBER", "SUBWAY" (case-insensitive),
     * you could use:
     */
    @NotNull(message = "Transportation type is required")
    @Pattern(regexp = "(?i)BUS|FLIGHT|UBER|SUBWAY", message = "Type must be BUS, FLIGHT, UBER or SUBWAY")
    private String type;

    /**
     * A set of integers representing the operating days.
     * For example: [1, 3, 5]. We require at least one operating day.
     */
    @NotEmpty(message = "Operating days must not be empty")
    private Set<Integer> operatingDays;
}
