package net.omni.omniportal.handler;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHandler {

    private final ItemStack bucket = new ItemStack(Material.BUCKET);
    private final ItemStack water_bucket = new ItemStack(Material.WATER_BUCKET);
    private final ItemStack wood_button = new ItemStack(Material.OAK_BUTTON);
    private final ItemStack flint_steel = new ItemStack(Material.FLINT_AND_STEEL);

    public ItemHandler(OmniPortalPlugin plugin) {
        ItemMeta buttonMeta = this.wood_button.getItemMeta();

        if (buttonMeta == null)
            buttonMeta = Bukkit.getItemFactory().getItemMeta(Material.OAK_BUTTON);

        if (buttonMeta != null) {
            buttonMeta.setDisplayName(plugin.translate("&3Reset &8(Right Click&8)"));

            this.wood_button.setItemMeta(buttonMeta);
        }
    }

    public void givePortalItems(Player player) {
        if (player == null)
            return;

        clear(player);

        player.getInventory().addItem(bucket, water_bucket, flint_steel);

        player.getInventory().setItem(8, wood_button);
    }

    public void clear(Player player) {
        if (player != null)
            player.getInventory().clear();
    }

    public boolean isButton(ItemStack itemStack) {
        return itemStack != null && itemStack.isSimilar(this.wood_button);
    }
}
