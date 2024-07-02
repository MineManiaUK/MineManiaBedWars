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

import com.github.cozyplugins.cozylibrary.scoreboard.Scoreboard;
import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsStatus;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the scoreboard component.
 * Handles the scoreboard for the players.
 */
public class BedWarsScoreboardComponent extends TaskContainer implements SessionComponent<BedWarsArena> {

    private static final @NotNull String SCOREBOARD_IDENTIFIER = "SCOREBOARD_IDENTIFIER";

    private final @NotNull BedWarsSession session;

    /**
     * Used to create a new session component.
     *
     * @param session The instance of the session.
     */
    public BedWarsScoreboardComponent(@NotNull BedWarsSession session) {
        this.session = session;
    }

    @Override
    public @NotNull BedWarsSession getSession() {
        return session;
    }

    @Override
    public void start() {
        this.runTaskLoop(SCOREBOARD_IDENTIFIER, () -> {

            Scoreboard scoreboard = this.generateScoreboard();

            for (Player player : this.getSession().getOnlinePlayers()) {
                new PlayerUser(player).setScoreboard(scoreboard);
            }

        }, 20);
    }

    @Override
    public void stop() {
        this.stopAllTasks();
    }

    /**
     * Used to generate a new updated instance of the scoreboard.
     *
     * @return The instance of the scoreboard.
     */
    public @NotNull Scoreboard generateScoreboard() {
        if (this.getSession().getStatus().equals(BedWarsStatus.ENDING)) {
            return new Scoreboard();
        }
        if (this.getSession().getStatus().equals(BedWarsStatus.SELECTING_TEAMS)) {
            return new Scoreboard()
                    .setTitle("&e&lBED WARS")
                    .setLines("&8" + this.getSession().getArenaIdentifier().toString().substring(0, 7),
                            "&7",
                            "&7Selecting Teams",
                            "&7",
                            "&fStarting in &a" + this.getSession().getComponent(BedWarsSelectTeamComponent.class).getCountDown().toSeconds() + "s",
                            "&7",
                            "&eplay.minemania.co"
                    );
        }

        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setTitle("&e&lBED WARS");
        scoreboard.setLines(
                "&8" + this.getSession().getArenaIdentifier().toString().substring(0, 7),
                "&7"
        );

        for (Team team : this.getSession().getTeamList()) {
            scoreboard.addLines(team.getLocation().getColor().getColorCode()
                    + "► &f" + team.getLocation().getColor().getTitle()
                    + (team.hasBed() ? " &a✔" : (team.getAlivePlayers().isEmpty() ? "&c❌" : "&e" + team.getAlivePlayers().size() + "/" + team.getPlayerList().size()))
            );
        }

        scoreboard.addLines(
                "&7",
                "&eplay.minemania.co"
        );

        return scoreboard;
    }
}
