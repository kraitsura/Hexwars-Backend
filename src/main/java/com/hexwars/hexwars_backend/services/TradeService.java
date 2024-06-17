package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TradeService {

    @Autowired
    private PlayerRepository playerRepository;

    // Method for player-to-player trade
    public boolean trade(Player fromPlayer, Player toPlayer, Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request) {
        if (fromPlayer.hasRequiredResources(offer) && toPlayer.hasRequiredResources(request)) {
            fromPlayer.useResources(offer);
            toPlayer.useResources(request);
            fromPlayer.addResources(request);
            toPlayer.addResources(offer);
            playerRepository.save(fromPlayer);
            playerRepository.save(toPlayer);
            return true; // Trade successful
        }
        return false; // Trade failed
    }

    // Method for bank and port trade
    public boolean tradeWithGame(Player player, ResourceType giveResource, ResourceType getResource, int rate) {
        Map<ResourceType, Integer> offer = new HashMap<>();
        offer.put(giveResource, rate);
        if (player.hasRequiredResources(offer)) {
            player.useResources(offer);
            player.addSingleResource(getResource, 1);
            playerRepository.save(player);
            return true; // Trade successful
        }
        return false; // Trade failed
    }
}
