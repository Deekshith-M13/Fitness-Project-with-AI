package com.fm.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;
    private String keyCloakId;

    @NotBlank(message = "Name is compulsory")
    private String firstname;
    private String lastname;


    private String password;


}
