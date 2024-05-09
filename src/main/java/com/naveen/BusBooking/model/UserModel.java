package com.naveen.BusBooking.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

//used to solve nested json data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserModel {

    @Id
    private ObjectId id;

    @NotNull
    private String userid;

    @NotBlank(message = "Username is required")
    @NotNull(message = "Username cannot be null")
    private String username;
    @Size(min = 6,message = "Password must be atleast 6 characters")
    private String password;
    @Email
    private String email;
    @NotBlank(message = "Role is Required")
    private String role;



    @DBRef
    @JsonIgnore
    private List<Booking> bookings=new
            ArrayList<>();

//    public List<Booking> getBookings() {
//        if (bookings == null) {
//            bookings = new ArrayList<>();
//        }
//        return bookings;
//    }
}
