package net.omni.omniportal.placeholder;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.entity.Player;

public class PortalPlaceholder extends PlaceholderExpansion {
    private final OmniPortalPlugin  plugin;

    public PortalPlaceholder(OmniPortalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "portal";
    }

    @Override
    public String getAuthor() {
        return "omni";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.startsWith("top")) {
            String top = "";

            switch (params) {
                case "top_1":
                    top = plugin.getTopHandler().getTop(1);
                    System.out.println(top);
                    break;
                case "top_2":
                    top = plugin.getTopHandler().getTop(2);
                    System.out.println(top);
                    break;
                case "top_3":
                    top = plugin.getTopHandler().getTop(3);
                    System.out.println(top);
                    break;
                case "top_4":
                    top = plugin.getTopHandler().getTop(4);
                    System.out.println(top);
                    break;
                case "top_5":
                    top = plugin.getTopHandler().getTop(5);
                    System.out.println(top);
                    break;
            }

            if (top == null || top.isEmpty())
                top = "Unavailable";

            return top;
        } else if (params.equals("timer")) {
            if (plugin.getTimerHandler().hasTimer(player.getName()))
                return plugin.convertTime(plugin.getTimerHandler().getTimer(player.getName()));
            else if (plugin.getTimerHandler().isInConfig(player.getName()))
                return plugin.convertTime(plugin.getTimerHandler().getTimeInConfig(player.getName()));
        }

        return "Unavailable";
    }
}