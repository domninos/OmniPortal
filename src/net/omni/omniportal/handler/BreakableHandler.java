package net.omni.omniportal.handler;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BreakableHandler {
    private final OmniPortalPlugin plugin;
    private final Set<Material> breakable = new HashSet<>();

    public BreakableHandler(OmniPortalPlugin plugin) {
        this.plugin = plugin;

        List<String> types = plugin.getConfig().getStringList("breakable_block_types");

        if (types.isEmpty()) {
            plugin.sendConsole("&cCould not load breakable block types" +
                    " because breakable_block_types is empty or null.");
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

            breakable.add(material);
        }
    }

    public boolean isBreakable(Material material) {
        return breakable.contains(material);
    }

    public void flush() {
        breakable.clear();
    }
}
