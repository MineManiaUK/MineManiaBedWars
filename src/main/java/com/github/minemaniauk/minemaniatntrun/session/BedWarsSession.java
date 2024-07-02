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

import com.github.cozyplugins.cozylibrary.MessageManager;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.api.MineManiaLocation;
import com.github.minemaniauk.api.database.collection.UserCollection;
import com.github.minemaniauk.api.database.record.GameRoomRecord;
import com.github.minemaniauk.api.game.session.Session;
import com.github.minemaniauk.api.user.MineManiaUser;
import com.github.minemaniauk.minemaniatntrun.MineManiaBedWars;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArenaFactory;
import com.github.minemaniauk.minemaniatntrun.generator.Generator;
import com.github.minemaniauk.minemaniatntrun.session.component.*;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.TeamColor;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    private Team winningTeam;

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
                location -> this.teamList.add(new Team(this, location))
        );

        this.registerComponent(new BedWarsBlockInteractionsComponent(this));
        this.registerComponent(new BedWarsGeneratorComponent(this));
        this.registerComponent(new BedWarsNPCComponent(this));
        this.registerComponent(new BedWarsOutOfBoundsComponent(this));
        this.registerComponent(new BedWarsScoreboardComponent(this));
        this.registerComponent(new BedWarsSelectTeamComponent(this));
        this.registerComponent(new BedWarsEndComponent(this));

        this.getComponent(BedWarsOutOfBoundsComponent.class).start();
        this.getComponent(BedWarsScoreboardComponent.class).start();
        this.getComponent(BedWarsSelectTeamComponent.class).start();
    }

    /**
     * Called when a block break event is triggered within the arena.
     *
     * @param event        The instance of the event.
     * @param teamLocation The instance of the team location.
     *                     This will be null if it wasn't in a team location.
     */
    public void onBlockBreak(@NotNull BlockBreakEvent event, @Nullable TeamLocation teamLocation) {
        this.getComponent(BedWarsBlockInteractionsComponent.class).onBlockBreak(event, teamLocation);
    }

    /**
     * Called when a block place event is triggered within the arena.
     *
     * @param event        The instance of the event.
     * @param teamLocation The instance of the team location.
     *                     This will be null if it wasn't in a team location.
     */
    public void onBlockPlace(@NotNull BlockPlaceEvent event, @Nullable TeamLocation teamLocation) {
        this.getComponent(BedWarsBlockInteractionsComponent.class).onBlockPlace(event, teamLocation);
    }

    public void onPlayerDeath(@NotNull EntityDamageEvent event) {
        Player player = (Player) event.getEntity();

        if (player.getHealth() - event.getDamage() > 0) {
            return;
        }

        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());

        if (this.getStatus() == BedWarsStatus.SELECTING_TEAMS) return;

        TeamPlayer teamPlayer = this.getTeamPlayer(player.getUniqueId()).orElse(null);
        if (teamPlayer == null) return;

        teamPlayer.kill();
    }

    public void onPlayerDeath(@NotNull TeamPlayer player) {
        if (this.getStatus() == BedWarsStatus.SELECTING_TEAMS) return;
        Bukkit.broadcastMessage(MessageManager.parse("&7&l> &7" + player.getName() + " died."));
        if (this.shouldEnd()) this.onEnd();
    }

    /**
     * Called when the game is started.
     */
    public void onStartGame() {

        // Set the status.
        this.setStatus(BedWarsStatus.GAME);

        // Ensure the teams are made.
        this.ensureTeams();

        if (this.getOnlinePlayers().size() <= 1) {
            this.endGameFully();
            return;
        }

        // Teleport players to the correct area.
        this.teleportTeams();

        // Put them in the correct game mode and clear there inventory.
        this.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
        });

        // Give the players there default items.
        this.giveDefaultItems();

        // Start the npc component.
        this.getComponent(BedWarsNPCComponent.class).start();

        // Start generator component.
        this.getComponent(BedWarsGeneratorComponent.class).start();
    }

    public void onEnd() {
        Team winningTeam = this.getWinningTeam();
        this.setStatus(BedWarsStatus.ENDING);

        final int pawReward = (this.getPlayerUuids().size() - 1) * 10;

        for (TeamPlayer teamPlayer : winningTeam.getPlayerList()) {

            // Give the winner the correct amount of paws.
            MineManiaBedWars.getAPI().getDatabase()
                    .getTable(UserCollection.class)
                    .getUserRecord(teamPlayer.getPlayerUuid())
                    .ifPresent(user -> {
                        user.addPaws(pawReward);
                        MineManiaBedWars.getAPI().getDatabase()
                                .getTable(UserCollection.class)
                                .insertRecord(user);
                    });

            // Send a message.
            teamPlayer.getPlayer().ifPresent(
                    player -> new PlayerUser(player).sendMessage("&a&l> &aYou have gained " + pawReward + " paws for winning.")
            );
        }

        this.getComponent(BedWarsEndComponent.class).start();
    }

    public void endGameFully() {
        MineManiaLocation location = new MineManiaLocation("hub", "null", 0, 0, 0);

        // Teleport the players.
        for (Player player : this.getOnlinePlayers()) {
            MineManiaUser user = new MineManiaUser(player.getUniqueId(), player.getName());
            user.getActions().teleport(location);
        }

        // Stop the arena and unregister the session.
        this.getArena().deactivate();
    }

    /**
     * Used to ensure that all players are in a team.
     *
     * @return This instance.
     */
    public @NotNull BedWarsSession ensureTeams() {

        final Iterator<Team> unbalancedTeams = this.getUnbalancedTeams().iterator();

        // Loop though the players that are not in a team.
        for (UUID playerUuid : this.getPlayersNotInATeam()) {

            // Check if all teams have been balanced.
            if (!unbalancedTeams.hasNext()) {

                // Add the player to a random team.
                final Team team = this.teamList.get(0);
                team.addPlayer(playerUuid);

                // When called again the unbalanced teams
                // list will be recalculated.
                this.ensureTeams();
                return this;
            }

            // Add the player to the next unbalanced team.
            unbalancedTeams.next().addPlayer(playerUuid);
        }

        return this;
    }

    public @NotNull BedWarsSession teleportTeams() {
        for (Team team : this.teamList) {
            team.teleportPlayers();
        }
        return this;
    }

    public @NotNull BedWarsSession giveDefaultItems() {
        for (Team team : this.teamList) {
            team.giveDefaultItems();
        }
        return this;
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

    /**
     * Used to get the list of player uuid's
     * where the player is not in a team.
     *
     * @return The list of player uuid's.
     */
    public @NotNull List<UUID> getPlayersNotInATeam() {
        List<UUID> list = new ArrayList<>();
        for (UUID playerUuid : this.getPlayerUuids()) {
            if (this.getTeam(playerUuid).isEmpty()) list.add(playerUuid);
        }
        return list;
    }

    public @NotNull BedWarsStatus getStatus() {
        return this.status;
    }

    public @NotNull List<Team> getTeamList() {
        return this.teamList;
    }

    /**
     * Used to get the number of players
     * in the biggest team.
     *
     * @return The number of players in the biggest team.
     */
    public int getAmountOfPlayersInBiggestTeam() {
        int currentAmount = 0;
        for (Team team : this.getTeamList()) {
            final int size = team.getPlayerList().size();
            if (size > currentAmount) currentAmount = size;
        }
        return currentAmount;
    }

    /**
     * Used to get the list of teams which have fewer
     * players then the biggest team.
     *
     * @return The list of unbalanced teams.
     */
    public @NotNull List<Team> getUnbalancedTeams() {

        // Get the number of players in the biggest team.
        final int amountOfPlayersInBiggestTeam = this.getAmountOfPlayersInBiggestTeam();

        List<Team> unbalancedTeamList = new ArrayList<>();
        for (Team team : this.getTeamList()) {
            if (team.getPlayerList().size() < amountOfPlayersInBiggestTeam) {
                unbalancedTeamList.add(team);
            }
        }
        return unbalancedTeamList;
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

    /**
     * Used to get a player as a team player.
     *
     * @param playerUuid The player's uuid.
     * @return The optional team player.
     */
    public @NotNull Optional<TeamPlayer> getTeamPlayer(@NotNull UUID playerUuid) {
        final Optional<Team> optionalTeam = this.getTeam(playerUuid);
        return optionalTeam.flatMap(team -> team.getPlayer(playerUuid));
    }

    public @NotNull BedWarsSession setStatus(@NotNull BedWarsStatus status) {
        this.status = status;
        return this;
    }

    public boolean shouldEnd() {
        int amountIn = 0;

        for (Team team : this.getTeamList()) {
            if (team.isOut()) continue;
            amountIn++;
        }

        return amountIn <= 1;
    }

    public @NotNull Team getWinningTeam() {

        if (this.winningTeam != null) {
            return this.winningTeam;
        }

        for (Team team : this.getTeamList()) {
            if (team.isOut()) continue;
            this.winningTeam = team;
            return team;
        }
        throw new RuntimeException("All teams are out.");
    }
}
