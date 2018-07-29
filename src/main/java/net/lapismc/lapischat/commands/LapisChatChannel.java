package net.lapismc.lapischat.commands;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapischat.utils.LapisChatCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;

public class LapisChatChannel extends LapisChatCommand {

    public LapisChatChannel(LapisChat plugin) {
        super(plugin, "channel", "Set, join or leave a channel", new ArrayList<>(Collections.singletonList("ch")));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 3) {
            sendCommandHelp(sender);
        } else {
            //ch (name) adds to if not already in and sets as main
            if (args.length == 1) {
                String channelName = args[0];
                if (ensurePlayer(sender)) return;
                ChatPlayer p = getChatPlayer(sender);
                if (plugin.channelManager.doesChannelExist(channelName)) {
                    Channel channel = plugin.channelManager.getChannel(channelName);
                    if (p.isBannedFromChannel(channel)) {
                        sendChannelMessage(p.getPlayer(), "Channel.Banned", channel);
                        return;
                    }
                    if (isNotPermitted(sender, channel.getPermission().getName())) {
                        sendChannelMessage(sender, "Channel.NotPermitted", channel);
                        p.removeChannel(channel);
                    }
                    if (p.getChannels().contains(channel)) {
                        p.setMainChannel(channel);
                        sendChannelMessage(sender, "Channel.Set", channel);
                    } else {
                        p.addChannel(channel);
                        p.setMainChannel(channel);
                        if (p.getChannels().contains(channel)) {
                            sendChannelMessage(sender, "Channel.Joined", channel);
                            sendChannelMessage(sender, "Channel.Set", channel);
                        }
                    }
                } else {
                    sendMessage(sender, "Error.ChannelDoesNotExist");
                }
            } else if (args.length == 2) {
                if (ensurePlayer(sender)) return;
                ChatPlayer p = getChatPlayer(sender);
                //ch leave (name) removes them from the channel
                if (args[0].equalsIgnoreCase("leave")) {
                    String channelName = args[1];
                    if (plugin.channelManager.doesChannelExist(channelName)) {
                        Channel channel = plugin.channelManager.getChannel(channelName);
                        if (p.isInChannel(channel)) {
                            p.removeChannel(channel);
                            sendChannelMessage(sender, "Channel.Left", channel);
                        } else {
                            sendChannelMessage(sender, "Channel.NotInChannel", channel);
                        }
                    }
                    //ch join (name) joins a channel but doesn't set it as main
                } else if (args[0].equalsIgnoreCase("join")) {
                    String channelName = args[1];
                    if (plugin.channelManager.doesChannelExist(channelName)) {
                        Channel channel = plugin.channelManager.getChannel(channelName);
                        if (p.isBannedFromChannel(channel)) {
                            sendChannelMessage(p.getPlayer(), "Channel.Banned", channel);
                            return;
                        }
                        if (isNotPermitted(sender, channel.getPermission().getName())) {
                            sendChannelMessage(sender, "Channel.NotPermitted", channel);
                            p.removeChannel(channel);
                        }
                        if (p.isInChannel(channel)) {
                            sendChannelMessage(sender, "Channel.AlreadyInChannel", channel);
                        } else {
                            p.addChannel(channel);
                            if (p.getChannels().contains(channel)) {
                                sendChannelMessage(sender, "Channel.Joined", channel);
                            }
                        }
                    }
                } else {
                    sendCommandHelp(sender);
                }
                //args length == 3
            } else {
                String channelName = args[0];
                String command = args[1];
                String playerName = args[2];
                if (isNotPermitted(sender, "LapisChat.Moderate." + channelName)) {
                    sendMessage(sender, "Error.NotPermitted");
                    return;
                }
                if (!plugin.channelManager.doesChannelExist(channelName)) {
                    sendMessage(sender, "Error.ChannelDoesNotExist");
                    return;
                }
                Channel channel = plugin.channelManager.getChannel(channelName);
                //noinspection deprecation
                ChatPlayer p = plugin.getPlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId());
                //ch (name) kick/ban (player) if moderator remove player or ban player from channel
                if (command.equalsIgnoreCase("kick")) {
                    if (p.isInChannel(channel)) {
                        p.removeChannel(channel);
                        sendModeratorMessage(sender, "Moderator.Kicked", channel, p);
                        if (p.getOfflinePlayer().isOnline()) {
                            sendChannelMessage(p.getPlayer(), "Channel.Kicked", channel);
                        }
                    }
                } else if (command.equalsIgnoreCase("ban")) {
                    p.banFromChannel(channel);
                    sendModeratorMessage(sender, "Moderator.Banned", channel, p);
                    if (p.getOfflinePlayer().isOnline()) {
                        sendChannelMessage(p.getPlayer(), "Channel.Banned", channel);
                    }
                    //ch (name) unban (player) if moderator and player is banned, remove ban
                } else if (command.equalsIgnoreCase("unban")) {
                    if (p.isBannedFromChannel(channel)) {
                        p.unBanFromChannel(channel);
                        sendModeratorMessage(sender, "Moderator.Unbanned", channel, p);
                        if (p.getOfflinePlayer().isOnline()) {
                            sendChannelMessage(p.getPlayer(), "Channel.Unbanned", channel);
                        }
                    }
                } else {
                    sendCommandHelp(sender);
                }
            }
        }
    }

    private void sendCommandHelp(CommandSender sender) {
        sendMessage(sender, "Help.Channel");
    }
}
