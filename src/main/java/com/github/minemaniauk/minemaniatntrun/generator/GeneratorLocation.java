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

import com.github.cozyplugins.cozylibrary.indicator.LocationConvertable;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.indicator.ConfigurationConvertible;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Represents a generator location.
 * This class is used in the bed wars arena and
 * saved in the arena configuration with the
 * corresponding area.
 */
public class GeneratorLocation implements ConfigurationConvertible<GeneratorLocation>, LocationConvertable {

    private final @NotNull UUID identifier;
    private final @NotNull Location location;
    private final @NotNull GeneratorType type;
    private final int startingLevel;

    /**
     * Used to create a new generator location.
     *
     * @param identifier The generator location identifier.
     * @param location The location of the generator.
     * @param type The type of generator it will be.
     * @param startingLevel The level the generator will start at.
     */
    public GeneratorLocation(@NotNull UUID identifier,
                             @NotNull Location location,
                             @NotNull GeneratorType type,
                             int startingLevel) {

        this.identifier = identifier;
        this.location = location;
        this.type = type;
        this.startingLevel = startingLevel;
    }

    /**
     * Used to create an instance of a generator location
     * using its identifier and instance of the configuration section.
     *
     * @param identifier The generator's identifier.
     * @param section The configuration section containing
     *                the generator infomation.
     */
    public GeneratorLocation(@NotNull UUID identifier,
                             @NotNull ConfigurationSection section) {

        this.identifier = identifier;
        this.location = this.convertLocation(section.getSection("location"));
        this.type = GeneratorType.valueOf(section.getString("type").toUpperCase());
        this.startingLevel = section.getInteger("starting_level");

        this.convert(section);
    }

    public @NotNull UUID getIdentifier() {
        return this.identifier;
    }

    public @NotNull Location getLocation() {
        return this.location;
    }

    public @NotNull GeneratorType getType() {
        return this.type;
    }

    public int getStartingLevel() {
        return this.startingLevel;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection();

        section.set("location", this.convertLocation(this.location));
        section.set("type", this.type.name().toUpperCase());
        section.set("starting_level", this.startingLevel);

        return section;
    }

    @Override
    public @NotNull GeneratorLocation convert(@NotNull ConfigurationSection section) {
        return this;
    }
}
