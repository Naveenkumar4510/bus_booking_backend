package com.naveen.BusBooking.Service;

import com.mongodb.MongoException;
import com.naveen.BusBooking.Exception.BookingException;
import com.naveen.BusBooking.Exception.BusBookingException;
import com.naveen.BusBooking.Repository.BookingRepository;
import com.naveen.BusBooking.Repository.BusRepository;
import com.naveen.BusBooking.Repository.UserRepository;
import com.naveen.BusBooking.model.Booking;
import com.naveen.BusBooking.model.BusModel;
import com.naveen.BusBooking.model.Seat;
import com.naveen.BusBooking.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.beans.Transient;
import java.util.*;

@Service
@Slf4j
public class BookingClass {
    @Autowired
    BookingRepository bookingRepository;
@Autowired
BusService busService;

    @Autowired
UserService userService;

//get all bookings

    public List<Booking> getBookingDetails(){
try{
        return bookingRepository.findAll();
}catch(Exception ex){
    log.error("Failed to get booking details :{} ",ex.getMessage());
    throw new BookingException("Failed to get booking details"+ex.getMessage());
}
    }

    public Booking getBookingDetailsById(ObjectId id){
        try {

            return bookingRepository.findById(id).orElseThrow(() -> new BookingException("Booking not found with ID: " + id));
        }catch (Exception ex)
        {
            log.error("Failed to get booking details by id :{} ",ex.getMessage());
            throw new BookingException("Failed to get booking details by id"+ex.getMessage());
        }
    }



    public Map<String,Object> displayBookingDetailsWithBusInfo(ObjectId bookingId)
{
    try {
        Map<String, Object> result = new HashMap<>();
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            // ObjectId busId=new ObjectId(booking.getBusId());
            UserModel user = booking.getUsers();
            BusModel bus = booking.getBus();
            String busId = booking.getBusId();
            BusModel busModel = busService.getBusById(busId);
            if (busModel != null) {
                result.put("booking", booking);
                result.put("bus", busModel);
            }
            return result;
        }else{
            throw new BookingException("Booking not found with ID: " + bookingId);
        }

    }catch (Exception e){
        log.error("Failed to display booking details :{} ",e.getMessage());
        throw new BookingException("Failed to display booking details"+e.getMessage());
    }
}

//get booking details by username
    public List<Booking> getBookingsByUserName(String userName) {
        try {
            Optional<UserModel> userModelOptional = userService.getUserByName(userName);
            List<Booking> bookings = new ArrayList<>();

            if (userModelOptional.isPresent()) {
                UserModel userModel = userModelOptional.get();

               return  userModel.getBookings();
            }
            else {
                throw new BookingException("User not found with username: " + userName);
            }

        }catch (Exception e){
            log.error("Failed to get booking details by username :{} ",e.getMessage());
            throw new BookingException("Error occurred while fetching booking details"+e.getMessage() );
        }
    }



//creating a UNIQUE booking id
    public String generateUniqueBookingId() {
        // Generate a UUID (Universally Unique Identifier) as the booking ID
        return UUID.randomUUID().toString();
    }

    //create a booking with user and with bus details
    @Transactional
    public Booking createBooking(String userId,String busId,List<String> bookedSeats,String bookingDate){
        try {

            Optional<UserModel> user = userService.getUserById(userId);
            BusModel bus = busService.getBusById(busId);

            String bookingId = generateUniqueBookingId();

            if (user.isPresent() && bus != null) {
                UserModel userModel = user.get();

                List<Integer> unavailableSeats = new ArrayList<>();
                for (String seat : bookedSeats) {
                    int seatNumber = Integer.parseInt(seat);
                    if (!busService.checkSeatAvailability(busId, seatNumber)) {
                        unavailableSeats.add(seatNumber);
                    }
                }
                if (!unavailableSeats.isEmpty()) {
                    throw new RuntimeException("Seats " + unavailableSeats + " are not available on this bus. Please book another seat.");
                }
                // Book seats
                for (String seat : bookedSeats) {
                    int seatNumber = Integer.parseInt(seat);
                    if (!busService.bookSeat(busId, seatNumber)) {
                        throw new RuntimeException("Failed to book seat " + seatNumber + " on bus " + busId);
                    }
                }
                Booking booking = new Booking();
                booking.setBookingId(bookingId);
                booking.setStatus("pending");
                booking.setUserId(userId);
                booking.setBusId(busId);
                booking.setBookedSeats(bookedSeats);
                booking.setBookingDate(bookingDate);
                booking.setUsers(userModel);
                booking.setBus(bus);

                Booking savedBooking = bookingRepository.save(booking);
                userModel.getBookings().add(savedBooking);
                //user.getBookings();
                userService.addUser(userModel);

                bus.getBookings().add(savedBooking);
                busService.addBus(bus);
                return savedBooking;

            } else {
                throw new RuntimeException("User or bus not found");
            }
        }catch (Exception ex){
            log.error("Failed to create booking details :{} ",ex.getMessage());
            throw new BookingException("Unable to create booking"+ex.getMessage());
        }
    }

    // Method to cancel a booked seat
    @Transactional
    public void cancelBookedSeat(ObjectId bookingId, int seatNumber) {
        try {
            // Retrieve the booking associated with the provided bookingId
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingException("Booking not found with ID: " + bookingId));
            if (booking != null) {
                // Update the booking status to indicate cancellation
                booking.setStatus("cancelled");
                bookingRepository.save(booking);

                // Mark the seat as available again
                BusModel bus = booking.getBus();
                if (bus != null) {

                    String busId = booking.getBusId();
                    busService.updateSeatAvailability(busId, seatNumber, true); // Assuming you have a method to update the bus
                } else {
                    throw new RuntimeException("Bus not found for booking ID: " + bookingId);
                }
            } else {
                throw new RuntimeException("Booking not found with ID: " + bookingId);
            }
        }catch (Exception ex){
            log.error("Failed to cancel booking details :{} ",ex.getMessage());
            throw new BookingException("Error occurred while cancelling"+ex.getMessage());
        }
    }

    //method to confirm a seat
    @Transactional
    public Booking confirmBooking(ObjectId bookingId) {
        try{
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            // Update booking status to confirmed
            booking.setStatus("confirmed");
            bookingRepository.save(booking);

            // Retrieve the booked seats
            List<String> bookedSeats = booking.getBookedSeats();

            // Update seat availability for each booked seat
            String busId = booking.getBusId();
            for (String seat : bookedSeats) {
                int seatNumber = Integer.parseInt(seat);
                busService.updateSeatAvailability(busId, seatNumber, false); // Set seat as unavailable
            }

            return booking;
        } else {
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }
    }catch (Exception ex){
            log.error("Failed to confirm booking details :{} ",ex.getMessage());
            throw new BookingException("Error occurred while confirm booking"+ex.getMessage());
        }
    }

}


