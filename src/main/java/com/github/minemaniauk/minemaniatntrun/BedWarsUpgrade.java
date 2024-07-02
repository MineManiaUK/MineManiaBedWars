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
    SHARPNESS(() -> new CozyItem("&f&lSharpness", Material.IRON_SWORD), 5) {
        @Override
        public void onPurchase(@NotNull Team team) {
            this.setLevel(1);
            team.setUpgrade(SHARPNESS, this);
        }
    };

    private final @NotNull ItemFactory factory;
    private final int cost;
    private int level;

    BedWarsUpgrade(@NotNull ItemFactory factory, int cost) {
        this.factory = factory;
        this.cost = cost;
        this.level = 0;
    }

    public interface ItemFactory {

        @NotNull CozyItem create();

    }

    public abstract void onPurchase(@NotNull Team team);

    public @NotNull CozyItem createDisplayItem() {
        return factory.create();
    }

    public int getCost() {
        return cost;
    }

    public int getLevel() {
        return level;
    }

    public BedWarsUpgrade setLevel(int level) {
        this.level = level;
        return this;
    }
}
