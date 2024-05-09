package com.naveen.BusBooking.Exception;

import com.naveen.BusBooking.model.AdminModel;

public class AdminException extends RuntimeException{
    public AdminException(String message){
        super(message);
    }
}
