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
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import net.royawesome.jlibnoise.module.combiner.Power;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the bed wars shop inventory.
 */
public class ShopInventory extends CozyInventory {

    private final @NotNull TeamPlayer teamPlayer;

    /**
     * Used to create a new shop inventory.
     */
    public ShopInventory(@NotNull TeamPlayer teamPlayer) {
        super(54, "&f₴₴₴₴₴₴₴₴钫");

        this.teamPlayer = teamPlayer;

        // Start regenerating just in case someone gives them items while the inventory is still open.
        this.startRegeneratingInventory(20);
    }

    @Override
    protected void onGenerate(PlayerUser user) {

        // Diamond Sword.
        this.setSimpleBuyItem(
                "&a&lDiamond Sword",
                new CozyItem(Material.DIAMOND_SWORD),
                Material.EMERALD,
                4,
                11
        );

        // Iron Sword.
        this.setSimpleBuyItem(
                "&6&lIron Sword",
                new CozyItem(Material.IRON_SWORD),
                Material.GOLD_INGOT,
                7,
                20
        );

        // Stone Sword.
        this.setSimpleBuyItem(
                "&f&lStone Sword",
                new CozyItem(Material.IRON_SWORD),
                Material.IRON_INGOT,
                10,
                29
        );

        // Enchanted Bow.
        this.setSimpleBuyItem(
                "&a&lEnchanted Bow",
                new CozyItem(Material.BOW)
                        .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                        .addEnchantment(Enchantment.DURABILITY, 2),
                Material.EMERALD,
                4,
                12
        );

        // Bow.
        this.setSimpleBuyItem(
                "&6&lBow",
                new CozyItem(Material.BOW),
                Material.GOLD_INGOT,
                7,
                21
        );

        // Arrow.
        this.setSimpleBuyItem(
                "&6&lArrow",
                new CozyItem(Material.ARROW).setAmount(6),
                Material.GOLD_INGOT,
                4,
                30
        );

        // Obsidian.
        this.setSimpleBuyItem(
                "&a&lObsidian",
                new CozyItem(Material.OBSIDIAN).setAmount(4),
                Material.EMERALD,
                3,
                30
        );

        // End Stone.
        this.setSimpleBuyItem(
                "&f&lEnd Stone",
                new CozyItem(Material.END_STONE).setAmount(12),
                Material.IRON_INGOT,
                12,
                22
        );

        // Oak Planks.
        this.setSimpleBuyItem(
                "&f&lOak Planks",
                new CozyItem(Material.OAK_PLANKS).setAmount(16),
                Material.IRON_INGOT,
                12,
                31
        );

        // Wool Planks.
        this.setSimpleBuyItem(
                "&f&lOak Planks",
                new CozyItem(this.teamPlayer.getTeam().getLocation().getColor().getWool()).setAmount(16),
                Material.IRON_INGOT,
                4,
                40
        );
    }

    public void setSimpleBuyItem(@NotNull String name,
                                 @NotNull CozyItem item,
                                 @NotNull Material costMaterial,
                                 int costAmount,
                                 int slot) {

        this.setItem(new InventoryItem(item.create())
                .setName(name)
                .setLore("&7You will lose this item when you die.",
                        "&7",
                        "&fCost &e" + costAmount + "x &a" + costMaterial.name(),
                        "&7",
                        "&7Click to buy " + item.getName())
                .addSlot(slot)
                .addAction((ClickAction) (user, type, inventory) -> {

                    final Inventory playerInventory = user.getPlayer().getInventory();

                    // Check if they have the correct
                    // amount of resources to buy.
                    if (!playerInventory.containsAtLeast(new CozyItem(costMaterial).create(), costAmount)) {
                        user.sendMessage("&7&l> &7You do not have enough &f" + costMaterial.name() + " &7to buy this.");
                        return;
                    }

                    this.removeResources(costMaterial, costAmount, playerInventory);
                    playerInventory.addItem(item.create());
                    user.sendMessage("&a&l> &aYou have brought &e" + costAmount + "x &f" + costMaterial.name() + "&a.");
                })
        );
    }

    private void removeResources(@NotNull Material costMaterial, int costAmount, @NotNull Inventory inventory) {
        int amountTaken = 0;

        for (ItemStack itemStack : inventory.getContents()) {
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
