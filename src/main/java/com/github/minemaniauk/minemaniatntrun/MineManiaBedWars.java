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
import com.github.minemaniauk.api.MineManiaAPI;
import com.github.minemaniauk.api.game.session.SessionManager;
import com.github.minemaniauk.bukkitapi.MineManiaAPI_Bukkit;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.configuration.ArenaConfiguration;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents the main class.
 */
public final class MineManiaBedWars extends CozyPlugin {

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
                )
        );
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
