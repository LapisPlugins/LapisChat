package net.lapismc.lapischat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.lapismc.lapischat.events.LapisChatEvent;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.TimeUnit;

public class LapisChatListeners implements Listener {

    private LapisChat plugin;
    private Cache<ChatPlayer, String> lastMessage;

    LapisChatListeners(LapisChat plugin) {
        this.plugin = plugin;
        lastMessage = CacheBuilder.newBuilder()
                .expireAfterWrite(plugin.getConfig().getInt("RateLimit"), TimeUnit.MILLISECONDS).build();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        ChatPlayer player = plugin.getPlayer(e.getPlayer().getUniqueId());
        if (lastMessage.getIfPresent(player) != null) {
            player.getPlayer().sendMessage(plugin.config.getMessage("Error.RateLimited"));
            return;
        }
        lastMessage.put(player, "");
        Channel channel = player.getMainChannel();
        if (channel == null) {
            player.getPlayer().sendMessage(plugin.config.getMessage("Error.NotInChannel"));
            return;
        }
        LapisChatEvent event = new LapisChatEvent(channel, player, e.getMessage());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            channel.sendMessage(player, event.getMessage(), event.getFormat());
        }
    }

}
