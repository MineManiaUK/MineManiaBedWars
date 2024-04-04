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

package com.github.minemaniauk.minemaniatntrun.team.player;

import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a team player.
 * A player in a bed wars team.
 */
public class TeamPlayer extends TaskContainer {

    public static final @NotNull String RESPAWN_TASK_IDENTIFIER = "RESPAWN_TASK_IDENTIFIER";
    public static final @NotNull Duration RESPAWN_TIME_LENGTH = Duration.ofSeconds(5);

    private final @NotNull Team teamPointer;
    private final @NotNull UUID playerUuid;
    private final @NotNull ArmorType armourType;

    private boolean isDead;

    /**
     * Used to create a new team player.
     *
     * @param team The instance of the team the player is in.
     * @param playerUuid The player's uuid.
     */
    public TeamPlayer(@NotNull Team team, @NotNull UUID playerUuid) {
        this.teamPointer = team;
        this.playerUuid = playerUuid;
        this.armourType = ArmorType.NONE;

        this.isDead = false;
    }

    /**
     * Used to get the instance of the team
     * the player is in.
     *
     * @return The instance of the team.
     */
    public @NotNull Team getTeam() {
        return this.teamPointer;
    }

    /**
     * Used to get this team member as a player user.
     *
     * @return The instance of the player user.
     */
    public @NotNull UUID getPlayerUuid() {
        return this.playerUuid;
    }

    /**
     * Used to get the player if they are online.
     * Otherwise, it will return empty.
     *
     * @return The instance of the player.
     */
    public @NotNull Optional<Player> getPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(this.playerUuid)) return Optional.of(player);
        }
        return Optional.empty();
    }

    public @NotNull String getName() {
        final String name = Bukkit.getOfflinePlayer(this.playerUuid).getName();
        return name == null ? "null" : name;
    }

    /**
     * Used to get this team player's armor type.
     *
     * @return The team player's armor type.
     */
    public @NotNull ArmorType getArmourType() {
        return this.armourType;
    }

    public boolean isDead() {
        return this.isDead;
    }

    /**
     * Used to kill the player.
     * This will put them in spectator and if the
     * bed is still active, start the respawn timer.
     *
     * @return This instance.
     */
    public @NotNull TeamPlayer kill() {
        this.isDead = true;

        this.getPlayer().ifPresent(player -> {
            player.teleport(this.getTeam().getLocation().getSpawnPoint());
            player.setGameMode(GameMode.SPECTATOR);
        });

        if (this.getTeam().hasBed()) {
            this.startRespawnTask();
        }

        return this;
    }

    public @NotNull TeamPlayer startRespawnTask() {

        long startRespawnTimeMillis = System.currentTimeMillis();

        this.runTaskLoop(TeamPlayer.RESPAWN_TASK_IDENTIFIER, () -> {

            final long endTimeMills = startRespawnTimeMillis + TeamPlayer.RESPAWN_TIME_LENGTH.toMillis();

            if (endTimeMills <= System.currentTimeMillis()) {
                this.respawn();
                this.stopTask(TeamPlayer.RESPAWN_TASK_IDENTIFIER);
            }

        }, 20);
        return this;
    }

    public @NotNull TeamPlayer respawn() {
        this.isDead = false;

        this.getPlayer().ifPresent(player -> {
            player.teleport(this.getTeam().getLocation().getSpawnPoint());
            player.setGameMode(GameMode.SURVIVAL);
        });

        return this;
    }
}
