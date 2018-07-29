package net.lapismc.lapischat.commands;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapischat.utils.LapisUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class LapisChatCommand extends net.lapismc.lapischat.utils.LapisChatCommand {

    public LapisChatCommand(LapisChat plugin) {
        super(plugin, "lapischat", "Shows plugin info/help and player stats", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            displayPluginInfo(sender);
        } else if (args.length == 1) {
            String argument = args[0];
            if (argument.equalsIgnoreCase("update")) {
                checkUpdate(sender);
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 2) {
            //lapischat player dart2112
            if (args[0].equalsIgnoreCase("player")) {
                if (isNotPermitted(sender, "LapisChat.Channels")) {
                    sendMessage(sender, "Error.NotPermitted");
                    return;
                }
                String playerName = args[1];
                //noinspection deprecation
                if (!Bukkit.getOfflinePlayer(playerName).hasPlayedBefore()) {
                    sendMessage(sender, "Error.PlayerNotFound");
                    return;
                }
                //noinspection deprecation
                ChatPlayer player = plugin.getPlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId());
                if (player.getChannels().isEmpty()) {
                    sendMessage(sender, "Moderator.EmptyList");
                } else {
                    sendMessage(sender, "Moderator.ChannelList");
                    StringBuilder s = new StringBuilder();
                    for (Channel channel : player.getChannels()) {
                        s.append(channel.getName()).append("  ");
                    }
                    sender.sendMessage(s.toString());
                }
            } else {
                sendHelp(sender);
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sendMessage(sender, "Help.Channel");
        sendMessage(sender, "Help.LapisChat");
    }

    private void checkUpdate(CommandSender sender) {
        if (isNotPermitted(sender, "LapisChat.Update")) {
            sendMessage(sender, "Error.NotPermitted");
            return;
        }
        LapisUpdater updater = new LapisUpdater(plugin);
        if (updater.checkUpdate()) {
            sender.sendMessage("Update available, downloading now");
            updater.downloadUpdate();
        } else {
            sender.sendMessage("No update available");
        }
    }

    private void displayPluginInfo(CommandSender sender) {
        String primary = plugin.primaryColor;
        String secondary = plugin.secondaryColor;
        String bars = secondary + "-------------";
        sender.sendMessage(bars + primary + "  LapisChat  " + bars);
        sender.sendMessage(primary + "Version: " + secondary + plugin.getDescription().getVersion());
        sender.sendMessage(primary + "Author: " + secondary + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(primary + "Spigot: " + secondary + "https://goo.gl/2o7ior");
        sender.sendMessage(primary + "If you need help use " + secondary + "/lapischat help");
        sender.sendMessage(bars + bars + bars);
    }
}
