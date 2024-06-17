package com.hexwars.hexwars_backend.repository;

import com.hexwars.hexwars_backend.models.structures.Tile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TileRepository extends JpaRepository<Tile, Long> {
    // Define custom query methods if needed
}
