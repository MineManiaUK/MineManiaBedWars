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

package com.github.minemaniauk.minemaniatntrun.inventory;

import com.github.cozyplugins.cozylibrary.inventory.CozyInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.minemaniatntrun.BedWarsUpgrade;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UpgradeInventory extends CozyInventory {

    protected final @NotNull TeamPlayer teamPlayer;

    /**
     * Used to create a new shop inventory.
     *
     * @param teamPlayer
     */
    public UpgradeInventory(@NotNull TeamPlayer teamPlayer) {
        super(54, "&f₴₴₴₴₴₴₴₴钻");

        this.teamPlayer = teamPlayer;

        // Start regenerating just in case someone gives them items while the inventory is still open.
        this.startRegeneratingInventory(20);
    }

    @Override
    protected void onGenerate(PlayerUser user) {
        this.resetInventory();

        if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.SHARPNESS) <= 0) {
            this.setUpgradeItem(BedWarsUpgrade.SHARPNESS, 19, 1);
        } else {
            this.setItem(new InventoryItem()
                    .addSlot(19)
                    .setMaterial(Material.BLACK_STAINED_GLASS_PANE)
                    .setName("&f&lSharpness")
                    .setLore("&eAlready brought")
            );
        }

        if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.PROTECTION) == 0) {
            this.setUpgradeItem(BedWarsUpgrade.PROTECTION, 20, 1);
        }
        else if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.PROTECTION) == 1) {
            this.setUpgradeItem(BedWarsUpgrade.PROTECTION, 20, 2);
        }
        else if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.PROTECTION) == 2) {
            this.setUpgradeItem(BedWarsUpgrade.PROTECTION, 20, 3);
        }
        else if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.PROTECTION) == 3) {
            this.setItem(new InventoryItem()
                    .addSlot(20)
                    .setMaterial(Material.BLACK_STAINED_GLASS_PANE)
                    .setName("&f&lProtection")
                    .setLore("&eAlready brought max level 3")
            );
        }

        if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.FORGE) == 0) {
            this.setUpgradeItem(BedWarsUpgrade.FORGE, 21, 1);
        }
        else if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.FORGE) == 1) {
            this.setUpgradeItem(BedWarsUpgrade.FORGE, 21, 2);
        }
        else if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.FORGE) == 2) {
            this.setUpgradeItem(BedWarsUpgrade.FORGE, 21, 3);
        }
        else if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.FORGE) == 3) {
            this.setItem(new InventoryItem()
                    .addSlot(21)
                    .setMaterial(Material.BLACK_STAINED_GLASS_PANE)
                    .setName("&f&lTeam Generator")
                    .setLore("&eAlready brought max level 3")
            );
        }

        if (this.teamPlayer.getTeam().getUpgradeLevel(BedWarsUpgrade.ALARM) <= 0) {
            this.setUpgradeItem(BedWarsUpgrade.ALARM, 23, 1);
        } else {
            this.setItem(new InventoryItem()
                    .addSlot(23)
                    .setMaterial(Material.LIME_STAINED_GLASS_PANE)
                    .setName("&f&lAlarm")
                    .setLore("&eAlready brought")
            );
        }
    }

    private void setUpgradeItem(@NotNull BedWarsUpgrade upgrade, int slot, int nextLevel) {

        CozyItem item = upgrade.createDisplayItem()
                .addLore("&7")
                .addLore("&aLevel &b" + nextLevel)
                .addLore("&aCost &b" + upgrade.getCost(nextLevel) + " diamonds");

        this.setItem(new InventoryItem(item.create())
                .addSlot(slot)
                .addAction((ClickAction) (user, type, inventory) -> {

                    final Inventory playerInventory = user.getPlayer().getInventory();

                    // Check if they have the correct
                    // amount of resources to buy.
                    if (!playerInventory.containsAtLeast(new CozyItem(Material.DIAMOND).create(), upgrade.getCost(nextLevel))) {
                        user.sendMessage("&7&l> &7You do not have enough &bdiamonds &7to buy this.");
                        return;
                    }

                    this.removeResources(Material.DIAMOND, upgrade.getCost(nextLevel), playerInventory);
                    upgrade.onPurchase(this.teamPlayer.getTeam());

                    for (TeamPlayer item2 : this.teamPlayer.getTeam().getOnlinePlayerList()) {
                        item2.getPlayer().ifPresent(player -> {
                            new PlayerUser(player).sendMessage("&b&l> &f" + user.getName() + " &bhas brought &f" + upgrade.name() + ".");
                        });
                    }
                })
        );
    }

    private void removeResources(@NotNull Material costMaterial, int costAmount, @NotNull Inventory inventory) {
        int amountTaken = 0;

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().equals(costMaterial)) {

                final int amountToTake = costAmount - amountTaken;

                // Check if the item stack will finish the amount to take.
                if (itemStack.getAmount() >= amountToTake) {
                    itemStack.setAmount(itemStack.getAmount() - amountToTake);
                    return;
                }

                // Otherwise take the whole stack.
                amountTaken += itemStack.getAmount();
                itemStack.setAmount(0);
            }
        }
    }
}
