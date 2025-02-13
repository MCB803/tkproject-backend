package com.example.tkproject.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

public class TransportationRequestDTO {

    @NotNull(message = "Origin must not be empty")
    private Long originId;

    @NotNull(message = "Destination must not be empty")
    private Long destinationId;

    @NotNull(message = "Transportation type is required")
    @Pattern(regexp = "(?i)BUS|FLIGHT|UBER|SUBWAY", message = "Type must be BUS, FLIGHT, UBER or SUBWAY")
    private String type;

    @NotNull(message = "Operating days must not be empty")
    private Set<Integer> operatingDays;

    public TransportationRequestDTO() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Integer> getOperatingDays() {
        return operatingDays;
    }

    public void setOperatingDays(Set<Integer> operatingDays) {
        this.operatingDays = operatingDays;
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }
}
