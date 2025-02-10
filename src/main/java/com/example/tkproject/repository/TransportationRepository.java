package com.example.tkproject.repository;


import com.example.tkproject.model.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
    // Additional custom queries can be defined here.
}