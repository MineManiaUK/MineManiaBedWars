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

package com.github.minemaniauk.minemaniatntrun.session;

import com.github.minemaniauk.api.game.session.Session;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArenaFactory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a bed wars session.
 */
public class BedWarsSession extends Session<BedWarsArena> {

    /**
     * Used to create a new bed wars session.
     *
     * @param arenaIdentifier The arena's identifier.
     */
    public BedWarsSession(@NotNull UUID arenaIdentifier) {
        super(arenaIdentifier, new BedWarsArenaFactory());
    }
}
