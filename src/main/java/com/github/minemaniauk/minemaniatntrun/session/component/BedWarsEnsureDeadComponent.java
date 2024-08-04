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
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

public class BedWarsEnsureDeadComponent extends TaskContainer implements SessionComponent<BedWarsArena> {

    private static final @NotNull String ENSURE_DEAD_IDENTIFIER = "ENSURE_DEAD_IDENTIFIER";

    private final @NotNull BedWarsSession session;

    /**
     * Used to create a new session component.
     *
     * @param session The instance of the session.
     */
    public BedWarsEnsureDeadComponent(@NotNull BedWarsSession session) {
        this.session = session;
    }

    @Override
    public @NotNull BedWarsSession getSession() {
        return session;
    }

    @Override
    public void start() {

        this.runTaskLoop(ENSURE_DEAD_IDENTIFIER, () -> {

            for (Team team : this.session.getTeamList()) {
                for (TeamPlayer player : team.getPlayerList()) {
                    if (player.isDead()) {
                        player.getPlayer().ifPresent(temp -> {
                            new PlayerUser(temp).forceGameMode(GameMode.SPECTATOR);
                        });
                    }
                }
            }

        }, 20);
    }

    @Override
    public void stop() {
        this.stopAllTasks();
    }
}