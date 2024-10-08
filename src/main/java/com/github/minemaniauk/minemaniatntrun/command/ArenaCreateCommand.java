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

package com.github.minemaniauk.minemaniatntrun.command;

import com.github.cozyplugins.cozylibrary.command.command.command.CondensedCommand;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandArguments;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandCredentials;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandStatus;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandSuggestions;
import com.github.cozyplugins.cozylibrary.location.Region;
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.minemaniauk.minemaniatntrun.MineManiaBedWarsPlugin;
import com.github.minemaniauk.minemaniatntrun.WorldEditUtility;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Represents the arena create command.
 */
public class ArenaCreateCommand implements CondensedCommand {

    @Override
    public @Nullable CommandCredentials getCredentials() {
        return new CommandCredentials()
                .addPermission("bedwars.admin");
    }

    @Override
    public @NotNull String getName() {
        return "create";
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull CommandArguments commandArguments) {
        return new CommandSuggestions()
                .append(List.of("[MinPlayers]"))
                .append(List.of("[MaxPlayers]"));
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull CommandArguments commandArguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayerUser(@NotNull PlayerUser user, @NotNull CommandArguments arguments, @NotNull CommandStatus status) {

        // Check that the player has made a world edit selection.
        com.sk89q.worldedit.regions.Region region = WorldEditUtility.getSelection(user);

        // Check if a region has been selected.
        if (region == null) {
            user.sendMessage("&c&l> &cYou must select the area with world edit first. &7//pos1 //pos2");
            return new CommandStatus();
        }

        // Check if they have provided enough arguments.
        if (arguments.getArguments().size() < 2) {
            user.sendMessage("&c&l> &cPlease provide the correct number of arguments. &7" + this.getSyntax());
            return new CommandStatus();
        }

        // Get the min and max players.
        final int minPlayers = Integer.parseInt(arguments.getArguments().get(0));
        final int maxPlayers = Integer.parseInt(arguments.getArguments().get(1));

        // Create a cozy region.
        Region region3D = new Region(
                new Location(Bukkit.getWorld(region.getWorld().getName()),
                        region.getMaximumPoint().getBlockX(),
                        region.getMaximumPoint().getBlockY(),
                        region.getMaximumPoint().getBlockZ()
                ),
                new Location(Bukkit.getWorld(region.getWorld().getName()),
                        region.getMinimumPoint().getBlockX(),
                        region.getMinimumPoint().getBlockY(),
                        region.getMinimumPoint().getBlockZ()
                )
        );

        // Create the arena.
        BedWarsArena arena = MineManiaBedWarsPlugin.getInstance().createArena(UUID.randomUUID(), region3D, minPlayers, maxPlayers);
        user.sendMessage("&a&l> &aArena has been created with id &f" + arena.getIdentifier() + " &a with region &f" + arena.getRegion());
        WorldEditUtility.clearSelection(user);
        return new CommandStatus();
    }

    @Override
    public @Nullable CommandStatus onFakeUser(@NotNull FakeUser fakeUser, @NotNull CommandArguments commandArguments, @NotNull CommandStatus commandStatus) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onConsoleUser(@NotNull ConsoleUser consoleUser, @NotNull CommandArguments commandArguments, @NotNull CommandStatus commandStatus) {
        return null;
    }
}
