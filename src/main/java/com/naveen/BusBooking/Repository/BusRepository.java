package com.naveen.BusBooking.Repository;

import com.naveen.BusBooking.model.BusModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends MongoRepository<BusModel, ObjectId> {
    Optional<BusModel> findBusByBusNumber(String busNumber);

    Optional<BusModel> findBusByBusName(String busName);

    Optional<BusModel> findBusById(String id);

    List<BusModel> findBusBySource(String source);
    List<BusModel> findBusByDestination(String destination);

    List<BusModel> findBySourceAndDestinationAndDepartureTimeAfterAndArrivalTimeBefore(String source,String destination,String departureTime, String arrivalTime);
}
