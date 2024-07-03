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

import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the available team colors.
 */
public enum TeamColor {
    RED("Red", "&c", Material.RED_BED, Material.RED_WOOL, Color.RED),
    ORANGE("Orange", "&6", Material.ORANGE_BED, Material.ORANGE_WOOL, Color.ORANGE),
    YELLOW("Yellow", "&e", Material.YELLOW_BED, Material.YELLOW_WOOL, Color.YELLOW),
    GREEN("Green", "&a", Material.LIME_BED, Material.LIME_WOOL, Color.GREEN),
    BLUE("Blue", "&b", Material.LIGHT_BLUE_BED, Material.LIGHT_BLUE_WOOL, Color.BLUE),
    PINK("Pink", "&d", Material.PINK_BED, Material.PINK_WOOL, Color.PURPLE),
    WHITE("White", "&f", Material.WHITE_BED, Material.WHITE_WOOL, Color.WHITE),
    GRAY("Gray", "&7", Material.GRAY_BED, Material.GRAY_WOOL, Color.GRAY);

    private final @NotNull String title;
    private final @NotNull String colorCode;
    private final @NotNull Material bed;
    private final @NotNull Material wool;
    private final @NotNull Color bukkitColor;

    /**
     * Used to create a new team color.
     *
     * @param title     The title of the team color.
     * @param colorCode The color code to use.
     * @param bed The material of the color teams bed.
     */
    TeamColor(@NotNull String title, @NotNull String colorCode, @NotNull Material bed, @NotNull Material wool, @NotNull Color bukkitColor) {
        this.title = title;
        this.colorCode = colorCode;
        this.bed = bed;
        this.wool = wool;
        this.bukkitColor = bukkitColor;
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

    public @NotNull Material getBed() {
        return this.bed;
    }

    public @NotNull Material getWool() {
         return this.wool;
    }

    public @NotNull Color getBukkitColor() {
        return this.bukkitColor;
    }
}
