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

package com.github.minemaniauk.minemaniatntrun;

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The items that can be used in a bed wars game.
 */
public enum BedWarsItem {

    // Tools.
    WOODEN_AXE(() -> new CozyItem(Material.WOODEN_AXE)
            .setName("Wooden Axe")
            .setLore("&7- This item will stay with you when you die.")
            .setUnbreakable(true),
            Material.IRON_INGOT,
            20
    ),
    STONE_AXE(() -> new CozyItem(Material.STONE_AXE)
            .setName("Stone Axe")
            .setLore("&7- This item will disappear when you die.")
            .setUnbreakable(true),
            Material.GOLD_INGOT,
            5
    ),
    WOODEN_PICKAXE(() -> new CozyItem(Material.WOODEN_PICKAXE)
            .setName("Wooden Pickaxe")
            .setLore("&7- This item will stay with you when you die.")
            .setUnbreakable(true),
            Material.IRON_INGOT,
            20
    ),
    STONE_PICKAXE(() -> new CozyItem(Material.STONE_PICKAXE)
            .setName("Stone Pickaxe")
            .setLore("&7- This item will disappear when you die.")
            .setUnbreakable(true),
            Material.GOLD_INGOT,
            5
    ),
    SHEARS(() -> new CozyItem(Material.SHEARS)
            .setName("Shears")
            .setLore("&7- This item will stay with you when you die.")
            .setUnbreakable(true),
            Material.IRON_INGOT,
            20
    ),

    // Swords.
    WOODEN_SWORD(() -> new CozyItem(Material.WOODEN_SWORD)
            .setName("Wooden Sword")
            .setLore("&7- This item will stay with you when you die.")
            .setUnbreakable(true)
    ),
    STONE_SWORD(() -> new CozyItem(Material.STONE_SWORD)
            .setName("Stone Sword")
            .setLore("&7- This item will disappear when you die.")
            .setUnbreakable(true),
            Material.IRON_INGOT,
            10
    ),
    IRON_SWORD(() -> new CozyItem(Material.IRON_SWORD)
            .setName("Iron Sword")
            .setLore("&7- This item will disappear when you die.")
            .setUnbreakable(true),
            Material.GOLD_INGOT,
            7
    ),
    DIAMOND_SWORD(() -> new CozyItem(Material.DIAMOND_SWORD)
            .setName("Diamond Sword")
            .setLore("&7- This item will disappear when you die.")
            .setUnbreakable(true),
            Material.EMERALD,
            3
    ),

    // Bows.
    ARROWS(() -> new CozyItem(Material.ARROW)
            .setName("Arrow")
            .setLore("&7- This item will disappear when you die.")
            .setAmount(6),
            Material.GOLD_INGOT,
            2
    ),
    BOW(() -> new CozyItem(Material.BOW)
            .setName("Bow")
            .setLore("&7- This item will disappear when you die.")
            .setUnbreakable(true),
            Material.GOLD_INGOT,
            12
    ),
    ENCHANTED_BOW(() -> new CozyItem(Material.BOW)
            .setName("Enchanted Bow")
            .setLore("&7- This item will disappear when you die.")
            .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
            .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
            .setUnbreakable(true),
            Material.EMERALD,
            6
    ),

    // Blocks. (Wool cannot be added as it relies on the players team.)
    OAK_PLANKS(() -> new CozyItem(Material.OAK_PLANKS)
            .setName("Oak Planks")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.")
            .setAmount(16),
            Material.GOLD_INGOT,
            4
    ),
    END_STONE(() -> new CozyItem(Material.END_STONE)
            .setName("End Stone")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.")
            .setAmount(12),
            Material.IRON_INGOT,
            24
    ),
    OBSIDIAN(() -> new CozyItem(Material.OBSIDIAN)
            .setName("Obsidian")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.")
            .setAmount(4),
            Material.EMERALD,
            4
    ),
    GLASS(() -> new CozyItem(Material.GLASS)
            .setName("Glass")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.")
            .setAmount(4),
            Material.IRON_INGOT,
            12
    ),
    LADDER(() -> new CozyItem(Material.LADDER)
            .setName("Ladder")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.")
            .setAmount(16),
            Material.IRON_INGOT,
            12
    ),

    // Utility.
    TNT(() -> new CozyItem(Material.TNT)
            .setName("TNT")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.",
                    "&7- The TNT will ignite once placed."),
            Material.GOLD_INGOT,
            8
    ),
    FIREBALL(() -> new CozyItem(Material.FIRE_CHARGE)
            .setName("Fireball")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When clicked this will throw a fireball in",
                    "&7 &7 the direction you are looking."),
            Material.IRON_INGOT,
            40
    ),
    WATER_BUCKET(() -> new CozyItem(Material.WATER_BUCKET)
            .setName("Water Bucket")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena."),
            Material.GOLD_INGOT,
            6
    ),
    POPUP_TOWER(() -> new CozyItem(Material.CHEST)
            .setName("Pop-Up Tower")
            .setLore("&7- This item will disappear when you die.",
                    "&7- Can be placed anywhere within the arena.",
                    "&7- When placed it will spawn a tower made of wool.")
            .setNBT("pop_up_tower", true),
            Material.IRON_INGOT,
            24
    ),
    BRIDGE_EGG(() -> new CozyItem(Material.EGG)
            .setName("Bridge Egg")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When you throw the egg it will spawn a",
                    "&7 &7 bridge in that direction."),
            Material.EMERALD,
            1
    ),
    ENDER_PEARL(() -> new CozyItem(Material.ENDER_PEARL)
            .setName("Ender Pearl")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When you throw the ender pearl you will",
                    "&7 &7 teleport to where it lands."),
            Material.EMERALD,
            4
    ),
    SWIFTNESS(() -> new CozyItem(Material.POTION)
            .setName("Speed Potion")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When consumed you will have increased &fspeed",
                    "&7 &7 &7for &f30s&7.")
            .addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, 600, 2),
                    true
            )
            .setPotionColor(Color.AQUA),
            Material.EMERALD,
            1
    ),
    JUMP(() -> new CozyItem(Material.POTION)
            .setName("Jump Potion")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When consumed you will have a increased &fjump",
                    "&7 &7 &7hight for &f30s&7.")
            .addPotionEffect(
                    new PotionEffect(PotionEffectType.JUMP, 600, 2),
                    true
            )
            .setPotionColor(Color.LIME),
            Material.EMERALD,
            1
    ),
    INVISIBILITY(() -> new CozyItem(Material.POTION)
            .setName("Invisibility Potion")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When consumed you will have &finvisibility",
                    "&7 &7 &7for &f30s&7.")
            .addPotionEffect(
                    new PotionEffect(PotionEffectType.INVISIBILITY, 600, 2),
                    true
            )
            .setPotionColor(Color.WHITE),
            Material.EMERALD,
            2
    ),
    GOLDEN_APPLE(() -> new CozyItem(Material.GOLDEN_APPLE)
            .setName("Golden Apple")
            .setLore("&7- This item will disappear when you die.",
                    "&7- When consumed you will get two extra hearts",
                    "&7 &7 &7and some beneficial potion effects."),
            Material.GOLD_INGOT,
            3
    );

    private final @NotNull ItemFactory factory;
    private final @Nullable Material costMaterial;
    private final @Nullable Integer costAmount;

    /**
     * Used to create a new bed wars item.
     *
     * @param factory The instance of the item factory.
     */
    BedWarsItem(@NotNull ItemFactory factory) {
        this.factory = factory;
        this.costMaterial = null;
        this.costAmount = null;
    }

    /**
     * Represents the item factory to create
     * a new instance of the item.
     */
    public interface ItemFactory {

        /**
         * Used to create a new instance of the item.
         *
         * @return The instance of the item.
         */
        @NotNull CozyItem create();
    }

    /**
     * Used to create a new bed wars item with a cost.
     *
     * @param factory      The instance of the item factory.
     * @param costMaterial The cost material.
     * @param costAmount   The cost amount.
     */
    BedWarsItem(@NotNull ItemFactory factory, @NotNull Material costMaterial, int costAmount) {
        this.factory = factory;
        this.costMaterial = costMaterial;
        this.costAmount = costAmount;
    }

    /**
     * Used to create a new instance of the item.
     *
     * @return The new instance of the item.
     */
    public @NotNull ItemStack create() {
        return factory.create()
                .setName(this.getItemName())
                .create();
    }

    /**
     * Used to get the color code that should
     * be used for this item.
     *
     * @return The item's color code.
     */
    public @NotNull String getColorCode() {
        if (costMaterial == null) return "&7";

        return switch (this.costMaterial) {
            default -> "&7";
            case IRON_INGOT -> "&f";
            case GOLD_INGOT -> "&6";
            case DIAMOND -> "&b";
            case EMERALD -> "&a";
        };
    }

    /**
     * Used to get the item's formatted name.
     * The color code + the item's name.
     *
     * @return The item's name.
     */
    public @NotNull String getItemName() {
        return this.getColorCode() + this.factory.create().getName();
    }

    /**
     * Used to get the item's title.
     * The color code + "&l" + the item's name.
     *
     * @return The item's title.
     */
    public @NotNull String getTitle() {
        return this.getColorCode() + "&l" + this.factory.create().getName();
    }

    /**
     * Used to get the material required
     * to buy this item.
     *
     * @return The optional material.
     * If empty the item does not have a cost.
     */
    public @NotNull Optional<Material> getCostMaterial() {
        return Optional.ofNullable(this.costMaterial);
    }

    /**
     * Used to get the amount of the material
     * that is required to buy this item.
     *
     * @return The optional amount.
     * If empty the item does not have a cost.
     */
    public @NotNull Optional<Integer> getCostAmount() {
        return Optional.ofNullable(this.costAmount);
    }
}
