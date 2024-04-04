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

import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.inventory.SelectTeamInventory;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class BedWarsSelectTeamComponent extends TaskContainer implements SessionComponent<BedWarsArena> {

    private static final @NotNull String SELECT_TEAMS_IDENTIFIER = "SELECT_TEAMS_IDENTIFIER";
    private static final Duration toWait = Duration.ofSeconds(40);

    private final @NotNull BedWarsSession session;
    private long startTimeStamp;

    /**
     * Used to create a new session component.
     *
     * @param session The instance of the session.
     */
    public BedWarsSelectTeamComponent(@NotNull BedWarsSession session) {
        this.session = session;
    }

    @Override
    public @NotNull BedWarsSession getSession() {
        return session;
    }

    @Override
    public void start() {

        this.startTimeStamp = System.currentTimeMillis();

        this.getSession().setStatus(BedWarsStatus.SELECTING_TEAMS);

        this.runTaskLoop(SELECT_TEAMS_IDENTIFIER, () -> {

            for (Player player : this.getSession().getOnlinePlayers()) {
                if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST)) continue;
                if (this.getSession().getTeam(player.getUniqueId()).isPresent()) continue;

                new SelectTeamInventory(this.getSession()).open(player);
            }

            if (getCountDown().toSeconds() < 0) {
                this.getSession().onStartGame();
                this.stop();
            }

        }, 100);
    }

    @Override
    public void stop() {
        this.stopAllTasks();
    }

    /**
     * Used to get how long until the session will start.
     *
     * @return The duration until the session will start.
     */
    public @NotNull Duration getCountDown() {
        return Duration.ofMillis((startTimeStamp + toWait.toMillis()) - System.currentTimeMillis());
    }
}