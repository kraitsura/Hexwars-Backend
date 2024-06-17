package com.hexwars.hexwars_backend.models;

import com.hexwars.hexwars_backend.models.enums.DevCardType;
import com.hexwars.hexwars_backend.models.enums.PlayerType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PlayerType color;

    private String name;
    private int victoryPoints;

    @ElementCollection
    private Map<ResourceType, Integer> resources;

    @ElementCollection
    private Map<DevCardType, Integer> devCards;

    private int position;

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    public Player(PlayerType color, String name) {
        this.color = color;
        this.name = name;
        this.victoryPoints = 0;
        this.resources = new HashMap<>();
        this.devCards = new HashMap<>();
        this.position = 0;
        // Initialize resource and devCard counts to 0
        for (ResourceType resource : ResourceType.values()) {
            resources.put(resource, 0);
        }
        for (DevCardType devCard : DevCardType.values()) {
            devCards.put(devCard, 0);
        }
    }

    public void addVictoryPoints(int points) {
        this.victoryPoints += points;
    }

    public boolean hasRequiredResources(Map<ResourceType, Integer> requiredResources) {
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            if (resources.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void addSingleResource(ResourceType resource, int count) {
        resources.put(resource, resources.getOrDefault(resource, 0) + count);
    }

    public void addResources(Map<ResourceType, Integer> resourcesToAdd) {
        for (Map.Entry<ResourceType, Integer> entry : resourcesToAdd.entrySet()) {
            addSingleResource(entry.getKey(), entry.getValue());
        }
    }

    public boolean removeResource(ResourceType resource, int count) {
        if (resources.getOrDefault(resource, 0) > 0) {
            resources.put(resource, resources.getOrDefault(resource, 0) - count);
            return true;
        }
        return false;
    }

    public void useResources(Map<ResourceType, Integer> requiredResources) {
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            removeResource(entry.getKey(), entry.getValue());
        }
    }

    public void addDevCard(DevCardType devCard, int count) {
        devCards.put(devCard, devCards.getOrDefault(devCard, 0) + count);
    }

    public void addDevelopmentCard(DevCardType devCard) {
        devCards.put(devCard, 1);
    }
}
