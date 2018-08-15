package net.lapismc.lapischat;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class MessageManager {

    private LapisChat plugin;
    private HashMap<UUID, UUID> conversations = new HashMap<>();

    MessageManager(LapisChat plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(UUID sender, UUID receiver, String msg) {
        //TODO add format and apply it
        conversations.put(sender, receiver);
        sendMessageToUUID(receiver, msg);
    }

    public boolean sendReply(UUID sender, String msg) {
        if (!conversations.containsKey(sender)) {
            return false;
        }
        if (!Bukkit.getOfflinePlayer(conversations.get(sender)).isOnline()) {
            conversations.remove(sender);
            return false;
        }
        sendMessage(sender, conversations.get(sender), msg);
        return true;
    }

    private void sendMessageToUUID(UUID receiver, String msg) {
        if (receiver.equals(plugin.consoleUUID)) {
            Bukkit.getLogger().info(msg);
        } else {
            Bukkit.getPlayer(receiver).sendMessage(msg);
        }
    }

}
