package net.omni.omniportal.handler;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemHandler {

    private final OmniPortalPlugin plugin;
    private final Set<ItemStack> portalItems = new HashSet<>();

    private final ItemStack wood_button = new ItemStack(Material.OAK_BUTTON);

    public ItemHandler(OmniPortalPlugin plugin) {
        this.plugin = plugin;

        ItemMeta buttonMeta = this.wood_button.getItemMeta();

        if (buttonMeta == null)
            buttonMeta = Bukkit.getItemFactory().getItemMeta(Material.OAK_BUTTON);

        if (buttonMeta != null) {
            buttonMeta.setDisplayName(plugin.translate("&3Reset &8(Right Click&8)"));

            this.wood_button.setItemMeta(buttonMeta);
        }

        List<String> items = plugin.getConfig().getStringList("portal_items");

        if (items.isEmpty()) {
            plugin.sendConsole("&cPortal items cannot be empty or null in config. (portal_items)");
            return;
        }

        for (String item : items) {
            if (item == null)
                continue;

            String[] splice = item.split(":");

            if (splice.length != 2) {
                plugin.sendConsole("&cSomething went wrong parsing portal item for " + item);
                continue;
            }

            String material_name = splice[0].toUpperCase();
            String amountString = splice[1];

            Material material = Material.getMaterial(material_name);

            if (material == null) {
                plugin.sendConsole("&cCould not find material " + material_name);
                continue;
            }

            try {
                int amount = Integer.parseInt(amountString);

                ItemStack itemStack = new ItemStack(material, amount);

                portalItems.add(itemStack);
                plugin.sendConsole("&bLoaded " + amount + " " + material.name());
            } catch (NumberFormatException e) {
                plugin.sendConsole("&cCould not parse integer from " + amountString);
            }
        }
    }

    public void givePortalItems(Player player) {
        if (player == null)
            return;

        if (portalItems.isEmpty()) {
            plugin.sendConsole("&cCould not give portal items to " + player.getName()
                    + " because portal items is empty.");
            return;
        }

        clear(player);

        player.getInventory().setItem(8, wood_button);

        portalItems.forEach(item -> {
            if (item != null)
                player.getInventory().addItem(item);
        });
    }

    public void clear(Player player) {
        if (player != null)
            player.getInventory().clear();
    }

    public boolean isButton(ItemStack itemStack) {
        return itemStack != null && itemStack.isSimilar(this.wood_button);
    }

    public void flush() {
        portalItems.clear();
    }
}
