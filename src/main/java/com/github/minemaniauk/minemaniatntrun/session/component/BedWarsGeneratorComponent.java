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
import com.github.minemaniauk.api.game.session.Session;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.generator.Generator;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the generator component.
 * Handles the generators in the session.
 */
public class BedWarsGeneratorComponent implements SessionComponent<BedWarsArena> {

    private final @NotNull BedWarsSession session;
    private @NotNull List<@NotNull Generator> generatorList;

    /**
     * Used to create a new component.
     *
     * @param session The instance of the session.
     */
    public BedWarsGeneratorComponent(@NotNull BedWarsSession session) {
        this.session = session;
        this.generatorList = new ArrayList<>();
    }

    @Override
    public @NotNull Session<BedWarsArena> getSession() {
        return this.session;
    }

    @Override
    public void start() {

        this.removeDrops();

        // Setup generators and start the generating.
        this.getSession().getArena().getGeneratorLocationList().forEach(
                location -> this.generatorList.add(new Generator(location).start())
        );
    }

    @Override
    public void stop() {

        this.removeDrops();

        // Remove generators.
        this.generatorList.removeIf(generator -> {
            generator.stop();
            return true;
        });
    }

    public @NotNull BedWarsGeneratorComponent removeDrops() {
        final World world = this.getSession().getArena().getSchematicLocation().orElseThrow().getWorld();
        final Region3D region = this.getSession().getArena().getRegion().orElseThrow();

        if (world == null) {
            throw new RuntimeException("World is null while trying to remove resources.");
        }

        for (Entity entity : world.getEntities()) {
            if (!region.contains(entity.getLocation())) continue;
            if (!(entity instanceof Item)) continue;
            entity.remove();
        }

        return this;
    }

    public @NotNull Optional<Generator> getTeamGenerator(@NotNull TeamLocation location) {
       for (Generator generator : this.generatorList) {
           if (location.getRegion().contains(generator.getLocation())) return Optional.of(generator);
       }
       return Optional.empty();
    }
}
