package com.hexwars.hexwars_backend.repository;

import com.hexwars.hexwars_backend.models.structures.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    // You can define custom query methods here if needed
}
