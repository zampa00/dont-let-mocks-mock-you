/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import it.workingsoftware.domain.exception.NoPotionsException;
import it.workingsoftware.domain.exception.PotionLimitUsageReachedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HealService {

    final List<UUID> usedPotions = new ArrayList<>();

    public Adventurer heal(Adventurer adventurer, UUID encounterId) {
        if (adventurer.numberOfPotions() <= 0) {
            throw new NoPotionsException();
        }
        if (usedPotions.contains(encounterId)) {
            throw new PotionLimitUsageReachedException();
        }

        usedPotions.add(encounterId);
        return new Adventurer(
            adventurer.id(),
            adventurer.name(),
            adventurer.weapons(),
            adventurer.hp() + 5,
            adventurer.attack(),
            adventurer.defense(),
            adventurer.money(),
            adventurer.numberOfPotions() - 1
        );
    }
}
