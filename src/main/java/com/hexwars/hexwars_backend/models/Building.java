package com.hexwars.hexwars_backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hexwars.hexwars_backend.models.enums.StructureType;

@Data
@NoArgsConstructor
public class Building {
    private Player player;
    private StructureType structureType;
    private Set<Coordinate> adjacentSpots = new HashSet<>();
    private boolean hasSettlement = false;
    private boolean hasCity = false;
    private Coordinate coordinate;

    public Building(Player player, StructureType structureType, Coordinate coord) {
        this.player = player;
        this.structureType = structureType;
        this.coordinate = coord;
    }

    public void addAdjacentCoordinate(Coordinate coord) {
        adjacentSpots.add(coord);
    }

    public boolean isEmpty() {
        return player == null && structureType == null;
    }

    public boolean canPlaceSettlement(Map<Coordinate, Building> structures, boolean checkRoads, Map<Edge, Road> roads) {
        for (Coordinate adjacent : adjacentSpots) {
            Building spot = structures.get(adjacent);
            if (spot != null && !spot.isEmpty()) {
                return false;
            }
        }

        if (checkRoads) {
            return isConnectedToRoad(roads);
        }
        return true;
    }

    public boolean isConnectedToRoad(Map<Edge, Road> roads) {
        for (Coordinate adjacent : adjacentSpots) {
            Edge edge = new Edge(this.getCoordinate(), adjacent);
            if (roads.containsKey(edge)) {
                return true;
            }
        }
        return false;
    }

    public void placeSettlement() {
        this.hasSettlement = true;
    }

    public void placeCity() {
        this.hasCity = true;
        this.hasSettlement = false;
    }

}
