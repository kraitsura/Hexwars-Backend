package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.structures.Coordinate;
import com.hexwars.hexwars_backend.models.structures.Edge;

public interface BuildService {
    boolean placeInitialSettlement(Long boardId, Long playerId, Coordinate coord);
    boolean placeBuilding(Long boardId, Long playerId, Coordinate coordinate, boolean upgrade);
    boolean placeRoad(Long boardId, Long playerId, Edge edge);
}
