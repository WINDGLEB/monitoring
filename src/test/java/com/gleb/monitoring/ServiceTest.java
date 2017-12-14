package com.gleb.monitoring;

import com.gleb.monitoring.dao.CarStateDao;
import com.gleb.monitoring.model.CarState;
import com.gleb.monitoring.model.GeographicalCoordinates;
import com.gleb.monitoring.model.Status;
import com.gleb.monitoring.service.CarStateService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest extends Assert {

    @MockBean
    private CarStateDao carStateDao;

    @Autowired
    private CarStateService carStateService;

    @Test
    public void testToProvideStatusOk(){
        CarState carState = new CarState("3", Instant.now(), 6, new GeographicalCoordinates(), null);
        when(carStateDao.findFirstByLicensePlateOrderByTimestampDesc("3")).thenReturn(new CarState("test", Instant.now(), 8, new GeographicalCoordinates(), null));
        Status actual = carStateService.provideStatus(carState);
        assertEquals(Status.OK, actual);
    }

    @Test
    public void testToProvideStatusOkIfPreviousNull(){
        CarState carState = new CarState("3", Instant.now(), 6, new GeographicalCoordinates(), null);
        when(carStateDao.findFirstByLicensePlateOrderByTimestampDesc("3")).thenReturn(null);
        Status actual = carStateService.provideStatus(carState);
        assertEquals(Status.OK, actual);
    }

    @Test
    public void testToProvideStatusOkIfPreviousEqualsCurrent(){
        CarState carState = new CarState("3", Instant.now(), 6, new GeographicalCoordinates(), null);
        when(carStateDao.findFirstByLicensePlateOrderByTimestampDesc("3")).thenReturn(new CarState("test", Instant.now(), 6, new GeographicalCoordinates(), null));
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

    @Test
    public  void testToFindLatestCarState(){
        List<CarState> mockCarStates = new ArrayList<>();
        mockCarStates.add(new CarState("ABC", null, 2, new GeographicalCoordinates(), null));
        mockCarStates.add(new CarState("ABC", null, 6, new GeographicalCoordinates(), null));
        mockCarStates.add(new CarState("ABC1", null, 3, new GeographicalCoordinates(), null));
        mockCarStates.add(new CarState("ABC1", null, 6, new GeographicalCoordinates(), null));

        List<CarState> expectedCarStates =  new ArrayList<>();
        expectedCarStates.add(new CarState("ABC1", null, 3, new GeographicalCoordinates(), null));
        expectedCarStates.add(new CarState("ABC", null, 2, new GeographicalCoordinates(), null));

        when(carStateDao.findAllByOrderByTimestampDesc()).thenReturn(mockCarStates);
        List<CarState> actualListCarStates = carStateService.findLatestCarState();

        assertEquals(expectedCarStates, actualListCarStates);
    }

}
