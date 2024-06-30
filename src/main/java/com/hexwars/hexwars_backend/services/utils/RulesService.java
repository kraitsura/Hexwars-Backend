package com.hexwars.hexwars_backend.services.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.hexwars.hexwars_backend.models.Board;
import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.models.structures.BuildingSpot;
import com.hexwars.hexwars_backend.models.structures.Coordinate;
import com.hexwars.hexwars_backend.models.structures.Edge;
import com.hexwars.hexwars_backend.models.structures.RoadSpot;

@Service
public class RulesService {

    public static boolean canPlaceCity(Board board, BuildingSpot spot, Player player) {
        if (spot.hasCity()) {
            System.out.println("Spot already has a city.");
            return false;
        }
        if (!spot.hasSettlement()) {
            System.out.println("Spot does not have a settlement.");
            return false;
        }
        if (spot.getPlayer() != player) {
            System.out.println("Player does not own this spot.");
            return false;
        }
        
        return canPlaceBuilding(board, spot);
    }

    public static boolean canPlaceSettlement(Board board, BuildingSpot spot) {
        if (spot.hasSettlement()) {
            System.out.println("Spot already has a settlement.");
            return false;
        }
        if (spot.hasCity()) {
            System.out.println("Spot already has a city.");
            return false;
        }
        return canPlaceBuilding(board, spot);
    }

    public static boolean canPlaceBuilding(Board board, BuildingSpot spot) {
        Map<Coordinate, BuildingSpot> vertices = board.getVertices();
        if (spot.hasSettlement()) {
            System.out.println("Spot already has a settlement.");
            return false;
        }
        Map<Edge, RoadSpot> edges = board.getEdges();
        Set<Coordinate> adjacentSpots = spot.getAdjacentSpots();
        for (Coordinate adjacent : adjacentSpots) {
            BuildingSpot adjacentSpot = vertices.get(adjacent);
            if (adjacentSpot != null && !adjacentSpot.isEmpty()) {
                System.out.println("Cannot place building here. Adjacent spots are not empty.");
                return false;
            }
        }
        return isConnectedToRoad(edges, adjacentSpots, spot.getCoordinate());
    }

    public static boolean canPlaceInitalSettlement(Board board, BuildingSpot spot) {
        Map<Coordinate, BuildingSpot> vertices = board.getVertices();
        Set<Coordinate> adjacentSpots = spot.getAdjacentSpots();
        for (Coordinate adjacent : adjacentSpots) {
            BuildingSpot adjacentSpot = vertices.get(adjacent);
            if (adjacentSpot != null && !adjacentSpot.isEmpty()) {
                System.out.println("Cannot place building here. Adjacent spots are not empty.");
                return false;
            }
        }
        return true;
    }

    private static boolean isConnectedToRoad(Map<Edge, RoadSpot> roads, Set<Coordinate> adjacentSpots, Coordinate thisSpot) {
        for (Coordinate adjacent : adjacentSpots) {
            Edge edge = new Edge(thisSpot, adjacent);
            if (roads.containsKey(edge)) {
                return true;
            }
        }
        System.out.println("Cannot place building here. Not connected to road.");
        return false;
    }

    public static boolean canPlaceRoad(Board board, RoadSpot spot, Player player) {
        Edge edge = spot.getEdge();
        if (edge == null) {
            System.out.println("Edge does not exist.");
            return false;
        }
        if (spot.isHasRoad()) {
            System.out.println("A road already exists at this edge.");
            return false;
        }
        
        for (RoadSpot r : board.getEdges().values()) {
            if (r.getPlayer() == player) {
                if (r.getEdge().getStart().equals(edge.getStart()) || 
                    r.getEdge().getEnd().equals(edge.getStart()) ||
                    r.getEdge().getStart().equals(edge.getEnd()) || 
                    r.getEdge().getEnd().equals(edge.getEnd())) {
                    return true;
                }
            }
        }

        for (BuildingSpot b : board.getVertices().values()) {
            if (b.getPlayer() == player) {
                if (b.getCoordinate().equals(edge.getStart()) || 
                    b.getCoordinate().equals(edge.getEnd())) {
                    return true;
                }
            }
        }

        System.out.println("Cannot place road here. Not connected to player's buildings or roads.");
        return false;
    }

    // HELPERS FOR TESTING 
    public static Coordinate parseCoordinate(String input) {
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

    public static Map<ResourceType, Integer> parseResources(String input) {
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
