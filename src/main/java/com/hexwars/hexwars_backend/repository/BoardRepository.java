package com.hexwars.hexwars_backend.repository;

import com.hexwars.hexwars_backend.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
   public Board findByID(Long id);
}
