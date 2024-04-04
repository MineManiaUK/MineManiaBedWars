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
import org.bukkit.Location;
import org.bukkit.entity.Panda;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an active generator.
 */
public class Generator extends TaskContainer {

    public static final @NotNull String GENERATOR_DROP_TASK_IDENTIFIER = "GENERATOR_DROP_TASK_IDENTIFIER";

    private final @NotNull GeneratorLocation location;

    private int level;
    private long lastDropTimeStamp;

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

        // Start the task.
        this.runTaskLoop(Generator.GENERATOR_DROP_TASK_IDENTIFIER, () -> {

            final long nextDropTimeStamp = this.lastDropTimeStamp + this.getType().getCooldown(this.level).toMillis();

            if (nextDropTimeStamp <= System.currentTimeMillis()) {
                this.generate();
            }

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
        this.getType().generate(this.getLocation(), this.level);

        // Set the last drop time stamp.
        this.lastDropTimeStamp = System.currentTimeMillis();
        return this;
    }

    public @NotNull Location getLocation() {
        return this.location.getLocation();
    }

    public @NotNull GeneratorType getType() {
        return this.location.getType();
    }

    public int getLevel() {
        return this.level;
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
