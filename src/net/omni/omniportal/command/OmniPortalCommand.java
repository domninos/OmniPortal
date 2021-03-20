package net.omni.omniportal.command;

import net.omni.omniportal.OmniPortalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class OmniPortalCommand implements CommandExecutor {
    private final OmniPortalPlugin plugin;
    private final String help;

    public OmniPortalCommand(OmniPortalPlugin plugin) {
        this.plugin = plugin;

        String[] msg = {
                "&0&l&m------------- &8[&fOmni&5Portal&8] &0&l&m-------------",
                "&bAlias: /omnip, /portal",
                "&b/omniportal join |player| &7» Sends the player to the schematic world.",
                "&b/omniportal leave |player| &7» Sends the player back to the main world.",
                "&b/omniportal start |player| &7» Starts a player's timer",
                "&b/omniportal stop |player| &7» Stops a player's timer.",
                "&b/omniportal timer |player| &7» Shows current timer of a player.",
                "&b/omniportal reset |player| &7» Resets a player's timer.",
                "&0&l&m---------------------------------"
        };

        this.help = String.join("\n", msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("omniportal.use"))
            return noPerms(sender);

        if (args.length == 0) {
            plugin.sendMessage(sender, help, false);
            return true;
        } else if (args.length == 1) {
            if (!(args[0].equalsIgnoreCase("join")
                    || args[0].equalsIgnoreCase("leave")
                    || args[0].equalsIgnoreCase("start")
                    || args[0].equalsIgnoreCase("stop")
                    || args[0].equalsIgnoreCase("timer")
                    || args[0].equalsIgnoreCase("reset"))) {
                plugin.sendMessage(sender, help);
                return true;
            }

            if (!(sender instanceof Player)) {
                plugin.sendMessage(sender, "&cOnly players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("join")) {
                if (!sender.hasPermission("omniportal.join"))
                    return noPerms(sender);

                if (plugin.getLavaPoolHandler().inLavaPool(player)) {
                    plugin.sendMessage(player,
                            "&cYou are already in a lava pool. Do /portal leave to leave.");
                    return true;
                }

                plugin.getPlayerHandler().setLastLocation(player, player.getLocation());
                plugin.getLavaPoolHandler().sendPlayer(player);
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (!sender.hasPermission("omniportal.leave"))
                    return noPerms(sender);

                if (!plugin.getLavaPoolHandler().inLavaPool(player)) {
                    plugin.sendMessage(sender, "&cYou are not in a lava pool.");
                    return true;
                }

                Location lastLocation = plugin.getPlayerHandler().getLastLocation(player);

                if (lastLocation == null) {
                    plugin.sendMessage(sender, "&cYour last location was not found.");
                    return true;
                }

                player.teleport(lastLocation);
                plugin.getLavaPoolHandler().removePlayer(player);
                plugin.getPlayerHandler().removePlayer(player);

                if (plugin.getTimerHandler().hasTimer(player.getName())) {
                    plugin.getTimerHandler().reset(player.getName());
                    plugin.sendConsole("&aReset timer of " + player.getName());
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("omniportal.start"))
                    return noPerms(sender);

                if (plugin.getTimerHandler().hasTimer(player.getName())) {
                    plugin.sendMessage(player, "&cYou already have a started timer!");
                    return true;
                }

                plugin.getTimerHandler().startTimer(player.getName());
                plugin.sendMessage(player, "&aYour timer has been started!");
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!sender.hasPermission("omniportal.stop"))
                    return noPerms(sender);

                if (!plugin.getTimerHandler().hasTimer(player.getName())) {
                    plugin.sendMessage(player, "&cYou don't have a started timer.");
                    return true;
                }

                plugin.getTimerHandler().stopTimer(player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "portal leave " + player.getName());
                plugin.sendMessage(player, "&aSuccessfully stopped your timer.");
            } else if (args[0].equalsIgnoreCase("timer")) {
                if (!sender.hasPermission("omniportal.timer"))
                    return noPerms(sender);

                String timer = "";

                if (plugin.getTimerHandler().hasTimer(player.getName()))
                    timer = plugin.convertTime(plugin.getTimerHandler().getTimer(player.getName()));
                else {
                    if (plugin.getTimerHandler().isInConfig(player.getName()))
                        timer = plugin.
                                convertTime(plugin.getTimerHandler().getTimeInConfig(player.getName()));
                }

                if (timer.isEmpty())
                    plugin.sendMessage(player, "&cTimer not found.");
                else
                    plugin.sendMessage(player, "&bYour timer is: " + timer);
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!sender.hasPermission("omniportal.reset"))
                    return noPerms(sender);

                if (!plugin.getTimerHandler().hasTimer(player.getName())) {
                    plugin.sendMessage(sender, "&cYou do not have a timer on.");
                    return true;
                }

                plugin.getTimerHandler().reset(player.getName());

                for (String command : plugin.getConfig().getStringList("commandsToExecuteOnReset")) {
                    if (command != null)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                command.replaceAll("%player%", player.getName()));
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "portal leave " + player.getName());
                plugin.sendMessage(player, "&aSuccessfully reset your timer.");
            } else
                plugin.sendMessage(sender, help);

            return true;
        } else if (args.length == 2) {
            if (!(args[0].equalsIgnoreCase("join")
                    || args[0].equalsIgnoreCase("leave")
                    || args[0].equalsIgnoreCase("start")
                    || args[0].equalsIgnoreCase("stop")
                    || args[0].equalsIgnoreCase("timer")
                    || args[0].equalsIgnoreCase("reset"))) {
                plugin.sendMessage(sender, help);
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (args[0].equalsIgnoreCase("join")) {
                if (!sender.hasPermission("omniportal.join.other"))
                    return noPerms(sender);

                if (target == null) {
                    plugin.sendMessage(sender, "&cPlayer not found.");
                    return true;
                }

                if (plugin.getLavaPoolHandler().inLavaPool(target)) {
                    plugin.sendMessage(sender,
                            "&c" + target.getName() + " is already in a lava pool.");
                    return true;
                }

                plugin.getPlayerHandler().setLastLocation(target, target.getLocation());
                plugin.getLavaPoolHandler().sendPlayer(target);
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (!sender.hasPermission("omniportal.leave.other"))
                    return noPerms(sender);

                if (target == null) {
                    plugin.sendMessage(sender, "&cPlayer not found.");
                    return true;
                }

                if (!plugin.getLavaPoolHandler().inLavaPool(target)) {
                    plugin.sendMessage(sender,
                            "&c" + target.getName() + " is not in a lava pool.");
                    return true;
                }

                Location lastLocation = plugin.getPlayerHandler().getLastLocation(target);

                if (lastLocation == null) {
                    plugin.sendMessage(sender,
                            "&c" + target.getName() + "'s last location not found.");
                    return true;
                }

                target.teleport(lastLocation);
                plugin.getLavaPoolHandler().removePlayer(target);
                plugin.getPlayerHandler().removePlayer(target);

                if (plugin.getTimerHandler().hasTimer(target.getName())) {
                    plugin.getTimerHandler().reset(target.getName());
                    plugin.sendConsole("&aReset timer of " + target.getName());
                }

            } else if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("omniportal.start.other"))
                    return noPerms(sender);

                if (target == null) {
                    plugin.sendMessage(sender, "&cPlayer not found.");
                    return true;
                }

                if (plugin.getTimerHandler().hasTimer(target.getName())) {
                    plugin.sendMessage(sender, "&c" + target.getName()
                            + " already have a timer on.");
                    return true;
                }

                plugin.getTimerHandler().startTimer(target.getName());
                plugin.sendMessage(sender, "&aSuccessfully started timer for " + target.getName());
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!sender.hasPermission("omniportal.stop.other"))
                    return noPerms(sender);

                if (target == null) {
                    plugin.sendMessage(sender, "&cPlayer not found.");
                    return true;
                }

                if (!plugin.getTimerHandler().hasTimer(target.getName())) {
                    plugin.sendMessage(sender, "&c" + target.getName()
                            + "does not have a timer on.");
                    return true;
                }

                plugin.getTimerHandler().stopTimer(target.getName());
                plugin.sendMessage(sender, "&aSuccessfully stopped timer for " + target.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "portal leave " + target.getName());
            } else if (args[0].equalsIgnoreCase("timer")) {
                if (!sender.hasPermission("omniportal.timer.other"))
                    return noPerms(sender);

                String timer = "0";
                String name;

                if (target != null) {
                    name = target.getName();

                    if (plugin.getTimerHandler().hasTimer(name))
                        timer = plugin.convertTime(plugin.getTimerHandler().getTimer(name));
                    else
                        name = "";
                } else {
                    name = args[1];

                    if (plugin.getTimerHandler().isInConfig(name))
                        timer = plugin.convertTime(plugin.getTimerHandler().getTimeInConfig(name));
                    else
                        name = "";
                }

                if (name == null || name.isEmpty()) {
                    plugin.sendMessage(sender, "&cTimer not found.");
                    return true;
                } else
                    plugin.sendMessage(sender, "&a" + name + "'s timer: " + timer);
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!sender.hasPermission("omniportal.reset.other"))
                    return noPerms(sender);

                if (target == null) {
                    plugin.sendMessage(sender, "&cPlayer not found.");
                    return true;
                }

                if (!plugin.getTimerHandler().hasTimer(target.getName())) {
                    plugin.sendMessage(sender,
                            "&c" + target.getName() + " does not have a timer on.");
                    return true;
                }

                plugin.getTimerHandler().reset(target.getName());

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "portal leave " + target.getName());

                plugin.sendMessage(sender, "&aSuccessfully reset " + target.getName() + "'s timer.");
                plugin.sendMessage(target, "&aYour timer has been reset by " + sender.getName());

                for (String command : plugin.getConfig().getStringList("commandsToExecuteOnReset")) {
                    if (command != null)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                command.replaceAll("%player%", target.getName()));
                }
            } else
                plugin.sendMessage(sender, help);
        } else
            plugin.sendMessage(sender, help);

        return true;
    }

    public void register() {
        PluginCommand command = plugin.getCommand("omniportal");

        if (command != null)
            command.setExecutor(this);
    }

    private boolean noPerms(CommandSender sender) {
        plugin.sendMessage(sender, "&cNo permission.");
        return true;
    }
}
