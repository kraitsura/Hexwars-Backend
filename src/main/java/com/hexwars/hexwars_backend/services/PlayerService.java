package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.structures.*;
import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.CostType;
import com.hexwars.hexwars_backend.models.enums.DevCardType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
public class PlayerService {

    @Autowired
    private BoardService boardService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CostService costManager;

    public void build(Player player, Scanner scanner) {
        System.out.println("Choose what to build: (1) Settlement, (2) City, (3) Road");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        CostType costType;
        switch (choice) {
            case 1:
                costType = CostType.SETTLEMENT;
                System.out.println("Enter the coordinates to place the settlement:");
                String settlementCoords = scanner.nextLine();
                Coordinate settlementCoordinate = parseCoordinate(settlementCoords);
                if (settlementCoordinate != null && boardService.placeBuilding(getBoardId(player), settlementCoordinate, player, false)) {
                    System.out.println("Settlement placed successfully.");
                } else {
                    System.out.println("Failed to place settlement.");
                }
                break;
            case 2:
                costType = CostType.CITY;
                System.out.println("Enter the coordinates to upgrade to a city:");
                String cityCoords = scanner.nextLine();
                Coordinate cityCoordinate = parseCoordinate(cityCoords);
                if (cityCoordinate != null && boardService.placeBuilding(getBoardId(player), cityCoordinate, player, true)) {
                    System.out.println("City upgraded successfully.");
                } else {
                    System.out.println("Failed to upgrade to city.");
                }
                break;
            case 3:
                costType = CostType.ROAD;
                System.out.println("Enter the coordinates for the road (format 'x1,y1-x2,y2'):");
                String roadCoords = scanner.nextLine();
                String[] roadParts = roadCoords.split("-");
                if (roadParts.length == 2) {
                    Coordinate roadStart = parseCoordinate(roadParts[0]);
                    Coordinate roadEnd = parseCoordinate(roadParts[1]);
                    if (roadStart != null && roadEnd != null && boardService.placeRoad(getBoardId(player), player, new Edge(roadStart, roadEnd))) {
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

        // Check if the player can afford the build
        if (costManager.canAfford(costType, player.getResources())) {
            costManager.deductCost(costType, player.getResources());
            playerRepository.save(player);
            System.out.println("Resources deducted for " + costType.name());
        } else {
            System.out.println("Insufficient resources to build " + costType.name());
        }
    }

    public void trade(Player player, Scanner scanner) {
        System.out.println("Do you want to trade with (1) Another player or (2) The bank or (3) make an open offer?");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.println("Enter the name of the player you want to trade with:");
                String otherPlayerName = scanner.nextLine();
                Player otherPlayer = playerRepository.findByName(otherPlayerName);
                if (otherPlayer != null) {
                    System.out.println("Enter the resources you want to offer (format 'wood:1,brick:1'):");
                    String offer = scanner.nextLine();
                    Map<ResourceType, Integer> offerMap = parseResources(offer);

                    System.out.println("Enter the resources you want in return (format 'wood:1,brick:1'):");
                    String request = scanner.nextLine();
                    Map<ResourceType, Integer> requestMap = parseResources(request);

                    if (tradeService.trade(player, otherPlayer, offerMap, requestMap)) {
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

                if (tradeService.tradeWithGame(player, giveResource, getResource, rate)) {
                    System.out.println("Trade with bank successful.");
                } else {
                    System.out.println("Trade with bank failed.");
                }
                break;
            case 3:
                System.out.println("Enter the resources you want to offer (format 'wood:1,brick:1'):");
                String offer = scanner.nextLine();
                Map<ResourceType, Integer> offerMap = parseResources(offer);

                System.out.println("Enter the resources you want in return (format 'wood:1,brick:1'):");
                String request = scanner.nextLine();
                Map<ResourceType, Integer> requestMap = parseResources(request);

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

    public void buyDevelopmentCard(Player player, Scanner scanner) {
        System.out.println("Choose which development card to buy: ");
        for (DevCardType type : DevCardType.values()) {
            System.out.println(type.ordinal() + 1 + ". " + type.name());
        }
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline

        if (choice < 0 || choice >= DevCardType.values().length) {
            System.out.println("Invalid choice.");
            return;
        }

        DevCardType chosenCard = DevCardType.values()[choice];

        if (!costManager.canAfford(CostType.DEVELOPMENT_CARD, player.getResources())) {
            System.out.println("Not enough resources to buy the development card.");
            return;
        }

        costManager.deductCost(CostType.DEVELOPMENT_CARD, player.getResources());
        player.addDevelopmentCard(chosenCard);
        playerRepository.save(player);
        System.out.println("Development card " + chosenCard.name() + " purchased successfully.");
    }

    private Long getBoardId(Player player) {
        return player.getGameSession().getBoard().getId();
    }

    private Coordinate parseCoordinate(String input) {
        try {
            String[] parts = input.trim().split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return new Coordinate(x, y);
        } catch (Exception e) {
            System.out.println("Invalid coordinate format. Please enter coordinates in the format 'x,y'.");
            return null;
        }
    }

    private Map<ResourceType, Integer> parseResources(String input) {
        Map<ResourceType, Integer> resourceMap = new HashMap<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            String[] resourceParts = part.split(":");
            if (resourceParts.length == 2) {
                try {
                    ResourceType resource = ResourceType.valueOf(resourceParts[0].trim().toUpperCase());
                    int quantity = Integer.parseInt(resourceParts[1].trim());
                    resourceMap.put(resource, quantity);
                } catch (Exception e) {
                    System.out.println("Invalid resource format: " + part);
                }
            }
        }
        return resourceMap;
    }
}
