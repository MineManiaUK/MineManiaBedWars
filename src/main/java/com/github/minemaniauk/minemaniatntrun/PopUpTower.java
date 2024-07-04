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

package com.github.minemaniauk.minemaniatntrun;

import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.component.BedWarsBlockInteractionsComponent;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PopUpTower {

    private final @NotNull Location location;
    private final @NotNull TeamPlayer teamPlayer;
    private final @NotNull List<List<Integer>> schematic;

    public PopUpTower(@NotNull Location location, @NotNull TeamPlayer player) {
        this.location = location;
        this.teamPlayer = player;
        this.schematic = (List<List<Integer>>) MineManiaBedWars.getInstance().getPopUpTowerConfig().getList("build");
    }

    public @NotNull Location getLocation() {
        return location;
    }

    public @NotNull PopUpTower construct(@NotNull List<Integer> part) {
        // Get part location.
        final Location partLocation = this.location.clone().add(new Vector(part.get(1), part.get(2), part.get(3)));

        if (!partLocation.getBlock().getType().equals(Material.AIR)) return this;

        // Check if it was in an arena.
        final BedWarsArena arena = MineManiaBedWars.getInstance().getArena(location).orElse(null);
        if (arena == null) return this;

        // Check if the arena is in a session.
        BedWarsSession session = MineManiaBedWars.getInstance().getSessionManager().getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return this;

        final boolean canPlace = session.checkIfPlayerCanPlaceHere(partLocation);
        if (!canPlace) return this;

        final Material woolMaterial = this.teamPlayer.getTeam().getLocation().getColor().getWool();

        if (part.get(0) == 0) partLocation.getBlock().setType(woolMaterial);
        else partLocation.getBlock().setType(Material.LADDER);

        session.getComponent(BedWarsBlockInteractionsComponent.class).addBlock(partLocation.getBlock().getLocation());
        return this;
    }

    public @NotNull PopUpTower constructAsync() {

        AtomicInteger step = new AtomicInteger(0);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(MineManiaBedWars.getPlugin(), task -> {

            if (step.get() >= schematic.size()) {
                task.cancel();
                return;
            }

            List<Integer> part = this.schematic.get(step.getAndIncrement());
            this.construct(part);

        }, 2, 2);

        return this;
    }
}
