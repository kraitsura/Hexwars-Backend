package com.hexwars.hexwars_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexwars.hexwars_backend.models.structures.Tile;

public interface TilesRepository extends JpaRepository<Tile, Long>{

}
