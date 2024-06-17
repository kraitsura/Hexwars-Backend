package com.hexwars.hexwars_backend.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hexwars.hexwars_backend.models.enums.TileType;

@Getter
public class HexMap {
    private Map<Coordinate, Building> structures = new HashMap<>();
    private Map<Coordinate, Tile> tiles = new HashMap<>();
    private Map<Edge, Road> roads = new HashMap<>();

    public HexMap() {
        initializeTiles(); 
    }

    // TILE SETUP
    private void initializeTiles() {
        // Define a full hexagon (radius 2)
        for (int q = -2; q <= 2; q++) {
            for (int r = Math.max(-2, -q-2); r <= Math.min(2, -q+2); r++) {
                Coordinate tileCoord = new Coordinate(q, r);
                Tile tile = new Tile(tileCoord, TileType.EMPTY, 0);
                tiles.put(tile.getCoordinate(), tile);
                calculateVertices(tile);
            }
        }
        populateEdges(); // Populate edges based on vertices
    }

        // Method to calculate and assign vertices to the tile
    private void calculateVertices(Tile tile) {
        double q = tile.getCoordinate().getX();
        double r = tile.getCoordinate().getY();

        Coordinate[] vertexCoords = getHexVertices(q, r);

        for (Coordinate vertexCoord : vertexCoords) {
            tile.addVertex(vertexCoord);
            if (!structures.containsKey(vertexCoord)) {
                structures.put(vertexCoord, new Building(null,null, vertexCoord));

            }
        }

        // Add adjacent vertices
        for (int i = 0; i < vertexCoords.length; i++) {
            Coordinate v1 = vertexCoords[i];
            Coordinate v2 = vertexCoords[(i + 1) % vertexCoords.length];
            Building spot1 = structures.get(v1);
            Building spot2 = structures.get(v2);
            spot1.addAdjacentCoordinate(v2);
            spot2.addAdjacentCoordinate(v1);
        }
    }
    

    // Hex Logic
    private static Coordinate[] getHexVertices(double q, double r) {
        double[] center = axialToCartesian(q, r);
        Coordinate[] vertices = new Coordinate[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            double x = center[0] + Math.cos(angle);
            double y = center[1] + Math.sin(angle);
            vertices[i] = new Coordinate(round(x, 6), round(y, 6));
        }
        return vertices;
    }

    private static double[] axialToCartesian(double q, double r) {
        double x = 1.5 * q;
        double y = Math.sqrt(3) * (r + q / 2.0);
        return new double[]{x, y};
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // Method to populate edges based on vertices
    private void populateEdges() {
        for (Tile tile : tiles.values()) {
            List<Coordinate> vertices = tile.getVertices();
            for (int i = 0; i < vertices.size(); i++) {
                Coordinate start = vertices.get(i);
                Coordinate end = vertices.get((i + 1) % vertices.size());
                Edge edge = new Edge(start, end);
                tile.addEdge(edge); 
                roads.putIfAbsent(edge, new Road(edge)); // Initialize edge in roads map
            }
        }
        placePorts(); // Place ports on predefined edges
        placeTiles(); // Place tiles on predefined vertices
    }

    // Method to place ports on predefined edges
    private void placePorts() {
        // rand ports here
        
    }

    private void placeTiles() {
        // rand tiles here
    }  
}
