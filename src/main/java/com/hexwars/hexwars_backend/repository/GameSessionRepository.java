package com.hexwars.hexwars_backend.repository;

import com.hexwars.hexwars_backend.models.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    public GameSession findByPlayerId(Long playerId);
    public GameSession findByBoardId(Long boardId);

}
