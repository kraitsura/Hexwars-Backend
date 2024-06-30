package com.hexwars.hexwars_backend.models.structures;

import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.PortType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "road_spots")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RoadSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Embedded
    private Edge edge;

    private boolean hasRoad;

    @Enumerated(EnumType.STRING)
    private PortType portType;

    public RoadSpot(Edge edge) {
        this.edge = edge;
        this.player = null;
        this.hasRoad = false;
        this.portType = PortType.NONE;
    }

    public boolean isEmpty() {
        return !hasRoad;
    }

    public void placeRoad(Player player) {
        if (!isEmpty()) {
            throw new IllegalStateException("Cannot place road on non-empty spot");
        }
        this.player = player;
        this.hasRoad = true;
    }

    public void removeRoad() {
        this.player = null;
        this.hasRoad = false;
    }

    public boolean isOwnedBy(Player player) {
        return this.player != null && this.player.equals(player);
    }

    public void setPort(PortType portType) {
        this.portType = portType;
    }

    public boolean hasPort() {
        return this.portType != PortType.NONE;
    }

    public PortType getPortType() {
        return this.portType;
    }

    @Override
    public String toString() {
        return "RoadSpot{" +
               "edge=" + edge +
               ", hasRoad=" + hasRoad +
               ", player=" + (player != null ? player.getName() : "None") +
               ", portType=" + portType +
               '}';
    }
}