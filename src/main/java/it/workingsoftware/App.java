/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware;

import io.javalin.Javalin;
import it.workingsoftware.config.DatabaseInitializer;
import it.workingsoftware.controller.CombatController;
import it.workingsoftware.domain.*;
import it.workingsoftware.repository.SqliteAdventurerRepository;
import it.workingsoftware.repository.SqliteEncounterRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    private static final String SQLITE_APP_DB = "jdbc:sqlite:app.db";

    public static void main(String[] args) throws SQLException {
        initDb();
        final Connection connection = DriverManager.getConnection(SQLITE_APP_DB);
        final AdventurerRepository adventurerRepository = new SqliteAdventurerRepository(connection);
        final EncounterRepository encounterRepository = new SqliteEncounterRepository(connection);

        final DiceThrower diceThrower = new DiceThrower();
        final DamageCalculatorService damageCalculatorService = new DamageCalculatorService();
        final HealService healService = new HealService();
        final CombatService combatService = new CombatService(diceThrower, damageCalculatorService, healService);
        final AdventurerService adventurerService = new AdventurerService(adventurerRepository, encounterRepository, combatService);

        final CombatController combatController = new CombatController(adventurerService);

        Javalin app = Javalin.create().start(7000);
        combatController.registerRoutes(app);
    }

    public static void initDb() {
        DatabaseInitializer.init(SQLITE_APP_DB);
    }

}
