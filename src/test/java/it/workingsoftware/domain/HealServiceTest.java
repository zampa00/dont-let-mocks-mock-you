/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import it.workingsoftware.domain.exception.NoPotionsException;
import it.workingsoftware.domain.exception.PotionLimitUsageReachedException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HealServiceTest {

    private final HealService healService = new HealService();

    @Test
    void healIncreasesHpAndDecreasesPotions() {
        Adventurer adventurer = new Adventurer(
            null,
            "Aragorn",
            List.of(Weapon.SWORD),
            10,
            5,
            3,
            100,
            2
        );

        Adventurer healedAdventurer = healService.heal(adventurer, UUID.randomUUID());

        assertEquals(15, healedAdventurer.hp());
        assertEquals(1, healedAdventurer.numberOfPotions());
    }

    @Test
    void healDoesNotChangeHpIfNoPotions() {
        Adventurer adventurer = new Adventurer(
            null,
            "Gimli",
            List.of(Weapon.AXE),
            10,
            5,
            3,
            100,
            0
        );

        assertThrows(NoPotionsException.class, () -> healService.heal(adventurer, UUID.randomUUID()));
    }

    @Test
    void adventurerCanOnlyHealOncePerEncounter() {
        Adventurer adventurer = new Adventurer(
            null,
            "Frodo",
            List.of(Weapon.DAGGER),
            10,
            5,
            3,
            100,
            2
        );

        UUID encounterId = UUID.randomUUID();
        Adventurer healedAdventurer = healService.heal(adventurer, encounterId);
        assertEquals(15, healedAdventurer.hp());
        assertEquals(1, healedAdventurer.numberOfPotions());

        assertThrows(PotionLimitUsageReachedException.class, () -> healService.heal(healedAdventurer, encounterId));
    }
}
