package com.naveen.BusBooking.Service;

import com.mongodb.MongoException;
import com.naveen.BusBooking.Exception.AdminException;
import com.naveen.BusBooking.Repository.AdminRepository;
import com.naveen.BusBooking.model.AdminModel;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class AdminService {
    @Autowired
AdminRepository adminRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

 // private static final   Logger logger= LoggerFactory.getLogger(AdminService.class);

    //get all admin details
    public List<AdminModel> getAllAdmins() {
        try{
           // log.info("Getting all admin details");
            return adminRepository.findAll();
        }catch (Exception ex){
            log.error("Failed to get all admin details :{} ",ex.getMessage());
            throw new AdminException(" Failed to get all admin details "+ex.getMessage());
        }

    }

    //get admin details by id
    public Optional<AdminModel> getAdminById(ObjectId id) {
        try {
            //log.info("Getting all admin details");
            return adminRepository.findById(id);
        }catch(Exception ex){
            log.error("Failed to get  admin details by id:{}",ex.getMessage());
            throw new AdminException("Failed to get admin by id "+ex.getMessage());
        }
    }


    //Add admin details
    public AdminModel addAdmin(AdminModel admin) {
        try {
            return adminRepository.save(admin);
        }catch (MongoException ex)
        {
            log.error("Failed to add  admin {}",ex.getMessage());
            throw new AdminException("Failed to add admin"+ex.getMessage());
        }

    }

    public AdminModel addNewAdmin(AdminModel admin) {
        try {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
           // admin.setRoles(Arrays.asList("ADMIN"));
            return adminRepository.save(admin);
        }catch (MongoException ex)
        {

            log.error("Failed to register admin {}",ex.getMessage());

            throw new AdminException("Failed to add admin"+ex.getMessage());
        }

    }


    //update admin details
    public AdminModel updateAdmin(ObjectId id, AdminModel newAdmin) {

            if (adminRepository.existsById(id)) {
                newAdmin.setId(id);

                try {
                    return adminRepository.save(newAdmin);
                } catch (MongoException ex) {
                    log.error("Failed to update  admin details by id:{}",ex.getMessage());
                    throw new RuntimeException("Failed to update admin due to a database error");
                }
            }else {

                throw new NoSuchElementException("Admin not found with id: "+id);
            }

    }


    //delete admin
    public boolean deleteAdmin(ObjectId id) {
try {
    if (adminRepository.existsById(id)) {

        adminRepository.deleteById(id);
        return true;
    }
}catch (Exception ex){
    log.error("Failed to delete  admin details by id:{}",ex.getMessage());
    throw new AdminException("Failed to delete admin");
}
        return false; // Handle admin not found
    }

    //search admin details by admin name
    public Optional<AdminModel> findByUsername(String adminName) {
        try{
            return adminRepository.findByAdminName(adminName);
        }catch (Exception ex){
            log.error("Failed to find   admin details by username:{}",ex.getMessage());
            throw new AdminException("Failed to get admin by admin name"+ex.getMessage());
        }

    }
}
