package com.hexwars.hexwars_backend.models.structures;

import lombok.Data;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@Embeddable
public class Coordinate {
    private double x;
    private double y;
}
