package com.hexwars.hexwars_backend.services.implementation;

import com.hexwars.hexwars_backend.models.Player;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import com.hexwars.hexwars_backend.repository.PlayerRepository;
import com.hexwars.hexwars_backend.services.TradeService;
import com.hexwars.hexwars_backend.services.utils.CostService;
import com.hexwars.hexwars_backend.services.utils.Utils;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private CostService costService;

    @Autowired
    private PlayerRepository playerRepository;

    // Method for player-to-player trade
    @Override
    @Transactional
    public boolean trade(Player fromPlayer, Player toPlayer, Map<ResourceType, Integer> offer, Map<ResourceType, Integer> request) {
        if (fromPlayer == null || toPlayer == null) {
            System.out.println("Player not found");
            return false; // Trade failed
        }

        if (!Utils.hasRequiredResources(fromPlayer, offer) && !Utils.hasRequiredResources(toPlayer, request)) {
            System.out.println("Player does not have required resources");
            return false;
        }

        costService.useResources(fromPlayer, offer);
        costService.useResources(toPlayer, request);
        costService.giveResources(fromPlayer, request);
        costService.giveResources(toPlayer, offer);

        playerRepository.save(fromPlayer);
        playerRepository.save(toPlayer);
        System.out.println("Trade successful");
        return true; 
    }

    // Method for bank and port trade
    @Override
    @Transactional
    public boolean tradeWithGame(Player player, ResourceType giveResource, ResourceType getResource, int rate) {
        if (player == null) {
            System.out.println("Player not found");
            return false; // Trade failed
        }
        Map<ResourceType, Integer> offer = new HashMap<>();
        offer.put(giveResource, rate);
        if (!Utils.hasRequiredResources(player, offer)) {
            System.out.println("Player does not have required resources");
            return false; // Trade failed
        }
        
        costService.useResources(player, offer);
        player.addResource(getResource, 1);
        playerRepository.save(player);
        return true; // Trade successful
    }
}
