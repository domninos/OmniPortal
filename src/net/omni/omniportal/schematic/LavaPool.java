package net.omni.omniportal.schematic;

import net.omni.omniportal.OmniPortalPlugin;
import net.omni.omniportal.handler.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LavaPool {
    private final OmniPortalPlugin plugin;
    private final String worldName;
    private final int x, y, z;
    private CustomConfig config;
    private boolean occupied = false;
    private Location spawn;

    protected LavaPool(OmniPortalPlugin plugin, String worldName, int x, int y, int z) {
        this.plugin = plugin;
        this.worldName = worldName;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void loadConfig(String fileName) {
        this.config = new CustomConfig(plugin, fileName, plugin.getLavaPoolHandler().getLavaPoolsDir());
    }

    public void setConfigValues() {
        config.setNoSave("world", this.worldName);

        config.setNoSave("spawn.x", this.x);
        config.setNoSave("spawn.y", this.y);
        config.setNoSave("spawn.z", this.z);

        config.save();
    }

    public Location getSpawn() {
        if (spawn == null)
            this.spawn = new Location(getWorld(), this.x, this.y, this.z);

        return spawn;
    }

    public void reset() {
        World world = getSpawn().getWorld();

        if (world == null) {
            plugin.sendConsole("&cCould not reset lava pool because world is not found.");
            return;
        }

        plugin.getSchematicHandler().pasteSchematic(x, y, z);
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void occupy(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isOccupied() {
        return occupied;
    }

}