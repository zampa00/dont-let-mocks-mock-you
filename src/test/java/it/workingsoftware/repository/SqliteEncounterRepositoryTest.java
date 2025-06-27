/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.repository;

import it.workingsoftware.config.DatabaseInitializer;
import it.workingsoftware.domain.Encounter;
import it.workingsoftware.domain.Monster;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqliteEncounterRepositoryTest {

    private static final String DB_PATH = "./test.db";
    private SqliteEncounterRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Path.of(DB_PATH));
        String connectionString = "jdbc:sqlite:" + DB_PATH;
        Connection connection = DriverManager.getConnection(connectionString);
        repository = new SqliteEncounterRepository(DriverManager.getConnection(connectionString));

        DatabaseInitializer.init(connectionString);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO encounters (id, adventurer_id, monster_name, monster_hp, monster_attack, monster_defense) " +
                "VALUES ('2ffd9e0f-334c-4b16-969c-08fa9da7dc2c', '870d5ef2-8fbe-4ef9-ad23-5a1361df200e', 'Goblin', 100, 20, 10)");
        }
    }

    @Test
    void load() {
        var savedEncounter = repository.findById(UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c"));
        assertTrue(savedEncounter.isPresent());
        assertEquals("870d5ef2-8fbe-4ef9-ad23-5a1361df200e", savedEncounter.get().adventurerId().toString());
        assertEquals("Goblin", savedEncounter.get().monster().name());
    }

    @Test
    void save() {
        var encounter = new Encounter(
            UUID.fromString("82f8ec70-b7b4-4971-b764-3d49e35cd24a"),
            UUID.randomUUID(),
            new Monster("Goblin", 100, 20, 10)
        );
        repository.save(encounter);

        var savedEncounter = repository.findById(UUID.fromString("82f8ec70-b7b4-4971-b764-3d49e35cd24a"));
        assertTrue(savedEncounter.isPresent());
    }

    @Test
    void shouldUpdateEncounter() {
        var encounter = repository.findById(UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c")).get();
        var updatedEncounter = new Encounter(
            encounter.id(),
            encounter.adventurerId(),
            new Monster("Orc", 120, 25, 15)
        );
        repository.update(updatedEncounter);

        var savedEncounter = repository.findById(encounter.id());
        assertTrue(savedEncounter.isPresent());
        assertEquals("Orc", savedEncounter.get().monster().name());
        assertEquals(120, savedEncounter.get().monster().hp());
    }

    @Test
    void shouldDeleteEncounter() {
        var encounterId = UUID.fromString("2ffd9e0f-334c-4b16-969c-08fa9da7dc2c");

        repository.delete(encounterId);

        var deletedEncounter = repository.findById(encounterId);
        assertFalse(deletedEncounter.isPresent());
    }

    @Test
    void shouldFindByAdventurerId() {
        var adventurerId = UUID.fromString("870d5ef2-8fbe-4ef9-ad23-5a1361df200e");

        var encounter = repository.findByAdventurerId(adventurerId);

        assertTrue(encounter.isPresent());
        assertEquals("Goblin", encounter.get().monster().name());
    }
}
