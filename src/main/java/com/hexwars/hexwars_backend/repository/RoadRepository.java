package com.hexwars.hexwars_backend.repository;

import com.hexwars.hexwars_backend.models.structures.Road;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadRepository extends JpaRepository<Road, Long> {
    // Define custom query methods if needed
}
