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

import com.github.cozyplugins.cozylibrary.MessageManager;
import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.minemaniauk.api.game.session.SessionComponent;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BedWarsNPCComponent extends TaskContainer implements SessionComponent<BedWarsArena> {

    private final @NotNull BedWarsSession session;
    private final @NotNull List<Entity> npcList;

    /**
     * Represents the types of npc that can be spawned.
     */
    public enum NPCType {
        SHOP("&6&lShop"),
        UPGRADES("&6&lUpgrades");

        private final @NotNull String npcName;

        NPCType(@NotNull String npcName) {
            this.npcName = npcName;
        }

        public @NotNull String getNpcName() {
            return this.npcName;
        }
    }

    /**
     * Used to create a new session component.
     *
     * @param session The instance of the session.
     */
    public BedWarsNPCComponent(@NotNull BedWarsSession session) {
        this.session = session;
        this.npcList = new ArrayList<>();
    }

    @Override
    public @NotNull BedWarsSession getSession() {
        return session;
    }

    @Override
    public void start() {
        this.removeNPCS();
        this.spawnNPCS();
    }

    @Override
    public void stop() {
        this.removeNPCS();
    }

    /**
     * Used to spawn the shops at the team locations.
     *
     * @return This instance.
     */
    public @NotNull BedWarsNPCComponent spawnNPCS() {
        for (Team team : this.getSession().getTeamList()) {

            final Location shopLocation = team.getLocation().getShopLocation();
            final Location upgradesLocation = team.getLocation().getUpgradesLocation();

            if (shopLocation == null) {
                throw new RuntimeException("Location for shop is null.");
            }

            if (upgradesLocation == null) {
                throw new RuntimeException("Location for upgrades is null.");
            }

            this.spawn(shopLocation, NPCType.SHOP);
            this.spawn(upgradesLocation, NPCType.UPGRADES);
        }
        return this;
    }

    /**
     * Used to remove all the NPCs in this arena.
     *
     * @return This instance.
     */
    public @NotNull BedWarsNPCComponent removeNPCS() {
        final World world = this.getSession().getArena().getSchematicLocation()
                .orElseThrow().getWorld();

        if (world == null) {
            throw new RuntimeException("World is null when trying to remove NPCs.");
        }

        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Villager villager)) continue;
            if (!(villager.getName().contains("Shop")
                    || villager.getName().contains("Upgrades"))) continue;

            villager.remove();
        }

        return this;
    }

    /**
     * Used to spawn a npc type at a location.
     *
     * @param location The location to spawn the npc.
     * @param npc      The npc to spawn.
     * @return This instance.
     */
    public @NotNull BedWarsNPCComponent spawn(@NotNull Location location, @NotNull BedWarsNPCComponent.NPCType npc) {

        // Get the instance of the world.
        final World world = location.getWorld();

        // Check if the world is null.
        if (world == null) {
            throw new RuntimeException("World is null for location " + location);
        }

        Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomNameVisible(true);
        villager.setCustomName(MessageManager.parse(npc.getNpcName()));
        villager.setGravity(false);
        villager.setInvulnerable(true);
        villager.setPersistent(true);
        villager.setAI(false);

        return this;
    }
}
