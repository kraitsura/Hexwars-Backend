package com.hexwars.hexwars_backend.models.structures;

import com.hexwars.hexwars_backend.models.Board;
import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.StructureType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "building_spots")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BuildingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Enumerated(EnumType.STRING)
    private StructureType structureType;

    @Embedded
    private Coordinate coordinate;

    @ElementCollection
    @CollectionTable(name = "adjacent_spots", joinColumns = @JoinColumn(name = "building_spot_id"))
    private Set<Coordinate> adjacentSpots = new HashSet<>();

    public BuildingSpot(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.structureType = null;
        this.player = null;
    }

    public boolean isEmpty() {
        return player == null && structureType == null;
    }

    public boolean hasSettlement() {
        return structureType == StructureType.SETTLEMENT;
    }

    public boolean hasCity() {
        return structureType == StructureType.CITY;
    }

    public void placeSettlement(Player player) {
        if (!isEmpty()) {
            throw new IllegalStateException("Cannot place settlement on non-empty spot");
        }
        this.player = player;
        this.structureType = StructureType.SETTLEMENT;
    }

    public void upgradeToCity() {
        if (!hasSettlement()) {
            throw new IllegalStateException("Can only upgrade settlement to city");
        }
        this.structureType = StructureType.CITY;
    }

    public void removeStructure() {
        this.player = null;
        this.structureType = null;
    }

    public void addAdjacentSpot(Coordinate coord) {
        adjacentSpots.add(coord);
    }

    public boolean isAdjacentTo(Coordinate coord) {
        return adjacentSpots.contains(coord);
    }

    @Override
    public String toString() {
        return "BuildingSpot{" +
               "coordinate=" + coordinate +
               ", structureType=" + (structureType != null ? structureType : "None") +
               ", player=" + (player != null ? player.getName() : "None") +
               '}';
    }
}