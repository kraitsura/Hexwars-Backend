package com.hexwars.hexwars_backend.models;

import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hexwars.hexwars_backend.models.enums.GameStatus;

@Entity
@Table(name = "game_sessions")
@Data
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players;


    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.WAITING;

    @ManyToOne
    @JoinColumn(name = "current_player_id")
    private Player currentPlayer;

    private int currentRound = 0;

    public GameSession(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        this.players = new ArrayList<>();
        this.board = new Board(this);
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setGameSession(this);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setGameSession(null);
    }

    public Player getPlayerById(Long playerId) {
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public Player getPlayerByName(String playerName) {
        return players.stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .orElse(null);
    }

    public void startGame() {
        if (players.size() < 3 || players.size() > 4) {
            throw new IllegalStateException("Game can only start with 3 or 4 players");
        }
        this.status = GameStatus.IN_PROGRESS;
        this.currentPlayer = players.get(0);
        this.currentRound = 1;
    }

    public void nextTurn() {
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentIndex + 1) % players.size());
        if (currentIndex == players.size() - 1) {
            currentRound++;
        }
    }

    public boolean isGameOver() {
        return players.stream().anyMatch(player -> player.getVictoryPoints() >= 10);
    }

}
