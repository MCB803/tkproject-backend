package com.example.tkproject.service;

import com.example.tkproject.model.Transportation;
import com.example.tkproject.repository.TransportationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportationServiceImpl implements TransportationService {

    private final TransportationRepository transportationRepository;

    public TransportationServiceImpl(TransportationRepository transportationRepository) {
        this.transportationRepository = transportationRepository;
    }

    @Override
    public List<Transportation> findAll() {
        return transportationRepository.findAll();
    }

    @Override
    public Optional<Transportation> findById(Long id) {
        return transportationRepository.findById(id);
    }

    @Override
    public Transportation create(Transportation transportation) {
        return transportationRepository.save(transportation);
    }

    @Override
    public Transportation update(Long id, Transportation transportation) {
        Transportation existing = transportationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transportation not found with id: " + id));
        existing.setType(transportation.getType());
        existing.setOrigin(transportation.getOrigin());
        existing.setDestination(transportation.getDestination());
        return transportationRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        transportationRepository.deleteById(id);
    }
}
