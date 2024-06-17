package com.hexwars.hexwars_backend.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class Coordinate {
    private double x;
    private double y;
}
