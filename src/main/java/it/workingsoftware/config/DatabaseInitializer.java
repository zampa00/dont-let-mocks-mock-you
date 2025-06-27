/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void init(String jdbcUrl) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS adventurers (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    weapon TEXT NOT NULL,
                    hp INTEGER NOT NULL,
                    attack INTEGER NOT NULL,
                    defense INTEGER NOT NULL,
                    money INTEGER NOT NULL,
                    number_of_potions INTEGER NOT NULL
                );
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS encounters (
                    id TEXT PRIMARY KEY,
                    adventurer_id TEXT NOT NULL,
                    monster_name TEXT NOT NULL,
                    monster_hp INTEGER NOT NULL,
                    monster_attack INTEGER NOT NULL,
                    monster_defense INTEGER NOT NULL
                );
                """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
