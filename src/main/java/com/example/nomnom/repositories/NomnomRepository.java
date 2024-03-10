package com.example.nomnom.repositories;

import com.example.nomnom.models.Nomnom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NomnomRepository extends JpaRepository<Nomnom, Long> {

}
