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
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.minemaniauk.minemaniatntrun.MineManiaBedWars;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.team.TeamColor;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class TeamLocationCreateCommand implements CondensedCommand {

    @Override
    public @Nullable CommandCredentials getCredentials() {
        return new CommandCredentials()
                .addPermission("bedwars.admin")
                .setSyntax("/bedwars arena team create [Color] [RadiusFromSpawnPoint]");
    }

    @Override
    public @NotNull String getName() {
        return "create";
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull CommandArguments commandArguments) {
        return new CommandSuggestions()
                .append(Arrays.stream(TeamColor.values()).map(TeamColor::getName).toList())
                .append(List.of("[RadiusFromSpawnPoint]"));
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull CommandArguments commandArguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayerUser(@NotNull PlayerUser user, @NotNull CommandArguments arguments, @NotNull CommandStatus status) {

        // Check if they have provided enough arguments.
        if (arguments.getArguments().size() < 2) {
            user.sendMessage("&c&l> &cPlease provide the correct number of arguments. &7" + this.getSyntax());
            return new CommandStatus();
        }

        // Get the min and max players.
        try {
            final TeamColor teamColor = TeamColor.valueOf(arguments.getArguments().get(0).toUpperCase());
            final int radiusFromSpawnPoint = Integer.parseInt(arguments.getArguments().get(1));

            BedWarsArena arena = MineManiaBedWars.getInstance()
                    .getArena(user.getPlayer().getLocation())
                    .orElse(null);

            // Check if they are not in an arena.
            if (arena == null) {
                user.sendMessage("&7&l> &7You are not standing in an arena.");
                return new CommandStatus();
            }

            // Check if there is already a team in this location.
            TeamLocation currentTeam = arena.getTeamLocation(user.getPlayer().getLocation())
                    .orElse(null);

            if (currentTeam != null) {
                user.sendMessage("&7&l> &7There is already a team location here. The team is "
                        + currentTeam.getColor().getColorCode() + currentTeam.getColor().getTitle()
                        + "&7."
                );
                return new CommandStatus();
            }

            TeamLocation team = new TeamLocation(
                    teamColor,
                    user.getPlayer().getLocation(),
                    radiusFromSpawnPoint
            );
            arena.addTeamLocation(team);
            arena.save();

            user.sendMessage("&a&l> &aCreated a new team location. &f" + team);
            return new CommandStatus();

        } catch (Exception exception) {
            user.sendMessage("&7&l> &7Incorrect arguments." + this.getSyntax());
            return new CommandStatus();
        }
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
