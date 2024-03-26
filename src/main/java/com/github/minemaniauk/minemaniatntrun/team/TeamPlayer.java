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

package com.github.minemaniauk.minemaniatntrun.team;

import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a team player.
 * A player in a bed wars team.
 */
public class TeamPlayer {

    private final @NotNull Team teamPointer;
    private final @NotNull PlayerUser user;
    private final @NotNull ArmorType armourType;

    /**
     * Used to create a new team player.
     *
     * @param team The instance of the team the player is in.
     * @param user The instance of the user.
     */
    public TeamPlayer(@NotNull Team team, @NotNull PlayerUser user) {
        this.teamPointer = team;
        this.user = user;
        this.armourType = ArmorType.NONE;
    }

    /**
     * Used to get the instance of the team
     * the player is in.
     *
     * @return The instance of the team.
     */
    public @NotNull Team getTeam() {
        return this.teamPointer;
    }

    /**
     * Used to get this team member as a player user.
     *
     * @return The instance of the player user.
     */
    public @NotNull PlayerUser getUser() {
        return this.user;
    }

    /**
     * Used to get this team player's armor type.
     *
     * @return The team player's armor type.
     */
    public @NotNull ArmorType getArmourType() {
        return this.armourType;
    }
}
