/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.repository;

import it.workingsoftware.domain.Adventurer;
import it.workingsoftware.domain.AdventurerRepository;
import it.workingsoftware.domain.Weapon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SqliteAdventurerRepository implements AdventurerRepository {
    private final Connection connection;

    public SqliteAdventurerRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Adventurer adventurer) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO adventurers (id, name, weapon, hp, attack, defense, money, number_of_potions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, adventurer.id().toString());
            stmt.setString(2, adventurer.name());
            stmt.setString(3, adventurer.weapons().stream().map(Weapon::name).collect(Collectors.joining(",")));
            stmt.setInt(4, adventurer.hp());
            stmt.setInt(5, adventurer.attack());
            stmt.setInt(6, adventurer.defense());
            stmt.setInt(7, adventurer.money());
            stmt.setInt(8, adventurer.numberOfPotions());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Adventurer> findById(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id, name, weapon, hp, attack, defense, money, number_of_potions FROM adventurers WHERE id = ?");
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Adventurer(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        Arrays.stream(rs.getString("weapon").split(","))
                                .filter(s -> !s.isEmpty())
                                .map(Weapon::valueOf)
                                .collect(Collectors.toList()),
                        rs.getInt("hp"),
                        rs.getInt("attack"),
                        rs.getInt("defense"),
                        rs.getInt("money"),
                        rs.getInt("number_of_potions")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Adventurer adventurer) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE adventurers SET name = ?, weapon = ?, hp = ?, attack = ?, defense = ?, money = ?, number_of_potions = ? WHERE id = ?");
            stmt.setString(1, adventurer.name());
            stmt.setString(2, adventurer.weapons().stream().map(Weapon::name).collect(Collectors.joining(",")));
            stmt.setInt(3, adventurer.hp());
            stmt.setInt(4, adventurer.attack());
            stmt.setInt(5, adventurer.defense());
            stmt.setInt(6, adventurer.money());
            stmt.setInt(7, adventurer.numberOfPotions());
            stmt.setString(8, adventurer.id().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM adventurers WHERE id = ?");
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
