/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.repository;

import it.workingsoftware.config.DatabaseInitializer;
import it.workingsoftware.domain.Adventurer;
import it.workingsoftware.domain.Weapon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteAdventurerRepositoryTest {
    private static final String DB_PATH = "./test2.db";
    private SqliteAdventurerRepository repository;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Path.of(DB_PATH));
        String connectionString = "jdbc:sqlite:" + DB_PATH;
        connection = DriverManager.getConnection(connectionString);
        repository = new SqliteAdventurerRepository(DriverManager.getConnection(connectionString));

        DatabaseInitializer.init(connectionString);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO adventurers (id, name, weapon, hp, attack, defense, money, number_of_potions) " +
                    "VALUES ('2ffd9e0f-334c-4b16-969c-08fa9da7dc2c', 'Gianni', 'SWORD', 100, 20, 10, 0, 2)");
        }
    }

    @Test
    void returnsCharacterWhenExists() {
        var c = repository.findById(UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c"));
        assertTrue(c.isPresent());
        assertEquals("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c", c.get().id().toString());
        assertEquals("Gianni", c.get().name());
        assertEquals(100, c.get().hp());
    }

    @Test
    void returnsEmptyWhenNotExists() {
        var c = repository.findById(UUID.fromString("870d5ef2-8fbe-4ef9-ad23-5a1361df200e"));
        assertFalse(c.isPresent());
    }

    @Test
    void shouldSaveANewAdventurer() {
        var newAdventurer = new Adventurer(
                UUID.randomUUID(),
                "Aragorn",
                List.of(Weapon.SWORD),
                120,
                30,
                20,
                100,
                5
        );
        repository.save(newAdventurer);

        var savedAdventurer = repository.findById(newAdventurer.id());

        assertTrue(savedAdventurer.isPresent());
        assertEquals("Aragorn", savedAdventurer.get().name());
        assertEquals(120, savedAdventurer.get().hp());
    }

    @Test
    void shouldUpdateAdventurer() {
        var adventurer = repository.findById(UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c")).get();
        var updatedAdventurer = new Adventurer(
                adventurer.id(),
                adventurer.name(),
                adventurer.weapons(),
                90, // Updated HP
                adventurer.attack(),
                adventurer.defense(),
                adventurer.money(),
                adventurer.numberOfPotions()
        );
        repository.update(updatedAdventurer);

        var updatedAdventurerOnDb = repository.findById(UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c")).get();
        assertEquals(90, updatedAdventurerOnDb.hp());
    }

    @Test
    void shouldMapEmptyWeaponStringToEmptyList() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO adventurers (id, name, weapon, hp, attack, defense, money, number_of_potions) " +
                    "VALUES ('12345678-1234-1234-1234-123456789012', 'Legolas', '', 100, 20, 10, 0, 2)");
        }

        var adventurer = repository.findById(UUID.fromString("12345678-1234-1234-1234-123456789012"));

        assertTrue(adventurer.isPresent());
        assertEquals("Legolas", adventurer.get().name());
        assertTrue(adventurer.get().weapons().isEmpty());
    }

    @Test
    void shouldDeleteAdventurer() {
        var adventurerId = UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c");

        repository.delete(adventurerId);

        var deletedAdventurer = repository.findById(adventurerId);
        assertFalse(deletedAdventurer.isPresent());
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.close();
        Files.deleteIfExists(Path.of(DB_PATH));
    }
}
