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

package com.github.minemaniauk.minemaniatntrun.team;

import com.github.cozyplugins.cozylibrary.indicator.LocationConvertable;
import com.github.cozyplugins.cozylibrary.location.Region;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.indicator.ConfigurationConvertible;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

/**
 * Contains the static infomation about a
 * team platform in a bedwars arena.
 */
public class TeamLocation implements ConfigurationConvertible<TeamLocation>, LocationConvertable {

    private final @NotNull TeamColor color;
    private @NotNull Location spawnPoint;
    private @NotNull Integer radiusFromSpawnPoint;
    private @Nullable Region generatorRegion;
    private @Nullable Location shopLocation;
    private @Nullable Location upgradesLocation;

    /**
     * Used to create a new team location.
     * Contains all static infomation that will never change
     * between games played.
     *
     * @param color                The color of the team.
     * @param spawnPoint           The location of the spawn point.
     * @param radiusFromSpawnPoint The radius from the spawn point
     *                             which will become the team region.
     */
    public TeamLocation(@NotNull TeamColor color,
                        @NotNull Location spawnPoint,
                        int radiusFromSpawnPoint) {

        this.color = color;
        this.spawnPoint = spawnPoint;
        this.radiusFromSpawnPoint = radiusFromSpawnPoint;
    }

    /**
     * Used to create a new team location from a
     * configuration section.
     *
     * @param color   The color of the team.
     * @param section The instance of the configuration section.
     */
    public TeamLocation(@NotNull TeamColor color,
                        @NotNull ConfigurationSection section) {

        this.color = color;
        this.spawnPoint = this.convertLocation(section.getSection("spawn_point"));
        this.radiusFromSpawnPoint = section.getInteger("radius");

        this.convert(section);
    }

    public @NotNull TeamColor getColor() {
        return this.color;
    }

    public @NotNull Location getSpawnPoint() {
        return this.spawnPoint;
    }

    /**
     * Used to get the team's region.
     * This is the area that will set off
     * traps and also protect chests.
     * <p>
     * The region is decided by the spawn point
     * expanded by the radius.
     *
     * @return A new instance of the team's region.
     */
    public @NotNull Region getRegion() {
        return new Region(this.spawnPoint, this.spawnPoint)
                .expand(this.radiusFromSpawnPoint);
    }

    /**
     * Used to get the region where the generator will
     * spawn the team's resources.
     *
     * @return The instance of the generator region.
     */
    public @Nullable Region getGeneratorRegion() {
        return this.generatorRegion;
    }

    /**
     * Used to get the location of where the
     * shop npc should be located.
     *
     * @return The location of the shop.
     */
    public @Nullable Location getShopLocation() {
        return this.shopLocation;
    }

    /**
     * Used to get the location of where the
     * upgrades npc should be located.
     *
     * @return The location of the upgrades.
     */
    public @Nullable Location getUpgradesLocation() {
        return this.upgradesLocation;
    }

    public @NotNull TeamLocation setSpawnPoint(@NotNull Location spawnPoint) {
        this.spawnPoint = spawnPoint;
        return this;
    }

    /**
     * Used to set the radius of the team's region
     * from the spawn point location.
     *
     * @param radius The radius of the team's region.
     * @return This instance.
     */
    public @NotNull TeamLocation setRadiusFromSpawnPoint(@NotNull Integer radius) {
        this.radiusFromSpawnPoint = radius;
        return this;
    }

    public @NotNull TeamLocation setGeneratorRegion(@NotNull Region region) {
        this.generatorRegion = region;
        return this;
    }

    public @NotNull TeamLocation setShopLocation(@NotNull Location location) {
        this.shopLocation = location;
        return this;
    }

    public @NotNull TeamLocation setUpgradesLocation(@NotNull Location location) {
        this.upgradesLocation = location;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection();

        section.set("spawn_point", this.convertLocation(this.spawnPoint));
        section.set("radius", this.radiusFromSpawnPoint);

        section.set("generator_region", this.generatorRegion == null ? null
                : this.generatorRegion.convert().getMap());

        section.set("shop_location", this.shopLocation == null ? null
                : this.convertLocation(this.shopLocation));

        section.set("upgrades_location", this.upgradesLocation == null ? null
                : this.convertLocation(this.upgradesLocation));

        return section;
    }

    @Override
    public @NotNull TeamLocation convert(@NotNull ConfigurationSection section) {

        this.spawnPoint = this.convertLocation(section.getSection("spawn_point"));
        this.radiusFromSpawnPoint = section.getInteger("radius");

        if (section.getKeys().contains("generator_region")) {
            this.generatorRegion = new Region(section.getSection("generator_region"));
        }

        if (section.getKeys().contains("shop_location")) {
            this.shopLocation = this.convertLocation(section.getSection("shop_location"));
        }

        if (section.getKeys().contains("upgrades_location")) {
            this.upgradesLocation = this.convertLocation(section.getSection("upgrades_location"));
        }

        return this;
    }

    @Override
    public String toString() {
        return "{color: " + this.getColor().getName() + ", " + this.convert().getMap() + "}";
    }
}
