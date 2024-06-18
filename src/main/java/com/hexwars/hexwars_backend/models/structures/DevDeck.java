package com.hexwars.hexwars_backend.models.structures;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hexwars.hexwars_backend.models.enums.DevCardType;

@Entity
@Getter
@Setter
public class DevDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<DevCardType> deck = new ArrayList<>();

    public DevDeck() {
        initializeDeck();
    }

    // Initialize the deck with a fixed number of each type of dev card
    private void initializeDeck() {
        addCards(DevCardType.KNIGHT, 14);
        addCards(DevCardType.MONOPOLY, 2);
        addCards(DevCardType.ROAD_BUILDING, 2);
        addCards(DevCardType.YEAR_OF_PLENTY, 2);
        addCards(DevCardType.VP_UNIVERSITY, 1);
        addCards(DevCardType.VP_MARKET, 1);
        addCards(DevCardType.VP_GREAT_HALL, 1);
        addCards(DevCardType.VP_CHAPEL, 1);
        addCards(DevCardType.VP_LIBRARY, 1);
        Collections.shuffle(deck);
    }

    // Add a specific number of a type of dev card to the deck
    private void addCards(DevCardType type, int count) {
        for (int i = 0; i < count; i++) {
            deck.add(type);
        }
    }

    // Draw a random card from the deck
    public DevCardType drawCard() {
        if (deck.isEmpty()) {
            return null; // Or throw an exception
        }
        int randomIndex = (int) (Math.random() * deck.size());
        return deck.remove(randomIndex);
    }

    // Check if the deck is empty
    public boolean isEmpty() {
        return deck.isEmpty();
    }

    // Get the number of remaining cards in the deck
    public int remainingCards() {
        return deck.size();
    }
}
