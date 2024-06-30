package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.*;
import com.hexwars.hexwars_backend.models.structures.*;
import com.hexwars.hexwars_backend.models.enums.*;
import com.hexwars.hexwars_backend.repository.BoardRepository;
import com.hexwars.hexwars_backend.repository.PlayerRepository;

import jakarta.transaction.Transactional;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class BuildingService {

    @Autowired
    private CostService costService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

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
                Coordinate settlementCoordinate = RulesService.parseCoordinate(settlementCoords);
                if (settlementCoordinate != null && placeBuilding(boardId, playerId, settlementCoordinate, false)) {
                    System.out.println("Settlement placed successfully.");
                } else {
                    System.out.println("Failed to place settlement.");
                }
                break;
            case 2:
                System.out.println("Enter the coordinates to upgrade to a city:");
                String cityCoords = scanner.nextLine();
                Coordinate cityCoordinate = RulesService.parseCoordinate(cityCoords);
                if (cityCoordinate != null && placeBuilding(boardId, playerId, cityCoordinate, true)) {
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
                    Coordinate roadStart = RulesService.parseCoordinate(roadParts[0]);
                    Coordinate roadEnd = RulesService.parseCoordinate(roadParts[1]);
                    if (roadStart != null && roadEnd != null && placeRoad(boardId, playerId, new Edge(roadStart, roadEnd))) {
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

    @Transactional
    public boolean placeInitialSettlement(Long boardId, Long playerId, Coordinate coord) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);

        if (board == null) {
            System.out.println("Board not found.");
            return false;
        }

        BuildingSpot spot = board.getVertices().get(coord);
        if (spot == null) {
            System.out.println("Coordinate does not exist.");
            return false;
        }

        if(!RulesService.canPlaceInitalSettlement(board, spot)) {
            System.out.println("Cannot place initial settlement.");
            return false;
        }

        player.addVictoryPoints(1);
        spot.placeSettlement(player);

        boardRepository.save(board);
        playerRepository.save(player);
        return true;
    }

    @Transactional
    public boolean placeBuilding(Long boardId, Long playerId, Coordinate coord, boolean isCity) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);
        if(board == null || player == null) {
            System.out.println("Board or player not found.");
            return false;
        }

        BuildingSpot spot = board.getVertices().get(coord);
        if (spot == null) {
            System.out.println("Coordinate does not exist.");
            return false;
        }

        if (isCity && RulesService.canPlaceCity(board, spot, player) && CostService.canAfford(CostType.CITY, player)) {

            costService.deductCost(CostType.CITY, player);
            player.addVictoryPoints(1);
            spot.upgradeToCity();

            System.out.println("City placed successfully.");

        } else {

            if (!RulesService.canPlaceSettlement(board, spot) && !CostService.canAfford(CostType.SETTLEMENT, player)){
                return false;
            }

            costService.deductCost(CostType.SETTLEMENT, player);
            spot.placeSettlement(player);
            player.addVictoryPoints(1);

            System.out.println("Settlement placed successfully.");
        }

        boardRepository.save(board);
        playerRepository.save(player);
        return true;
    }

    @Transactional
    public boolean placeRoad(Long boardId, Long playerId, Edge edge) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player player = playerRepository.findById(playerId).orElse(null);
        if (board == null || player == null) {
            System.out.println("Board or Player not found.");
            return false;
        }

        RoadSpot spot = board.getRoadSpot(edge);
        if (spot == null) {
            System.out.println("Edge does not exist.");
            return false;
        }

        if (!RulesService.canPlaceRoad(board, spot, player) && !CostService.canAfford(CostType.ROAD, player)) {
            return false;
        }

        costService.deductCost(CostType.ROAD, player);
        spot.placeRoad(player);

        boardRepository.save(board);
        playerRepository.save(player);

        System.out.println("Road placed successfully.");
        return true;
    }
}
