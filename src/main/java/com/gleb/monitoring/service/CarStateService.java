package com.gleb.monitoring.service;


import com.gleb.monitoring.model.CarState;
import com.gleb.monitoring.model.Status;

import java.util.List;

public interface CarStateService {
    /**
     * Provides status by comparing with previous car state
     * @param currentState new status from endpoint
     * @return result of comparing
     */
    Status provideStatus(CarState currentState);

    CarState addNewCarState(CarState carState);

    List<CarState> findAllByLicensePlate(String licensePlate);

    void deletePreviousCarState(CarState carState);

    List<CarState> findLatestCarState();


}
