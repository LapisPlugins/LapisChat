package net.lapismc.lapischat;

import net.lapismc.lapischat.events.LapisMessageEvent;
import net.lapismc.lapischat.hooks.PlaceholderAPIHook;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.UUID;

public class MessageManager {

    private Chat vaultChat;
    private LapisChat plugin;
    private HashMap<UUID, UUID> conversations = new HashMap<>();

    MessageManager(LapisChat plugin) {
        this.plugin = plugin;
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        vaultChat = rsp.getProvider();
    }

    public void sendMessage(UUID sender, UUID receiver, String msg) {
        String format = plugin.getConfig().getString("PrivateMessageFormat");
        LapisMessageEvent event = new LapisMessageEvent(sender, receiver, msg, format);
        Bukkit.getPluginManager().callEvent(event);
        msg = applyFormat(sender, receiver, event.getFormat(), event.getMessage());
        sendMessageToUUID(sender, msg);
        sendMessageToUUID(receiver, msg);
        conversations.put(sender, receiver);
        conversations.put(receiver, sender);
    }

    public boolean sendReply(UUID sender, String msg) {
        if (!conversations.containsKey(sender)) {
            return false;
        }
        if (!conversations.get(sender).equals(plugin.consoleUUID) && !Bukkit.getOfflinePlayer(conversations.get(sender)).isOnline()) {
            conversations.remove(sender);
            return false;
        }
        sendMessage(sender, conversations.get(sender), msg);
        return true;
    }

    private void sendMessageToUUID(UUID receiver, String msg) {
        if (receiver.equals(plugin.consoleUUID)) {
            msg = plugin.getConfig().getBoolean("StripColorFromConsole") ? ChatColor.stripColor(msg) : msg;
            Bukkit.getLogger().info(msg);
        } else {
            Bukkit.getPlayer(receiver).sendMessage(msg);
        }
    }


    private String applyFormat(UUID sender, UUID receiver, String format, String msg) {
        if (!sender.equals(plugin.consoleUUID)) {
            String playerPrefix = vaultChat.getPlayerPrefix(Bukkit.getPlayer(sender));
            String playerSuffix = vaultChat.getPlayerSuffix(Bukkit.getPlayer(sender));
            format = format.replace("{SENDER_PREFIX}", playerPrefix);
            format = format.replace("{SENDER}", Bukkit.getPlayer(sender).getDisplayName());
            format = format.replace("{SENDER_SUFFIX}", playerSuffix);
        } else {
            format = format.replace("{SENDER_PREFIX}", "");
            format = format.replace("{SENDER}", "Console");
            format = format.replace("{SENDER_SUFFIX}", "");
        }
        if (!receiver.equals(plugin.consoleUUID)) {
            String playerPrefix = vaultChat.getPlayerPrefix(Bukkit.getPlayer(receiver));
            String playerSuffix = vaultChat.getPlayerSuffix(Bukkit.getPlayer(receiver));
            format = format.replace("{RECEIVER_PREFIX}", playerPrefix);
            format = format.replace("{RECEIVER}", Bukkit.getPlayer(receiver).getDisplayName());
            format = format.replace("{RECEIVER_SUFFIX}", playerSuffix);
        } else {
            format = format.replace("{RECEIVER_PREFIX}", "");
            format = format.replace("{RECEIVER}", "Console");
            format = format.replace("{RECEIVER_SUFFIX}", "");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && !sender.equals(plugin.consoleUUID)) {
            format = new PlaceholderAPIHook().format(format, Bukkit.getPlayer(sender));
        } else {
            format = ChatColor.translateAlternateColorCodes('&', format);
        }
        format = format.replaceAll(" {2}", " ");
        format = format.replace("{MESSAGE}", msg);
        return format;
    }
}
