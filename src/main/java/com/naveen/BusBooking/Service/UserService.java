package com.naveen.BusBooking.Service;

import com.naveen.BusBooking.Exception.UserException;
import com.naveen.BusBooking.Repository.UserRepository;
import com.naveen.BusBooking.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.print.MultiDocPrintService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;


    //get all user

    public List<UserModel> getAllUser() {
        try {
            return userRepository.findAll();
        } catch (Exception ex) {
            log.error("Failed to get all user details :{} ",ex.getMessage());
           throw new UserException("Failed to get all users"+ex.getMessage());
        }
    }

    //get user by id
    public Optional<UserModel> getUserById(String id)
    {
        try {
            return userRepository.findUserById(id);
        }catch (Exception ex){
            log.error("Failed to get all user details by id :{} ",ex.getMessage());
            throw new UserException("Failed to get user by id"+ex.getMessage());
        }
    }
//get user by usernname
    public Optional<UserModel> getUserByName(String name) {
        try {
            return userRepository.findByUsername(name);
        }catch (Exception ex){
            log.error("Failed to get all user by name details :{} ",ex.getMessage());
            throw new UserException("Failed to get user by it username"+ex.getMessage());
        }
    }

    //add a user
    public UserModel addUser(UserModel userModel) {
        try {
            return userRepository.save(userModel);
        } catch (Exception ex) {
           // ex.printStackTrace();
            log.error("Failed to add user details :{} ",ex.getMessage());
            throw new UserException("Failed to add user details"+ex.getMessage());
        }
    }

    //delete user
    public boolean deleteUser(ObjectId id) {
        try {
            userRepository.deleteUserById(id);
            return true;
        } catch (Exception e) {
            // Log the error or handle it as needed
            log.error("Failed to delete user details :{} ",e.getMessage());
            throw new UserException("Failed to delete user");
        }
    }

    //update userName
    public boolean UpdateUserName(String username, UserModel userModel) {
        try {


            Optional<UserModel> existingUser = userRepository.findByUsername(username);
            // Declare user outside the if block
            if (existingUser.isPresent()) {
                UserModel user = existingUser.get();

                user.setUsername(userModel.getUsername());
                user.setPassword(userModel.getPassword());

                userRepository.save(user);
                return true;// Save user if it exists
            } else {
                // Handle case where user does not exist
                // This might involve creating a new user or returning an error
                // For now, let's just log that the user does not exist
                System.out.println("User does not exist with username: " + username);
                return false;
            }
        }catch (Exception ex){
            log.error("Failed to update user by username :{} ",ex.getMessage());
            throw new UserException("Failed to update user by username"+ex.getMessage());
        }
        // Return existingUser regardless of whether it exists or not

    }

    //update user
    public boolean updateUser(String id, UserModel updateduser) {
        Optional<UserModel> existingUser = userRepository.findUserById(id);
        if (existingUser.isPresent()) {
            UserModel user = existingUser.get();
            user.setUsername(updateduser.getUsername());
            user.setPassword(updateduser.getPassword());
            user.setEmail(updateduser.getEmail());
            user.setUserid(updateduser.getUserid());
            user.setRole(updateduser.getRole());

            try {
                userRepository.save(user);
                return true;
            } catch (Exception ex) {
                log.error("Failed to update user details :{} ",ex.getMessage());
                throw new UserException("Failed to update user");
            }

        }
        else {
            return false;
        }
        }

        //update specific user details

        public Optional<UserModel> patchUser(@PathVariable String id, Map<String,Object >updates){
        try {

            Optional<UserModel> existingUser = userRepository.findUserById(id);
            if (existingUser.isPresent()) {
                UserModel updateUser = existingUser.get();

                if (updates != null) {
                    updates.forEach((key, value) -> {
                        switch (key) {
                            case "userid":
                                updateUser.setUserid((String) value);
                                break;
                            case "userName":
                                updateUser.setUsername((String) value);
                                break;
                            case "password":
                                updateUser.setPassword((String) value);
                                break;
                            case "email":
                                updateUser.setEmail((String) value);
                                break;
                            case "role":
                                updateUser.setRole((String) value);
                                break;
                            default:
                                break;
                        }
                    });
                    userRepository.save(updateUser);
                    return Optional.of(updateUser);

                } else {
                    return Optional.empty();
                }
            }
        }catch (Exception ex){
            log.error("Failed to update user details :{} ",ex.getMessage());
            throw new UserException("Failed to updated specific details"+ex.getMessage());
        }
        return Optional.empty();
        }

}

