/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.workingsoftware.domain.Weapon;
import org.junit.jupiter.api.Test;

import io.javalin.http.Context;

import it.workingsoftware.domain.AdventurerService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CombatControllerTest {

    @Test
    void creatingANewCharacterWithAxeCallsTheCorrectService() {
        String name = "Gianni";
        String weapon = "Axe";
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);

        when(ctx.pathParam("name")).thenReturn(name);
        when(ctx.pathParam("weapon")).thenReturn(weapon);

        controller.createAdventurer(ctx);

        verify(mockAdventurerService).createAdventurer(name, List.of(Weapon.AXE));
    }

    @Test
    void creatingANewCharacterWithDaggersCallsTheCorrectService() {
        String name = "Gianni";
        String weapon = "Daggers";
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);

        when(ctx.pathParam("name")).thenReturn(name);
        when(ctx.pathParam("weapon")).thenReturn(weapon);

        controller.createAdventurer(ctx);

        verify(mockAdventurerService).createAdventurer(name, List.of(Weapon.DAGGER, Weapon.DAGGER));
    }

    @Test
    void creatingANewCharacterWithSwordCallsTheCorrectService() {
        String name = "Gianni";
        String weapon = "Sword";
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);

        when(ctx.pathParam("name")).thenReturn(name);
        when(ctx.pathParam("weapon")).thenReturn(weapon);

        controller.createAdventurer(ctx);

        verify(mockAdventurerService).createAdventurer(name, List.of(Weapon.SWORD));
    }

    @Test
    void creatingANewCharacterWithFistsCallsTheCorrectService() {
        String name = "Gianni";
        String weapon = "Fists";
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);

        when(ctx.pathParam("name")).thenReturn(name);
        when(ctx.pathParam("weapon")).thenReturn(weapon);

        controller.createAdventurer(ctx);

        verify(mockAdventurerService).createAdventurer(name, Collections.emptyList());
    }

    @Test
    void startingANewEncounterCallsTheCorrectService() {
        UUID adventurerId = UUID.randomUUID();
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);
        when(ctx.pathParam("adventurerId")).thenReturn(adventurerId.toString());

        controller.startEncounter(ctx);

        verify(mockAdventurerService).startEncounter(adventurerId);
    }

    @Test
    void attackingCallsTheCorrectService() {
        UUID adventurerId = UUID.randomUUID();
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);
        when(ctx.pathParam("adventurerId")).thenReturn(adventurerId.toString());

        controller.attack(ctx);

        verify(mockAdventurerService).attack(adventurerId);
    }

    @Test
    void healingCallsTheCorrectService() {
        UUID adventurerId = UUID.randomUUID();
        AdventurerService mockAdventurerService = mock(AdventurerService.class);
        Context ctx = mock(Context.class);
        CombatController controller = new CombatController(mockAdventurerService);
        when(ctx.pathParam("adventurerId")).thenReturn(adventurerId.toString());

        controller.heal(ctx);

        verify(mockAdventurerService).heal(adventurerId);
    }
}
