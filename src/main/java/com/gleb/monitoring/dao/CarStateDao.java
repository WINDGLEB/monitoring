package com.gleb.monitoring.dao;

import com.gleb.monitoring.model.CarState;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CarStateDao extends MongoRepository<CarState, String> {
    @Override
    <T extends CarState> T insert(T carState);

    List<CarState> findAllByLicensePlate(String licensePlate);

    List<CarState> findAllByTimestamp(Instant time);

    CarState findFirstByLicensePlateOrderByTimestampDesc(String licensePlate);

}
