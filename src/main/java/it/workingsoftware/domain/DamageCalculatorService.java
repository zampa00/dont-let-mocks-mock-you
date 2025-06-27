/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import it.workingsoftware.domain.exception.MissingWeaponException;

import java.util.List;

public class DamageCalculatorService {
    int getDamage(int attack, List<Weapon> weapons, int defense) {
        if (weapons.isEmpty()) {
            throw new MissingWeaponException();
        }

        int damageWithWeapon = weapons.stream().mapToInt(weapon -> switch (weapon) {
            case AXE, SWORD -> 3;
            case CLUB -> 1;
            case DAGGER -> 2;
        }).sum();

        return Math.max(attack + damageWithWeapon - defense, 0);
    }
}
