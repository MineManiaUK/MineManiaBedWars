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

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;

/**
 * Represents a generator type.
 * This will specify the kind of drops
 * the generator will drop.
 */
public enum GeneratorType {
    TEAM("&f", "Team Generator") {
        @Override
        public @NotNull GeneratorType generate(@NotNull Location location, int level) {
            if (level == 1) {
                if (this.getStep() == 8) {
                    this.generate(location, Material.GOLD_INGOT);
                    this.setStep(-1);
                } else {
                    this.generate(location, Material.IRON_INGOT);
                }
            }
            if (level == 2) {
                if (this.getStep() == 4) {
                    this.generate(location, Material.GOLD_INGOT);
                    this.generate(location, Material.IRON_INGOT);
                    this.setStep(-1);
                } else {
                    this.generate(location, Material.IRON_INGOT);
                }
            }
            if (level == 3) {
                if (this.getStep() == 2) {
                    this.generate(location, Material.GOLD_INGOT);
                    this.generate(location, Material.IRON_INGOT);
                    this.setStep(-1);
                } else {
                    this.generate(location, Material.IRON_INGOT);
                }
            }
            if (level == 4) {
                if (this.getStep() == 20) {
                    this.generate(location, Material.EMERALD);
                    this.setStep(-1);
                } else if (this.getStep() == 2) {
                    this.generate(location, Material.GOLD_INGOT);
                    this.generate(location, Material.IRON_INGOT);
                } else {
                    this.generate(location, Material.IRON_INGOT);
                }
            }
            this.incrementStep();
            return this;
        }

        @Override
        public @NotNull Duration getCooldown(int level) {
            return Duration.ofSeconds(1);
        }
    },
    DIAMOND("&b", "Diamond Generator") {
        @Override
        public @NotNull GeneratorType generate(@NotNull Location location, int level) {
            this.generate(location, Material.DIAMOND);
            return this;
        }

        @Override
        public @NotNull Duration getCooldown(int level) {
            return switch (level) {
                case 1 -> Duration.ofSeconds(40);
                case 2 -> Duration.ofSeconds(20);
                default -> Duration.ofSeconds(10);
            };
        }
    },
    EMERALD("&a", "Emerald Generator") {
        @Override
        public @NotNull GeneratorType generate(@NotNull Location location, int level) {
            this.generate(location, Material.EMERALD);
            return this;
        }

        @Override
        public @NotNull Duration getCooldown(int level) {
            return switch (level) {
                case 1 -> Duration.ofSeconds(80);
                case 2 -> Duration.ofSeconds(40);
                default -> Duration.ofSeconds(20);
            };
        }
    };

    /**
     * The step variable is used to determine the
     * step in the generator cycle of drops.
     * <p>
     * For example, the team generator may drop 1
     * iron on step 0, but 1 gold on step 2.
     * <p>
     * Manually changing the step can make the
     * rate of drops more random.
     */
    private int step;

    private @NotNull String colorCode;
    private @NotNull String title;

    /**
     * Used to create a new generator type.
     */
    GeneratorType(@NotNull String colorCode, @NotNull String title) {
        this.step = 0;
        this.colorCode = colorCode;
        this.title = title;
    }

    public int getStep() {
        return this.step;
    }

    public @NotNull String getColorCode() {
        return this.colorCode;
    }

    public @NotNull String getTitle() {
        return this.title;
    }

    public @NotNull GeneratorType setStep(int step) {
        this.step = step;
        return this;
    }

    public @NotNull GeneratorType incrementStep() {
        this.step++;
        return this;
    }

    /**
     * Used to generate the specific drops at a specific location.
     *
     * @param location The location to spawn the drops at.
     * @return This instance.
     */
    public abstract @NotNull GeneratorType generate(@NotNull Location location, int level);

    /**
     * Used to get the amount of time to wait
     * before generating again.
     *
     * @param level The level of the generator.
     * @return The duration to wait.
     */
    public abstract @NotNull Duration getCooldown(int level);

    /**
     * Used to generate specific items.
     *
     * @param location The location to generate the items at.
     * @param items The items to generate.
     * @return This instance.
     */
    public @NotNull GeneratorType generate(@NotNull Location location, @NotNull Material... items) {
        if (location.getWorld() == null) {
            throw new RuntimeException("While trying to generate " + Arrays.stream(items).toList() + " the location's world returned null.");
        }

        for (Material material : items) {
            location.getWorld().dropItem(
                    location,
                    new CozyItem(material).create()
            );
        }
        return this;
    }
}
