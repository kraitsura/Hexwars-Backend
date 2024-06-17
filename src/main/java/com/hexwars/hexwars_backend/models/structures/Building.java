package com.hexwars.hexwars_backend.models.structures;

import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.StructureType;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = true) // Allow null values
    private Player player;

    @Enumerated(EnumType.STRING)
    private StructureType structureType;

    @ElementCollection
    private Set<Coordinate> adjacentSpots = new HashSet<>();

    private boolean hasSettlement = false;
    private boolean hasCity = false;

    @Embedded
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

    public void placeSettlement(Player player) {
        this.player = player;
        this.structureType = StructureType.SETTLEMENT;
        this.hasSettlement = true;
    }

    public void placeCity() {
        this.structureType = StructureType.CITY;
        this.hasCity = true;
        this.hasSettlement = false;
    }
}
