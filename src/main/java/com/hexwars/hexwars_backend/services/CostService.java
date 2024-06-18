package com.hexwars.hexwars_backend.services;

import com.hexwars.hexwars_backend.models.enums.CostType;
import com.hexwars.hexwars_backend.models.enums.ResourceType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Service
public class CostService {

    private static final Map<CostType, Map<ResourceType, Integer>> costMap;

    static {
        Map<CostType, Map<ResourceType, Integer>> map = new EnumMap<>(CostType.class);

        // Initialize costs for development cards
        Map<ResourceType, Integer> devCardCost = new EnumMap<>(ResourceType.class);
        devCardCost.put(ResourceType.WHEAT, 1);
        devCardCost.put(ResourceType.SHEEP, 1);
        devCardCost.put(ResourceType.ORE, 1);
        map.put(CostType.DEVELOPMENT_CARD, Collections.unmodifiableMap(devCardCost));

        // Initialize costs for settlements
        Map<ResourceType, Integer> settlementCost = new EnumMap<>(ResourceType.class);
        settlementCost.put(ResourceType.BRICK, 1);
        settlementCost.put(ResourceType.WOOD, 1);
        settlementCost.put(ResourceType.SHEEP, 1);
        settlementCost.put(ResourceType.WHEAT, 1);
        map.put(CostType.SETTLEMENT, Collections.unmodifiableMap(settlementCost));

        // Initialize costs for cities
        Map<ResourceType, Integer> cityCost = new EnumMap<>(ResourceType.class);
        cityCost.put(ResourceType.WHEAT, 2);
        cityCost.put(ResourceType.ORE, 3);
        map.put(CostType.CITY, Collections.unmodifiableMap(cityCost));

        // Initialize costs for roads
        Map<ResourceType, Integer> roadCost = new EnumMap<>(ResourceType.class);
        roadCost.put(ResourceType.BRICK, 1);
        roadCost.put(ResourceType.WOOD, 1);
        map.put(CostType.ROAD, Collections.unmodifiableMap(roadCost));

        costMap = Collections.unmodifiableMap(map);
    }

    public Map<ResourceType, Integer> getCost(CostType costType) {
        return costMap.get(costType);
    }

    public boolean canAfford(CostType costType, Map<ResourceType, Integer> playerResources) {
        Map<ResourceType, Integer> requiredResources = getCost(costType);
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            ResourceType resource = entry.getKey();
            int requiredAmount = entry.getValue();
            if (playerResources.getOrDefault(resource, 0) < requiredAmount) {
                return false;
            }
        }
        return true;
    }

    public void deductCost(CostType costType, Map<ResourceType, Integer> playerResources) {
        Map<ResourceType, Integer> requiredResources = getCost(costType);
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            ResourceType resource = entry.getKey();
            int requiredAmount = entry.getValue();
            playerResources.put(resource, playerResources.get(resource) - requiredAmount);
        }
    }
}
