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

import com.github.cozyplugins.cozylibrary.item.CozyItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the types of armour that can
 * exist in a bed wars game.
 */
public enum ArmorType {
    NONE(0, "No Armor", Material.IRON_INGOT, 0, () -> new CozyItem().setMaterial(Material.AIR)) {
        @Override
        public @NotNull TeamPlayer applyArmor(@NotNull TeamPlayer teamPlayer) {
            this.applyBasicArmour(teamPlayer);
            return teamPlayer;
        }
    },
    LEATHER(1, "Leather Armor", Material.IRON_INGOT, 20, () -> new CozyItem()
            .setMaterial(Material.LEATHER_CHESTPLATE)
            .setName("&f&lLeather Boots and Leggings")) {
        @Override
        public @NotNull TeamPlayer applyArmor(@NotNull TeamPlayer teamPlayer) {
            Color color = teamPlayer.getTeam().getLocation().getColor().getBukkitColor();

            teamPlayer.getPlayer().ifPresent(player -> {
                player.getInventory().setBoots(this.setColor(new CozyItem().setMaterial(Material.LEATHER_BOOTS), color)
                        .setName("&f&lLeather Boots")
                        .setLore("&7Armour will not disappear when you die.",
                                "&7",
                                "&7- You can purchase better armour in the shop.",
                                "&7- You can enchant your teams armour by clicking",
                                "&7  the upgrades villager")
                        .create()
                );

                player.getInventory().setLeggings(this.setColor(new CozyItem().setMaterial(Material.LEATHER_LEGGINGS), color)
                        .setName("&f&lLeather Leggings")
                        .setLore("&7Armour will not disappear when you die.",
                                "&7",
                                "&7- You can purchase better armour in the shop.",
                                "&7- You can enchant your teams armour by clicking",
                                "&7  the upgrades villager")
                        .create()
                );
            });
            return teamPlayer;
        }
    },
    IRON(2, "Iron Armor", Material.GOLD_INGOT, 12, () -> new CozyItem()
            .setMaterial(Material.IRON_CHESTPLATE)
            .setName("&6&lIron Boots and Leggings")) {
        @Override
        public @NotNull TeamPlayer applyArmor(@NotNull TeamPlayer teamPlayer) {
            teamPlayer.getPlayer().ifPresent(player -> {
                player.getInventory().setBoots(new CozyItem()
                        .setMaterial(Material.IRON_BOOTS)
                        .setName("&6&lIron Boots")
                        .setLore("&7Armour will not disappear when you die.",
                                "&7",
                                "&7- You can purchase better armour in the shop.",
                                "&7- You can enchant your teams armour by clicking",
                                "&7  the upgrades villager")
                        .create()
                );

                player.getInventory().setLeggings(new CozyItem()
                        .setMaterial(Material.IRON_LEGGINGS)
                        .setName("&6&lIron Leggings")
                        .setLore("&7Armour will not disappear when you die.",
                                "&7",
                                "&7- You can purchase better armour in the shop.",
                                "&7- You can enchant your teams armour by clicking",
                                "&7  the upgrades villager")
                        .create()
                );
            });
            return teamPlayer;
        }
    },
    DIAMOND(3, "Diamond Armor", Material.EMERALD, 6, () -> new CozyItem()
            .setMaterial(Material.DIAMOND_CHESTPLATE)
            .setName("&a&lDiamond Boots and Leggings")) {
        @Override
        public @NotNull TeamPlayer applyArmor(@NotNull TeamPlayer teamPlayer) {
            teamPlayer.getPlayer().ifPresent(player -> {
                player.getInventory().setBoots(new CozyItem()
                        .setMaterial(Material.DIAMOND_BOOTS)
                        .setName("&a&lDiamond Boots")
                        .setLore("&7Armour will not disappear when you die.",
                                "&7",
                                "&7- You can enchant your teams armour by clicking",
                                "&7  the upgrades villager")
                        .create()
                );

                player.getInventory().setLeggings(new CozyItem()
                        .setMaterial(Material.DIAMOND_LEGGINGS)
                        .setName("&a&lDiamond Leggings")
                        .setLore("&7Armour will not disappear when you die.",
                                "&7",
                                "&7- You can enchant your teams armour by clicking",
                                "&7  the upgrades villager")
                        .create()
                );
            });
            return teamPlayer;
        }
    };

    private final int index;
    private final @NotNull String title;
    private final @NotNull Material costMaterial;
    private final int costAmount;
    private final @NotNull DisplayItemFactory factory;

    /**
     * Used to create a new armor type.
     *
     * @param index The index that the armor is compared to another armor.
     *              The higher the number the better.
     * @param title The title of the armor used in the shop.
     */
    ArmorType(int index, @NotNull String title, @NotNull Material costMaterial, int costAmount, @NotNull DisplayItemFactory factory) {
        this.index = index;
        this.title = title;
        this.costMaterial = costMaterial;
        this.costAmount = costAmount;
        this.factory = factory;
    }

    public interface DisplayItemFactory {
        @NotNull CozyItem create();
    }

    public abstract @NotNull TeamPlayer applyArmor(@NotNull TeamPlayer teamPlayer);

    public void applyBasicArmour(@NotNull TeamPlayer teamPlayer) {
        Color color = teamPlayer.getTeam().getLocation().getColor().getBukkitColor();

        teamPlayer.getPlayer().ifPresent(player -> {
            player.getInventory().setHelmet(this.setColor(new CozyItem().setMaterial(Material.LEATHER_HELMET), color)
                    .setName("&7&lLeather Helmet")
                    .setLore("&7Armour will not disappear when you die.",
                            "&7",
                            "&7- You can purchase better armour in the shop.",
                            "&7- You can enchant your teams armour by clicking",
                            "&7  the upgrades villager")
                    .create()
            );
            player.getInventory().setHelmet(this.setColor(new CozyItem().setMaterial(Material.LEATHER_CHESTPLATE), color)
                    .setName("&7&lLeather Chestplate")
                    .setLore("&7Armour will not disappear when you die.",
                            "&7",
                            "&7- You can purchase better armour in the shop.",
                            "&7- You can enchant your teams armour by clicking",
                            "&7  the upgrades villager")
                    .create()
            );
        });
    }

    protected @NotNull CozyItem setColor(@NotNull CozyItem item, @NotNull Color color) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta meta)) return item;
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Used to get the armor index.
     * This is used to see how good the armor is
     * compared to another armor.
     * The higher the number the better.
     *
     * @return The armor index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Used to get the armor's title.
     * This can be used in the shop.
     *
     * @return The armor's title.
     */
    public @NotNull String getTitle() {
        return title;
    }

    public @NotNull Material getCostMaterial() {
        return costMaterial;
    }

    public int getCostAmount() {
        return costAmount;
    }

    public @NotNull DisplayItemFactory getFactory() {
        return factory;
    }

    /**
     * Used to get the name of the armor.
     * This is the title but in lowercase.
     *
     * @return The armor's name.
     */
    public @NotNull String getName() {
        return this.title.toLowerCase();
    }
}
