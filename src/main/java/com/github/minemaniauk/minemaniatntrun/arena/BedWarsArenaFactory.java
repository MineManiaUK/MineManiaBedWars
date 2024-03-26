/*
 * MineManiaBedWars
 * Copyright (C) 2023  MineManiaUK Staff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.minemaniauk.minemaniatntrun.arena;

import com.github.minemaniauk.api.game.session.Session;
import com.github.minemaniauk.minemaniatntrun.MineManiaBedWars;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents the bed wars arena factory.
 * Used to get an instance of an arena from the configuration.
 */
public class BedWarsArenaFactory implements Session.ArenaFactory<BedWarsArena> {

    @Override
    public @NotNull Optional<BedWarsArena> getArena(@NotNull UUID uuid) {
        return MineManiaBedWars.getInstance()
                .getArenaConfiguration()
                .getType(uuid.toString());
    }
}
