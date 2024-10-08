/*
 * MineManiaTNTRun
 * Used for interacting with the database and message broker.
 * Copyright (C) 2023  MineManiaUK Staff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
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

import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.cozyplugins.cozylibrary.command.CommandManager;
import com.github.cozyplugins.cozylibrary.command.command.command.ProgrammableCommand;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.location.Region;
import com.github.cozyplugins.cozylibrary.placeholder.PlaceholderManager;
import com.github.cozyplugins.cozylibrary.task.TaskContainer;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.minemaniauk.api.MineManiaAPI;
import com.github.minemaniauk.api.game.session.SessionManager;
import com.github.minemaniauk.bukkitapi.MineManiaAPI_BukkitPlugin;
import com.github.minemaniauk.minemaniatntrun.arena.BedWarsArena;
import com.github.minemaniauk.minemaniatntrun.command.*;
import com.github.minemaniauk.minemaniatntrun.configuration.ArenaConfiguration;
import com.github.minemaniauk.minemaniatntrun.inventory.ShopInventory;
import com.github.minemaniauk.minemaniatntrun.inventory.UpgradeInventory;
import com.github.minemaniauk.minemaniatntrun.session.BedWarsSession;
import com.github.minemaniauk.minemaniatntrun.session.component.BedWarsBlockInteractionsComponent;
import com.github.minemaniauk.minemaniatntrun.team.Team;
import com.github.minemaniauk.minemaniatntrun.team.TeamLocation;
import com.github.minemaniauk.minemaniatntrun.team.player.TeamPlayer;
import com.github.smuddgge.squishyconfiguration.implementation.YamlConfiguration;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the main class.
 */
public final class MineManiaBedWarsPlugin extends CozyPlugin implements Listener {

    private static @NotNull MineManiaBedWarsPlugin instance;

    private @NotNull ArenaConfiguration arenaConfiguration;
    private @NotNull SessionManager<BedWarsSession, BedWarsArena> sessionManager;
    private @NotNull Configuration popUpTowerConfig;

    private @NotNull TaskContainer taskContainer;

    public MineManiaBedWarsPlugin(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isCommandTypesEnabled() {
        return false;
    }

    @Override
    public void onEnable() {

        // Initialize this instance.
        MineManiaBedWarsPlugin.instance = this;

        // Task container.
        this.taskContainer = new TaskContainer();

        // Add configuration.
        this.arenaConfiguration = new ArenaConfiguration();
        this.arenaConfiguration.load();

        // Add arenas from configuration to api.
        this.arenaConfiguration.getAll().forEach(
                arena -> MineManiaBedWarsPlugin.getAPI().getGameManager().registerArena(arena)
        );

        // Add session manager.
        this.sessionManager = new SessionManager<>();

        // Register listener.
        this.getPlugin().getServer().getPluginManager().registerEvents(this, this.getPlugin());

        // Set up pop tower config.
        this.popUpTowerConfig = new YamlConfiguration(this.getPlugin().getDataFolder(), "pop_up_tower.yml");
        this.popUpTowerConfig.load();
    }

    @Override
    public void onDisable() {

        // Stop tasks.
        this.taskContainer.stopAllTasks();

        // Loop though all sessions and stop them.
        this.sessionManager.stopAllSessionComponents();

        // Remove game identifier.
        this.getArenaConfiguration().resetGameIdentifiers();

        // Unregister the local arenas.
        MineManiaBedWarsPlugin.getAPI().getGameManager().unregisterLocalArenas();
    }

    @Override
    protected void onLoadCommands(@NotNull CommandManager commandManager) {

        commandManager.addCommand(new ProgrammableCommand("bedwars")
                .setDescription("Contains bed wars commands.")
                .setSyntax("/bedwars")
                .addSubCommand(new ProgrammableCommand("arena")
                        .setDescription("Contains the arena commands")
                        .setSyntax("/bedwars arena")
                        .addSubCommand(new ArenaCreateCommand())
                        .addSubCommand(new ArenaSetSchematicCommand())
                        .addSubCommand(new ArenaSetSchematicLocationCommand())
                        .addSubCommand(new ArenaSetSpawnPointCommand())
                        .addSubCommand(new ArenaSetGeneratorCommand())
                        .addSubCommand(new ProgrammableCommand("team")
                                .setDescription("Contains the team commands")
                                .setSyntax("/bedwars arena team")
                                .addSubCommand(new TeamLocationCreateCommand())
                                .addSubCommand(new TeamLocationSetGeneratorRegionCommand())
                                .addSubCommand(new TeamLocationSetShopLocationCommand())
                                .addSubCommand(new TeamLocationSetSpawnPointCommand())
                                .addSubCommand(new TeamLocationSetUpgradesLocationCommand())
                        )
                )
        );
    }

    @Override
    protected void onLoadPlaceholders(@NotNull PlaceholderManager placeholderManager) {

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        for (BedWarsSession session : this.sessionManager.getSessionList()) {
            TeamPlayer player = session.getTeamPlayer(event.getPlayer().getUniqueId()).orElse(null);
            if (player == null) continue;

            player.stopAllTasks();

            Team team = player.getTeam();

            if (!team.hasBed()) {
                player.kill();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        for (BedWarsSession session : this.sessionManager.getSessionList()) {
            TeamPlayer player = session.getTeamPlayer(event.getPlayer().getUniqueId()).orElse(null);
            if (player == null) continue;

            player.kill();
            return;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        final Location location = event.getBlock().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Check if it was in a team location.
        final TeamLocation teamLocation = arena.getTeamLocation(location).orElse(null);

        session.onBlockBreak(event, teamLocation);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        final Location location = event.getBlock().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Check if it was in a team location.
        final TeamLocation teamLocation = arena.getTeamLocation(location).orElse(null);

        session.onBlockPlace(event, teamLocation);
    }

    @EventHandler
    public void onEntityInteractEvent(PlayerInteractEntityEvent event) {

        final Location location = event.getPlayer().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Get the instance of the team player that clicked.
        final Optional<TeamPlayer> optionalTeamPlayer = session.getTeamPlayer(event.getPlayer().getUniqueId());
        if (optionalTeamPlayer.isEmpty()) return;

        final TeamPlayer teamPlayer = optionalTeamPlayer.get();

        if (!(event.getRightClicked() instanceof Villager villager)) return;

        if (villager.getName().contains("Shop")) {
            new ShopInventory(teamPlayer).open(event.getPlayer());
        }

        if (villager.getName().contains("Upgrades")) {
            new UpgradeInventory(teamPlayer).open(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        final Location location = event.getEntity().getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        session.onPlayerDeath(event);
    }

    @EventHandler
    public void onArmourClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        CozyItem item = new CozyItem(event.getCurrentItem());
        if (item.getMaterial().equals(Material.AIR)) return;
        if (!item.hasNBT()) return;
        if (item.getNBTString("bed_wars_armour").equals("true")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        event.setCancelled(true);

        for (Block block : event.blockList()) {

            final Location location = block.getLocation();

            // Check if it was in an arena.
            final BedWarsArena arena = this.getArena(location).orElse(null);
            if (arena == null) continue;

            // Check if the arena is in a session.
            BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
            if (session == null) continue;

            boolean isPlayerBlock = session.checkIfPlayerCanBreakHere(block.getLocation());
            if (isPlayerBlock && !block.getType().equals(Material.GLASS)) block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onFireBall(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.getType().equals(Material.FIRE_CHARGE)) return;

        // Remove 1 fire charge.
        item.setAmount(item.getAmount() - 1);

        final Vector direction = player.getEyeLocation().getDirection().multiply(1);

        World world = player.getWorld();
        Fireball fireball = world.spawn(player.getEyeLocation(), Fireball.class);
        fireball.setShooter(player);
        fireball.setVelocity(direction);
    }

    @EventHandler
    public void eggBridge(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile instanceof Egg egg)) return;

        // Start timer
        BukkitRunnable eggTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (egg.isDead() || egg.getLocation().getY() < 80) {
                    this.cancel();
                    return;
                }

                // Check if it was in an arena.
                final BedWarsArena arena = MineManiaBedWarsPlugin.this.getArena(egg.getLocation()).orElse(null);
                if (arena == null) return;

                // Check if the arena is in a session.
                BedWarsSession session = MineManiaBedWarsPlugin.this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
                if (session == null) return;

                TeamPlayer teamPlayer = session.getTeamPlayer(((Player) egg.getShooter()).getUniqueId()).orElse(null);
                if (teamPlayer == null) return;

                List<Location> locationList = new ArrayList<>();
                locationList.add(egg.getLocation().clone().add(new Vector(0, -2, 0)));
                locationList.add(egg.getLocation().clone().add(new Vector(1, -2, 0)));
                locationList.add(egg.getLocation().clone().add(new Vector(-1, -2, 0)));
                locationList.add(egg.getLocation().clone().add(new Vector(0, -2, 1)));
                locationList.add(egg.getLocation().clone().add(new Vector(0, -2, -1)));

                for (Location location : locationList) {
                    boolean canPlace = session.checkIfPlayerCanPlaceHere(location);
                    if (!canPlace) continue;
                    if (!location.getBlock().getType().equals(Material.AIR)) continue;

                    location.getBlock().setType(teamPlayer.getTeam().getLocation().getColor().getWool());
                    session.getComponent(BedWarsBlockInteractionsComponent.class).addBlock(location.getBlock().getLocation());
                }
            }
        };
        eggTimer.runTaskTimer(this.getPlugin(), 1L, 1L);
    }

    @EventHandler
    public void onInvisibility(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getNewEffect() == null) return;
        if (!event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) return;

        final Location location = player.getLocation();

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        for (Player temp : Bukkit.getOnlinePlayers()) {
            temp.hidePlayer(this.getPlugin(), player);
        }

        this.taskContainer.runTaskLater(player.getUniqueId().toString(), () -> {
            for (Player temp : Bukkit.getOnlinePlayers()) {
                temp.showPlayer(this.getPlugin(), player);
            }
        }, event.getNewEffect().getDuration());
    }

    @EventHandler
    public void onPlayerInteractWithInvisibility(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

            final Location location = event.getPlayer().getLocation().clone();
            final Region region = new Region(location.clone().add(new Vector(-4, -4, -4)), location.clone().add(new Vector(4, 4, 4)));

            for (Player temp : Bukkit.getOnlinePlayers()) {
                if (!temp.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
                if (region.contains(temp.getLocation())) {
                    for (Player temp2 : Bukkit.getOnlinePlayers()) {
                        temp2.showPlayer(this.getPlugin(), temp);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEnemyInBase(PlayerMoveEvent event) {
        Location location = event.getTo();
        if (location == null) return;

        // Check if it was in an arena.
        final BedWarsArena arena = this.getArena(location).orElse(null);
        if (arena == null) return;

        // Check if the arena is in a session.
        BedWarsSession session = this.sessionManager.getSession(arena.getIdentifier()).orElse(null);
        if (session == null) return;

        // Check if it was in a team location.
        final TeamLocation teamLocation = arena.getTeamLocation(location).orElse(null);
        if (teamLocation == null) return;

        Team team = session.getTeam(teamLocation.getColor()).orElse(null);
        if (team == null) return;

        // Check if the player is on their team.
        if (team.getPlayerList().stream().map(TeamPlayer::getPlayerUuid).toList().contains(event.getPlayer().getUniqueId()))
            return;

        // Check if the team has an alarm.
        if (team.getUpgradeLevel(BedWarsUpgrade.ALARM) >= 1) {

            // Use the alarm.
            team.setUpgradeLevel(BedWarsUpgrade.ALARM, 0);

            for (TeamPlayer teamPlayer : team.getPlayerList()) {
                teamPlayer.getPlayer().ifPresent(player -> {
                    new PlayerUser(player).sendMessage("&c&lALARM > &cAn enemy team is at your base!");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F);
                });
            }
        }

        // Check if the team has a mining trap.
        if (team.getUpgradeLevel(BedWarsUpgrade.MINING_TRAP) >= 1) {

            // Use the alarm.
            team.setUpgradeLevel(BedWarsUpgrade.MINING_TRAP, 0);

            // Give 30s of slow digging.
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 500, 1));
        }

        // Check if the team has a slowness trap.
        if (team.getUpgradeLevel(BedWarsUpgrade.SLOWNESS_TRAP) >= 1) {

            // Use the alarm.
            team.setUpgradeLevel(BedWarsUpgrade.SLOWNESS_TRAP, 0);

            // Give 30s of slowness.
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 500, 1));
        }
    }

    @EventHandler
    public void onCrafting(CraftItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Used to get the instance of the local arena configuration.
     *
     * @return The local arena configuration.
     */
    public @NotNull ArenaConfiguration getArenaConfiguration() {
        return this.arenaConfiguration;
    }

    /**
     * Used to get the instance of an online player from the uuid.
     *
     * @param playerUuid The player uuid to look for.
     * @return The optional player.
     */
    public @NotNull Optional<Player> getOnlinePlayer(@NotNull UUID playerUuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(playerUuid)) return Optional.of(player);
        }
        return Optional.empty();
    }

    /**
     * Used to get the instance of an arena from a specific location.
     *
     * @param location The location inside an arena.
     * @return The arena that contains the location.
     */
    public @NotNull Optional<BedWarsArena> getArena(@NotNull Location location) {
        for (BedWarsArena arena : this.getArenaConfiguration().getAll()) {
            if (arena.getRegion().isEmpty()) continue;
            if (arena.getRegion().get().contains(location)) return Optional.of(arena);
        }

        return Optional.empty();
    }

    public @NotNull SessionManager<BedWarsSession, BedWarsArena> getSessionManager() {
        return this.sessionManager;
    }

    public @NotNull Configuration getPopUpTowerConfig() {
        return this.popUpTowerConfig;
    }

    /**
     * Used to create a new arena and register it
     * with the api and plugin.
     *
     * @param identifier The instance of the identifier.
     * @param region     The instance of the arena region.
     * @param minPlayers The min number of players.
     * @param maxPlayers The max number of players.
     * @return The instance of the tnt arena created.
     */
    public @NotNull BedWarsArena createArena(@NotNull UUID identifier,
                                             @NotNull Region region,
                                             int minPlayers,
                                             int maxPlayers) {

        BedWarsArena arena = new BedWarsArena(identifier);
        arena.setRegion(region);
        arena.setMinPlayers(minPlayers);
        arena.setMaxPlayers(maxPlayers);

        // Register and save the arena.
        MineManiaBedWarsPlugin.getAPI().getGameManager().registerArena(arena);
        return arena;
    }

    /**
     * Used to get the instance of this plugin.
     *
     * @return The instance of this plugin.
     */
    public static @NotNull MineManiaBedWarsPlugin getInstance() {
        return MineManiaBedWarsPlugin.instance;
    }

    /**
     * Used to get the instance of the mine mania api.
     *
     * @return The instance of the mine mania api.
     */
    public static @NotNull MineManiaAPI getAPI() {
        return MineManiaAPI_BukkitPlugin.getInstance().getAPI();
    }
}
