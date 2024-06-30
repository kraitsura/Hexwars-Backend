package com.hexwars.hexwars_backend.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hexwars.hexwars_backend.models.Board;

import com.hexwars.hexwars_backend.models.GameSession;
import com.hexwars.hexwars_backend.repository.GameSessionRepository;
import com.hexwars.hexwars_backend.repository.PlayerRepository;

@Service
public class SessionService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public GameSession createGameSession() {
        LocalDateTime localTime = LocalDateTime.now();
        GameSession gameSession = new GameSession(localTime);
        gameSessionRepository.save(gameSession);
        return gameSession;
    }

    public void addPlayerbyId(Long id, Long playerId) {
        GameSession gameSession = gameSessionRepository.findById(id).orElse(null);
        gameSession.addPlayer(playerRepository.findByID(playerId));
        gameSessionRepository.save(gameSession);
    }

    public GameSession getGameSession(Long id) {
        return gameSessionRepository.findById(id).orElse(null);
    }

    public void deleteGameSession(Long id) {
        gameSessionRepository.deleteById(id);
    }

    public void updateGameSession(GameSession gameSession) {
        gameSessionRepository.save(gameSession);
    }

    public Board getBoard(Long id) {
        return gameSessionRepository.findById(id).orElse(null).getBoard();
    }

    public void updateBoard(Board board) {
        gameSessionRepository.save(board.getGameSession());
    }

    public void deleteGameSession(GameSession gameSession) {
        gameSessionRepository.delete(gameSession);
    }
}
