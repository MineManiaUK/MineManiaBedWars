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

package com.github.minemaniauk.minemaniatntrun.team.player;

import com.github.cozyplugins.cozylibrary.MessageManager;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.minemaniatntrun.BedWarsItem;
import com.github.minemaniauk.minemaniatntrun.BedWarsUpgrade;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsStatus;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a team player.
 * A player in a bed wars team.
 */
public class TeamPlayer extends TaskContainer {

    public static final @NotNull String RESPAWN_TASK_IDENTIFIER = "RESPAWN_TASK_IDENTIFIER";
    public static final @NotNull Duration RESPAWN_TIME_LENGTH = Duration.ofSeconds(5);

    private final @NotNull Team teamPointer;
    private final @NotNull UUID playerUuid;
    private @NotNull ArmorType armourType;
    private @Nullable BedWarsItem pickaxe;
    private @Nullable BedWarsItem axe;
    private @Nullable BedWarsItem shears;

    private boolean isDead;

    /**
     * Used to create a new team player.
     *
     * @param team       The instance of the team the player is in.
     * @param playerUuid The player's uuid.
     */
    public TeamPlayer(@NotNull Team team, @NotNull UUID playerUuid) {
        this.teamPointer = team;
        this.playerUuid = playerUuid;
        this.armourType = ArmorType.NONE;

        this.isDead = false;
    }

    /**
     * Used to get the instance of the team
     * the player is in.
     *
     * @return The instance of the team.
     */
    public @NotNull Team getTeam() {
        return this.teamPointer;
    }

    /**
     * Used to get this team member as a player user.
     *
     * @return The instance of the player user.
     */
    public @NotNull UUID getPlayerUuid() {
        return this.playerUuid;
    }

    /**
     * Used to get the player if they are online.
     * Otherwise, it will return empty.
     *
     * @return The instance of the player.
     */
    public @NotNull Optional<Player> getPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(this.playerUuid)) return Optional.of(player);
        }
        return Optional.empty();
    }

    /**
     * Used to get the player's name.
     * If they have never joined before it will return "null".
     *
     * @return The player's name.
     */
    public @NotNull String getName() {
        final String name = Bukkit.getOfflinePlayer(this.playerUuid).getName();
        return name == null ? "null" : name;
    }

    /**
     * Used to get this team player's armor type.
     *
     * @return The team player's armor type.
     */
    public @NotNull ArmorType getArmourType() {
        return this.armourType;
    }

    public @NotNull TeamPlayer setArmourType(@NotNull ArmorType armourType) {
        this.armourType = armourType;
        return this;
    }

    public boolean hasPickaxe() {
        return this.pickaxe != null;
    }

    public boolean hasAxe() {
        return this.axe != null;
    }

    public boolean hasShears() {
        return this.shears != null;
    }

    public @NotNull BedWarsItem getPickaxe() {
        return this.pickaxe;
    }

    public @NotNull BedWarsItem getAxe() {
        return this.axe;
    }

    public @NotNull BedWarsItem getShears() {
        return this.shears;
    }

    public @NotNull TeamPlayer setPickaxe(@NotNull BedWarsItem pickaxe) {
        this.updateEfficiencyOnTools();
        this.pickaxe = pickaxe;
        return this;
    }

    public @NotNull TeamPlayer setAxe(@NotNull BedWarsItem axe) {
        this.updateEfficiencyOnTools();
        this.axe = axe;
        return this;
    }

    public @NotNull TeamPlayer setShears(@NotNull BedWarsItem shears) {
        this.updateEfficiencyOnTools();
        this.shears = shears;
        return this;
    }

    /**
     * Used to check if the player is dead within the game.
     * This is different from spigot.
     *
     * @return True if the player is dead.
     */
    public boolean isDead() {
        if (this.getPlayer().isEmpty()) return true;
        return this.isDead;
    }

    /**
     * Used to kill the player.
     * This will put them in spectator and if the
     * bed is still active, start the respawn timer.
     *
     * @return This instance.
     */
    public @NotNull TeamPlayer kill() {
        this.isDead = true;

        this.getTeam().getSession().onPlayerDeath(this);

        this.getPlayer().ifPresent(player -> {
            player.setGameMode(GameMode.SPECTATOR);
        });

        if (this.getTeam().hasBed()) {
            this.startRespawnTask();
        }

        return this;
    }

    /**
     * Used to start the respawn task.
     * This will start the count-down till when they
     * can respawn and then respawn them.
     *
     * @return This instance.
     */
    public @NotNull TeamPlayer startRespawnTask() {

        long startRespawnTimeMillis = System.currentTimeMillis();

        this.getPlayer().ifPresent(player -> {
            player.sendTitle(
                    "",
                    MessageManager.parse("&eRespawning in &f" + TeamPlayer.RESPAWN_TIME_LENGTH.getSeconds() + "s"),
                    20,
                    80,
                    20
            );
            new PlayerUser(player).sendMessage("&e&l> &eRespawning in &f" + TeamPlayer.RESPAWN_TIME_LENGTH.getSeconds() + "s");
        });

        this.runTaskLoop(TeamPlayer.RESPAWN_TASK_IDENTIFIER, () -> {

            // Check if the player is offline.
            if (this.getPlayer().isEmpty()) {

                // Check if the game has stopped.
                if (this.getTeam().getSession().getStatus().equals(BedWarsStatus.ENDING)) {
                    this.stopTask(TeamPlayer.RESPAWN_TASK_IDENTIFIER);
                }

                return;
            }

            final long endTimeMills = startRespawnTimeMillis + TeamPlayer.RESPAWN_TIME_LENGTH.toMillis();

            if (endTimeMills <= System.currentTimeMillis()) {

                // Check if the team doesn't have a bed.
                if (!this.getTeam().hasBed()) {
                    this.stopTask(TeamPlayer.RESPAWN_TASK_IDENTIFIER);
                    return;
                }

                this.respawn();
                this.stopTask(TeamPlayer.RESPAWN_TASK_IDENTIFIER);
            }

        }, 20);
        return this;
    }

    /**
     * Used to respawn the team player instantly.
     * If they were not dead originally it will
     * pretend they died.
     *
     * @return This instance.
     */
    public @NotNull TeamPlayer respawn() {
        this.isDead = false;

        this.getPlayer().ifPresent(player -> {
            player.getInventory().clear();
            player.teleport(this.getTeam().getLocation().getSpawnPoint());
            player.setGameMode(GameMode.SURVIVAL);
        });

        this.giveDefaultItems();
        return this;
    }

    /**
     * Used to give the player there default items.
     *
     * @return This instance.
     */
    public @NotNull TeamPlayer giveDefaultItems() {

        // Give the default items if they are online.
        this.getPlayer().ifPresent(player -> {

            // Get the instance of the inventory.
            final PlayerInventory inventory = player.getInventory();

            // Add the default wooden sword.
            BedWarsItem item = BedWarsItem.WOODEN_SWORD;
            ItemStack itemCreated = item.create();
            if (this.getTeam().getUpgradeLevel(BedWarsUpgrade.SHARPNESS) >= 1) {
                itemCreated.addEnchantment(Enchantment.DAMAGE_ALL, 2);
            }
            inventory.addItem(itemCreated);

            // Add the obtainable items that are saved.
            if (this.pickaxe != null) inventory.addItem(this.pickaxe.create());
            if (this.axe != null) inventory.addItem(this.axe.create());
            if (this.shears != null) inventory.addItem(this.shears.create());

            // Efficiency upgrade.
            this.updateEfficiencyOnTools();

            // Set armour.
            this.armourType.applyArmor(this);
            this.getTeam().updateArmour();
        });

        return this;
    }

    public @NotNull TeamPlayer updateEfficiencyOnTools() {
        if (this.getTeam().getUpgradeLevel(BedWarsUpgrade.EFFICIENCY) == 0) return this;

        this.getPlayer().ifPresent(player -> {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().equals(Material.AIR)) continue;

                if (item.getType().equals(Material.WOODEN_AXE)
                        || item.getType().equals(Material.STONE_AXE)
                        || item.getType().equals(Material.DIAMOND_AXE)

                        || item.getType().equals(Material.WOODEN_PICKAXE)
                        || item.getType().equals(Material.STONE_PICKAXE)
                        || item.getType().equals(Material.DIAMOND_PICKAXE)

                        || item.getType().equals(Material.SHEARS)) {

                    CozyItem cozyItem = new CozyItem(item);
                    cozyItem.addEnchantment(Enchantment.DIG_SPEED, 3);
                }
            }
        });

        return this;
    }
}
