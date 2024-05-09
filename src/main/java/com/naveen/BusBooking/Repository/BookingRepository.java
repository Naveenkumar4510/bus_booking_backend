package com.naveen.BusBooking.Repository;

import com.naveen.BusBooking.model.Booking;
import com.naveen.BusBooking.model.UserModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Booking,ObjectId> {
List<Booking> findByUserId(String userId);
    Optional<Booking> findByUserIdAndBusId(String userId, String busId);




}
