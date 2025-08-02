package com.fm.userservice.Repository;

import com.fm.userservice.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    Boolean existsByKeyCloakId(String keyCloakId);

    User findByEmail(String email);
}
