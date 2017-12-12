package com.gleb.monitoring.service;

import com.gleb.monitoring.dao.CarStateDao;
import com.gleb.monitoring.model.CarState;
import com.gleb.monitoring.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.CSS;
import java.time.Instant;
import java.util.List;

@Service
public class CarStateServiceImpl implements CarStateService {

    private final CarStateDao carStateDao;

    @Autowired
    public CarStateServiceImpl(CarStateDao carStateDao) {
        this.carStateDao = carStateDao;
    }

    @Override
    public Status provideStatus(CarState currentState) {
        String licensePlate = currentState.getLicensePlate();
        CarState previousState = carStateDao.findFirstByLicensePlateOrderByTimestampDesc(licensePlate);
        if (previousState == null) {
            return Status.OK;
        }
        return checkDifference(currentState, previousState);
    }

    @Override
    public CarState addNewCarState(CarState carState) {
        carState.setTimestamp(Instant.now());
        return carStateDao.insert(carState);

    }

    @Override
    public List<CarState> findAllByLicensePlate(String licensePlate) {
        return carStateDao.findAllByLicensePlate(licensePlate);
    }

    @Override
    public void deletePreviousCarState(CarState carState) {
        String licensePlate = carState.getLicensePlate();
        CarState previousState = carStateDao.findFirstByLicensePlateOrderByTimestampDesc(licensePlate);
        if (previousState != null){
            carStateDao.deleteByLicensePlate(licensePlate);
        }
    }

    private Status checkDifference(CarState current, CarState previous) {
        double fuelAmountCurentState = current.getFuelAmount();
        double fuelAmountPreviosState = previous.getFuelAmount();
        if (fuelAmountPreviosState - fuelAmountCurentState > 4) {
            return Status.FUEL_DISCHARGE;
        }
        if (fuelAmountPreviosState == fuelAmountCurentState) {
            return Status.OK;
        }
        if (fuelAmountPreviosState < fuelAmountCurentState ) {
            return Status.FUELING;
        }
        return Status.OK;
    }



}
