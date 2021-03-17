package net.omni.omniportal;

import net.omni.omniportal.command.OmniPortalCommand;
import net.omni.omniportal.handler.ItemHandler;
import net.omni.omniportal.handler.PlayerHandler;
import net.omni.omniportal.handler.TimerHandler;
import net.omni.omniportal.handler.TopHandler;
import net.omni.omniportal.listener.PlayerListener;
import net.omni.omniportal.placeholder.PortalPlaceholder;
import net.omni.omniportal.schematic.LavaPoolHandler;
import net.omni.omniportal.schematic.SchematicHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;

public class OmniPortalPlugin extends JavaPlugin {

    /*
    TODO:
     * if performance is ded = make a runnable run everytime a player starts
     */

    private TimerHandler timerHandler;
    private TopHandler topHandler;
    private SchematicHandler schematicHandler;
    private LavaPoolHandler lavaPoolHandler;
    private PlayerHandler playerHandler;
    private ItemHandler itemHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.schematicHandler = new SchematicHandler(this);

        this.lavaPoolHandler = new LavaPoolHandler(this);

        lavaPoolHandler.loadLavaPools();

        this.playerHandler = new PlayerHandler(this);
        this.timerHandler = new TimerHandler(this);
        this.topHandler = new TopHandler(this);
        this.itemHandler = new ItemHandler(this);

        // [+] LISTENERS [+]
        new PlayerListener(this).register();

        // [+] COMMANDS [+]
        new OmniPortalCommand(this).register();

        topHandler.update();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PortalPlaceholder(this).register();

            sendConsole("&aPlaceholderAPI found, registered placeholders.");
        }

        sendConsole("&aSuccessfully enabled OmniPortal v-" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        timerHandler.flush();
        lavaPoolHandler.flush();
        playerHandler.flush();

        sendConsole("&aSuccessfully disabled OmniPortal");
    }

    public void sendConsole(String message) {
        sendMessage(Bukkit.getConsoleSender(), message);
    }

    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, true);
    }

    public void sendMessage(CommandSender sender, String message, boolean prefix) {
        sender.sendMessage(translate(prefix ? "&7[&fOmni&5Portal&7]&r " + message : message));
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String convertTime(double time) {
        return timerHandler.getFormat().format(time) + "s";
    }

    public String getNextLavaPool() {
        String fileName = "lavaPool-1";

        try {
            fileName = "lavaPool-" + (Files.list(lavaPoolHandler.getLavaPoolsDir().toPath()).count() + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public TimerHandler getTimerHandler() {
        return timerHandler;
    }

    public TopHandler getTopHandler() {
        return topHandler;
    }

    public LavaPoolHandler getLavaPoolHandler() {
        return lavaPoolHandler;
    }

    public SchematicHandler getSchematicHandler() {
        return schematicHandler;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }
}
