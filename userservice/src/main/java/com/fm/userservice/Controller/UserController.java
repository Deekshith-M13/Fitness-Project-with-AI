package com.fm.userservice.Controller;


import com.fm.userservice.Service.UserService;
import com.fm.userservice.dto.RegisterRequest;
import com.fm.userservice.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userid}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userid) {
        return ResponseEntity.ok(userService.getUserProfile(userid)) ;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{userid}/validate")
    public ResponseEntity<Boolean> isUserPresent(@PathVariable String userid) {
        return ResponseEntity.ok(userService.existsByUserId(userid)) ;
    }

}
