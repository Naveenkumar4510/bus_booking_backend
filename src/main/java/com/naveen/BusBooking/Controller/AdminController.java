package com.naveen.BusBooking.Controller;

import com.naveen.BusBooking.Exception.AdminException;
import com.naveen.BusBooking.Repository.AdminRepository;
import com.naveen.BusBooking.Service.AdminService;
import com.naveen.BusBooking.model.AdminModel;
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
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    AdminRepository adminRepository;

    //including password encoder
    @Autowired
    PasswordEncoder passwordEncoder;


    //get all admins
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")//checking hasRole of admin
    public ResponseEntity<List<AdminModel>> getAllAdmins() {
        try {
            List<AdminModel> admins = adminService.getAllAdmins();
            return new ResponseEntity<>(admins, HttpStatus.OK);
        }catch(AdminException ex){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AdminModel> getAdminById(@PathVariable ObjectId id) {
        try {
            Optional<AdminModel> admin = adminService.getAdminById(id);
            if (admin.isPresent()) {
                return ResponseEntity.ok(admin.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (AdminException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    //Add admins
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AdminModel> addAdmin(@Valid  @RequestBody AdminModel admin) {
        try {
            AdminModel newAdmin = adminService.addAdmin(admin);
            return new ResponseEntity<>(newAdmin, HttpStatus.CREATED);
        }catch(AdminException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //update admin
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AdminModel> updateAdmin(@PathVariable ObjectId id, @Valid @RequestBody AdminModel newAdmin) {
        try {
            AdminModel updatedAdmin = adminService.updateAdmin(id, newAdmin);
            if (updatedAdmin != null) {
                return ResponseEntity.ok(updatedAdmin);
            }
        else{
                return ResponseEntity.notFound().build();
            }
        } catch (AdminException ex) {

            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    //delete admin
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable ObjectId id) {
        try {
            boolean deleted = adminService.deleteAdmin(id);
            if (deleted) {
                return  ResponseEntity.ok("Admin deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (AdminException ex){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    //register the user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AdminModel adminModel) {
        try {
            // Check if the username already exists

            if (adminService.findByUsername(adminModel.getAdminName()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken. Please try again");
            }
//        //AdminModel adminModel1=new AdminModel();
//            String encodedPassword = passwordEncoder.encode(adminModel.getPassword());
//            // Encode the password before saving
//            adminModel.setPassword(encodedPassword);

            // Save the user
            adminService.addNewAdmin(adminModel);

            // Return a success response
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e) {
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
