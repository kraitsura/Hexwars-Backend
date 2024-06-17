package com.hexwars.hexwars_backend.repository;

import com.hexwars.hexwars_backend.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    // You can define custom query methods here if needed
}
