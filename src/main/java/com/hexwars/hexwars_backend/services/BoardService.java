package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.*;
import com.hexwars.hexwars_backend.models.structures.*;
import com.hexwars.hexwars_backend.models.enums.*;
import com.hexwars.hexwars_backend.repository.BoardRepository;
import com.hexwars.hexwars_backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    // Add initial settlement
    public boolean addInitialSettlement(Long boardId, Player player, Coordinate coord) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            System.out.println("Board not found.");
            return false;
        }

        Building spot = board.getStructures().get(coord);
        if (spot == null) {
            System.out.println("Coordinate does not exist.");
            return false;
        }

        if (spot.hasSettlement()) {
            System.out.println("Spot already has a settlement.");
            return false;
        }

        if (!spot.canPlaceSettlement(board.getStructures(), false, null)) {
            System.out.println("Cannot place settlement here. Adjacent spots are not empty.");
            return false;
        }

        player.addVictoryPoints(1);
        spot.placeSettlement(player);

        boardRepository.save(board);
        playerRepository.save(player);
        return true;
    }

    // Method to place a building (settlement or city)
    public boolean placeBuilding(Long boardId, Coordinate coord, Player player, boolean isCity) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            System.out.println("Board not found.");
            return false;
        }

        Building spot = board.getStructures().get(coord);
        if (spot == null) {
            System.out.println("Coordinate does not exist.");
            return false;
        }

        if (isCity) {
            if (!spot.hasSettlement() || spot.hasCity() || !player.hasRequiredResources(StructureType.CITY.getRequiredResources())) {
                System.out.println("Cannot place city here. Either no settlement exists or a city already exists or not enough resources.");
                return false;
            }
            player.useResources(StructureType.CITY.getRequiredResources());
            spot.setStructureType(StructureType.CITY);
            player.addVictoryPoints(1);
            spot.placeCity();
        } else {
            if (spot.hasSettlement()) {
                System.out.println("Spot already has a settlement.");
                return false;
            }

            if (!spot.canPlaceSettlement(board.getStructures(), true, board.getRoads()) || !player.hasRequiredResources(StructureType.SETTLEMENT.getRequiredResources())) {
                System.out.println("Cannot place settlement here. Adjacent spots are not empty or no roads leading to it or not enough resources.");
                return false;
            }

            player.useResources(StructureType.SETTLEMENT.getRequiredResources());
            spot.placeSettlement(player);
            player.addVictoryPoints(1);
        }

        boardRepository.save(board);
        playerRepository.save(player);
        return true;
    }

    public boolean placeRoad(Long boardId, Player player, Edge edge) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            System.out.println("Board not found.");
            return false;
        }

        Road roadSpot = board.getRoadSpot(edge);
        if (roadSpot.getPlayer() != null) {
            System.out.println("A road already exists at this edge.");
            return false;
        }

        if (!board.isValidRoadSpot(edge, player)) {
            System.out.println("Invalid road placement. The edge is not a valid building spot.");
            return false;
        }

        Map<ResourceType, Integer> requiredResources = StructureType.ROAD.getRequiredResources();
        if (!player.hasRequiredResources(requiredResources)) {
            System.out.println("Not enough resources to build a road.");
            return false;
        }

        player.useResources(requiredResources);
        roadSpot.placeRoad(player);

        boardRepository.save(board);
        playerRepository.save(player);

        System.out.println("Road placed successfully.");
        return true;
    }
}
