package com.gleb.monitoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CarState {

    private String licensePlate;

    @JsonIgnore
    private Instant timestamp;

    private double fuelAmount;

    private GeographicalCoordinates geolocation;

    @JsonIgnore
    private Status stateStatus;

}
