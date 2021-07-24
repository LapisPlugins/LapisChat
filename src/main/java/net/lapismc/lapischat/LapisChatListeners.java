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
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.concurrent.TimeUnit;

class LapisChatListeners implements Listener {

    private final LapisChat plugin;
    private final Cache<ChatPlayer, String> lastMessage;

    LapisChatListeners(LapisChat plugin) {
        this.plugin = plugin;
        lastMessage = CacheBuilder.newBuilder()
                .expireAfterWrite(plugin.getConfig().getInt("RateLimit"), TimeUnit.MILLISECONDS).build();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        File file = new File(plugin.getDataFolder() + File.separator + "Players"
                + File.separator + e.getPlayer().getUniqueId() + ".yml");
        if (!file.exists()) {
            for (Channel channel : plugin.channelManager.getChannels()) {
                if (e.getPlayer().hasPermission(channel.getAutoJoinPermission())) {
                    plugin.getPlayer(e.getPlayer().getUniqueId()).forceAddChannel(channel);
                }
            }
        }
        for (Channel channel : plugin.channelManager.getChannels()) {
            if (e.getPlayer().hasPermission(channel.getSetMainPermission())) {
                if (!plugin.getPlayer(e.getPlayer().getUniqueId()).getChannels().contains(channel)) {
                    plugin.getPlayer(e.getPlayer().getUniqueId()).forceAddChannel(channel);
                }
                plugin.getPlayer(e.getPlayer().getUniqueId()).setMainChannel(channel);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        //Disregard cancelled chat events since this means another plugin stopped it
        if (e.isCancelled())
            return;
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
        Bukkit.getScheduler().runTask(plugin, () -> {
            LapisChatEvent event = new LapisChatEvent(channel, player, e.getMessage());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                channel.sendMessage(player, event.getMessage(), event.getFormat());
            }
        });
    }

}
