package com.hexwars.hexwars_backend.models;


import lombok.Data;

import com.hexwars.hexwars_backend.models.enums.PortType;

@Data
public class Edge {
    private Coordinate start;
    private Coordinate end;
    private PortType port = PortType.NONE;
    private boolean hasPort = false;

    public Edge(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    public void setPort(PortType port) {
        this.port = port;
        this.hasPort = true;
    }

    public boolean hasPort() {
        return hasPort;
    }
}
