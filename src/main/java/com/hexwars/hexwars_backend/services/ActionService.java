package com.hexwars.hexwars_backend.services;

import java.util.Scanner;

public interface ActionService {
    void build(Long boardId, Long playerId, Scanner scanner);
    void trade(Long gameId, Long playerId, Scanner scanner);
    void buyDevCard(Long boardId, Long playerId);
}
