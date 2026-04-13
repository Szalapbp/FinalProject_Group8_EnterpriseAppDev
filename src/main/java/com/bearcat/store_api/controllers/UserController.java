package com.bearcat.store_api.controllers;

import com.bearcat.store_api.dtos.RegisterRequest;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


  //  private final PasswordEncoder passwordEncoder;
    private final UserService userService;


//
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
//
//        if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Email already registered");
//        }
//
//        try {
//
//            User user = new User();
//            user.setEmail(registerRequest.getEmail());
//            user.setFullName(registerRequest.getName());
//            user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
//           User savedUser = userService.saveUser(user);
//
//            if (savedUser.getId() != null) {
//                return ResponseEntity.status(HttpStatus.CREATED).
//                        body("Given user details are successfully registered");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
//                        body("User registration failed");
//            }
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
//                    body("An exception occurred: " + ex.getMessage());
//        }
//    }
//


}
