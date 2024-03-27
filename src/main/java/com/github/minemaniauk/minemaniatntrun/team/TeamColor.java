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

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the available team colors.
 */
public enum TeamColor {
    RED("Red", "&c"),
    ORANGE("Orange", "&6"),
    YELLOW("Yellow", "&e"),
    GREEN("Green", "&a"),
    BLUE("Blue", "&b"),
    PINK("Pink", "&d"),
    WHITE("White", "&f"),
    GRAY("Gray", "&7");

    private final @NotNull String title;
    private final @NotNull String colorCode;

    /**
     * Used to create a new team color.
     *
     * @param title The title of the team color.
     * @param colorCode The color code to use.
     */
    TeamColor(@NotNull String title, @NotNull String colorCode) {
        this.title = title;
        this.colorCode = colorCode;
    }

    public @NotNull String getTitle() {
        return this.title;
    }

    public @NotNull String getName() {
        return this.title.toLowerCase();
    }

    public @NotNull String getColorCode() {
        return this.colorCode;
    }
}
