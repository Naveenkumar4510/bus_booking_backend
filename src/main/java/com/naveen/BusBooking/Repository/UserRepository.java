package com.naveen.BusBooking.Repository;

import com.naveen.BusBooking.model.UserModel;
import org.apache.catalina.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findUserById(String id);

    void  deleteUserById(ObjectId id);
}
