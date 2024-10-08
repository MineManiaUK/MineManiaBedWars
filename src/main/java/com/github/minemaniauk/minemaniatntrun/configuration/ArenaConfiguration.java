/*
 * MineManiaTNTRun
 * Used for interacting with the database and message broker.
 * Copyright (C) 2023  MineManiaUK Staff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.minemaniauk.minemaniatntrun.configuration;

import com.github.minemaniauk.minemaniatntrun.MineManiaBedWarsPlugin;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.squishylib.configuration.directory.SingleTypeConfigurationDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

/**
 * Represents an arena configuration directory.
 */
public class ArenaConfiguration extends SingleTypeConfigurationDirectory<BedWarsArena> {

    /**
     * Used to create an arena configuration directory.
     */
    public ArenaConfiguration() {
        super(new File(MineManiaBedWarsPlugin.getInstance().getPlugin().getDataFolder(), "arenas"),
                identifier -> new BedWarsArena(UUID.fromString(identifier)),
                false);
    }

    /**
     * Used to update arenas that are registered.
     *
     * @return This instance.
     */
    public @NotNull ArenaConfiguration reloadRegisteredArenas() {
        MineManiaBedWarsPlugin.getAPI().getGameManager().unregisterLocalArenas();

        for (BedWarsArena arena : this.getAll()) {
            MineManiaBedWarsPlugin.getAPI().getGameManager().registerArena(arena);
        }

        return this;
    }

    /**
     * Used to reset the game identifiers for each arena.
     *
     * @return This instance.
     */
    public @NotNull ArenaConfiguration resetGameIdentifiers() {
        for (BedWarsArena arena : this.getAll()) {
            arena.setGameRoomIdentifier(null);
            arena.save();
        }

        return this;
    }
}
