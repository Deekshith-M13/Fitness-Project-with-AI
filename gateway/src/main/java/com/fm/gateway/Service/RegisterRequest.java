package com.fm.gateway.Service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;
    private String keyCloakId;

    private String firstname;
    private String lastname;


    private String password;


}
