package com.example.tkproject.repository;

import com.example.tkproject.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    // No need for findByLocationCode anymore, use findById instead.
}
