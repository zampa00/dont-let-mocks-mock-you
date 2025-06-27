/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import it.workingsoftware.domain.exception.MissingWeaponException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DamageCalculatorServiceTest {
    @Test
    void shouldCalculateAxeDamage() {
        final DamageCalculatorService damageCalculator = new DamageCalculatorService();

        final int damage = damageCalculator.getDamage(10, List.of(Weapon.AXE), 10);

        assertEquals(3, damage);
    }

    @Test
    void shouldCalculateClubDamage() {
        final DamageCalculatorService damageCalculator = new DamageCalculatorService();

        final int damage = damageCalculator.getDamage(10, List.of(Weapon.CLUB), 10);

        assertEquals(1, damage);
    }

    @Test
    void shouldCalculateDaggerDamage() {
        final DamageCalculatorService damageCalculator = new DamageCalculatorService();

        final int damage = damageCalculator.getDamage(10, List.of(Weapon.DAGGER), 10);

        assertEquals(2, damage);
    }

    @Test
    void shouldCalculateTwoDaggersDamage() {
        final DamageCalculatorService damageCalculator = new DamageCalculatorService();

        final int damage = damageCalculator.getDamage(10, List.of(Weapon.DAGGER, Weapon.DAGGER), 10);

        assertEquals(4, damage);
    }

    @Test
    void shouldCalculateSwordDaggersDamage() {
        final DamageCalculatorService damageCalculator = new DamageCalculatorService();

        final int damage = damageCalculator.getDamage(10, List.of(Weapon.SWORD), 10);

        assertEquals(3, damage);
    }

    @Test
    void shouldThrowWhenNoWeapons() {
        final DamageCalculatorService damageCalculator = new DamageCalculatorService();

        assertThrows(MissingWeaponException.class, () -> damageCalculator.getDamage(10, List.of(), 10));
    }
}
