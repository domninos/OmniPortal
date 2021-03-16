package net.omni.omniportal.handler;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TimerHandler {
    private final ConcurrentMap<String, Double> timers = new ConcurrentHashMap<>();
    private final OmniPortalPlugin plugin;
    private final DecimalFormat format = new DecimalFormat("#.#");
    private final int timerID;

    public TimerHandler(OmniPortalPlugin plugin) {
        this.plugin = plugin;

        this.timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            for (Map.Entry<String, Double> entry : timers.entrySet()) {
                Player player = Bukkit.getPlayerExact(entry.getKey());

                if (player == null) {
                    stopTimer(entry.getKey());
                    continue;
                }

                entry.setValue(entry.getValue() + 0.1);

            }
        }, 2, 2);
    }

    /**
     * Starts the timer for a player.
     *
     * @param name - {@code String} to start the timer
     */
    public void startTimer(String name) {
        if (!isInConfig(name)) {
            plugin.sendConsole("&aCreating timer for " + name);
            timers.put(name, 0D);
            save(name);
        } else {
            timers.put(name, getTimeInConfig(name));
            plugin.sendConsole("&AContinued timer of " + name);
        }
    }

    /**
     * Stops and saves the timer for a player.
     *
     * @param name - {@code String} to stop the timer
     */
    public void stopTimer(String name) {
        save(name);
        timers.remove(name);
    }

    /**
     * Adds player to the finished list in config.
     *
     * @param name {@code String} finished player
     */
    public void finish(String name) {
        if (timers.containsKey(name)) {
            plugin.getConfig().set("finished." + name, Double.valueOf(format.format(timers.get(name))));
            plugin.getConfig().set("players." + name, null);
            plugin.saveConfig();

            timers.remove(name);
        }
    }

    /**
     * Restarts/resets player's timer.
     *
     * @param name {@code String} player
     */
    public void reset(String name) {
        if (timers.containsKey(name)) {
            plugin.getConfig().set("players." + name, null);
            plugin.saveConfig();

            timers.remove(name);
        }
    }

    /**
     * Saves current time to config.
     *
     * @param name - {@code String} to save
     */
    public void save(String name) {
        if (timers.containsKey(name)) {
            plugin.getConfig().set("players." + name, timers.get(name));
            plugin.saveConfig();
        }
    }

    /**
     * Saves all from cache to config
     */
    public void save() {
        for (Map.Entry<String, Double> entry : timers.entrySet())
            save(entry.getKey());
    }

    /**
     * Use this only if the player is online.
     *
     * @param name - {@code String} of player
     * @return true - if player is in cache
     */
    public boolean hasTimer(String name) {
        return timers.containsKey(name);
    }

    /**
     * Use this only if the player is in config.
     *
     * @param name - {@code String} of to check
     * @return true - if uuid is in config
     */
    public boolean isInConfig(String name) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("players");

        if (section == null)
            return false;

        for (String key : section.getKeys(false)) {
            if (key == null)
                continue;

            if (key.equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    /**
     * Returns the time from config.
     *
     * @param name - {@code String} to check from config
     * @return {@code Double} the time from config
     */
    public double getTimeInConfig(String name) {
        return isInConfig(name) ? plugin.getConfig().getDouble("players." + name) : 0D;
    }

    /**
     * Returns the timer of a player in cache
     *
     * @param name - {@code String} to get timer
     * @return {@code Double} the time stored in cache
     */
    public double getTimer(String name) {
        return timers.getOrDefault(name, 0D);
    }

    /**
     * Stops the timer task and uses the save method.
     */
    public void flush() {
        if (Bukkit.getScheduler().isCurrentlyRunning(timerID))
            Bukkit.getScheduler().cancelTask(timerID);

        save();
        timers.clear();
    }

    /**
     * Returns the format #.#
     * @return {@code DecimalFormat} the format: #.#
     */
    public DecimalFormat getFormat() {
        return format;
    }
}