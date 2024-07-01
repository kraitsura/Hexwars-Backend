package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.*;
import com.hexwars.hexwars_backend.models.enums.*;
import com.hexwars.hexwars_backend.models.structures.Coordinate;
import com.hexwars.hexwars_backend.models.structures.Edge;

import java.util.List;
import java.util.Map;

public interface GameService {
    // Game management
    GameSession createGame(List<String> playerNames);
    GameSession getGame(Long gameId);
    void startGame(Long gameId);
    void endGame(Long gameId);

    // Turn management
    void startTurn(Long gameId);
    void endTurn(Long gameId);
    Player getCurrentPlayer(Long gameId);

    // Game actions
    boolean rollDice(Long gameId);
    boolean buildSettlement(Long gameId, Long playerId, Coordinate coordinate);
    boolean buildCity(Long gameId, Long playerId, Coordinate coordinate);
    boolean buildRoad(Long gameId, Long playerId, Edge edge);
    boolean buyDevelopmentCard(Long gameId, Long playerId);
    boolean playDevelopmentCard(Long gameId, Long playerId, DevCardType cardType);
    boolean tradeWithBank(Long gameId, Long playerId, ResourceType give, ResourceType receive);
    boolean tradeWithPlayer(Long gameId, Long fromPlayerId, Long toPlayerId, 
                            Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request);

    // Special actions
    void moveRobber(Long gameId, Coordinate newPosition);
    void stealResource(Long gameId, Long stealingPlayerId, Long targetPlayerId);

    // Game state queries
    int getCurrentRound(Long gameId);
    List<Player> getPlayers(Long gameId);
    Board getBoard(Long gameId);
    boolean isGameOver(Long gameId);
    Player getWinner(Long gameId);

    // Game statistics
    Map<Long, Integer> getPlayerScores(Long gameId);
    Map<Long, Integer> getPlayerResourceCounts(Long gameId);
    Map<Long, Integer> getPlayerDevelopmentCardCounts(Long gameId);
}