package com.hexwars.hexwars_backend.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Board {
    private Map<Coordinate, Building> structures;
    private Map<Edge, Road> roads;
    private Map<Coordinate, Tile> tiles;
    private Map<String, Player> players = new HashMap<>();

    public Board() {
        HexMap newMap = new HexMap();
        this.structures = newMap.getStructures();
        this.roads = newMap.getRoads();
        this.tiles = newMap.getTiles();
    }

    public boolean isValidBuildingSpot(Coordinate coord, Boolean checkRoads) {
        Building spot = structures.get(coord);
        return spot.canPlaceSettlement(structures, checkRoads, roads);
    }

    public boolean isValidRoadSpot(Edge edge, Player player) {
        Coordinate start = edge.getStart();
        Coordinate end = edge.getEnd();
        Road road = roads.get(edge);
        if (!road.hasRoad()) {
            for(Road r : roads.values()) {
                if (r.getPlayer() == player) {
                    if (r.getEdge().getStart().equals(start) || r.getEdge().getEnd().equals(start) || r.getEdge().getStart().equals(end) || r.getEdge().getEnd().equals(end)) {
                        return true;
                    }
                }
            }
            for(Building b : structures.values()) {
                if (b.getPlayer() == player) {
                    if (b.getCoordinate().equals(start) || b.getCoordinate().equals(end)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // HELPERS
    public Building getBuildingSpot(Coordinate coordinate) {
        return structures.get(coordinate);
    }

    public Road getRoadSpot(Edge edge) {
        return roads.get(edge);
    }

    public Player getPlayer(String playerName) {
        return players.get(playerName);
    }

}