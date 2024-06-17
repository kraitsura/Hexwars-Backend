package com.hexwars.hexwars_backend.models.structures;

import lombok.Data;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@Embeddable
public class Coordinate implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double x;
    private double y;
}
