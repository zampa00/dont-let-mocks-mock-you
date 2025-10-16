/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import it.workingsoftware.domain.exception.IllegalPotionUsageException;
import it.workingsoftware.domain.exception.NoPotionsException;

import java.util.List;
import java.util.UUID;

public class AdventurerService {
    private final AdventurerRepository adventurerRepository;
    private final EncounterRepository encounterRepository;
    private final CombatService combatService;

    public AdventurerService(AdventurerRepository adventurerRepository, EncounterRepository encounterRepository,
                             CombatService combatService) {
        this.adventurerRepository = adventurerRepository;
        this.encounterRepository = encounterRepository;
        this.combatService = combatService;
    }

    public Adventurer createAdventurer(String name, List<Weapon> weapons) {
        Adventurer adventurer = new Adventurer(
            UUID.randomUUID(),
            name,
            weapons,
            20,
            5,
            5,
            0,
            2
        );

        adventurerRepository.save(adventurer);

        return adventurer;
    }

    public Encounter startEncounter(UUID adventurerId) {
        Encounter encounter = adventurerRepository.findById(adventurerId)
            .map(adventurer -> new Encounter(
                UUID.randomUUID(),
                adventurer.id(),
                spawnMonster()
            ))
            .orElseThrow(() -> new IllegalArgumentException("Adventurer not found"));

        encounterRepository.save(encounter);

        return encounter;
    }

    public List<String> attack(UUID id) {
        Adventurer adventurer = adventurerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Adventurer not found"));
        Encounter encounter = encounterRepository.findByAdventurerId(adventurer.id())
            .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));
        Monster monster = encounter.monster();
        AttackResult attackResult = combatService.handleAttack(adventurer, monster);

        if (attackResult.updatedAdventurer().hp() <= 0) {
            adventurerRepository.delete(adventurer.id());
            encounterRepository.delete(encounter.id());
        } else {
            adventurerRepository.update(attackResult.updatedAdventurer());
        }
        if (encounter.monster().hp() <= 0) {
            encounterRepository.delete(encounter.id());
        } else {
            encounterRepository.update(new Encounter(encounter.id(), encounter.adventurerId(), attackResult.updatedMonster()));
        }
        return attackResult.actions();
    }

    public List<String> heal(UUID adventurerId) {
        Adventurer adventurer = adventurerRepository.findById(adventurerId)
            .orElseThrow(() -> new IllegalArgumentException("Adventurer not found"));
        Encounter encounter = encounterRepository.findByAdventurerId(adventurer.id())
            .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));

        try {
            AttackResult attackResult = combatService.heal(adventurer, encounter);
            adventurerRepository.update(attackResult.updatedAdventurer());
            return attackResult.actions();
        } catch (NoPotionsException | IllegalPotionUsageException e) {
            throw e;
        }
    }

    private Monster spawnMonster() {
        return Math.random() < 0.66 ? new Monster("Goblin", 10, 2, 2) : new Monster("Azog il Goblin", 100, 10, 5);
    }
}
