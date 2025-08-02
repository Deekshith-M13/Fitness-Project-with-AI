package com.fm.userservice.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private String id;
    private String firstname;
    private String lastname;
    private String keyCloakId;
    private String email;
    private String password;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
