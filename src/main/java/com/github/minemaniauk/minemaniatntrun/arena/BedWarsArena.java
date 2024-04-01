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

package com.github.minemaniauk.minemaniatntrun.arena;

import com.github.cozyplugins.cozylibrary.indicator.LocationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.cozyplugins.cozylibrary.location.Region3D;
import com.github.minemaniauk.api.game.Arena;
import com.github.minemaniauk.api.game.GameType;
import com.github.minemaniauk.minemaniatntrun.MineManiaBedWars;
import com.github.minemaniauk.minemaniatntrun.team.TeamColor;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import com.github.smuddgge.squishyconfiguration.indicator.ConfigurationConvertable;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a bed wars arena.
 */
public class BedWarsArena extends Arena implements ConfigurationConvertable<BedWarsArena>, Savable, LocationConvertable {

    private final @NotNull List<@NotNull TeamLocation> teamLocationList;
    private @Nullable Region3D region;
    private @Nullable Location spawnPoint;
    private @Nullable Location schematicLocation;
    private @Nullable String schematic;

    /**
     * Used to create a new instance of a bed wars arena.
     *
     * @param identifier The arena's identifier.
     */
    public BedWarsArena(@NotNull UUID identifier) {
        super(identifier, MineManiaBedWars.getAPI().getServerName(), GameType.BED_WARS);

        this.teamLocationList = new ArrayList<>();
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    /**
     * Used to get the instance of the list of team locations.
     *
     * @return The list of team location.
     */
    public @NotNull List<TeamLocation> getTeamLocationList() {
        return this.teamLocationList;
    }

    /**
     * Used to get a team location given the color of the team.
     *
     * @param color The color of the team to look for.
     * @return The optional team location.
     */
    public @NotNull Optional<TeamLocation> getTeamLocation(@NotNull TeamColor color) {
        for (TeamLocation location : this.teamLocationList) {
            if (location.getColor().equals(color)) return Optional.of(location);
        }

        return Optional.empty();
    }

    /**
     * Used to get a team location that a location is within.
     *
     * @param location The location within a team location.
     * @return The team location the location is within.
     * If the location is not in a team location, it will return empty.
     */
    public @NotNull Optional<TeamLocation> getTeamLocation(@NotNull Location location) {
        for (TeamLocation teamLocation : this.teamLocationList) {
            if (teamLocation.getRegion().contains(location)) return Optional.of(teamLocation);
        }

        return Optional.empty();
    }

    public @NotNull Optional<Region3D> getRegion() {
        return Optional.ofNullable(this.region);
    }

    public @NotNull Optional<Location> getSpawnPoint() {
        return Optional.ofNullable(this.spawnPoint);
    }

    public @NotNull Optional<Location> getSchematicLocation() {
        return Optional.ofNullable(this.schematicLocation);
    }

    public @NotNull Optional<String> getSchematic() {
        return Optional.ofNullable(this.schematic);
    }

    public @NotNull BedWarsArena addTeamLocation(@NotNull TeamLocation location) {
        this.teamLocationList.add(location);
        return this;
    }

    public @NotNull BedWarsArena removeTeamLocation(@NotNull TeamColor color) {
        this.teamLocationList.removeIf(teamLocation -> teamLocation.getColor().equals(color));
        return this;
    }

    public @NotNull BedWarsArena setRegion(@NotNull Region3D region) {
        this.region = region;
        return this;
    }

    public @NotNull BedWarsArena setSpawnPoint(@NotNull Location location) {
        this.spawnPoint = location;
        return this;
    }

    public @NotNull BedWarsArena setSchematicLocation(@NotNull Location location) {
        this.schematicLocation = location;
        return this;
    }

    public @NotNull BedWarsArena setSchematic(@NotNull String schematic) {
        this.schematic = schematic;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        for (TeamLocation location : this.teamLocationList) {
            section.set(
                    "team_locations." + location.getColor().getName(),
                    location.convert().getMap()
            );
        }

        section.set("region", this.region == null ? null : this.region.convert().getMap());
        section.set("spawn_point", this.spawnPoint == null ? null : this.convertLocation(this.spawnPoint));
        section.set("schematic_location", this.schematicLocation == null ? null : this.convertLocation(this.schematicLocation));
        section.set("schematic", this.schematic);

        return section;
    }

    @Override
    public @NotNull BedWarsArena convert(@NotNull ConfigurationSection section) {

        for (String key : section.getSection("team_locations").getKeys()) {
            this.teamLocationList.add(new TeamLocation(
                    TeamColor.valueOf(key),
                    section.getSection("team_locations." + key)
            ));
        }

        if (section.getKeys().contains("region")) this.region = new Region3D(section.getSection("region"));
        if (section.getKeys().contains("spawn_point"))
            this.spawnPoint = this.convertLocation(section.getSection("spawn_point"));
        if (section.getKeys().contains("schematic_location"))
            this.schematicLocation = this.convertLocation(section.getSection("schematic_location"));
        if (section.getKeys().contains("schematic")) this.schematic = section.getString("schematic");

        return this;
    }

    @Override
    public void save() {

        // Save the api.
        super.save();

        // Save to local storage.
        MineManiaBedWars.getInstance().getArenaConfiguration()
                .insertType(this.getIdentifier().toString(), this);
    }
}
