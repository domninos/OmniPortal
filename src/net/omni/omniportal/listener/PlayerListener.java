package net.omni.omniportal.listener;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final OmniPortalPlugin plugin;

    public PlayerListener(OmniPortalPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (world.getEnvironment() != World.Environment.NETHER)
            return;

        if (!plugin.getTimerHandler().hasTimer(player.getName()))
            return;

        for (String command : plugin.getConfig().getStringList("commandsToExecute")) {
            if (command != null)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replaceAll("%player%", player.getName()));
        }

        plugin.sendMessage(player, "&aYou finished at " +
                plugin.convertTime(plugin.getTimerHandler().getTimer(player.getName())));
        plugin.getTimerHandler().finish(player.getName());
        plugin.getTopHandler().update();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            plugin.getPlayerHandler().tpToLastLocation(player);
            plugin.getPlayerHandler().removePlayer(player);
        }, 25L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().getName().equalsIgnoreCase(plugin.getSchematicHandler().getWorldName()))
            return;

        if (!plugin.getTimerHandler().hasTimer(player.getName()))
            return;

        event.setCancelled(true);
        plugin.sendMessage(player, "&cYou cannot break blocks here.");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if (item == null || block == null)
            return;

        Player player = event.getPlayer();

        if (!player.getWorld().getName().equalsIgnoreCase(plugin.getSchematicHandler().getWorldName()))
            return;

        if (!plugin.getTimerHandler().hasTimer(player.getName()))
            return;

        if (item.getType() != Material.LAVA_BUCKET && item.getType() != Material.WATER_BUCKET
                && item.getType() != Material.FLINT_AND_STEEL) {
            event.setCancelled(true);
            plugin.sendMessage(player, "&cYou cannot place blocks here.");
        } else {
            if (item.getType() == Material.WATER_BUCKET)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                        () -> {
                            Block aboveBlock = block.getRelative(BlockFace.UP);

                            if (aboveBlock.getType() == Material.WATER)
                                aboveBlock.setType(Material.AIR);
                        }, 10L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getTimerHandler().hasTimer(player.getName())) {
            plugin.getTimerHandler().reset(player.getName());
            plugin.sendConsole("&aTimer reset for " + player.getName());
        }

        plugin.getPlayerHandler().tpToLastLocation(player);
        plugin.getPlayerHandler().removePlayer(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (plugin.getTimerHandler().hasTimer(player.getName())) {
            plugin.getTimerHandler().reset(player.getName());
            plugin.sendMessage(player, "&aYour timer has been reset.");

            for (String command : plugin.getConfig().getStringList("commandsToExecuteOnReset")) {
                if (command != null)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            command.replaceAll("%player%", player.getName()));
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.spigot().respawn(), 1L);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.getPlayerHandler().tpToLastLocation(player);
                plugin.getPlayerHandler().removePlayer(player);

                plugin.getLavaPoolHandler().removePlayer(player);
            }, 30L);
        }
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
