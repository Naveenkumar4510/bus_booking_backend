package com.naveen.BusBooking.Service;

import com.naveen.BusBooking.Repository.AdminRepository;
import com.naveen.BusBooking.model.AdminModel;
import com.naveen.BusBooking.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AdminAuthService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AdminModel> authUserOptional = adminRepository.findByAdminName(username.toLowerCase());
        if (authUserOptional.isPresent()) {
            AdminModel admin = authUserOptional.get();
            return org.springframework.security.core.userdetails.User
                    .withUsername(admin.getAdminName())
                    .password(admin.getPassword())
                    .roles(admin.getRoles())
                    .build();
        } else {
            throw new UsernameNotFoundException("Admin not found with username: " + username);
        }
    }
    }

