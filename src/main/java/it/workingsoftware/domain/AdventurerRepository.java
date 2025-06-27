/*
 * Copyright (c) 2025 Marco Zamprogno and Gianni Bombelli
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package it.workingsoftware.domain;

import java.util.Optional;
import java.util.UUID;

public interface AdventurerRepository {
    void save(Adventurer adventurer);

    Optional<Adventurer> findById(UUID id);

    void update(Adventurer adventurer);

    void delete(UUID id);
}

