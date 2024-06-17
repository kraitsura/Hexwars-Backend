package com.hexwars.hexwars_backend.models.structures;

import com.hexwars.hexwars_backend.models.enums.TileType;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
@Data
@NoArgsConstructor
public class Tile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Coordinate coordinate;

    @Enumerated(EnumType.STRING)
    private TileType type;

    @ElementCollection
    @CollectionTable(name = "tile_vertices", joinColumns = @JoinColumn(name = "tile_id"))
    @Column(name = "vertex")
    private List<Coordinate> vertices = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "tile_edges",
        joinColumns = @JoinColumn(name = "tile_id"),
        inverseJoinColumns = @JoinColumn(name = "edge_id")
    )
    private Set<Edge> edges = new HashSet<>();

    private int number;
    private boolean hasRobber = false;

    public Tile(Coordinate coordinate, TileType type, int number) {
        this.coordinate = coordinate;
        this.type = type;
        this.number = number;
    }

    public void addVertex(Coordinate vertex) {
        this.vertices.add(vertex);
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    // Custom toString to include vertices and edges details
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tile at ").append(coordinate).append(" with type ").append(type).append(" and number ").append(number).append("\n");
        sb.append("Vertices:\n");
        for (Coordinate vertex : vertices) {
            sb.append("  ").append(vertex).append("\n");
        }
        sb.append("Edges:\n");
        for (Edge edge : edges) {
            sb.append("  ").append(edge).append("\n");
        }
        return sb.toString();
    }
}
