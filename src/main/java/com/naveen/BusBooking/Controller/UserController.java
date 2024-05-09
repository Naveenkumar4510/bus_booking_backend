package com.naveen.BusBooking.Controller;


import com.naveen.BusBooking.Exception.UserException;
import com.naveen.BusBooking.Repository.UserRepository;
import com.naveen.BusBooking.Service.UserService;
import com.naveen.BusBooking.model.UserModel;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    //@Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;


//get all user
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<UserModel>> allUser(){
        try {
            List<UserModel> user = userService.getAllUser();
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    //get user by id
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/id/{id}")
    public ResponseEntity<Optional<UserModel>> getUserById(@PathVariable String id){
        try {
            Optional<UserModel> user = userService.getUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //add user
    @PostMapping
    public ResponseEntity<UserModel> addUser(@Valid @RequestBody UserModel userModel)
    {
        try {

            UserModel createuser = userService.addUser(userModel);
            return new ResponseEntity<>(createuser, HttpStatus.CREATED);
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //delete user
    @DeleteMapping("id/{id}")
    public  ResponseEntity<String> deleteUser(@PathVariable ObjectId id){
        try {

            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.noContent().build(); // Return 204 if deletion is successful
            } else {
                return ResponseEntity.notFound().build(); // Return 404 if resource is not found
            }
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //update user by username

    @PutMapping("/name/{name}") // Corrected the path variable annotation
    public ResponseEntity<?> updateUserName(@PathVariable String name, @Valid @RequestBody UserModel userModel) {
        try {
            boolean userUpdated = userService.UpdateUserName(name, userModel); // Renamed method to follow conventions

            if (userUpdated) {
                Optional<UserModel> userModelOptional = userService.getUserByName(name);

                if (userModelOptional.isPresent()) {
                    return ResponseEntity.ok(userModelOptional.get()); // Return updated user if found
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve updated user"); // Handle case where user was updated but couldn't be retrieved
                }
            } else {
                return ResponseEntity.notFound().build(); // User not found
            }
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //update user
    @PutMapping("id/{id}")
public  ResponseEntity<?> updateUser(@PathVariable String id,@Valid  @RequestBody UserModel userModel){
        try {
            boolean updateuser = userService.updateUser(id, userModel);
            if (updateuser) {
                Optional<UserModel> userUpdated = userService.getUserById(id);
                return ResponseEntity.ok(userUpdated);
            } else {
                return ResponseEntity.notFound().build(); // User not found
            }
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
}

//update specific user details
@PatchMapping ("id/{id}")
public ResponseEntity<?> updateSpecificUserDetails(@PathVariable String id,@Valid @RequestBody Map<String,Object>updates){
        try {

            Optional<UserModel> userModel = userService.patchUser(id, updates);
            if (userModel.isPresent()) {
                return ResponseEntity.ok(userModel);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (UserException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

}

//register the user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel userModel) {
        try {
            // Check if the username already exists

            if (userRepository.findByUsername(userModel.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken. Please try again");
            }

            // Encode the password before saving
            userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));

            // Save the user
            userRepository.save(userModel);

            // Return a success response
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e) {
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
