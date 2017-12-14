package com.gleb.monitoring.model;

import com.vaadin.tapio.googlemaps.client.LatLon;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeographicalCoordinates extends LatLon {


    @Override
    public String toString() {
        return  getLat() +
                "; " + getLon();
    }
}
