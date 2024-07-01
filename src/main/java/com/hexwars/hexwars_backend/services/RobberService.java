package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.models.structures.Coordinate;

public interface RobberService {
    void moveRobber(Long boardId, Coordinate coord);
    void robPlayer(Long boardId, Long playerId, ResourceType resourceType);
}
