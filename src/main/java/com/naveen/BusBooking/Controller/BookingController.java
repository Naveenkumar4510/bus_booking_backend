package com.naveen.BusBooking.Controller;


import com.naveen.BusBooking.Exception.BookingException;
import com.naveen.BusBooking.Service.BookingClass;
import com.naveen.BusBooking.Service.BusService;
import com.naveen.BusBooking.Service.UserService;
import com.naveen.BusBooking.model.Booking;
import com.naveen.BusBooking.model.BusModel;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/booking")

public class BookingController {

    @Autowired
    BookingClass bookingService;
    @Autowired
    UserService userService;
    @Autowired
    BusService busService;

//getting all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBooking() {
        try {
            List<Booking> booking = bookingService.getBookingDetails();
            return ResponseEntity.ok(booking);
        }catch (BookingException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //fetching booking details by Object id
    @GetMapping("id/{id}")

    public ResponseEntity<Booking> getBookingById(@PathVariable ObjectId id) {
        try {
            Booking booking = bookingService.getBookingDetailsById(id);
            return ResponseEntity.ok(booking);
        }catch (BookingException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //fetching booking details by booking id

    @GetMapping("/bid/{bookingId}")
    public ResponseEntity<?> getBookingDetailsWithBusInfo(@PathVariable String bookingId) {
        try {
            ObjectId objectId = new ObjectId(bookingId);
            Map<String, Object> bookingDetailsWithBusInfo = bookingService.displayBookingDetailsWithBusInfo(objectId);
            if (!bookingDetailsWithBusInfo.isEmpty()) {
                return ResponseEntity.ok(bookingDetailsWithBusInfo);
            } else {
                return ResponseEntity.notFound().build(); // Booking or bus not found
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid booking ID"); // Handle invalid ObjectId
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error"); // Handle other exceptions
        }
    }

    //fetching booking details by username
    @GetMapping("/name/{userName}")
    public ResponseEntity<?> getBookingsByUserName(@PathVariable String userName) {
        try {
            List<Booking> bookings = bookingService.getBookingsByUserName(userName);
            if (!bookings.isEmpty()) {
                return ResponseEntity.ok(bookings);
            } else {
                return ResponseEntity.notFound().build(); // No bookings found for the user
            }
        }catch (BookingException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching bookings by username");
        }
    }

//create booking
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestParam String userId,
                                           @RequestParam String busId,
                                           @RequestParam List<String> bookedSeats,
                                           @RequestParam String bookingDate) {
        try {
            Booking createdBooking = bookingService.createBooking(userId, busId, bookedSeats, bookingDate);
            return ResponseEntity.ok(createdBooking);
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //cancel booking
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelBooking(@RequestParam ObjectId bookingId, @RequestParam int seatNumber) {
        try {
            bookingService.cancelBookedSeat(bookingId, seatNumber);
            return ResponseEntity.ok("Seat cancellation successful");
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    //confirm booking
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestParam ObjectId bookingId, @RequestParam int seatNumber) {
        try {
            Booking booking = bookingService.confirmBooking(bookingId);
            if (booking != null) {
                // Update seat availability
                busService.updateSeatAvailability(booking.getBusId(), seatNumber, false);
                return ResponseEntity.ok("Booking confirmed successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found with ID: " + bookingId);
            }
        } catch (BookingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
