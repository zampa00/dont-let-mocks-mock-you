/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AppTest {

    @BeforeAll
    static void startApp() {
        Thread appThread = new Thread(() -> {
            try {
                App.main(new String[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        appThread.setDaemon(true);
        appThread.start();

        boolean started = false;
        for (int i = 0; i < 30; i++) {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:7000/").openConnection();
                con.setRequestMethod("GET");
                int code = con.getResponseCode();
                if (code > 0) {
                    started = true;
                    break;
                }
            } catch (IOException ignored) {}
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
        if (!started) throw new RuntimeException("App did not start up in time");
    }

    @Test
    void shouldBeAbleToCreateAnAdventurerWithAxeWeapon() throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:7000/create/Gimli/AXE").openConnection();
        con.setRequestMethod("POST");

        assertEquals(200, con.getResponseCode());
    }

    @Test
    void shouldCreateAdventurerWithFistsWeapon() throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:7000/create/Gimli/fists").openConnection();
        con.setRequestMethod("POST");

        assertEquals(200, con.getResponseCode());
    }

    @Test
    void shouldCompleteFullCombatFlow() throws Exception {
        // 1. Create an adventurer
        HttpURLConnection createCon = (HttpURLConnection) new URL("http://localhost:7000/create/TestHero/axe").openConnection();
        createCon.setRequestMethod("POST");
        assertEquals(200, createCon.getResponseCode());

        String adventurerResponse = new String(createCon.getInputStream().readAllBytes());
        String adventurerId = extractAdventurerId(adventurerResponse);

        // 2. Start an encounter
        HttpURLConnection encounterCon = (HttpURLConnection) new URL("http://localhost:7000/start-encounter/" + adventurerId).openConnection();
        encounterCon.setRequestMethod("POST");
        assertEquals(200, encounterCon.getResponseCode());

        // 3. Attack the monster
        HttpURLConnection attackCon = (HttpURLConnection) new URL("http://localhost:7000/attack/" + adventurerId).openConnection();
        attackCon.setRequestMethod("POST");
        assertEquals(200, attackCon.getResponseCode());

        // 4. Use a healing potion
        HttpURLConnection healCon = (HttpURLConnection) new URL("http://localhost:7000/heal/" + adventurerId).openConnection();
        healCon.setRequestMethod("POST");
        assertEquals(200, healCon.getResponseCode());
    }

    private String extractAdventurerId(String jsonResponse) {
        return jsonResponse.substring(jsonResponse.indexOf("\"id\":\"") + 6,
                jsonResponse.indexOf("\"", jsonResponse.indexOf("\"id\":\"") + 6));
    }
}
