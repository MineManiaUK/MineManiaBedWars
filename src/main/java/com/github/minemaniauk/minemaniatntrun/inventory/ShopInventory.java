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

import com.github.cozyplugins.cozylibrary.MessageManager;
import com.github.cozyplugins.cozylibrary.inventory.CozyInventory;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.InventoryManager;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.minemaniatntrun.BedWarsItem;
import com.github.minemaniauk.minemaniatntrun.BedWarsUpgrade;
import com.github.minemaniauk.minemaniatntrun.team.player.ArmorType;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the bed wars shop inventory.
 */
public class ShopInventory extends CozyInventory {

    protected final @NotNull TeamPlayer teamPlayer;

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
        this.resetInventory();

        // Tools.
        this.setTools();

        // Armour.
        this.setArmour(ArmorType.DIAMOND, 10);
        this.setArmour(ArmorType.IRON, 19);
        this.setArmour(ArmorType.LEATHER, 28);

        // Swords.
        this.setBuyItem(BedWarsItem.DIAMOND_SWORD, 11, () -> this.teamPlayer.getTeam().updateSwords());
        this.setBuyItem(BedWarsItem.IRON_SWORD, 20, () -> this.teamPlayer.getTeam().updateSwords());
        this.setBuyItem(BedWarsItem.STONE_SWORD, 29, () -> this.teamPlayer.getTeam().updateSwords());

        // Bows.
        this.setSimpleBuyItem(BedWarsItem.ENCHANTED_BOW, 12);
        this.setSimpleBuyItem(BedWarsItem.BOW, 21);
        this.setSimpleBuyItem(BedWarsItem.ARROWS, 30);

        // Blocks
        this.setSimpleBuyItem(BedWarsItem.OBSIDIAN, 13);
        this.setSimpleBuyItem(BedWarsItem.END_STONE, 22);
        this.setSimpleBuyItem(BedWarsItem.OAK_PLANKS, 31);

        // Wool.
        this.setBuyItem(
                "&f&lWool",
                new CozyItem(this.teamPlayer.getTeam().getLocation().getColor().getWool()).setAmount(16),
                Material.IRON_INGOT,
                4,
                40,
                () -> {}
        );

        this.setSimpleBuyItem(BedWarsItem.GLASS, 14);
        this.setSimpleBuyItem(BedWarsItem.LADDER, 23);

        // Utility.
        this.setSimpleBuyItem(BedWarsItem.TNT, 32);
        this.setSimpleBuyItem(BedWarsItem.FIREBALL, 41);
        this.setSimpleBuyItem(BedWarsItem.WATER_BUCKET, 15);
        this.setSimpleBuyItem(BedWarsItem.POPUP_TOWER, 24);
        this.setSimpleBuyItem(BedWarsItem.BRIDGE_EGG, 33);
        this.setSimpleBuyItem(BedWarsItem.ENDER_PEARL, 42);

        // Potions
        this.setSimpleBuyItem(BedWarsItem.SWIFTNESS, 16);
        this.setSimpleBuyItem(BedWarsItem.JUMP, 25);
        this.setSimpleBuyItem(BedWarsItem.INVISIBILITY, 34);

        // Golden Apple.
        this.setSimpleBuyItem(BedWarsItem.GOLDEN_APPLE, 43);
    }

    public void setTools() {

        if (this.teamPlayer.hasAxe()) this.setSimpleBuyItem(BedWarsItem.STONE_AXE, 37);
        else this.setBuyItem(BedWarsItem.WOODEN_AXE, 37, () -> this.teamPlayer.setAxe(BedWarsItem.WOODEN_AXE));

        if (this.teamPlayer.hasPickaxe()) this.setSimpleBuyItem(BedWarsItem.STONE_PICKAXE, 38);
        else this.setBuyItem(BedWarsItem.WOODEN_PICKAXE, 38, () -> this.teamPlayer.setPickaxe(BedWarsItem.WOODEN_PICKAXE));

        if (!this.teamPlayer.hasShears()) this.setBuyItem(BedWarsItem.SHEARS, 39, () -> {
            this.teamPlayer.setShears(BedWarsItem.SHEARS);
        });
    }

    public void setArmour(@NotNull ArmorType armorType, int slot) {

        this.setItem(new InventoryItem(armorType.getFactory().create().create())
                .setLore("&7This item will not disappear when you die.",
                        "&7",
                        "&fCost &e" + armorType.getCostAmount() + "x &a" + armorType.getCostMaterial().name().split("_")[0].toLowerCase(),
                        "&7",
                        "&7Click to buy " + armorType.name().toLowerCase() + " armour")
                .addSlot(slot)
                .addAction((ClickAction) (user, type, inventory) -> {

                    final Inventory playerInventory = user.getPlayer().getInventory();

                    // Check if they have the correct
                    // amount of resources to buy.
                    if (!playerInventory.containsAtLeast(new CozyItem(armorType.getCostMaterial()).create(), armorType.getCostAmount())) {
                        user.sendMessage("&7&l> &7You do not have enough &f" + armorType.getCostMaterial().name().split("_")[0].toLowerCase() + " &7to buy this.");
                        return;
                    }

                    this.removeResources(armorType.getCostMaterial(), armorType.getCostAmount(), playerInventory);

                    armorType.applyArmor(this.teamPlayer);
                    this.teamPlayer.setArmourType(armorType);

                    user.sendMessage("&a&l> &aYou have brought &e" + type.name().toLowerCase() + " armour&a for &e" + armorType.getCostAmount() + "x &f" + armorType.getCostMaterial().name().split("_")[0].toLowerCase() + "&a.");
                })
        );

    }

    public void setSimpleBuyItem(@NotNull BedWarsItem item, int slot) {
        final CozyItem cozyItem = new CozyItem(item.create());
        this.setBuyItem(
                item.getTitle(),
                cozyItem,
                item.getCostMaterial().orElse(Material.IRON_INGOT),
                item.getCostAmount().orElse(1),
                slot,
                () -> {}
        );
    }


    public void setBuyItem(@NotNull BedWarsItem item, int slot, @NotNull Runnable runnable) {
        final CozyItem cozyItem = new CozyItem(item.create());
        this.setBuyItem(
                item.getTitle(),
                cozyItem,
                item.getCostMaterial().orElse(Material.IRON_INGOT),
                item.getCostAmount().orElse(1),
                slot,
                runnable
        );
    }

    public void setBuyItem(@NotNull String name,
                                 @NotNull CozyItem item,
                                 @NotNull Material costMaterial,
                                 int costAmount,
                                 int slot,
                                 Runnable onPurchase) {

        this.setItem(new InventoryItem(item.create())
                .setName(name)
                .setLore(item.getLore().isEmpty() ? "&7This item will disappear when you die." : item.getLore().get(0) ,
                        "&7",
                        "&fCost &e" + costAmount + "x &a" + costMaterial.name().split("_")[0].toLowerCase(),
                        "&7",
                        "&7Click to buy " + name)
                .addSlot(slot)
                .addAction((ClickAction) (user, type, inventory) -> {

                    final Inventory playerInventory = user.getPlayer().getInventory();

                    // Check if they have the correct
                    // amount of resources to buy.
                    if (!playerInventory.containsAtLeast(new CozyItem(costMaterial).create(), costAmount)) {
                        user.sendMessage("&7&l> &7You do not have enough &f" + costMaterial.name().split("_")[0].toLowerCase() + " &7to buy this.");
                        return;
                    }

                    this.removeResources(costMaterial, costAmount, playerInventory);
                    this.removeOldSwords(user, item.getMaterial());
                    playerInventory.addItem(item.setName(name.replace("&l", "")).create());
                    user.sendMessage("&a&l> &aYou have brought &e" + item.getName() + "&a for &e" + costAmount + "x &f" + costMaterial.name().split("_")[0].toLowerCase() + "&a.");
                    onPurchase.run();
                })
        );
    }

    private void removeOldSwords(@NotNull PlayerUser user, @NotNull Material material) {
        if (material.equals(Material.STONE_SWORD)) {
            user.getPlayer().getInventory().remove(Material.WOODEN_SWORD);
        }
        if (material.equals(Material.IRON_SWORD)) {
            user.getPlayer().getInventory().remove(Material.WOODEN_SWORD);
            user.getPlayer().getInventory().remove(Material.STONE_SWORD);
        }
        if (material.equals(Material.DIAMOND_SWORD)) {
            user.getPlayer().getInventory().remove(Material.WOODEN_SWORD);
            user.getPlayer().getInventory().remove(Material.STONE_SWORD);
            user.getPlayer().getInventory().remove(Material.IRON_SWORD);
        }
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
