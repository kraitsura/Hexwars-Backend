package com.hexwars.hexwars_backend.services.implementation;

import com.hexwars.hexwars_backend.models.*;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.models.structures.*;
import com.hexwars.hexwars_backend.repository.BoardRepository;
import com.hexwars.hexwars_backend.repository.PlayerRepository;
import com.hexwars.hexwars_backend.services.RobberService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RobberServiceImpl implements RobberService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public void handleRobber(Long boardId, Long robberId, Coordinate tileCoordinate, boolean stealFromAll) {
        Board board = boardRepository.findById(boardId).orElse(null);
        Player robber = playerRepository.findById(robberId).orElse(null);
        if (board == null) {
            System.out.println("Board not found.");
            return;
        }

        Tile targetTile = board.getTiles().get(tileCoordinate);
        if (targetTile == null) {
            System.out.println("Invalid tile coordinate.");
            return;
        }

        List<Player> playersOnTile = new ArrayList<>();
        for (BuildingSpot spot : targetTile.getVertices()) {
            if (spot != null && spot.getPlayer() != null) {
                playersOnTile.add(spot.getPlayer());
            }
        }

        if (playersOnTile.isEmpty()) {
            System.out.println("No players to rob on this tile.");
            return;
        }

        if (stealFromAll) {
            for (Player victim : playersOnTile) {
                stealResource(robber, victim);
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose a player to rob:");
            for (int i = 0; i < playersOnTile.size(); i++) {
                System.out.println((i + 1) + ". " + playersOnTile.get(i).getName());
            }
            int choice = scanner.nextInt() - 1;
            if (choice >= 0 && choice < playersOnTile.size()) {
                Player victim = playersOnTile.get(choice);
                stealResource(robber, victim);
            } else {
                System.out.println("Invalid choice.");
            }
            scanner.close();
        }

        boardRepository.save(board); // Persist changes to the board
        playerRepository.save(robber); // Persist changes to the robber
    }

    private void stealResource(Player robber, Player victim) {
        Map<ResourceType, Integer> victimResources = victim.getResources();
        if (victimResources.isEmpty()) {
            System.out.println(victim.getName() + " has no resources to steal.");
            return;
        }

        List<ResourceType> resourceList = new ArrayList<>(victimResources.keySet());
        ResourceType stolenResource = resourceList.get(new Random().nextInt(resourceList.size()));
        victim.removeResource(stolenResource, 1);
        robber.addResource(stolenResource, 1);
        System.out.println(robber.getName() + " stole a " + stolenResource + " from " + victim.getName());

        playerRepository.save(victim);  
        playerRepository.save(robber);  
    }
    
    @Override
    @Transactional
    public void moveRobber(Long boardId, Coordinate newTileCoordinate) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            System.out.println("Board not found.");
            return;
        }

        // Find the new tile and move the robber
        Tile newTile = board.getTiles().get(newTileCoordinate);
        if (newTile == null) {
            System.out.println("Invalid tile coordinate.");
            return;
        }

        // Find the old tile with the robber
        Tile oldTile = board.getTiles().values().stream()
            .filter(Tile::isHasRobber)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile currently has the robber"));

        if (oldTile == null) {
            System.out.println("Robber not found.");
            return;
        } 

        oldTile.setHasRobber(false);  // Remove the robber from the old tile
        newTile.setHasRobber(true);  // Place the robber on the new tile


        boardRepository.save(board); // Persist changes to the board
    }

    @Override
    @Transactional
    public void robPlayer(Long robberId, Long victimId, ResourceType resourceType) {
        Player robber = playerRepository.findById(robberId).orElse(null);
        Player victim = playerRepository.findById(victimId).orElse(null);
        if (victim.removeResource(resourceType, 1)) {
            robber.addResource(resourceType, 1);
            System.out.println(robber.getName() + " stole from " + victim.getName());
        } else {
            System.out.println("Failed to rob.");
            return;
        }
        playerRepository.save(victim);  // Persist changes to the victim
        playerRepository.save(robber);  // Persist changes to the robber
    }
}
