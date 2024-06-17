package com.hexwars.hexwars_backend.models.enums;

public enum TileType {
    WOOD,
    BRICK,
    WHEAT,
    SHEEP,
    ORE,
    DESERT, EMPTY;

    ResourceType getResourceType() {
        switch (this) {
            case WOOD:
            return ResourceType.WOOD;
            case BRICK:
            return ResourceType.BRICK;
            case WHEAT:
            return ResourceType.WHEAT;
            case SHEEP:
            return ResourceType.SHEEP;
            case ORE:
            return ResourceType.ORE;
            case DESERT:
            return ResourceType.NONE;
            case EMPTY:
            return ResourceType.NONE;
            default:
            throw new IllegalArgumentException("Invalid tile type");
        }
    }
}
