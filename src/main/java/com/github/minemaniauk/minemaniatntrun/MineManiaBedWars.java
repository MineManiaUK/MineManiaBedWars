/*
 * MineManiaTNTRun
 * Used for interacting with the database and message broker.
 * Copyright (C) 2023  MineManiaUK Staff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
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

import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.cozyplugins.cozylibrary.command.command.command.ProgrammableCommand;
import com.github.cozyplugins.cozylibrary.location.Region3D;
import com.github.minemaniauk.api.MineManiaAPI;
import com.github.minemaniauk.api.game.session.SessionManager;
import com.github.minemaniauk.bukkitapi.MineManiaAPI_Bukkit;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.command.*;
import com.github.minemaniauk.minemaniatntrun.configuration.ArenaConfiguration;
import com.github.minemaniauk.minemaniatntrun.inventory.ShopInventory;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents the main class.
 */
public final class MineManiaBedWars extends CozyPlugin implements Listener {

    private static @NotNull MineManiaBedWars instance;

    private @NotNull ArenaConfiguration arenaConfiguration;
    private @NotNull SessionManager<BedWarsSession, BedWarsArena> sessionManager;

    @Override
    public boolean enableCommandDirectory() {
        return false;
    }

    @Override
    public void onCozyEnable() {

        // Initialize this instance.
        MineManiaBedWars.instance = this;

        // Add configuration.
        this.arenaConfiguration = new ArenaConfiguration();
        this.arenaConfiguration.reload();

        // Add arenas from configuration to api.
        this.arenaConfiguration.getAllTypes().forEach(
                arena -> MineManiaBedWars.getAPI().getGameManager().registerArena(arena)
        );

        // Add session manager.
        this.sessionManager = new SessionManager<>();

        // Add commands.
        this.addCommand(new ProgrammableCommand("bedwars")
                .setDescription("Contains bed wars commands.")
                .setSyntax("/bedwars")
                .addSubCommand(new ProgrammableCommand("arena")
                        .setDescription("Contains the arena commands")
                        .setSyntax("/bedwars arena")
                        .addSubCommand(new ArenaCreateCommand())
                        .addSubCommand(new ArenaSetSchematicCommand())
                        .addSubCommand(new ArenaSetSchematicLocationCommand())
                        .addSubCommand(new ArenaSetSpawnPointCommand())
                        .addSubCommand(new ArenaSetGeneratorCommand())
                        .addSubCommand(new ProgrammableCommand("team")
                                .setDescription("Contains the team commands")
                                .setSyntax("/bedwars arena team")
                                .addSubCommand(new TeamLocationCreateCommand())
                                .addSubCommand(new TeamLocationSetGeneratorRegionCommand())
                                .addSubCommand(new TeamLocationSetShopLocationCommand())
                                .addSubCommand(new TeamLocationSetSpawnPointCommand())
                                .addSubCommand(new TeamLocationSetUpgradesLocationCommand())
                        )
                )
        );

        // Register listener.
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // Loop though all sessions and stop them.
        this.sessionManager.stopAllSessionComponents();

        // Remove game identifier.
        this.getArenaConfiguration().resetGameIdentifiers();

        // Unregister the local arenas.
        MineManiaBedWars.getAPI().getGameManager().unregisterLocalArenas();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        final Location location = event.getBlock().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Check if it was in a team location.
        final TeamLocation teamLocation = arena.getTeamLocation(location).orElse(null);

        session.onBlockBreak(event, teamLocation);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        final Location location = event.getBlock().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Check if it was in a team location.
        final TeamLocation teamLocation = arena.getTeamLocation(location).orElse(null);

        session.onBlockPlace(event, teamLocation);
    }

    @EventHandler
    public void onEntityInteractEvent(PlayerInteractEntityEvent event) {

        final Location location = event.getPlayer().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Get the instance of the team player that clicked.
        final Optional<TeamPlayer> optionalTeamPlayer = session.getTeamPlayer(event.getPlayer().getUniqueId());
        if (optionalTeamPlayer.isEmpty()) return;

        final TeamPlayer teamPlayer = optionalTeamPlayer.get();

        if (!(event.getRightClicked() instanceof Villager villager)) return;

        if (villager.getName().contains("Shop")) {
            new ShopInventory(teamPlayer).open(event.getPlayer());
        }
    }

    /**
     * Used to get the instance of the local arena configuration.
     *
     * @return The local arena configuration.
     */
    public @NotNull ArenaConfiguration getArenaConfiguration() {
        return this.arenaConfiguration;
    }

    /**
     * Used to get the instance of an online player from the uuid.
     *
     * @param playerUuid The player uuid to look for.
     * @return The optional player.
     */
    public @NotNull Optional<Player> getOnlinePlayer(@NotNull UUID playerUuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(playerUuid)) return Optional.of(player);
        }
        return Optional.empty();
    }

    /**
     * Used to get the instance of an arena from a specific location.
     *
     * @param location The location inside an arena.
     * @return The arena that contains the location.
     */
    public @NotNull Optional<BedWarsArena> getArena(@NotNull Location location) {
        for (BedWarsArena arena : this.getArenaConfiguration().getAllTypes()) {
            if (arena.getRegion().isEmpty()) continue;
            if (arena.getRegion().get().contains(location)) return Optional.of(arena);
        }

        return Optional.empty();
    }

    public @NotNull SessionManager<BedWarsSession, BedWarsArena> getSessionManager() {
        return this.sessionManager;
    }

    /**
     * Used to create a new arena and register it
     * with the api and plugin.
     *
     * @param identifier The instance of the identifier.
     * @param region     The instance of the arena region.
     * @param minPlayers The min number of players.
     * @param maxPlayers The max number of players.
     * @return The instance of the tnt arena created.
     */
    public @NotNull BedWarsArena createArena(@NotNull UUID identifier,
                                             @NotNull Region3D region,
                                             int minPlayers,
                                             int maxPlayers) {

        BedWarsArena arena = new BedWarsArena(identifier);
        arena.setRegion(region);
        arena.setMinPlayers(minPlayers);
        arena.setMaxPlayers(maxPlayers);

        // Register and save the arena.
        MineManiaBedWars.getAPI().getGameManager().registerArena(arena);
        return arena;
    }

    /**
     * Used to get the instance of this plugin.
     *
     * @return The instance of this plugin.
     */
    public static @NotNull MineManiaBedWars getInstance() {
        return MineManiaBedWars.instance;
    }

    /**
     * Used to get the instance of the mine mania api.
     *
     * @return The instance of the mine mania api.
     */
    public static @NotNull MineManiaAPI getAPI() {
        return MineManiaAPI_Bukkit.getInstance().getAPI();
    }
}
