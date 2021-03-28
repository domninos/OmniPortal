package net.omni.omniportal.handler;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaceableHandler {
    private final Set<Material> placeable = new HashSet<>();

    public PlaceableHandler(OmniPortalPlugin plugin) {
        List<String> types = plugin.getConfig().getStringList("placeable_block_types");

        if (types.isEmpty()) {
            plugin.sendConsole("&cCould not load placeable  block types" +
                    " because placeable_block_types is empty or null.");
            return;
        }

        for (String type : types) {
            if (type == null)
                continue;

            String material_name = type.toUpperCase();

            Material material = Material.getMaterial(material_name);

            if (material == null) {
                plugin.sendConsole("&cCould not parse material " + material_name);
                continue;
            }

            placeable.add(material);
        }
    }

    public boolean isPlaceable(Material material) {
        return placeable.contains(material);
    }

    public void flush() {
        placeable.clear();
    }
}
