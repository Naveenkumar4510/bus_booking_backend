package com.naveen.BusBooking.Controller;

import com.naveen.BusBooking.Exception.BusBookingException;
import com.naveen.BusBooking.Service.BusService;
import com.naveen.BusBooking.model.BusModel;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bus")
@CrossOrigin(origins = "*")
public class BusController {

    @Autowired
    BusService busService;
//default http request at home page:localhsot:8080/api/bus
    //getting the all bus at default get request at default http:
@GetMapping
public ResponseEntity<List<BusModel>> getAllBuses() {
    try {
        List<BusModel> buses = busService.allBuses();
        return ResponseEntity.ok(buses);
    } catch (BusBookingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
}

//search the bus using source and destination with their arrival and departure time
    @GetMapping("/searchBus")
    public ResponseEntity<List<BusModel>> searchBuses(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String departureTime,
            @RequestParam String arrivalTime
    ) {
    try{
        List<BusModel> availableBuses = busService.searchBuses(source, destination, departureTime,arrivalTime);
        return ResponseEntity.ok(availableBuses);
    }catch (BusBookingException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }

    }
    //getting bus number at /busNumber endpoint
    @GetMapping("/{busNumber}")
    public ResponseEntity<Optional<BusModel>> getSingleBus(@PathVariable String busNumber) {
        try {
            Optional<BusModel> bus = busService.getBusByNumber(busNumber);
            return ResponseEntity.ok(bus);
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Optional.empty()); // Return an empty optional with error status
        }
    }

    //getting bus source at /source/{} endpoint
    @GetMapping("/source/{source}")
    public ResponseEntity<List<BusModel>> getBusBySource(@PathVariable String source) {
        try {
            List<BusModel> buses = busService.getBusBySource(source);
            return ResponseEntity.ok(buses);
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList()); // Return an empty list with error status
        }
    }

    //getting bus destination at /destination/{} endpoint
    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<BusModel>> getBusByDestination(@PathVariable String destination) {
        try {
            List<BusModel> buses = busService.getBusDestination(destination);
            return ResponseEntity.ok(buses);
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    //getting bus id at /id/{} endpoint
    @GetMapping("/id/{id}")
    public ResponseEntity<BusModel> getSingleBusById(@PathVariable String id) {
        try {
            BusModel bus = busService.getBusById(id);
            if (bus != null) {
                return ResponseEntity.ok(bus);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //getting bus name at /byName/{} endpoint
    @GetMapping("/byName/{busName}")
    public ResponseEntity<Optional<BusModel>> getBusByName(@PathVariable String busName) {
        try {
            Optional<BusModel> bus = busService.getBusByName(busName);
            return ResponseEntity.ok(bus);
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Optional.empty());
        }
    }

//  //performing inserting bus details at / endpoint
    @PostMapping
    public ResponseEntity<?> addBus(@Valid  @RequestBody BusModel busModel) {
        try {
            BusModel createdBus = busService.addBus(busModel);
            return new ResponseEntity<>(createdBus, HttpStatus.CREATED);
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add bus: " + ex.getMessage());
        }
    }

    //updating full bus details at /{id} endpoint
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBus(@PathVariable String id, @Valid @RequestBody BusModel updatedBus) {
        try {
            boolean updated = busService.updateBus(id, updatedBus);
            if (updated) {
                BusModel updatedEntity = busService.getBusById(id);
                return ResponseEntity.ok(updatedEntity); // Return ResponseEntity<BusModel>
            } else {
                return ResponseEntity.notFound().build(); // Bus not found
            }
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update bus: " + ex.getMessage());
        }
    }

    //updating required bus details at /{id} endpoint
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchBus(@PathVariable("id") ObjectId id, @Valid @RequestBody Map<String, Object> updates) {
        try {
            Optional<BusModel> patchedBus = busService.patchBus(id, updates);
            if (patchedBus.isPresent()) {
                return ResponseEntity.ok(patchedBus.get()); // Return patched bus
            } else {
                return ResponseEntity.notFound().build(); // Bus not found
            }
        }catch(BusBookingException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to patch bus"+ex.getMessage());
        }
    }

    //deleting bus  details by id at /{id} endpoint
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBus(@PathVariable ObjectId id) {
        try {
            boolean deleted = busService.deleteBus(id);
            if (deleted) {
                return ResponseEntity.ok().body("Bus with ID " + id + " deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch(BusBookingException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete bus"+ex.getMessage());
        }
    }

    //checking the seat number at bus is available or not busId/busNumber/availability endpoint
    @GetMapping("/{busId}/seats/{seatNumber}/availability")
    public ResponseEntity<String> checkSeatAvailability(@PathVariable String busId, @PathVariable int seatNumber) {
        try {
            // Check seat availability using your service method
            boolean isAvailable = busService.checkSeatAvailability(busId, seatNumber);

            // Prepare the response based on seat availability
            if (isAvailable) {
                return ResponseEntity.ok().body("Seat number " + seatNumber + " is available.");
            } else {
                return ResponseEntity.ok().body("Seat number " + seatNumber + " is not available.");
            }
        } catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to check seat availability: " + ex.getMessage());
        }
    }


    //Book the seat number at busId/busSeat endpoint

    @PostMapping("/{busId}/bookSeat/{seatNumber}")
    public ResponseEntity<String> bookSeat(@PathVariable String busId, @PathVariable int seatNumber) {
        try {
            //ObjectId objectId = new ObjectId(busId);
            boolean booked = busService.bookSeat(busId, seatNumber);
            if (booked) {
                return ResponseEntity.ok().body("Seat " + seatNumber + " booked successfully for bus " + busId);
            } else {
                return ResponseEntity.badRequest().body("Seat " + seatNumber + " is not available or booking failed for bus " + busId);
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid ObjectId format
            return ResponseEntity.badRequest().body("Invalid busId format");
        }catch (BusBookingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to book seat: " + ex.getMessage());
        }
    }

    //getting available seats at /busId/availableBus endpoint
    @GetMapping("/{busId}/availableSeats")
    public ResponseEntity<List<Integer>> getAvailableSeats(@PathVariable String busId) {
    try {
        ObjectId objectId = new ObjectId(busId);
        List<Integer> availableSeats = busService.getAvailableSeats(objectId);
        return ResponseEntity.ok(availableSeats);
    }catch (BusBookingException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
    }

    //getting booked seats list at /busId/bookedSeats endpoint
    @GetMapping("/{busId}/bookedSeats")
    public ResponseEntity<List<Integer>> getBookedSeats(@PathVariable String busId) {
    try {
        ObjectId objectId = new ObjectId(busId);
        List<Integer> bookedSeats = busService.getBookedSeats(objectId);
        return ResponseEntity.ok(bookedSeats);
    }catch (BusBookingException ex)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
    }

}
