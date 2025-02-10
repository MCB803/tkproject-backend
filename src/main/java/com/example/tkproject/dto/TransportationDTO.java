package com.example.tkproject.dto;

import lombok.Data;
import java.util.Set;

@Data
public class TransportationDTO {
    private Long id;
    private Long originId;
    private Long destinationId;
    /**
     * The type of transportation. For example: "flight", "bus", etc.
     * This value will be converted (to uppercase) to match the TransportationType enum.
     */
    private String type;
    /**
     * A set of integers representing the operating days.
     * For example: [1, 3, 5] (you can define your own meaning for each day)
     */
    private Set<Integer> operatingDays;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}