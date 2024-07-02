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

import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a bedwars team instance.
 */
public class Team {

    private final @NotNull BedWarsSession sessionPointer;

    private final @NotNull TeamLocation location;
    private final @NotNull List<TeamPlayer> playerList;

    public Team(@NotNull BedWarsSession sessionPointer, @NotNull TeamLocation location) {
        this.sessionPointer = sessionPointer;
        this.location = location;
        this.playerList = new ArrayList<>();
    }

    public @NotNull BedWarsSession getSession() {
        return sessionPointer;
    }

    /**
     * Used to get the instance of the
     * team location info.
     *
     * @return The team location class.
     */
    public @NotNull TeamLocation getLocation() {
        return this.location;
    }

    /**
     * Used to get the list of player
     * uuid's in this team.
     *
     * @return The list of player uuid's.
     */
    public @NotNull List<TeamPlayer> getPlayerList() {
        return this.playerList;
    }

    /**
     * Used to get the list of team members
     * that are online.
     *
     * @return The list of online players
     * on this team.
     */
    public @NotNull List<TeamPlayer> getOnlinePlayerList() {
        return this.playerList.stream()
                .filter(teamPlayer -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getUniqueId)
                        .toList()
                        .contains(teamPlayer.getPlayerUuid())
                )
                .collect(Collectors.toList());
    }

    /**
     * Used to get a specific player on this team.
     *
     * @param playerUuid The player's uuid to look for.
     * @return The optional player.
     */
    public @NotNull Optional<TeamPlayer> getPlayer(@NotNull UUID playerUuid) {
        for (TeamPlayer player : this.playerList) {
            if (player.getPlayerUuid().equals(playerUuid)) return Optional.of(player);
        }
        return Optional.empty();
    }

    /**
     * Used to add a player to this team.
     *
     * @param playerUuid The player's uuid.
     * @return This instance.
     */
    public @NotNull Team addPlayer(@NotNull UUID playerUuid) {
        this.playerList.add(new TeamPlayer(this, playerUuid));
        return this;
    }

    /**
     * Used to remove a player from this team.
     *
     * @param playerUuid The player's uuid.
     * @return This instance.
     */
    public @NotNull Team removePlayer(@NotNull UUID playerUuid) {
        this.playerList.removeIf(
                player -> player.getPlayerUuid().equals(playerUuid)
        );
        return this;
    }

    /**
     * Used to check if the team still has
     * a bed in their base.
     *
     * @return True if the team has a bed.
     */
    public boolean hasBed() {
        if (this.getPlayerList().isEmpty()) return false;
        return this.getLocation().getRegion().contains(
                this.getLocation().getColor().getBed()
        );
    }

    /**
     * Used to teleport all the players in this team
     * to the team spawn point.
     *
     * @return This instance.
     */
    public @NotNull Team teleportPlayers() {
        for (TeamPlayer player : this.getOnlinePlayerList()) {
            player.getPlayer().orElseThrow().teleport(this.getLocation().getSpawnPoint());
        }
        return this;
    }

    /**
     * Used to give each player in the team there
     * default items.
     *
     * @return This instance.
     */
    public @NotNull Team giveDefaultItems() {
        for (TeamPlayer player : this.getOnlinePlayerList()) {
            player.giveDefaultItems();
        }
        return this;
    }

    public @NotNull List<TeamPlayer> getAlivePlayers() {
        return this.playerList.stream().filter(player -> !player.isDead()).toList();
    }

    public boolean isOut() {
        if (this.hasBed()) return false;
        return !this.getPlayerList().stream()
                .map(TeamPlayer::isDead).toList()
                .contains(false);
    }
}
