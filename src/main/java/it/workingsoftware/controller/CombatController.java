/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import it.workingsoftware.domain.Adventurer;
import it.workingsoftware.domain.AdventurerService;
import it.workingsoftware.domain.Encounter;
import it.workingsoftware.domain.Weapon;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CombatController {
    private final AdventurerService adventurerService;

    public CombatController(AdventurerService adventurerService) {
        this.adventurerService = adventurerService;
    }

    public void createAdventurer(Context ctx) {
        String name = ctx.pathParam("name");
        String weapon = ctx.pathParam("weapon");

        Adventurer adventurer = adventurerService.createAdventurer(name, mapWeapons(weapon));

        ctx.json(adventurer);
    }

    private static List<Weapon> mapWeapons(String weapon) {
        return switch (weapon.toLowerCase()) {
            case "axe"-> List.of(Weapon.AXE);
            case "daggers"-> List.of(Weapon.DAGGER, Weapon.DAGGER);
            case "sword" -> List.of(Weapon.SWORD);
            default -> Collections.emptyList();
        };
    }

    public void startEncounter(Context ctx) {
        String adventurerId = ctx.pathParam("adventurerId");

        Encounter encounter = adventurerService.startEncounter(UUID.fromString(adventurerId));

        ctx.json(encounter);
    }

    public void attack(Context ctx) {
        String adventurerId = ctx.pathParam("adventurerId");

        List<String> actions = adventurerService.attack(UUID.fromString(adventurerId));

        ctx.json(actions);
    }

    public void heal(Context ctx) {
        String adventurerId = ctx.pathParam("adventurerId");

        List<String> actions = adventurerService.heal(UUID.fromString(adventurerId));

        ctx.json(actions);
    }

    public void registerRoutes(Javalin app) {
        app.post("/create/{name}/{weapon}", this::createAdventurer);
        app.post("/start-encounter/{adventurerId}", this::startEncounter);
        app.post("/attack/{adventurerId}", this::attack);
        app.post("/heal/{adventurerId}", this::heal);
    }
}
