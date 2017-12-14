package com.gleb.monitoring;

import com.gleb.monitoring.dao.CarStateDao;
import com.gleb.monitoring.model.CarState;
import com.gleb.monitoring.model.GeographicalCoordinates;
import com.gleb.monitoring.model.Status;
import com.gleb.monitoring.service.CarStateServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.Instant;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Service extends Assert {

    @Mock
    private CarStateDao carStateDao;

    @InjectMocks
    private CarStateServiceImpl carStateService;

    @Test
    public void testToProvideStatusOk(){
        CarState carState = new CarState("3", Instant.now(), 6, new GeographicalCoordinates(), null);
        when(carStateDao.findFirstByLicensePlateOrderByTimestampDesc("3")).thenReturn(new CarState("test", Instant.now(), 8, new GeographicalCoordinates(), null));
        Status actual = carStateService.provideStatus(carState);
        assertEquals(Status.OK, actual);
    }

    @Test
    public void testToProvideStatusFuelDischarge(){
        CarState carState = new CarState("3", Instant.now(), 6, new GeographicalCoordinates(), null);
        when(carStateDao.findFirstByLicensePlateOrderByTimestampDesc("3")).thenReturn(new CarState("test", Instant.now(), 22, new GeographicalCoordinates(), null));
        Status actual = carStateService.provideStatus(carState);
        assertEquals(Status.FUEL_DISCHARGE, actual);
    }

    @Test
    public void testToProvideStatusFueling(){
        CarState carState = new CarState("3", Instant.now(), 6, new GeographicalCoordinates(), null);
        when(carStateDao.findFirstByLicensePlateOrderByTimestampDesc("3")).thenReturn(new CarState("test", Instant.now(), 1, new GeographicalCoordinates(), null));
        Status actual = carStateService.provideStatus(carState);
        assertEquals(Status.FUELING, actual);
    }



}
