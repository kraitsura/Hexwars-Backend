package com.hexwars.hexwars_backend.models.structures;


import lombok.Data;

import java.io.Serializable;

import com.hexwars.hexwars_backend.models.enums.PortType;

import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class Edge implements Serializable {
    private static final long serialVersionUID = 1L;
    
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
