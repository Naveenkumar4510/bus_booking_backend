package com.naveen.BusBooking.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminModel {

    @Id
    private ObjectId id;
    @NotBlank
    private String adminName;

    @Size(min = 6, message = "Password must be atleast 6 characters")
    private String password;

    @Email
    private String email;
    @NotBlank(message = "Role is Required")
    private String roles;
}
