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

package com.github.minemaniauk.minemaniatntrun;

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum BedWarsUpgrade {
    SHARPNESS(() -> new CozyItem()
            .setMaterial(Material.IRON_SWORD)
            .setName("&f&lSharpness")
            .setLore("&7Gives everyone on your team infinite sharpness!")) {

        @Override
        public int getCost(int level) {
            return 5;
        }

        @Override
        public void onPurchase(@NotNull Team team) {
            team.setUpgradeLevel(SHARPNESS, team.getUpgradeLevel(SHARPNESS) + 1);

            // Update team.
            team.updateSwords();
        }
    },
    PROTECTION(() -> new CozyItem()
            .setMaterial(Material.IRON_CHESTPLATE)
            .setName("&f&lProtection")
            .setLore("&7Gives everyone on your team armour protection.")) {
        @Override
        public int getCost(int level) {
            return level * 5;
        }

        @Override
        public void onPurchase(@NotNull Team team) {
            team.setUpgradeLevel(PROTECTION, team.getUpgradeLevel(PROTECTION) + 1);

            // Update team.
            team.updateArmour();
        }
    },
    FORGE(() -> new CozyItem()
            .setMaterial(Material.FURNACE)
            .setName("&f&lTeam Generator")
            .setLore("&7Upgrades your teams generator.")) {
        @Override
        public int getCost(int level) {
            return level * 4;
        }

        @Override
        public void onPurchase(@NotNull Team team) {
            team.setUpgradeLevel(FORGE, team.getUpgradeLevel(FORGE) + 1);

            team.getGenerator().ifPresent(generator -> {
                generator.setLevel(generator.getLevel() + 1);
            });
        }
    };

    private final @NotNull ItemFactory factory;

    BedWarsUpgrade(@NotNull ItemFactory factory) {
        this.factory = factory;
    }

    public interface ItemFactory {

        @NotNull CozyItem create();

    }

    public abstract int getCost(int level);

    public abstract void onPurchase(@NotNull Team team);

    public @NotNull CozyItem createDisplayItem() {
        return factory.create();
    }
}
