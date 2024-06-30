package com.hexwars.hexwars_backend.models.structures;

import com.hexwars.hexwars_backend.models.enums.PortType;
import com.hexwars.hexwars_backend.models.enums.TileType;
import lombok.Getter;

import java.util.*;

@Getter
public class HexMap {
    private final Map<Coordinate, Tile> tiles = new HashMap<>();
    private final Map<Coordinate, BuildingSpot> buildingSpots = new HashMap<>();
    private final Map<Edge, RoadSpot> roadSpots = new HashMap<>();

    private static final int BOARD_RADIUS = 2;

    public HexMap() {
        initializeTiles();
        initializeBuildingSpots();
        initializeRoadSpots();
        setTileRelationships();
        setCoastalTiles();
        placePorts();
    }

    private void initializeTiles() {
        List<TileType> tileTypes = getShuffledTileTypes();
        List<Integer> numbers = getShuffledNumbers();
        int typeIndex = 0;
        int numberIndex = 0;

        for (int q = -BOARD_RADIUS; q <= BOARD_RADIUS; q++) {
            for (int r = Math.max(-BOARD_RADIUS, -q-BOARD_RADIUS); r <= Math.min(BOARD_RADIUS, -q+BOARD_RADIUS); r++) {
                Coordinate coord = new Coordinate(q, r);
                TileType type = tileTypes.get(typeIndex++);
                int number = type == TileType.DESERT ? 0 : numbers.get(numberIndex++);
                Tile tile = new Tile(coord, type, number);
                tiles.putIfAbsent(coord, tile);
            }
        }
    }

    private void initializeBuildingSpots() {
        Set<Coordinate> vertexCoords = new HashSet<>();
        for (Tile tile : tiles.values()) {
            vertexCoords.addAll(Arrays.asList(calculateTileVertices(tile.getCoordinate())));
        }
        for (Coordinate coord : vertexCoords) {
            buildingSpots.putIfAbsent(coord, new BuildingSpot(coord));
        }
    }

    private void initializeRoadSpots() {
        for (Tile tile : tiles.values()) {
            Coordinate[] vertices = calculateTileVertices(tile.getCoordinate());
            for (int i = 0; i < vertices.length; i++) {
                Coordinate start = vertices[i];
                Coordinate end = vertices[(i + 1) % vertices.length];
                Edge edge = new Edge(start, end);
                roadSpots.putIfAbsent(edge, new RoadSpot(edge));
            }
        }
    }

    private void setTileRelationships() {
        for (Tile tile : tiles.values()) {
            Coordinate[] vertices = calculateTileVertices(tile.getCoordinate());
            for (int i = 0; i < vertices.length; i++) {
                BuildingSpot vertex = buildingSpots.get(vertices[i]);
                tile.addVertex(vertex);

                Edge edge = new Edge(vertices[i], vertices[(i + 1) % vertices.length]);
                RoadSpot roadSpot = roadSpots.get(edge);
                tile.addEdge(roadSpot);

                Coordinate v1 = vertices[i];
                Coordinate v2 = vertices[(i + 1) % vertices.length];
                BuildingSpot spot1 = buildingSpots.get(v1);
                BuildingSpot spot2 = buildingSpots.get(v2);
                spot1.addAdjacentSpot(v2);
                spot2.addAdjacentSpot(v1);
            }
        }
    }

    /** HELPERS **/

    private static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private static Coordinate[] calculateTileVertices(Coordinate tileCoord) {
        double q = tileCoord.getX();
        double r = tileCoord.getY();
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

    /** PORT LOGIC **/

    private void setCoastalTiles() {
        for (Tile tile : tiles.values()) {
            tile.setCoastalTile(isCoastalTile(tile.getCoordinate()));
        }
    }

    private void placePorts() {
        List<PortType> portTypes = getShuffledPortTypes();
        List<RoadSpot> coastalRoadSpots = getCoastalRoadSpots();
        Collections.shuffle(coastalRoadSpots);

        for (int i = 0; i < Math.min(portTypes.size(), coastalRoadSpots.size()); i++) {
            coastalRoadSpots.get(i).setPort(portTypes.get(i));
        }
    }

    private boolean isCoastalTile(Coordinate coord) {
        for (int dq = -1; dq <= 1; dq++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dq != dr) {
                    Coordinate neighbor = new Coordinate(coord.getX() + dq, coord.getY() + dr);
                    if (!tiles.containsKey(neighbor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<RoadSpot> getCoastalRoadSpots() {
        Set<RoadSpot> coastalRoadSpots = new HashSet<>();
        for (Tile tile : tiles.values()) {
            if (tile.isCoastalTile()) {
                coastalRoadSpots.addAll(tile.getEdges());
            }
        }
        return new ArrayList<>(coastalRoadSpots);
    }

    /** SHUFFLE LOGIC **/

    private List<TileType> getShuffledTileTypes() {
        List<TileType> types = new ArrayList<>(Arrays.asList(
            TileType.WOOD, TileType.WOOD, TileType.WOOD, TileType.WOOD,
            TileType.BRICK, TileType.BRICK, TileType.BRICK,
            TileType.ORE, TileType.ORE, TileType.ORE,
            TileType.GRAIN, TileType.GRAIN, TileType.GRAIN, TileType.GRAIN,
            TileType.WOOL, TileType.WOOL, TileType.WOOL, TileType.WOOL,
            TileType.DESERT
        ));
        Collections.shuffle(types);
        return types;
    }

    private List<Integer> getShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12));
        Collections.shuffle(numbers);
        return numbers;
    }

    private List<PortType> getShuffledPortTypes() {
        List<PortType> types = new ArrayList<>(Arrays.asList(
            PortType.GENERIC, PortType.GENERIC, PortType.GENERIC, PortType.GENERIC,
            PortType.WOOD, PortType.BRICK, PortType.ORE, PortType.GRAIN, PortType.WOOL
        ));
        Collections.shuffle(types);
        return types;
    }
}