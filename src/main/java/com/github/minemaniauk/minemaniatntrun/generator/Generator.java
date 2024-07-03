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

package com.github.minemaniauk.minemaniatntrun.generator;

import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

/**
 * Represents an active generator.
 */
public class Generator extends TaskContainer {

    public static final @NotNull String GENERATOR_DROP_TASK_IDENTIFIER = "GENERATOR_DROP_TASK_IDENTIFIER";

    private final @NotNull GeneratorLocation location;

    private int step;
    private int level;
    private long lastDropTimeStamp;
    private @NotNull String hologramIdentifier;

    /**
     * Used to create a new active generator.
     *
     * @param location The generator location.
     */
    public Generator(@NotNull GeneratorLocation location) {
        this.location = location;
        this.level = location.getStartingLevel();
    }

    /**
     * This will start generating resources at the
     * generator location based on the generator type.
     *
     * @return This instance.
     */
    public @NotNull Generator start() {

        // Generate the first drops.
        this.generate();

        this.hologramIdentifier = UUID.randomUUID().toString();

        // Create the hologram.
        Hologram hologram = DHAPI.createHologram(this.hologramIdentifier, this.getHologramLocation());
        DHAPI.addHologramLine(hologram, "");
        DHAPI.addHologramLine(hologram, "");

        // Start the task.
        this.runTaskLoop(Generator.GENERATOR_DROP_TASK_IDENTIFIER, () -> {

            final long nextDropTimeStamp = this.lastDropTimeStamp + this.getType().getCooldown(this.level).toMillis();

            if (nextDropTimeStamp <= System.currentTimeMillis()) {
                this.generate();
            }

            DHAPI.setHologramLine(hologram, 0, this.getType().getColorCode() + "&l" + this.getType().getTitle());
            DHAPI.setHologramLine(hologram, 1,
                    "&7Spawning in " + this.getType().getColorCode()
                            + this.getTimeTillNextDrop().getSeconds() + "s"
                            + " [level&f" + this.getLevel() + "&7]"
            );

        }, 10);
        return this;
    }

    /**
     * Used to stop this generator.
     *
     * @return This instance.
     */
    public @NotNull Generator stop() {
        this.stopAllTasks();
        DHAPI.removeHologram(this.hologramIdentifier);
        return this;
    }

    /**
     * Used to generate drops at the generator
     * and reset cooldown.
     *
     * @return This instance.
     */
    public @NotNull Generator generate() {

        // Generate the first type at the location.
        this.getType().generate(this.getLocation(), this.level, this.step, () -> this.step = 0);
        this.step++;

        // Set the last drop time stamp.
        this.lastDropTimeStamp = System.currentTimeMillis();
        return this;
    }

    public @NotNull Location getLocation() {
        return this.location.getLocation();
    }

    public @NotNull Location getHologramLocation() {
        return this.location.getLocation().clone().add(new Vector(0, 2, 0));
    }

    public @NotNull GeneratorType getType() {
        return this.location.getType();
    }

    public int getLevel() {
        return this.level;
    }

    public long getNextDropTimeStamp() {
        return this.lastDropTimeStamp + this.getType().getCooldown(this.level).toMillis();
    }

    public @NotNull Duration getTimeTillNextDrop() {
        return Duration.ofMillis(this.getNextDropTimeStamp() - System.currentTimeMillis());
    }

    public @NotNull Generator setLevel(int level) {
        this.level = level;
        return this;
    }

    public @NotNull Generator incrementLevel(int amount) {
        this.level += amount;
        return this;
    }
}
