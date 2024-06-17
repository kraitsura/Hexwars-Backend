package com.hexwars.hexwars_backend.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import com.hexwars.hexwars_backend.models.enums.TileType;

@Data
@NoArgsConstructor
public class Tile {
    private Coordinate coordinate = null;
    private TileType type;
    private List<Coordinate> vertices = new ArrayList<>();
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
