package net.omni.omniportal.schematic;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LavaPoolHandler {
    private final OmniPortalPlugin plugin;
    private final Set<LavaPool> lavaPools = new HashSet<>();
    private final Map<Player, LavaPool> playerLavaPool = new HashMap<>();
    private final File lavaPoolsDir;

    public LavaPoolHandler(OmniPortalPlugin plugin) {
        this.plugin = plugin;
        this.lavaPoolsDir = new File(plugin.getDataFolder(), "LavaPools");

        if (!lavaPoolsDir.exists()) {
            if (lavaPoolsDir.mkdirs())
                plugin.sendConsole("&aSuccessfully created LavaPools directory");
        }
    }

    public void loadLavaPools() {
        flush();

        try {
            World schemWorld = plugin.getSchematicHandler().getSchematicWorld();

            if (schemWorld == null) {
                plugin.sendConsole("&cCould not load lava pools because schematic world is not found.");
                return;
            }

            Files.list(lavaPoolsDir.toPath()).map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".yml"))
                    .forEach(file -> {
                        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                        String worldName = config.getString("world");
                        int x = config.getInt("spawn.x");
                        int y = config.getInt("spawn.y");
                        int z = config.getInt("spawn.z");

                        LavaPool lavaPool = new LavaPool(plugin, worldName, x, y, z);

                        lavaPool.loadConfig(file.getName());

                        add(lavaPool);

                        plugin.sendConsole("&aLoaded " + file.getName());
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayer(Player player) {
        LavaPool lavaPool = getAvailableLavaPool();

        if (lavaPool == null) {
            plugin.sendConsole("&cCould not find an available lava pool.");
            return;
        }

        lavaPool.occupy(true);

        player.teleport(lavaPool.getSpawn());

        new BukkitRunnable() {
            int count = 3;

            @Override
            public void run() {
                if (!lavaPool.isOccupied()) {
                    cancel();
                    return;
                }

                if (count > 0)
                    player.sendTitle(ChatColor.GOLD + "Starting in ",
                            ChatColor.GOLD + String.valueOf(count--), 10, 10, 10);
                else {
                    plugin.getTimerHandler().startTimer(player.getName());
                    player.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        playerLavaPool.put(player, lavaPool);
    }

    public void removePlayer(Player player) {
        LavaPool lavaPool = getLavaPoolFromPlayer(player);

        if (lavaPool == null) {
            plugin.sendConsole("&cCould not find lava pool from " + player.getName());
            return;
        }

        player.getInventory().clear();

        lavaPool.occupy(false);

        lavaPool.reset();

        playerLavaPool.remove(player);
    }

    public LavaPool getLavaPoolFromPlayer(Player player) {
        return playerLavaPool.getOrDefault(player, null);
    }

    public LavaPool getAvailableLavaPool() {
        Optional<LavaPool> optional = lavaPools.stream().filter(lavaPool -> !lavaPool.isOccupied()).findFirst();

        if (optional.isPresent())
            return optional.get();
        else {
            Location randomLocationOrigin = plugin.getSchematicHandler().
                    getRandomLocation(plugin.getSchematicHandler().getSchematicWorld().getSpawnLocation(),
                            100);

            Location randomLocation = plugin.getSchematicHandler().
                    getRandomLocation(randomLocationOrigin, 1000);

            randomLocation.setY(plugin.getSchematicHandler().getSchematicWorld()
                    .getHighestBlockYAt(randomLocation) + 2);

            int x = randomLocation.getBlockX();
            int y = randomLocation.getBlockY();
            int z = randomLocation.getBlockZ();

            plugin.sendConsole("Origin: &cX: " + x + " Y: " + y + " Z: " + z);

            return plugin.getSchematicHandler().pasteSchem(x, y, z);
        }
    }

    public void add(LavaPool lavaPool) {
        this.lavaPools.add(lavaPool);
    }

    public boolean isOccupied(LavaPool lavaPool) {
        return lavaPool.isOccupied();
    }

    public void flush() {
        lavaPools.clear();
        playerLavaPool.clear();
    }

    public File getLavaPoolsDir() {
        return lavaPoolsDir;
    }
}
