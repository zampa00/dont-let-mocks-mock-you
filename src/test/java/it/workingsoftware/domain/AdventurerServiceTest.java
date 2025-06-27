/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdventurerServiceTest {

    @Mock
    AdventurerRepository adventurerRepository;
    @Mock
    EncounterRepository encounterRepository;
    @Mock
    CombatService combatService;

    private AdventurerService adventurerService;

    @BeforeEach
    void setUp() {
        adventurerService = new AdventurerService(adventurerRepository, encounterRepository, combatService);
    }

    @Test
    void shouldSaveANewHeroWithDefaultValues() {
        adventurerService.createAdventurer("Gimli", List.of(Weapon.AXE));

        verify(adventurerRepository).save(new Adventurer(
                any(),
                "Gimli",
                List.of(Weapon.AXE),
                20,
                5,
                5,
                0,
                2
        ));
    }

    @Test
    void canStartANewEncounter() {
        UUID id = UUID.randomUUID();
        when(adventurerRepository.findById(any())).thenReturn(Optional.of(new Adventurer(
                id,
                "Gimli",
                List.of(Weapon.AXE),
                20,
                5,
                5,
                0,
                2
        )));

        Encounter encounter = adventurerService.startEncounter(id);

        verify(encounterRepository).save(encounter);
    }

    @Test
    void canAttackAMonster() {
        UUID adventurerId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        Adventurer adventurer = new Adventurer(
                adventurerId,
                "Gimli",
                List.of(Weapon.AXE),
                20,
                5,
                5,
                0,
                2
        );
        Encounter encounter = new Encounter(
                encounterId,
                adventurerId,
                new Monster("Goblin", 10, 2, 2)
        );
        when(adventurerRepository.findById(adventurerId)).thenReturn(Optional.of(adventurer));
        when(encounterRepository.findByAdventurerId(adventurerId)).thenReturn(Optional.of(encounter));
        when(combatService.handleAttack(adventurer, encounter.monster())).thenReturn(new AttackResult(List.of("Attack successful!"),
                adventurer, encounter.monster()));

        List<String> attack = adventurerService.attack(adventurerId);

        Assertions.assertNotNull(attack);
        verify(adventurerRepository).findById(adventurerId);
        verify(encounterRepository).findByAdventurerId(adventurerId);
        verify(combatService).handleAttack(adventurer, encounter.monster());
        verify(adventurerRepository).update(adventurer);
        verify(encounterRepository).update(encounter);
    }

    @Test
    void handleMonsterDefeat() {
        UUID adventurerId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        Adventurer adventurer = new Adventurer(
                adventurerId,
                "Gimli",
                List.of(Weapon.AXE),
                20,
                5,
                5,
                0,
                2
        );
        Encounter encounter = new Encounter(
                encounterId,
                adventurerId,
                new Monster("Goblin", 0, 2, 2)
        );
        when(adventurerRepository.findById(adventurerId)).thenReturn(Optional.of(adventurer));
        when(encounterRepository.findByAdventurerId(adventurerId)).thenReturn(Optional.of(encounter));
        when(combatService.handleAttack(adventurer, encounter.monster()))
                .thenReturn(new AttackResult(List.of("Attack successful!"), adventurer, encounter.monster()));

        List<String> attack = adventurerService.attack(adventurerId);

        Assertions.assertNotNull(attack);
        verify(adventurerRepository).findById(adventurerId);
        verify(encounterRepository).findByAdventurerId(adventurerId);
        verify(combatService).handleAttack(adventurer, encounter.monster());
        verify(adventurerRepository).update(adventurer);
        verify(encounterRepository).delete(encounter.id());
    }

    @Test
    void handleAdventurerDefeat() {
        UUID adventurerId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        Adventurer adventurer = new Adventurer(
                adventurerId,
                "Gimli",
                List.of(Weapon.AXE),
                0,
                5,
                5,
                0,
                2
        );
        Encounter encounter = new Encounter(
                encounterId,
                adventurerId,
                new Monster("Goblin", 1, 2, 2)
        );
        when(adventurerRepository.findById(adventurerId)).thenReturn(Optional.of(adventurer));
        when(encounterRepository.findByAdventurerId(adventurerId)).thenReturn(Optional.of(encounter));
        when(combatService.handleAttack(adventurer, encounter.monster()))
                .thenReturn(new AttackResult(List.of("Attack successful!"), adventurer, encounter.monster()));

        List<String> attack = adventurerService.attack(adventurerId);

        Assertions.assertNotNull(attack);
        verify(adventurerRepository).findById(adventurerId);
        verify(encounterRepository).findByAdventurerId(adventurerId);
        verify(combatService).handleAttack(adventurer, encounter.monster());
        verify(adventurerRepository).delete(adventurer.id());
        verify(encounterRepository).delete(encounter.id());
    }

    @Test
    void adventurerCanHeal() {
        UUID adventurerId = UUID.randomUUID();
        UUID encounterId = UUID.randomUUID();
        Adventurer adventurer = new Adventurer(
            adventurerId,
            "Gimli",
            List.of(Weapon.AXE),
            1,
            5,
            5,
            0,
            2
        );
        Encounter encounter = new Encounter(
            encounterId,
            adventurerId,
            new Monster("Goblin", 1, 2, 2)
        );
        when(adventurerRepository.findById(adventurerId)).thenReturn(Optional.of(adventurer));
        when(encounterRepository.findByAdventurerId(adventurerId)).thenReturn(Optional.of(encounter));
        Adventurer healedAdventurer = new Adventurer(
            adventurerId,
            "Gimli",
            List.of(Weapon.AXE),
            6,
            5,
            5,
            0,
            1
        );
        when(combatService.heal(adventurer, encounter)).thenReturn(new AttackResult(
            List.of("Gimli heals for 5 hp!"),
            healedAdventurer,
            encounter.monster()
        ));

        List<String> actions = adventurerService.heal(adventurerId);

        Assertions.assertNotNull(actions);
        verify(adventurerRepository).findById(adventurerId);
        verify(encounterRepository).findByAdventurerId(adventurerId);
        verify(combatService).heal(adventurer, encounter);
        verify(adventurerRepository).update(healedAdventurer);
    }
}
