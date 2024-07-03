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

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.minemaniauk.minemaniatntrun.BedWarsUpgrade;
import com.github.minemaniauk.minemaniatntrun.generator.Generator;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.component.BedWarsGeneratorComponent;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a bedwars team instance.
 */
public class Team {

    private final @NotNull BedWarsSession sessionPointer;

    private final @NotNull TeamLocation location;
    private final @NotNull List<TeamPlayer> playerList;
    private final @NotNull Map<BedWarsUpgrade, Integer> upgradeMap;

    public Team(@NotNull BedWarsSession sessionPointer, @NotNull TeamLocation location) {
        this.sessionPointer = sessionPointer;
        this.location = location;
        this.playerList = new ArrayList<>();
        this.upgradeMap = new HashMap<>();
    }

    public @NotNull BedWarsSession getSession() {
        return sessionPointer;
    }

    /**
     * Used to get the instance of the
     * team location info.
     *
     * @return The team location class.
     */
    public @NotNull TeamLocation getLocation() {
        return this.location;
    }

    public @NotNull Optional<Generator> getGenerator() {
        return this.sessionPointer.getComponent(BedWarsGeneratorComponent.class).getTeamGenerator(this.getLocation());
    }

    /**
     * Used to get the list of player
     * uuid's in this team.
     *
     * @return The list of player uuid's.
     */
    public @NotNull List<TeamPlayer> getPlayerList() {
        return this.playerList;
    }

    /**
     * Used to get the list of team members
     * that are online.
     *
     * @return The list of online players
     * on this team.
     */
    public @NotNull List<TeamPlayer> getOnlinePlayerList() {
        return this.playerList.stream()
                .filter(teamPlayer -> Bukkit.getOnlinePlayers().stream()
                        .map(Player::getUniqueId)
                        .toList()
                        .contains(teamPlayer.getPlayerUuid())
                )
                .collect(Collectors.toList());
    }

    /**
     * Used to get a specific player on this team.
     *
     * @param playerUuid The player's uuid to look for.
     * @return The optional player.
     */
    public @NotNull Optional<TeamPlayer> getPlayer(@NotNull UUID playerUuid) {
        for (TeamPlayer player : this.playerList) {
            if (player.getPlayerUuid().equals(playerUuid)) return Optional.of(player);
        }
        return Optional.empty();
    }

    /**
     * Used to add a player to this team.
     *
     * @param playerUuid The player's uuid.
     * @return This instance.
     */
    public @NotNull Team addPlayer(@NotNull UUID playerUuid) {
        this.playerList.add(new TeamPlayer(this, playerUuid));
        return this;
    }

    /**
     * Used to remove a player from this team.
     *
     * @param playerUuid The player's uuid.
     * @return This instance.
     */
    public @NotNull Team removePlayer(@NotNull UUID playerUuid) {
        this.playerList.removeIf(
                player -> player.getPlayerUuid().equals(playerUuid)
        );
        return this;
    }

    /**
     * Used to check if the team still has
     * a bed in their base.
     *
     * @return True if the team has a bed.
     */
    public boolean hasBed() {
        if (this.getPlayerList().isEmpty()) return false;
        return this.getLocation().getRegion().contains(
                this.getLocation().getColor().getBed()
        );
    }

    /**
     * Used to teleport all the players in this team
     * to the team spawn point.
     *
     * @return This instance.
     */
    public @NotNull Team teleportPlayers() {
        for (TeamPlayer player : this.getOnlinePlayerList()) {
            player.getPlayer().orElseThrow().teleport(this.getLocation().getSpawnPoint());
        }
        return this;
    }

    /**
     * Used to give each player in the team there
     * default items.
     *
     * @return This instance.
     */
    public @NotNull Team giveDefaultItems() {
        for (TeamPlayer player : this.getOnlinePlayerList()) {
            player.giveDefaultItems();
        }
        return this;
    }

    public @NotNull List<TeamPlayer> getAlivePlayers() {
        return this.playerList.stream().filter(player -> !player.isDead()).toList();
    }

    public boolean isOut() {
        if (this.hasBed()) return false;
        return !this.getPlayerList().stream()
                .map(TeamPlayer::isDead).toList()
                .contains(false);
    }

    public int getUpgradeLevel(BedWarsUpgrade upgrade) {
        for (Map.Entry<BedWarsUpgrade, Integer> entry : this.upgradeMap.entrySet()) {
            if (entry.getKey().name().equals(upgrade.name())) return entry.getValue();
        }
        return 0;
    }

    public void setUpgradeLevel(BedWarsUpgrade type, int level) {
        this.upgradeMap.put(type, level);
    }

    public void updateSwords() {
        if (!(this.getUpgradeLevel(BedWarsUpgrade.SHARPNESS) >= 1)) return;

        for (TeamPlayer player : this.getOnlinePlayerList()) {
            for (ItemStack item : player.getPlayer().orElseThrow().getInventory().getContents()) {
                if (item == null) continue;
                if (item.getType().name().contains("SWORD")) {
                    new CozyItem(item).addEnchantment(Enchantment.DAMAGE_ALL, 2);
                }
            }
        }
    }

    public void updateArmour() {
        int protectionLevel = this.getUpgradeLevel(BedWarsUpgrade.PROTECTION);

        if (protectionLevel == 0) return;

        for (TeamPlayer teamPlayer : this.getOnlinePlayerList()) {
            Player player = teamPlayer.getPlayer().orElseThrow();
            if (player.getInventory().getHelmet() != null) {
                new CozyItem(player.getInventory().getHelmet()).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
            }
            if (player.getInventory().getChestplate() != null) {
                new CozyItem(player.getInventory().getChestplate()).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
            }
            if (player.getInventory().getLeggings() != null) {
                new CozyItem(player.getInventory().getLeggings()).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
            }
            if (player.getInventory().getBoots() != null) {
                new CozyItem(player.getInventory().getBoots()).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
            }
        }
    }
}
