package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.GameSession;
import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.DevCardType;
import com.hexwars.hexwars_backend.models.enums.PlayerType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;

import java.util.Map;
import java.util.List;

public interface PlayerService {
    // Player management
    Player createPlayer(String name, PlayerType color, GameSession game);
    Player getPlayer(Long playerId);
    List<Player> getAllPlayers(Long gameId);
    boolean removePlayer(Long playerId);

    // Resource management
    boolean updatePlayerResources(Long playerId, ResourceType resourceType, int amount);
    Map<ResourceType, Integer> getPlayerResources(Long playerId);
    boolean hasRequiredResources(Long playerId, Map<ResourceType, Integer> requiredResources);

    // Development cards
    boolean addDevelopmentCard(Long playerId, DevCardType cardType);
    boolean playDevelopmentCard(Long playerId, DevCardType cardType);
    Map<DevCardType, Integer> getPlayerDevelopmentCards(Long playerId);

    // Game actions
    boolean addVictoryPoints(Long playerId, int points);
    int getVictoryPoints(Long playerId);

    // Special achievements
    boolean setLongestRoad(Long playerId, boolean hasLongestRoad);
    boolean setLargestArmy(Long playerId, boolean hasLargestArmy);

    // Trading
    boolean canTrade(Long playerId, Map<ResourceType, Integer> offer);
    boolean executeTrade(Long fromPlayerId, Long toPlayerId, Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request);
    boolean executeTradeWithBank(Long playerId, ResourceType give, ResourceType receive, int rate);

    // Game state
    boolean resetDevCardPlayedStatus(Long playerId);
    boolean hasPlayedDevCardThisTurn(Long playerId);

    // Statistics
    void printPlayerStats(Long playerId);
    void printPlayerResources (Long playerId);
    int getNumberOfSettlements(Long playerId);
    int getNumberOfCities(Long playerId);
    int getNumberOfRoads(Long playerId);
}