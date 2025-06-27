/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import it.workingsoftware.domain.Encounter;
import it.workingsoftware.domain.EncounterRepository;
import it.workingsoftware.domain.Monster;

public class SqliteEncounterRepository implements EncounterRepository {
    private final Connection connection;

    public SqliteEncounterRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Encounter encounter) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO encounters (id, adventurer_id, monster_name, monster_hp, monster_attack, monster_defense) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, encounter.id().toString());
            stmt.setString(2, encounter.adventurerId().toString());
            stmt.setString(3, encounter.monster().name());
            stmt.setInt(4, encounter.monster().hp());
            stmt.setInt(5, encounter.monster().attack());
            stmt.setInt(6, encounter.monster().defense());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Encounter> findById(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id, adventurer_id, monster_name, monster_hp, monster_attack, monster_defense FROM encounters WHERE id = ?");
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(
                    new Encounter(UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("adventurer_id")),
                        new Monster(rs.getString("monster_name"), rs.getInt("monster_hp"), rs.getInt("monster_attack"),
                            rs.getInt("monster_defense"))));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Encounter> findByAdventurerId(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id, adventurer_id, monster_name, monster_hp, monster_attack, monster_defense FROM encounters WHERE adventurer_id = ?");
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            return Optional.of(
                new Encounter(UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("adventurer_id")),
                    new Monster(rs.getString("monster_name"), rs.getInt("monster_hp"), rs.getInt("monster_attack"),
                        rs.getInt("monster_defense"))));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Encounter encounter) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE encounters SET adventurer_id = ?, monster_name = ?, monster_hp = ?, monster_attack = ?, monster_defense = ? WHERE id = ?");
            stmt.setString(1, encounter.adventurerId().toString());
            stmt.setString(2, encounter.monster().name());
            stmt.setInt(3, encounter.monster().hp());
            stmt.setInt(4, encounter.monster().attack());
            stmt.setInt(5, encounter.monster().defense());
            stmt.setString(6, encounter.id().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM encounters WHERE id = ?");
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
