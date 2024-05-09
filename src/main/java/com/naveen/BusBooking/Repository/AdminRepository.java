package com.naveen.BusBooking.Repository;

import com.naveen.BusBooking.model.AdminModel;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<AdminModel,ObjectId> {
    Optional<AdminModel> findByAdminName(String adminName);
}
