package com.hexwars.hexwars_backend.services.implementation;

import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hexwars.hexwars_backend.models.Board;
import com.hexwars.hexwars_backend.models.GameSession;
import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.CostType;
import com.hexwars.hexwars_backend.models.enums.DevCardType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.models.structures.Coordinate;
import com.hexwars.hexwars_backend.models.structures.DevDeck;
import com.hexwars.hexwars_backend.models.structures.Edge;
import com.hexwars.hexwars_backend.repository.*;
import com.hexwars.hexwars_backend.services.*;
import com.hexwars.hexwars_backend.services.utils.CostService;
import com.hexwars.hexwars_backend.services.utils.Utils;

import jakarta.transaction.Transactional;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private BuildService buildService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CostService costService;
    
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private GameSessionRepository gameRepository;

    @Override
    @Transactional
    public void build(Long boardId, Long playerId, Scanner scanner) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null || board == null) {
            System.out.println("Player or board not found.");
            return;
        }
        System.out.println("Choose what to build: (1) Settlement, (2) City, (3) Road");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                System.out.println("Enter the coordinates to place the settlement:");
                String settlementCoords = scanner.nextLine();
                Coordinate settlementCoordinate = Utils.parseCoordinate(settlementCoords);
                if (settlementCoordinate != null && buildService.placeBuilding(boardId, playerId, settlementCoordinate, false)) {
                    System.out.println("Settlement placed successfully.");
                } else {
                    System.out.println("Failed to place settlement.");
                }
                break;
            case 2:
                System.out.println("Enter the coordinates to upgrade to a city:");
                String cityCoords = scanner.nextLine();
                Coordinate cityCoordinate = Utils.parseCoordinate(cityCoords);
                if (cityCoordinate != null && buildService.placeBuilding(boardId, playerId, cityCoordinate, true)) {
                    System.out.println("City upgraded successfully.");
                } else {
                    System.out.println("Failed to upgrade to city.");
                }
                break;
            case 3:
                System.out.println("Enter the coordinates for the road (format 'x1,y1-x2,y2'):");
                String roadCoords = scanner.nextLine();
                String[] roadParts = roadCoords.split("-");
                if (roadParts.length == 2) {
                    Coordinate roadStart = Utils.parseCoordinate(roadParts[0]);
                    Coordinate roadEnd = Utils.parseCoordinate(roadParts[1]);
                    if (roadStart != null && roadEnd != null && buildService.placeRoad(boardId, playerId, new Edge(roadStart, roadEnd))) {
                        System.out.println("Road placed successfully.");
                    } else {
                        System.out.println("Failed to place road.");
                    }
                } else {
                    System.out.println("Invalid road coordinates format.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
    }


    @Override
    @Transactional
    public void trade(Long gameId, Long playerId, Scanner scanner) {
        GameSession game = gameRepository.findById(gameId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);

        System.out.println("Do you want to trade with (1) Another player or (2) The bank or (3) make an open offer?");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.println("Enter the name of the player you want to trade with:");
                String otherPlayerName = scanner.nextLine();
                Player otherPlayer = playerRepository.findByName(otherPlayerName);
                Long otherPlayerId = otherPlayer != null ? otherPlayer.getId() : null;
                if (otherPlayerId != null) {
                    System.out.println("Enter the resources you want to offer (format 'wood:1,brick:1'):");
                    String offer = scanner.nextLine();
                    Map<ResourceType, Integer> offerMap = Utils.parseResources(offer);

                    System.out.println("Enter the resources you want in return (format 'wood:1,brick:1'):");
                    String request = scanner.nextLine();
                    Map<ResourceType, Integer> requestMap = Utils.parseResources(request);

                    if (playerService.executeTrade(playerId, otherPlayerId, offerMap, requestMap)) {
                        System.out.println("Trade successful.");
                    } else {
                        System.out.println("Trade failed.");
                    }
                } else {
                    System.out.println("Player not found.");
                }
                break;
            case 2:
                System.out.println("Enter the resource you want to give:");
                String giveResourceStr = scanner.nextLine();
                ResourceType giveResource = ResourceType.valueOf(giveResourceStr.toUpperCase());

                System.out.println("Enter the resource you want to receive:");
                String getResourceStr = scanner.nextLine();
                ResourceType getResource = ResourceType.valueOf(getResourceStr.toUpperCase());

                System.out.println("Enter the trade rate (number of resources to give):");
                int rate = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (playerService.executeTradeWithBank(playerId, giveResource, getResource, rate)) {
                    System.out.println("Trade with bank successful.");
                } else {
                    System.out.println("Trade with bank failed.");
                }
                break;
            case 3:
                System.out.println("Enter the resources you want to offer (format 'wood:1,brick:1'):");
                String offer = scanner.nextLine();
                Map<ResourceType, Integer> offerMap = Utils.parseResources(offer);

                System.out.println("Enter the resources you want in return (format 'wood:1,brick:1'):");
                String request = scanner.nextLine();
                Map<ResourceType, Integer> requestMap = Utils.parseResources(request);

                System.out.println("Trade offer failed.");

                // if (tradeService.openTrade(player, offerMap, requestMap)) {
                //     System.out.println("Trade offer posted successfully.");
                // } else {
                //     System.out.println("Trade offer failed.");
                // }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    @Override
    @Transactional
    public void buyDevCard(Long boardId, Long playerId) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null || board == null) {
            System.out.println("Player or Board not found.");
            return;
        }
        System.out.println("Buying Random Dev Card....");

        if (!CostService.canAfford(CostType.DEVELOPMENT_CARD, player)) {
            System.out.println("Not enough resources to buy the development card.");
            return;
        }

        DevDeck deck = board.getDevDeck();
        DevCardType chosenCard = deck.drawCard();
        if (chosenCard == null) {
            System.out.println("No more development cards left.");
            return;
        }

        costService.deductCost(CostType.DEVELOPMENT_CARD, player);
        player.addDevCard(chosenCard);
        playerRepository.save(player);
        boardRepository.save(board);
        System.out.println("Development card " + chosenCard.name() + " purchased successfully.");
    }

    @Override
    @Transactional
    public void rob(Long boardId, Long playerId, Scanner scanner) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null || board == null) {
            System.out.println("Player or Board not found.");
            return;
        }
        System.out.println("Enter the coordinates for the robber:");
        String robberCoords = scanner.nextLine();
        Coordinate robberCoordinate = Utils.parseCoordinate(robberCoords);
        if (robberCoordinate == null) {
            System.out.println("Invalid robber coordinates.");
            return;
        }
        //buildService.moveRobber(boardId, robberCoordinate);
        System.out.println("Robber moved successfully.");
    }
}
