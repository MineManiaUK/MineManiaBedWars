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

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the types of armour that can
 * exist in a bed wars game.
 */
public enum ArmorType {
    NONE(0, "No Armor") {
        @Override
        public @NotNull Player applyArmor(@NotNull Player player) {
            return player;
        }
    },
    LEATHER(1, "Leather Armor") {
        @Override
        public @NotNull Player applyArmor(@NotNull Player player) {
            player.getInventory().setBoots(new CozyItem()
                    .setName("&7&lLeather Boots")
                    .setLore("&7Open the shop and click the armor",
                            "&7upgrades to upgrade your armor.")
                    .create()
            );

            player.getInventory().setLeggings(new CozyItem()
                    .setName("&7&lLeather Leggings")
                    .setLore("&7Open the shop and click the armor",
                            "&7upgrades to upgrade your armor.")
                    .create()
            );
            return player;
        }
    };

    private final int index;
    private final @NotNull String title;

    /**
     * Used to create a new armor type.
     *
     * @param index The index that the armor is compared to another armor.
     *              The higher the number the better.
     * @param title The title of the armor used in the shop.
     */
    ArmorType(int index, @NotNull String title) {
        this.index = index;
        this.title = title;
    }

    public abstract @NotNull Player applyArmor(@NotNull Player player);

    /**
     * Used to get the armor index.
     * This is used to see how good the armor is
     * compared to another armor.
     * The higher the number the better.
     *
     * @return The armor index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Used to get the armor's title.
     * This can be used in the shop.
     *
     * @return The armor's title.
     */
    public @NotNull String getTitle() {
        return title;
    }

    /**
     * Used to get the name of the armor.
     * This is the title but in lowercase.
     *
     * @return The armor's name.
     */
    public @NotNull String getName() {
        return this.title.toLowerCase();
    }
}
