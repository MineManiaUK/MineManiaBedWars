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

import com.github.minemaniauk.api.database.record.GameRoomRecord;
import com.github.minemaniauk.api.game.session.Session;
import com.github.minemaniauk.minemaniatntrun.MineManiaBedWars;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArenaFactory;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.TeamColor;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a bed wars session.
 */
public class BedWarsSession extends Session<BedWarsArena> {

    private final @NotNull GameRoomRecord record;
    private @NotNull BedWarsStatus status;
    private @NotNull List<@NotNull Team> teamList;

    /**
     * Used to create a new bed wars session.
     *
     * @param arenaIdentifier The arena's identifier.
     */
    public BedWarsSession(@NotNull UUID arenaIdentifier) {
        super(arenaIdentifier, new BedWarsArenaFactory());

        // Attempt to get the instance of the game room record.
        final Optional<GameRoomRecord> optionalRecord = this.getArena().getGameRoom();

        // Check if the game room record is null.
        if (optionalRecord.isEmpty()) {
            throw new RuntimeException("Game room record is null.");
        }

        this.record = optionalRecord.get();
        this.status = BedWarsStatus.SELECTING_TEAMS;
        this.teamList = new ArrayList<>();

        // Populate the team list.
        this.getArena().getTeamLocationList().forEach(
                location -> this.teamList.add(new Team(location))
        );
    }

    /**
     * Called when a block break event is triggered within the arena.
     *
     * @param event The instance of the event.
     * @param teamLocation The instance of the team location.
     *                     This will be null if it wasn't in a team location.
     */
    public void onBlockBreak(@NotNull BlockBreakEvent event, @Nullable TeamLocation teamLocation) {
    }

    /**
     * Used to get the list of player uuids.
     *
     * @return The list of player uuids.
     */
    public @NotNull List<UUID> getPlayerUuids() {
        return this.record.getPlayerUuids();
    }

    /**
     * Used to get the list of online players.
     * The players in the game room that are online.
     *
     * @return The list of online players.
     */
    public @NotNull List<Player> getOnlinePlayers() {

        // Create the instance of the list.
        List<Player> playerList = new ArrayList<>();

        // Add the online players.
        this.record.getPlayers().forEach(
                user -> MineManiaBedWars.getInstance()
                        .getOnlinePlayer(user.getUniqueId())
                        .ifPresent(playerList::add)
        );

        return playerList;
    }

    public @NotNull BedWarsStatus getStatus() {
        return this.status;
    }

    public @NotNull List<Team> getTeamList() {
        return this.teamList;
    }

    public @NotNull Optional<Team> getTeam(@NotNull TeamColor color) {
        for (Team team : this.teamList) {
            if (team.getLocation().getColor().equals(color)) return Optional.of(team);
        }
        return Optional.empty();
    }

    public @NotNull Optional<Team> getTeam(@NotNull UUID playerUuid) {
        for (Team team : this.teamList) {
            if (team.getPlayer(playerUuid).isPresent()) return Optional.of(team);
        }
        return Optional.empty();
    }

    public @NotNull BedWarsSession setStatus(@NotNull BedWarsStatus status) {
        this.status = status;
        return this;
    }
}
