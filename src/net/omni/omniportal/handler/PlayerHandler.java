package net.omni.omniportal.handler;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerHandler {
    private final OmniPortalPlugin plugin;
    private final Map<Player, Location> playerLocation = new HashMap<>();

    public PlayerHandler(OmniPortalPlugin plugin) {
        this.plugin = plugin;
    }

    public void setLastLocation(Player player, Location location) {
        playerLocation.put(player, location);
    }

    public void removePlayer(Player player) {
        playerLocation.remove(player);
    }

    public void tpToLastLocation(Player player) {
        if (player == null) {
            plugin.sendConsole("&cCould not find player for teleporting to last location.");
            return;
        }

        Location lastLocation = getLastLocation(player);

        if (lastLocation == null)
            return;

        player.teleport(lastLocation);
    }

    public Location getLastLocation(Player player) {
        return playerLocation.getOrDefault(player, null);
    }

    public void flush() {
        playerLocation.clear();
    }
}
