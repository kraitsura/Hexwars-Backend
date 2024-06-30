package com.hexwars.hexwars_backend.models.structures;

import com.hexwars.hexwars_backend.models.enums.TileType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tiles")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Tile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Coordinate coordinate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TileType type;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private boolean hasRobber;

    @Column(nullable = false)
    private boolean isCoastalTile;

    @ManyToMany
    @JoinTable(
        name = "tile_vertices",
        joinColumns = @JoinColumn(name = "tile_id"),
        inverseJoinColumns = @JoinColumn(name = "building_spot_id")
    )
    private Set<BuildingSpot> vertices = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "tile_edges",
        joinColumns = @JoinColumn(name = "tile_id"),
        inverseJoinColumns = @JoinColumn(name = "road_spot_id")
    )
    private Set<RoadSpot> edges = new HashSet<>();

    public Tile(Coordinate coordinate, TileType type, int number) {
        this.coordinate = coordinate;
        this.type = type;
        this.number = number;
        this.hasRobber = type == TileType.DESERT;
        this.isCoastalTile = false;
    }

    public void addVertex(BuildingSpot vertex) {
        vertices.add(vertex);
    }

    public void addEdge(RoadSpot edge) {
        edges.add(edge);
    }

    public boolean isProductionTile() {
        return type != TileType.DESERT && number != 0;
    }

    // Utility method to check if this tile is adjacent to another tile
    public boolean isAdjacentTo(Tile other) {
        int dx = (int) (this.coordinate.getX() - other.coordinate.getX());
        int dy = (int) (this.coordinate.getY() - other.coordinate.getY());
        int dz = -dx - dy;
        return Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz))) == 1;
    }

    @Override
    public String toString() {
        return "Tile{" +
               "id=" + id +
               ", coordinate=" + coordinate +
               ", type=" + type +
               ", number=" + (number != 0 ? number : "N/A") +
               ", hasRobber=" + hasRobber +
               ", isCoastalTile=" + isCoastalTile +
               '}';
    }
}