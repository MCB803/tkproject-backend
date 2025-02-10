package com.example.tkproject.service;

import com.example.tkproject.model.Transportation;

import java.util.List;
import java.util.Optional;

public interface TransportationService {
    List<Transportation> findAll();
    Optional<Transportation> findById(Long id);
    Transportation create(Transportation transportation);
    Transportation update(Long id, Transportation transportation);
    void delete(Long id);
}
