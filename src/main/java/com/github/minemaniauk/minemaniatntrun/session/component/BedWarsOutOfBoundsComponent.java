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

package com.github.minemaniauk.minemaniatntrun.session.component;

import com.github.cozyplugins.cozylibrary.location.Region3D;
import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsStatus;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BedWarsOutOfBoundsComponent extends TaskContainer implements SessionComponent<BedWarsArena> {

    private static final @NotNull String OUT_OF_BOUNDS_IDENTIFIER = "OUT_OF_BOUNDS_IDENTIFIER";
    private static final @NotNull String SPECTATOR_IDENTIFIER = "SPECTATOR_IDENTIFIER";

    private final @NotNull BedWarsSession session;

    /**
     * Used to create a new session component.
     *
     * @param session The instance of the session.
     */
    public BedWarsOutOfBoundsComponent(@NotNull BedWarsSession session) {
        this.session = session;
    }

    @Override
    public @NotNull BedWarsSession getSession() {
        return session;
    }

    @Override
    public void start() {
        this.runTaskLoop(OUT_OF_BOUNDS_IDENTIFIER, () -> {

            final BedWarsArena arena = this.getSession().getArena();
            final Location spawnPoint = arena.getSpawnPoint().orElse(null);

            if (arena.getRegion().isEmpty()) {
                throw new RuntimeException("Arena has no region. " + arena.getIdentifier());
            }

            if (spawnPoint == null) {
                throw  new RuntimeException("Spawn point doesnt exist for " + arena.getIdentifier());
            }

            Region3D region = arena.getRegion().get();

            for (Player player : this.getSession().getOnlinePlayers()) {
                if (region.contains(player.getLocation())) continue;

                // Check if it is the Y cord.
                if (player.getLocation().getBlockY() <= region.getMinPoint().getBlockY()) {

                    final TeamPlayer teamPlayer = this.getSession().getTeamPlayer(player.getUniqueId()).orElseThrow();

                    if (teamPlayer.isDead()) {
                        player.teleport(spawnPoint);
                        return;
                    }

                    teamPlayer.kill();
                }

                player.teleport(spawnPoint);
            }

        }, 4);

        this.runTaskLoop(SPECTATOR_IDENTIFIER, () -> {

            if (this.getSession().getStatus().equals(BedWarsStatus.SELECTING_TEAMS)) {
                for (Player player : this.getSession().getOnlinePlayers()) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }

        }, 20);
    }

    @Override
    public void stop() {
        this.stopAllTasks();
    }
}