/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import java.util.ArrayList;
import java.util.List;

public class CombatService {

    private final DiceThrower diceThrower;
    private final DamageCalculatorService damageCalculatorService;
    private final HealService healService;

    public CombatService(DiceThrower diceThrower, DamageCalculatorService damageCalculatorService,
                         HealService healService) {
        this.diceThrower = diceThrower;
        this.damageCalculatorService = damageCalculatorService;
        this.healService = healService;
    }

    public AttackResult handleAttack(Adventurer adventurer, Monster monster) {
        List<String> actions = new ArrayList<>();
        int damageDealtByAdventurer = 0;
        int damageDealtByMonster = 0;
        if (diceThrower.rollToHit()) {
            damageDealtByAdventurer =
                damageCalculatorService.getDamage(adventurer.attack(), adventurer.weapons(), monster.defense());
            actions.add(adventurer.name() + " attacks... And hit for " + damageDealtByAdventurer + " damage!");
            if (monster.hp() - damageDealtByAdventurer <= 0) {
                actions.add(monster.name() + " is dead!");
            }
        } else {
            actions.add(adventurer.name() + " attacks... But misses!");
        }
        if (monster.hp() - damageDealtByAdventurer > 0) {
            if (diceThrower.rollToHit()) {
                damageDealtByMonster =
                    damageCalculatorService.getDamage(monster.attack(), List.of(Weapon.CLUB), adventurer.defense());
                actions.add(monster.name() + " attacks... And hit for " + damageDealtByMonster + " damage!");
                if (adventurer.hp() - damageDealtByMonster <= 0) {
                    actions.add(adventurer.name() + " is dead!");
                }
            } else {
                actions.add(monster.name() + " attacks... But misses!");
            }
        }

        return new AttackResult(
            actions,
            new Adventurer(
                adventurer.id(),
                adventurer.name(),
                adventurer.weapons(),
                adventurer.hp() - damageDealtByMonster,
                adventurer.attack(),
                adventurer.defense(),
                adventurer.money(),
                adventurer.numberOfPotions()
            ),
            new Monster(
                monster.name(),
                monster.hp() - damageDealtByAdventurer,
                monster.attack(),
                monster.defense()
            )
        );
    }

    public AttackResult heal(Adventurer adventurer, Encounter encounter) {
        Monster monster = encounter.monster();
        List<String> actions = new ArrayList<>();
        int damageDealtByMonster = 0;

        Adventurer healedAdventurer = healService.heal(adventurer, encounter.id());
        actions.add(adventurer.name() + " heals for 5 hp!");

        if (diceThrower.rollToHit()) {
            damageDealtByMonster =
                damageCalculatorService.getDamage(monster.attack(), List.of(Weapon.CLUB), adventurer.defense());
            actions.add(monster.name() + " attacks... And hit for " + damageDealtByMonster + " damage!");
            if (healedAdventurer.hp() - damageDealtByMonster <= 0) {
                actions.add(adventurer.name() + " is dead!");
            }
        } else {
            actions.add(monster.name() + " attacks... But misses!");
        }

        return new AttackResult(
            actions,
            new Adventurer(
                adventurer.id(),
                adventurer.name(),
                adventurer.weapons(),
                healedAdventurer.hp() - damageDealtByMonster,
                adventurer.attack(),
                adventurer.defense(),
                adventurer.money(),
                adventurer.numberOfPotions()
            ),
            monster
        );
    }
}
