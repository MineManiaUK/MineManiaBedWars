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

package com.github.minemaniauk.minemaniatntrun.inventory;

import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.jetbrains.annotations.NotNull;

public class UpgradeInventory extends ShopInventory {

    /**
     * Used to create a new shop inventory.
     *
     * @param teamPlayer
     */
    public UpgradeInventory(@NotNull TeamPlayer teamPlayer) {
        super(54, "&f₴₴₴₴₴₴₴₴钫", teamPlayer);

        // Start regenerating just in case someone gives them items while the inventory is still open.
        this.startRegeneratingInventory(20);
    }

    @Override
    protected void onGenerate(PlayerUser user) {

    }
}
