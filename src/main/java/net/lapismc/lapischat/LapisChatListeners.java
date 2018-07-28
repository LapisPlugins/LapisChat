package net.lapismc.lapischat;

import net.lapismc.lapischat.events.LapisChatEvent;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LapisChatListeners implements Listener {

    private LapisChat plugin;
    //TODO deal with adding players as they join and removing when they leave

    LapisChatListeners(LapisChat plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        ChatPlayer player = plugin.getPlayer(e.getPlayer().getUniqueId());
        Channel channel = player.getMainChannel();
        LapisChatEvent event = new LapisChatEvent(channel, player, e.getMessage());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            channel.sendMessage(player, event.getMessage());
        }
    }

}
