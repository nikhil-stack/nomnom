package com.example.nomnom.repositories;

import com.example.nomnom.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
     Optional<Users> findByEmail(String email);
}
