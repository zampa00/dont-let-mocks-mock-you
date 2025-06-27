/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import it.workingsoftware.domain.exception.IllegalPotionUsageException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CombatServiceTest {

    @Mock
    DiceThrower diceThrower;

    @Mock
    DamageCalculatorService damageCalculatorService;

    @Mock
    HealService healService;

    CombatService combatService;

    @BeforeEach
    void setUp() {
        lenient().when(damageCalculatorService.getDamage(anyInt(), any(), anyInt())).thenReturn(1);
        combatService = new CombatService(diceThrower, damageCalculatorService, healService);
    }

    @Test
    void adventurerAndMonsterCanMissAndNothingHappens() {
        Adventurer gimli = new Adventurer(
            null,
            "Gimli",
            List.of(Weapon.AXE),
            20,
            5,
            5,
            0,
            2
        );
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(false, false);

        AttackResult result = combatService.handleAttack(gimli, goblin);

        assertEquals(2, result.actions().size());
        assertEquals("Gimli attacks... But misses!", result.actions().get(0));
        assertEquals("Goblin attacks... But misses!", result.actions().get(1));
        assertEquals(gimli, result.updatedAdventurer());
        assertEquals(goblin, result.updatedMonster());
    }

    @Test
    void adventurerCanAttackAMonsterWithFists() {
        Adventurer gimli = new Adventurer(
                null,
                "Gimli",
                List.of(),
                20,
                5,
                5,
                0,
                2
        );
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(true, false);

        AttackResult result = combatService.handleAttack(gimli, goblin);

        assertEquals(2, result.actions().size());
        assertEquals("Gimli attacks... And hit for 1 damage!", result.actions().get(0));
        assertEquals("Goblin attacks... But misses!", result.actions().get(1));
        assertEquals(gimli, result.updatedAdventurer());
        assertEquals(new Monster(
            "Goblin",
            1,
            5,
            7
        ), result.updatedMonster());
    }

    @Test
    void adventurerCanHitTheMonsterAndBeMissed() {
        Adventurer gimli = gimli();
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(true, false);

        AttackResult result = combatService.handleAttack(gimli, goblin);

        assertEquals(2, result.actions().size());
        assertEquals("Gimli attacks... And hit for 1 damage!", result.actions().get(0));
        assertEquals("Goblin attacks... But misses!", result.actions().get(1));
        assertEquals(gimli, result.updatedAdventurer());
        assertEquals(new Monster(
            "Goblin",
            1,
            5,
            7
        ), result.updatedMonster());
    }

    @Test
    void bothAdventurerAndMonsterCanHit() {
        Adventurer gimli = new Adventurer(
            null,
            "Gimli",
            List.of(),
            20,
            5,
            5,
            0,
            2
        );
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(true, true);

        AttackResult result = combatService.handleAttack(gimli, goblin);

        assertEquals(2, result.actions().size());
        assertEquals("Gimli attacks... And hit for 1 damage!", result.actions().get(0));
        assertEquals("Goblin attacks... And hit for 1 damage!", result.actions().get(1));
        assertEquals(new Adventurer(
            null,
            "Gimli",
            List.of(),
            19,
            5,
            5,
            0,
            2
        ), result.updatedAdventurer());
        assertEquals(new Monster(
            "Goblin",
            1,
            5,
            7
        ), result.updatedMonster());
    }

    @Test
    void aMonsterDieWhenReaching0HP() {
        Adventurer gimli = gimli();
        when(diceThrower.rollToHit()).thenReturn(true, false);

        AttackResult result = combatService.handleAttack(gimli, new Monster(
            "Goblin",
            1,
            5,
            7
        ));

        assertEquals(2, result.actions().size());
        assertEquals("Gimli attacks... And hit for 1 damage!", result.actions().get(0));
        assertEquals("Goblin is dead!", result.actions().get(1));
        assertEquals(gimli, result.updatedAdventurer());
        assertEquals(new Monster(
            "Goblin",
            0,
            5,
            7
        ), result.updatedMonster());
    }

    @Test
    void adventurerCanDie() {
        Adventurer gimli = new Adventurer(
            null,
            "Gimli",
            List.of(Weapon.AXE),
            1,
            5,
            5,
            0,
            2
        );
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(true, true);

        AttackResult result = combatService.handleAttack(gimli, goblin);

        assertEquals(3, result.actions().size());
        assertEquals("Gimli attacks... And hit for 1 damage!", result.actions().get(0));
        assertEquals("Goblin attacks... And hit for 1 damage!", result.actions().get(1));
        assertEquals("Gimli is dead!", result.actions().get(2));
        assertEquals(new Adventurer(
            null,
            "Gimli",
            List.of(Weapon.AXE),
            0,
            5,
            5,
            0,
            2
        ), result.updatedAdventurer());
        assertEquals(new Monster(
            "Goblin",
            1,
            5,
            7
        ), result.updatedMonster());
    }

    @Test
    void adventurerCanHealThemself() {
        Adventurer gimli = new Adventurer(
            null,
            "Gimli",
            List.of(),
            1,
            5,
            5,
            0,
            2
        );
        UUID encounterId = UUID.randomUUID();
        when(healService.heal(gimli, encounterId)).thenReturn(new Adventurer(
            null,
            "Gimli",
            List.of(),
            6,
            5,
            5,
            0,
            1
        ));
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(true);

        AttackResult result = combatService.heal(gimli, new Encounter(
            encounterId,
            gimli.id(),
            goblin
        ));

        assertEquals(2, result.actions().size());
        assertEquals("Gimli heals for 5 hp!", result.actions().get(0));
        assertEquals("Goblin attacks... And hit for 1 damage!", result.actions().get(1));
        assertEquals(new Adventurer(
            null,
            "Gimli",
            List.of(),
            5,
            5,
            5,
            0,
            2
        ), result.updatedAdventurer());
    }

    @Test
    void adventurerCanHealThemselfOnlyTwicePerEncounter() {
        Adventurer gimli = new Adventurer(
            null,
            "Gimli",
            List.of(),
            1,
            5,
            5,
            0,
            2
        );
        UUID encounterId = UUID.randomUUID();
        Adventurer healedGimli = new Adventurer(
            null,
            "Gimli",
            List.of(),
            6,
            5,
            5,
            0,
            1
        );
        when(healService.heal(gimli, encounterId)).thenReturn(healedGimli);
        when(healService.heal(gimli, encounterId)).thenReturn(healedGimli);
        when(healService.heal(healedGimli, encounterId)).thenThrow(new IllegalPotionUsageException());
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(false, false);

        for (int i = 0; i < 2; i++) {
            combatService.heal(gimli, new Encounter(
                encounterId,
                gimli.id(),
                goblin
            ));
        }
        assertThrows(IllegalPotionUsageException.class, () -> {
            combatService.heal(healedGimli, new Encounter(
                encounterId,
                gimli.id(),
                goblin
            ));
        });
    }

    @Test
    void adventurerCanHealAndThenDieFromMonsterAttack() {
        Adventurer gimli = new Adventurer(
            null,
            "Gimli",
            List.of(),
            1,
            5,
            5,
            0,
            2
        );
        UUID encounterId = UUID.randomUUID();
        when(healService.heal(gimli, encounterId)).thenReturn(new Adventurer(
            null,
            "Gimli",
            List.of(),
            6,
            5,
            5,
            0,
            1
        ));
        Monster goblin = goblin();
        when(diceThrower.rollToHit()).thenReturn(true);
        when(damageCalculatorService.getDamage(anyInt(), any(), anyInt())).thenReturn(10);

        AttackResult result = combatService.heal(gimli, new Encounter(
            encounterId,
            gimli.id(),
            goblin
        ));

        assertEquals(3, result.actions().size());
        assertEquals("Gimli heals for 5 hp!", result.actions().get(0));
        assertEquals("Goblin attacks... And hit for 10 damage!", result.actions().get(1));
        assertEquals("Gimli is dead!", result.actions().get(2));
        assertEquals(new Adventurer(
            null,
            "Gimli",
            List.of(),
            -4,
            5,
            5,
            0,
            2
        ), result.updatedAdventurer());
    }

    private Adventurer gimli() {
        return new Adventurer(
            null,
            "Gimli",
            List.of(Weapon.AXE),
            20,
            5,
            5,
            0,
            2
        );
    }

    private Monster goblin() {
        return new Monster(
            "Goblin",
            2,
            5,
            7
        );
    }
}
