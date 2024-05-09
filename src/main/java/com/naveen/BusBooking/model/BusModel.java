package com.naveen.BusBooking.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "bus")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//used to solve nested json data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BusModel {
    @Id
private ObjectId id;
    @NotBlank(message = "Bus name cannot be blank")
    private String busName;
    @NotBlank(message = "Bus number cannot be blank")
    private String busNumber;
    @NotBlank(message = "Source name cannot be blank")
    private String source;
    @NotBlank(message = "Destination name cannot be blank")
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private List<Seat> seats;

    @DBRef
    private List<Booking> bookings=new ArrayList<>();;

//    public List<Booking> getBookings() {
//        return bookings;
//    }
}
