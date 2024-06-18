package com.hexwars.hexwars_backend.models;

import lombok.Getter;

import java.util.Map;

import com.hexwars.hexwars_backend.models.structures.Building;
import com.hexwars.hexwars_backend.models.structures.Coordinate;
import com.hexwars.hexwars_backend.models.structures.DevDeck;
import com.hexwars.hexwars_backend.models.structures.Edge;
import com.hexwars.hexwars_backend.models.structures.HexMap;
import com.hexwars.hexwars_backend.models.structures.Road;
import com.hexwars.hexwars_backend.models.structures.Tile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.OneToOne;

@Entity
@Getter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @MapKeyClass(Coordinate.class)
    @CollectionTable(name = "board_structures", joinColumns = @JoinColumn(name = "board_id"))
    private Map<Coordinate, Building> structures;

    @ElementCollection
    @MapKeyClass(Edge.class)
    @CollectionTable(name = "board_roads", joinColumns = @JoinColumn(name = "board_id"))
    private Map<Edge, Road> roads;

    @ElementCollection
    @MapKeyClass(Coordinate.class)
    @CollectionTable(name = "board_tiles", joinColumns = @JoinColumn(name = "board_id"))
    private Map<Coordinate, Tile> tiles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dev_deck_id", referencedColumnName = "id")
    private DevDeck devDeck;

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

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

}