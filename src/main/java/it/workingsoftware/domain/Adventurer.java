/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import java.util.List;
import java.util.UUID;

public record Adventurer(
        UUID id,
        String name,
        List<Weapon> weapons,
        int hp,
        int attack,
        int defense,
        int money,
        int numberOfPotions
) {
}
