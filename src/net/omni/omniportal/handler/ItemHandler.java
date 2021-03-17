package net.omni.omniportal.handler;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemHandler {

    private final ItemStack bucket = new ItemStack(Material.BUCKET);
    private final ItemStack water_bucket = new ItemStack(Material.WATER_BUCKET);
    private final ItemStack flint_steel = new ItemStack(Material.FLINT_AND_STEEL);

    public void givePortalItems(Player player) {
        if (player == null)
            return;

        clear(player);

        player.getInventory().addItem(bucket, water_bucket, flint_steel);
    }

    public void clear(Player player) {
        if (player != null)
            player.getInventory().clear();
    }
}
