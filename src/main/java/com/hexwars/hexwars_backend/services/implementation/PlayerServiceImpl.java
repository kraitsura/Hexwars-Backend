package com.hexwars.hexwars_backend.services.implementation;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hexwars.hexwars_backend.models.GameSession;
import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.DevCardType;
import com.hexwars.hexwars_backend.models.enums.PlayerType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.repository.GameSessionRepository;
import com.hexwars.hexwars_backend.repository.PlayerRepository;
import com.hexwars.hexwars_backend.services.PlayerService;
import com.hexwars.hexwars_backend.services.TradeService;

import jakarta.transaction.Transactional;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;
    
    // Player management
    @Override
    @Transactional
    public Player createPlayer(String name, PlayerType color, GameSession game) {

        Player player = new Player(name, color, game);
        playerRepository.save(player);

        return player;
    }

    @Override
    public Player getPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
    }

    @Override
    @Transactional
    public List<Player> getAllPlayers(Long gameId) {
        GameSession game = gameSessionRepository.findById(gameId).orElse(null);
        if (game != null) {
            return game.getPlayers();
        } else {
            System.out.println("Game not found");
        }
        return null;
    }

    @Override
    @Transactional
    public boolean removePlayer(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            playerRepository.delete(player);
            return true;
        } 
        return false;
    }

    // Resource management
    @Override
    @Transactional
    public boolean updatePlayerResources(Long playerId, ResourceType resourceType, int amount) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.addResource(resourceType, amount);
            playerRepository.save(player);
            return true;
        }
        System.out.println("Player not found, could not update resources");
        return false;
    }

    @Override
    public Map<ResourceType, Integer> getPlayerResources(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.getResources();
        }
        System.out.println("Player not found, could not get resources");
        return null;
    }

    @Override
    public boolean hasRequiredResources(Long playerId, Map<ResourceType, Integer> requiredResources) {
        Player player = getPlayer(playerId);
        Map<ResourceType, Integer> playerResources = player.getResources();
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            ResourceType resource = entry.getKey();
            int quantity = entry.getValue();
            if (playerResources.getOrDefault(resource, 0) < quantity) {
                System.out.println("Player does not have enough " + resource);
                return false;
            }
        }
        return true;
    }

    // Development cards
    @Override
    @Transactional
    public boolean addDevelopmentCard(Long playerId, DevCardType cardType) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.addDevCard(cardType);
            if(cardType == DevCardType.VP_CHAPEL || cardType == DevCardType.VP_LIBRARY || cardType == DevCardType.VP_MARKET || cardType == DevCardType.VP_GREAT_HALL || cardType == DevCardType.VP_UNIVERSITY) {
                player.addVictoryPoints(1);
            }
            playerRepository.save(player);
            return true;
        }
        System.out.println("Player not found, could not add development card");
        return false;
    }

    @Override
    @Transactional
    public boolean playDevelopmentCard(Long playerId, DevCardType cardType) {
        Player player = getPlayer(playerId);
        if (player != null) {
            if(cardType == DevCardType.VP_CHAPEL || cardType == DevCardType.VP_LIBRARY || cardType == DevCardType.VP_MARKET || cardType == DevCardType.VP_GREAT_HALL || cardType == DevCardType.VP_UNIVERSITY) {
                System.out.println("Cannot play VP card");
                return false;
            }
            player.playDevCard(cardType);
            if(cardType == DevCardType.KNIGHT) {
                player.knightsPlayed++;
            }
            playerRepository.save(player);
            return true;
        }
        System.out.println("Player not found, could not play development card");
        return false;
    }

    @Override
    public Map<DevCardType, Integer> getPlayerDevelopmentCards(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.getDevCards();
        }
        System.out.println("Player not found, could not get development cards");
        return null;
    }

    // Game actions    
    @Override
    @Transactional
    public boolean addVictoryPoints(Long playerId, int points) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.addVictoryPoints(points);
            playerRepository.save(player);
            return true;
        }
        return false;

    }

    @Override
    public int getVictoryPoints(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.getVictoryPoints();
        }
        return 0;
    }

    // Special achievements
    @Override
    @Transactional
    public boolean setLongestRoad(Long playerId, boolean hasLongestRoad) {
        Player player = getPlayer(playerId);
        if (player == null) {
            System.out.println("Player not found, could not set longest road");
            return false;
        }
        player.setLongestRoad(hasLongestRoad);
        playerRepository.save(player);
        return true;
    }

    @Override
    @Transactional
    public boolean setLargestArmy(Long playerId, boolean hasLargestArmy) {
        Player player = getPlayer(playerId);
        if (player == null) {
            System.out.println("Player not found, could not set largest army");
            return false;
        }
        player.setLargestArmy(hasLargestArmy);
        playerRepository.save(player);
        return true;
    }

    // Trading
    @Override
    public boolean canTrade(Long playerId, Map<ResourceType, Integer> offer) {
        return hasRequiredResources(playerId, offer);
    }

    @Override
    @Transactional
    public boolean executeTrade(Long fromPlayerId, Long toPlayerId, Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request) {
        Player fromPlayer = getPlayer(fromPlayerId);
        Player toPlayer = getPlayer(toPlayerId);
        return tradeService.trade(fromPlayer, toPlayer, offer, request);
    }

    @Override
    @Transactional
    public boolean executeTradeWithBank(Long playerId, ResourceType give, ResourceType receive, int rate) {
        Player player = getPlayer(playerId);
        return tradeService.tradeWithGame(player, give, receive, rate);
    }

    // Game state
    @Override
    @Transactional
    public boolean resetDevCardPlayedStatus(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.resetDevCardPlayedStatus();
            playerRepository.save(player);
            return true;
        }
        System.out.println("Player not found, could not reset dev card played status");
        return false;
    }

    @Override
    public boolean hasPlayedDevCardThisTurn(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.isHasPlayedDevCardThisTurn();
        }
        System.out.println("Player not found, could not check if dev card played this turn");
        return false;
    }

    // Statistics
    @Override
    public void printPlayerStats(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            printPlayerResources(playerId);
            System.out.println(player.getName() + " has " + getNumberOfCities(playerId) + " cities.");
            System.out.println(player.getName() + " has " + getNumberOfSettlements(playerId) + " settlements.");
            System.out.println(player.getName() + " has " + getNumberOfRoads(playerId) + " roads.");
            System.out.println(player.getName() + " has " + player.getVictoryPoints() + " victory points.");
            System.out.println(player.getName() + " has played " + player.getKnightsPlayed() + " knights.");
        }
    }
    @Override
    public void printPlayerResources (Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            System.out.println(player.getName() + " has the following resources:");
            for (Map.Entry<ResourceType, Integer> entry : player.getResources().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }
    @Override
    public int getNumberOfSettlements(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.getSettlementsBuilt();
        }
        return 0;
    }
    @Override
    public int getNumberOfCities(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.getCitiesBuilt();
        }
        return 0;
    }
    @Override
    public int getNumberOfRoads(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            return player.getRoadsBuilt();
        }
        return 0;
    }
}
