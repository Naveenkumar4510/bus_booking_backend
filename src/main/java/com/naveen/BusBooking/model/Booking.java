package com.naveen.BusBooking.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//used to solve nested json data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Booking {

    @Id
    private ObjectId id;
    @NotNull(message = "Booking ID cannot be null")
    private String bookingId;
    @NotNull(message = "User ID cannot be null")
    private String userId;
    @NotNull(message = "Bus ID cannot be null")
    private String busId;
    @NotBlank(message = "status cannot be blank")
    private String status;
    private List<String> bookedSeats;

    private String bookingDate;

@DBRef

private UserModel users;

    @DBRef
    @JsonIgnore
    private BusModel bus;


}
