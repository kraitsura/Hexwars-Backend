package com.hexwars.hexwars_backend.services;

import java.util.Map;

import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.ResourceType;

public interface TradeService {
    boolean trade(Player fromPlayer, Player toPlayer, Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request);
    boolean tradeWithGame(Player player, ResourceType giveResource, ResourceType getResource, int rate);
}
