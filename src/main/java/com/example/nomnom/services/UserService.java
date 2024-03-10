package com.example.nomnom.services;


import com.example.nomnom.models.Users;
import com.example.nomnom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public List<Users> getUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e){
            System.out.println(e.getMessage());
            return List.of();
        }

    }

    public String createUser(Users user){

        try {
            Optional<Users> userOptional =  userRepository.findByEmail(user.getEmail());

            if(userOptional.isPresent()){
                return "Email already taken";
            }

            user.setUserId( UUID.randomUUID().toString());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            return "User successfully created";
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "Error occurred";
        }

    }




}
