package com.gleb.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeographicalCoordinates {

    private double lat;
    private double lon;

    @Override
    public String toString() {
        return  lat +
                "; " + lon;
    }
}
