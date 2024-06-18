package com.hexwars.hexwars_backend.models;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionName;
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private Board board;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players;

    public GameSession(String sessionName, LocalDateTime createdAt, List<Player> players) {
        this.sessionName = sessionName;
        this.createdAt = createdAt;
        this.board = new Board();
        this.players = players;
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

}
