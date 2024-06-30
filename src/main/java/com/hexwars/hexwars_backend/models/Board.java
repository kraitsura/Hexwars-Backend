package com.hexwars.hexwars_backend.models;

import lombok.Getter;

import java.util.Map;

import com.hexwars.hexwars_backend.models.structures.*;

import jakarta.persistence.*;

@Entity
@Table(name = "boards")
@Getter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    @MapKeyClass(Coordinate.class)
    private Map<Coordinate, BuildingSpot> vertices;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    @MapKeyClass(Edge.class)
    private Map<Edge, RoadSpot> edges;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    @MapKeyClass(Coordinate.class)
    private Map<Coordinate, Tile> tiles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dev_deck_id", referencedColumnName = "id")
    private DevDeck devDeck;

    @OneToOne(mappedBy = "board")
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    public Board(GameSession gameSession) {
        HexMap newMap = new HexMap();
        this.devDeck = new DevDeck();
        this.vertices = newMap.getBuildingSpots();
        this.edges = newMap.getRoadSpots();
        this.tiles = newMap.getTiles();
        this.gameSession = gameSession;
    }

    // HELPERS
    public BuildingSpot getBuildingSpot(Coordinate coordinate) {
        return vertices.get(coordinate);
    }

    public RoadSpot getRoadSpot(Edge edge) {
        return edges.get(edge);
    }

    public Tile getTile(Coordinate coordinate) {
        return tiles.get(coordinate);
    }
}