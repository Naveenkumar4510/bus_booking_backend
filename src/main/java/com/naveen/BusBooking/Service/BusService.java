package com.naveen.BusBooking.Service;

import com.mongodb.MongoException;
import com.mongodb.MongoSocketWriteException;
import com.naveen.BusBooking.Exception.BusBookingException;
import com.naveen.BusBooking.Exception.SSLHandshakeException;
import com.naveen.BusBooking.Repository.BusRepository;
import com.naveen.BusBooking.model.BusModel;
import com.naveen.BusBooking.model.Seat;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class BusService {


    @Autowired
    BusRepository busRepository;

    //Listing all buses
    public List<BusModel> allBuses(){
        try{
            return busRepository.findAll();
        }
        catch(Exception ex)
        {
            log.error("Failed to get all bus details :{} ",ex.getMessage());
            throw new BusBookingException("Failed to get all bus details"+ex.getMessage());
        }

    }
    //get Bus Details By Number
    public Optional<BusModel> getBusByNumber(String busNumber){
        try{
            return busRepository.findBusByBusNumber(busNumber);
        }catch(Exception ex)
        {
            log.error("Failed to get bus  details by busNumber :{} ",ex.getMessage());
            throw new BusBookingException("Failed to get  bus by  number: "+ex.getMessage());
        }

    }

    //get bus by BusName
    public Optional<BusModel> getBusByName(String busName){
        try {
            return busRepository.findBusByBusName(busName);
        }
        catch (Exception ex){
            log.error("Failed to get  bus details by name :{} ",ex.getMessage());
            throw new BusBookingException("Failed to get  bus by  its name: "+ex.getMessage());
        }

    }


//get bus by id
    public BusModel getBusById(String id){

        try {
            return busRepository.findBusById(id).orElse(null);
        }
        catch (Exception ex){
            log.error("Failed to get bus  details by id :{} ",ex.getMessage());
            throw new BusBookingException("Failed to get  bus by  its id: "+ex.getMessage());
        }
    }
    //get bus details by bus source
    public List<BusModel> getBusBySource(String busSource){

        try {
            return busRepository.findBusBySource(busSource);
        }
        catch (Exception ex){
            ex.printStackTrace();
            log.error("Failed to get all all bus details by its source :{} ",ex.getMessage());
            throw new BusBookingException("Failed to get  bus by  its Source: "+ex.getMessage());
        }
    }
//get bus details by bus destination
    public List<BusModel> getBusDestination(String busDestination){

        try {
            return busRepository.findBusByDestination(busDestination);
        }
        catch (Exception ex){
            log.error("Failed to get all bus details by its destination :{} ",ex.getMessage());
            throw new BusBookingException("Failed to get  bus by Destination: "+ex.getMessage());
        }
    }
    public List<BusModel> searchBuses(String source, String destination, String departureTime, String arrivalTime) {
        try {
            return busRepository.findBySourceAndDestinationAndDepartureTimeAfterAndArrivalTimeBefore(
                    source, destination, departureTime, arrivalTime);
        } catch (Exception ex) {
            log.error("Failed to search bus details :{} ",ex.getMessage());
            throw new BusBookingException("Failed to search for buses: " + ex.getMessage());
        }
    }



    //insert the bus details

    public BusModel addBus(BusModel busModel){

        try{
            //someMongoDBOperation();
            return busRepository.save(busModel);
        }catch(MongoException ex)
        {
            log.error("Failed to add bus details :{} ",ex.getMessage());
            throw new BusBookingException("Failed to add bus"+ex.getMessage());
        }

    }

    //delete the bus by id
    public boolean deleteBus(ObjectId id){
        try{
            busRepository.deleteById(id);
            return  true;
        }
        catch(MongoException ex)
        {
            log.error("Failed to delete bus details :{} ",ex.getMessage());
            throw new BusBookingException("Failed to delete bus"+ex.getMessage());

        }

    }
    public boolean updateBus(String id, BusModel updatedBus) {
        Optional<BusModel> existingBusOptional = busRepository.findBusById(id);
        if (existingBusOptional.isPresent()) {
            BusModel existingBus = existingBusOptional.get();
            existingBus.setBusName(updatedBus.getBusName());
            existingBus.setBusNumber(updatedBus.getBusNumber());
            existingBus.setSource(updatedBus.getSource());
            existingBus.setDestination(updatedBus.getDestination());
            existingBus.setArrivalTime(updatedBus.getArrivalTime());
            existingBus.setDepartureTime(updatedBus.getDepartureTime());
            // Update other fields similarly
try{
    busRepository.save(existingBus);
    return true; // Update successful
}
catch (DataAccessException ex) {
    log.error("Failed to update bus details :{} ",ex.getMessage());
    throw  new BusBookingException("failed to update bus"+ex.getMessage());
}

        } else {
            return false; // Bus not found
        }
    }

    public boolean updateSeatAvailability(String busId, int seatNumber, boolean available) {
        Optional<BusModel> busOptional = busRepository.findBusById(busId);
        if (busOptional.isPresent()) {
            BusModel bus = busOptional.get();
            List<Seat> seats = bus.getSeats();
            for (Seat seat : seats) {
                if (seat.getSeatNumber() == seatNumber) {
                    seat.setAvailable(available);
                    busRepository.save(bus);
                    return true; // Successfully updated seat availability
                }
            }
            // Seat not found
            throw new RuntimeException("Seat " + seatNumber + " not found on bus " + busId);
        } else {
            // Bus not found
            throw new RuntimeException("Bus not found with ID: " + busId);
        }
    }

    public Optional<BusModel> patchBus(ObjectId id, Map<String, Object> updates) {
        Optional<BusModel> busOptional = busRepository.findById(id);
        if (busOptional.isPresent()) {
            BusModel bus = busOptional.get();
            // Apply updates from the map
            if (updates != null) {
                updates.forEach((key, value) -> {
                    switch (key) {
                        case "busName":
                            bus.setBusName((String) value);
                            break;
                        case "busNumber":
                            bus.setBusNumber((String) value);
                            break;
                        // Add cases for other fields to update
                        case "source":
                            bus.setSource((String) value);
                            break;
                        case "destination":
                            bus.setDestination((String) value);
                            break;
                        case "departureTime":
                            bus.setDepartureTime((String) value);
                            break;
                        case "arrivalTime":
                            bus.setArrivalTime((String) value);
                            break;
                        default:
                            // Handle unexpected key
                            // For example, log an error or throw an exception
                            break;
                    }
                });
                // Save the updated bus
                try{
                    busRepository.save(bus);
                    return Optional.of(bus);
                }
                catch(DataAccessException ex){
                    log.error("Failed to update bus  details :{} ",ex.getMessage());
                    throw new BusBookingException("failed to update "+ex.getMessage());
                    //return Optional.empty();//update failed due to database error
                }


            } else {
                // Handle null updates map
                // For example, log an error or throw an exception
                return Optional.empty();
            }
        } else {
            return Optional.empty(); // Bus not found
        }
    }

//checking how many seats are available

    public boolean checkSeatAvailability(String busId, int seatNumber) {
        try {
            //ObjectId objectId = new ObjectId(busId); // Convert string to ObjectId
            Optional<BusModel> busOptional = busRepository.findBusById(busId);
            if (busOptional.isPresent()) {
                BusModel bus = busOptional.get();
                List<Seat> seats = bus.getSeats();
                for (Seat seat : seats) {
                    if (seat.getSeatNumber() == seatNumber) {
                        return seat.isAvailable();
                    }
                }
            }
        }catch(IllegalArgumentException ex)
        {
            log.error("Failed to check seat availability  :{} ",ex.getMessage());
           throw new BusBookingException("Invalid busId format");
        }
        return false; // Seat not found or bus not found
    }
//book the seat
public boolean bookSeat(String busId,int seatNumber){
        try {
           // ObjectId objectId = new ObjectId(busId);
            Optional<BusModel> busOptional = busRepository.findBusById(busId);
            if (busOptional.isPresent()) {
                BusModel bus = busOptional.get();
                List<Seat> seats = bus.getSeats();
                for (Seat seat : seats) {
                    if (seat.getSeatNumber() == seatNumber && seat.isAvailable()) {
                        seat.setAvailable(false);
                        busRepository.save(bus);
                        return true;
                    }
                }
            }
        }catch(IllegalArgumentException ex)
        {
            log.error("Failed to book a seat :{} ",ex.getMessage());
           throw new BusBookingException("Unable to book seat");
        }
        return false;
}

//get the available seats in bus
public List<Integer> getAvailableSeats(ObjectId busId) {
    try {
        List<Integer> availableSeats = new ArrayList<>();
        Optional<BusModel> busOptional = busRepository.findById(busId);
        if (busOptional.isPresent()) {
            BusModel bus = busOptional.get();
            List<Seat> seats = bus.getSeats();
            for (Seat seat : seats) {
                if (seat.isAvailable()) {
                    availableSeats.add(seat.getSeatNumber());
                }
            }
        }
        return availableSeats;
    } catch (IllegalArgumentException ex) {
        log.error("Failed to get available seats details :{} ",ex.getMessage());
        throw new BusBookingException("Unable to get the available seats");
    }
}


    //get the booked seats in bus
    public List<Integer> getBookedSeats(ObjectId busId) {
        try {
            List<Integer> bookedSeats = new ArrayList<>();
            Optional<BusModel> busOptional = busRepository.findById(busId);
            if (busOptional.isPresent()) {
                BusModel bus = busOptional.get();
                List<Seat> seats = bus.getSeats();
                for (Seat seat : seats) {
                    if (!seat.isAvailable()) {
                        bookedSeats.add(seat.getSeatNumber());
                    }
                }
            }
            return bookedSeats;
        } catch (IllegalArgumentException ex) {
            log.error("Failed to get booked seats details :{} ",ex.getMessage());
            throw new BusBookingException("Unable to get the booked seats");
            //return new ArrayList<>();
        }
    }


}