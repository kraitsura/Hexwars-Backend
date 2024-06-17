package com.hexwars.hexwars_backend.models.enums;

import java.util.Map;

public enum StructureType {
    SETTLEMENT(Map.of(ResourceType.WOOD, 1, ResourceType.BRICK, 1, ResourceType.WHEAT, 1, ResourceType.SHEEP, 1)),
    CITY(Map.of(ResourceType.WHEAT, 2, ResourceType.ORE, 3)),
    ROAD(Map.of(ResourceType.WOOD, 1, ResourceType.BRICK, 1));

    private final Map<ResourceType, Integer> requiredResources;

    StructureType(Map<ResourceType, Integer> requiredResources) {
        this.requiredResources = requiredResources;
    }

    public Map<ResourceType, Integer> getRequiredResources() {
        return requiredResources;
    }
}
