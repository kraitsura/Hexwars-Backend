package com.hexwars.hexwars_backend.models.structures;

import lombok.Data;

import com.hexwars.hexwars_backend.models.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Data
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "edge_id")
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
