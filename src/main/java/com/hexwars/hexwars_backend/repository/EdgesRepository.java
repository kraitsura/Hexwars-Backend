package com.hexwars.hexwars_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexwars.hexwars_backend.models.structures.RoadSpot;

public interface EdgesRepository extends JpaRepository<RoadSpot, Long>{

}
