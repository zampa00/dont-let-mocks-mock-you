/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import java.util.Optional;
import java.util.UUID;

public interface EncounterRepository {

    void save(Encounter encounter);

    Optional<Encounter> findById(UUID id);

    Optional<Encounter> findByAdventurerId(UUID id);

    void update(Encounter encounter);

    void delete(UUID id);
}
