package com.fm.userservice.Service;

import com.fm.userservice.Exception.UserNotFoundException;
import com.fm.userservice.Repository.UserRepository;
import com.fm.userservice.dto.RegisterRequest;
import com.fm.userservice.dto.UserResponse;
import com.fm.userservice.model.User;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public UserResponse register(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){


            User existingUser = userRepository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();
            userResponse.setId(existingUser.getId());
            userResponse.setCreatedDate(existingUser.getCreatedDate());
            userResponse.setUpdatedDate(existingUser.getUpdatedDate());
            userResponse.setKeyCloakId(existingUser.getKeyCloakId());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setFirstname(existingUser.getFirstname());
            userResponse.setLastname(existingUser.getLastname());
            return userResponse;
        }

        if(userRepository.existsByKeyCloakId(request.getKeyCloakId())){
            throw new RuntimeException("User with same keycloak id already present");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setKeyCloakId(request.getKeyCloakId());

        User newuser = userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(newuser.getId());
        userResponse.setCreatedDate(newuser.getCreatedDate());
        userResponse.setUpdatedDate(newuser.getUpdatedDate());
        userResponse.setKeyCloakId(newuser.getKeyCloakId());
        userResponse.setEmail(newuser.getEmail());
        userResponse.setPassword(newuser.getPassword());
        userResponse.setFirstname(newuser.getFirstname());
        userResponse.setLastname(newuser.getLastname());

        return userResponse;

    }

    public UserResponse getUserProfile(String userid) {

        User user =  userRepository.findById(userid).orElseThrow(() -> new UserNotFoundException("user not found"));

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setCreatedDate(user.getCreatedDate());
        userResponse.setUpdatedDate(user.getUpdatedDate());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());

        return userResponse;
    }

    public Boolean existsByUserId(String userid) {
        return userRepository.existsByKeyCloakId(userid);
    }
}
