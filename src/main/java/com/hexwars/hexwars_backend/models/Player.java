package com.hexwars.hexwars_backend.models;

import com.hexwars.hexwars_backend.models.enums.DevCardType;
import com.hexwars.hexwars_backend.models.enums.PlayerType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerType color;

    @Column(nullable = false)
    private int victoryPoints;

    @Column(nullable = false)
    private int knightsPlayed;

    @Column(nullable = false)
    private boolean longestRoad;

    @Column(nullable = false)
    private boolean largestArmy;

    @ElementCollection
    @CollectionTable(name = "player_resources", joinColumns = @JoinColumn(name = "player_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "resource_type")
    @Column(name = "quantity", nullable = false)
    private Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);

    @ElementCollection
    @CollectionTable(name = "player_dev_cards", joinColumns = @JoinColumn(name = "player_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "dev_card_type")
    @Column(name = "quantity", nullable = false)
    private Map<DevCardType, Integer> devCards = new EnumMap<>(DevCardType.class);

    @ElementCollection
    @CollectionTable(name = "player_played_dev_cards", joinColumns = @JoinColumn(name = "player_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "dev_card_type")
    @Column(name = "quantity", nullable = false)
    private Map<DevCardType, Integer> playedDevCards = new EnumMap<>(DevCardType.class);

    @Column(nullable = false)
    private boolean hasPlayedDevCardThisTurn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    public Player(String name, PlayerType color, GameSession gameSession) {
        this.name = name;
        this.color = color;
        this.victoryPoints = 0;
        this.knightsPlayed = 0;
        this.longestRoad = false;
        this.largestArmy = false;
        this.hasPlayedDevCardThisTurn = false;
        this.gameSession = gameSession;
        initializeResources();
        initializeDevCards();
    }

    private void initializeResources() {
        for (ResourceType resource : ResourceType.values()) {
            resources.put(resource, 0);
        }
    }

    private void initializeDevCards() {
        for (DevCardType devCard : DevCardType.values()) {
            devCards.put(devCard, 0);
            playedDevCards.put(devCard, 0);
        }
    }

    public void addResource(ResourceType resource, int count) {
        resources.merge(resource, count, Integer::sum);
    }

    public boolean removeResource(ResourceType resource, int count) {
        int currentCount = resources.getOrDefault(resource, 0);
        if (currentCount >= count) {
            resources.put(resource, currentCount - count);
            return true;
        }
        return false;
    }

    public void addDevCard(DevCardType devCard) {
        devCards.merge(devCard, 1, Integer::sum);
    }

    public boolean playDevCard(DevCardType devCard) {
        if (devCards.getOrDefault(devCard, 0) > 0 && !hasPlayedDevCardThisTurn) {
            devCards.merge(devCard, -1, Integer::sum);
            playedDevCards.merge(devCard, 1, Integer::sum);
            hasPlayedDevCardThisTurn = true;
            if (devCard == DevCardType.KNIGHT) {
                knightsPlayed++;
            }
            return true;
        }
        return false;
    }

    public void resetDevCardPlayedStatus() {
        hasPlayedDevCardThisTurn = false;
    }

    public int getTotalResources() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalDevCards() {
        return devCards.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getResourceCount(ResourceType resourceType) {
        return resources.getOrDefault(resourceType, 0);
    }

    public int getDevCardCount(DevCardType devCardType) {
        return devCards.getOrDefault(devCardType, 0);
    }

    public void addVictoryPoints(int points) {
        this.victoryPoints += points;
    }

    @PrePersist
    @PreUpdate
    private void validatePlayer() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Player name cannot be null or empty");
        }
        if (victoryPoints < 0) {
            throw new IllegalStateException("Victory points cannot be negative");
        }
        if (knightsPlayed < 0) {
            throw new IllegalStateException("Knights played cannot be negative");
        }
    }
}