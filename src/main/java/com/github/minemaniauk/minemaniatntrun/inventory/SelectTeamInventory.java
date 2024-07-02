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

import com.github.cozyplugins.cozylibrary.inventory.CozyInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsStatus;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.TeamColor;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Represents the select team inventory.
 */
public class SelectTeamInventory extends CozyInventory {

    private final @NotNull BedWarsSession session;

    /**
     * Used to create a new shop inventory.
     *
     * @param session The instance of the session.
     */
    public SelectTeamInventory(@NotNull BedWarsSession session) {
        super(54, "&f₴₴₴₴₴₴₴₴钾");

        this.session = session;

        // Start the regenerating task.
        this.startRegeneratingInventory(4);
    }

    @Override
    protected void onGenerate(PlayerUser user) {
        this.resetInventory();

        if (this.session.getStatus() != BedWarsStatus.SELECTING_TEAMS) {
            this.close();
        }

        final int maxTeamSize = (int) Math.ceil((double) this.session.getPlayerUuids().size() / this.session.getTeamList().size());

        // The list of slots.
        List<Integer> slotList = List.of(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        );

        // The slot iterator.
        Iterator<Integer> slotIterator = slotList.iterator();

        // Loop though teams.
        for (Team team : this.session.getTeamList()) {

            // Check if there is a slot available.
            if (!slotIterator.hasNext()) continue;

            final TeamColor color = team.getLocation().getColor();

            final List<String> lore = new java.util.ArrayList<>(List.of(
                    "&7Click to join this team.",
                    "&7",
                    "&f&lPlayers &7[&f" + team.getPlayerList().size() + "&7/&f" + maxTeamSize + "&7]"
            ));
            for (TeamPlayer player : team.getPlayerList()) {
                lore.add("&7- &f" + player.getName());
            }

            this.setItem(new InventoryItem()
                    .setMaterial(color.getBed())
                    .setName(color.getColorCode() + "&l" + color.getTitle())
                    .setLore(lore)
                    .addAction((ClickAction) (player, type, inventory) -> {

                        if (team.getPlayerList().size() >= maxTeamSize) {
                            player.sendMessage("&7&l> &7This team already has the max amount of players.");
                            return;
                        }

                        final Optional<Team> optionalTeam = this.session.getTeam(player.getUuid());
                        optionalTeam.ifPresent(value -> value.removePlayer(player.getUuid()));

                        player.sendMessage("&a&l> &aJoined the &f" + color.getName() + "&a team.");
                        team.addPlayer(player.getUuid());
                    })
                    .addSlot(slotIterator.next())
            );
        }
    }
}
