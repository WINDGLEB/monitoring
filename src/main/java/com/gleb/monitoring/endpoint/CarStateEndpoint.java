package com.gleb.monitoring.endpoint;

import com.gleb.monitoring.model.CarState;
import com.gleb.monitoring.model.GeographicalCoordinates;
import com.gleb.monitoring.model.Status;
import com.gleb.monitoring.service.CarStateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CarStateEndpoint {

    private final CarStateServiceImpl carStateService;

    @Autowired
    public CarStateEndpoint(CarStateServiceImpl carStateService) {
        this.carStateService = carStateService;
    }

    @RequestMapping(value = "/state", method = RequestMethod.POST)
    public CarState updateCarState(@RequestBody CarState carState) {
        Status status = carStateService.provideStatus(carState);
        carState.setStateStatus(status);
        //carStateService.deletePreviousCarState(carState);
        carStateService.addNewCarState(carState);
        return carState;
    }
}
