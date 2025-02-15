package com.example.tkproject.repository;

import com.example.tkproject.model.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    @Query("SELECT t FROM Transportation t WHERE :day MEMBER OF t.operatingDays")
    List<Transportation> findByOperatingDaysContaining(@Param("day") Integer day);
}
