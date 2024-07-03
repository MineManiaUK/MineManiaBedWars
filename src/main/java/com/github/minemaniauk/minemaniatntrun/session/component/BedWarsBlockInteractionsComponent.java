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
import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BedWarsBlockInteractionsComponent extends TaskContainer implements SessionComponent<BedWarsArena> {

    private final @NotNull BedWarsSession session;
    private final @NotNull List<Location> blockLocationList;

    /**
     * Used to create a new session component.
     *
     * @param session The instance of the session.
     */
    public BedWarsBlockInteractionsComponent(@NotNull BedWarsSession session) {
        this.session = session;
        this.blockLocationList = new ArrayList<>();
    }

    @Override
    public @NotNull BedWarsSession getSession() {
        return session;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public void onBlockBreak(@NotNull BlockBreakEvent event, @Nullable TeamLocation teamLocation) {

        // Check if it happened at a team location.
        if (teamLocation == null) {

            // Check if the block is part of the build.
            if (!this.blockLocationList.contains(event.getBlock().getLocation())) {
                event.setCancelled(true);
                return;
            }
            return;
        }

        final Team team = this.getSession().getTeam(teamLocation.getColor()).orElseThrow();

        // Check if the block broken was a bed.
        if (event.getBlock().getType().equals(teamLocation.getColor().getBed())) {

            // Check if the player is on the same team as the bed.
            if (team.getPlayer(event.getPlayer().getUniqueId()).isPresent()) {
                new PlayerUser(event.getPlayer()).sendMessage("&7&l> &7You cannot destroy your own bed. &fOnce your bed is &cdestroyed &fyou can no longer &arespawn&f.");
                event.setCancelled(true);
                return;
            }

            // Otherwise the bed has been destroyed.

            // Spawn particles.
            final World world = event.getBlock().getWorld();
            world.spawnParticle(Particle.DAMAGE_INDICATOR, event.getBlock().getLocation().clone().add(new Vector(0, 2, 0)), 10);

            // Inform the players.
            for (TeamPlayer player : team.getOnlinePlayerList()) {
                final PlayerUser user = new PlayerUser(player.getPlayer().orElseThrow());
                user.sendMessage(List.of(
                        "&8&l---------------------",
                        "&7",
                        "&f&lYour Bed Has Been Destroyed!",
                        "&7",
                        "&cSomeone has destroyed your bed. You can no longer",
                        "&crespawn when you are killed from now on.",
                        "&7",
                        "&8&l---------------------"
                ));
            }

            event.setDropItems(false);
            return;
        }

        // Check if the block is part of the build.
        if (!this.blockLocationList.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void onBlockPlace(@NotNull BlockPlaceEvent event, @Nullable TeamLocation teamLocation) {

        // Check if someone is trying to block a team's spawn.
        if (teamLocation != null) {

            final Location center = teamLocation.getRegion().getCenter().clone();
            final Region3D tooCloseRegion = new Region3D(center, center).expand(3);

            boolean blockTooClose = tooCloseRegion.contains(event.getBlock().getLocation());

            if (blockTooClose) {
                new PlayerUser(event.getPlayer()).sendMessage("&7&l> &7You cannot place a block here. It is too close to a {team}'s spawn point."
                        .replace("{team}", teamLocation.getColor().getName())
                );
                event.setCancelled(true);
            }
        }

        // Check if the block is being placed out of bounds.
        final Region3D arenaRegion = this.getSession().getArena().getRegion().orElseThrow();

        if (event.getBlock().getLocation().getBlockY() <= arenaRegion.getMinPoint().getBlockY()) {
            new PlayerUser(event.getPlayer()).sendMessage("&7&l> &7You cannot place a block here. It is outside the arena.");
            event.setCancelled(true);
        }

        // Log the block's location.
        this.blockLocationList.add(event.getBlock().getLocation());

        if (event.getBlock().getType().equals(Material.TNT)) {
            event.getBlockPlaced().setType(Material.AIR);
            event.getPlayer().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
        }
    }

    public boolean checkIfPlayerCanBreakHere(Location location) {
        return this.blockLocationList.contains(location);
    }

    public void addBlock(Location location) {
        this.blockLocationList.add(location);
    }

    public boolean checkIfPlayerCanPlaceHere(@NotNull Location location) {

        // Check if it was in a team location.
        final TeamLocation teamLocation = this.getSession().getArena().getTeamLocation(location).orElse(null);

        // Check if someone is trying to block a team's spawn.
        if (teamLocation != null) {

            final Location center = teamLocation.getRegion().getCenter().clone();
            final Region3D tooCloseRegion = new Region3D(center, center).expand(3);

            boolean blockTooClose = tooCloseRegion.contains(location);

            if (blockTooClose) {
                return false;
            }
        }

        // Check if the block is being placed out of bounds.
        final Region3D arenaRegion = this.getSession().getArena().getRegion().orElseThrow();

        if (location.getBlockY() <= arenaRegion.getMinPoint().getBlockY()) {
            return false;
        }

        return true;
    }
}
