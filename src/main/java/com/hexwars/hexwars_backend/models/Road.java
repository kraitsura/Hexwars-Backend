package com.hexwars.hexwars_backend.models;

import lombok.Data;

@Data
public class Road {
    private Player player;
    private Edge edge;
    private boolean hasRoad;

    public Road(Edge edge) {
        this.player = null;
        this.edge = edge;
        this.hasRoad = false;
    }

    public void placeRoad(Player player) {
        this.player = player;
        this.hasRoad = true;
    }

    public boolean hasRoad() {
        return hasRoad;
    }
}
