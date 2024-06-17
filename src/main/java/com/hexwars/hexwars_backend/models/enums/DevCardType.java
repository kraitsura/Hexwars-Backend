package com.hexwars.hexwars_backend.models.enums;

import java.util.EnumMap;
import java.util.Map;

public enum DevCardType {
    KNIGHT(createCost()),
    VICTORY_POINT(createCost()),
    ROAD_BUILDING(createCost()),
    YEAR_OF_PLENTY(createCost()),
    MONOPOLY(createCost());

    private final Map<ResourceType, Integer> cost;

    DevCardType(Map<ResourceType, Integer> cost) {
        this.cost = cost;
    }

    public Map<ResourceType, Integer> getCost() {
        return cost;
    }

    private static Map<ResourceType, Integer> createCost() {
        Map<ResourceType, Integer> cost = new EnumMap<>(ResourceType.class);
        cost.put(ResourceType.SHEEP, 1);
        cost.put(ResourceType.ORE, 1);
        cost.put(ResourceType.WHEAT, 1);
        return cost;
    }
}