package net.lapismc.lapischat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.lapismc.lapischat.api.ChannelAPI;
import net.lapismc.lapischat.channels.Global;
import net.lapismc.lapischat.channels.Local;
import net.lapismc.lapischat.commands.LapisChatChannel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class LapisChat extends JavaPlugin {

    private static LapisChat instance;
    public ChannelManager channelManager;
    public LapisChatConfiguration config;
    String primaryColor = ChatColor.WHITE.toString();
    String secondaryColor = ChatColor.AQUA.toString();
    private Cache<UUID, ChatPlayer> players = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public static LapisChat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new LapisChatConfiguration(this);
        channelManager = new ChannelManager(this);
        new ChannelAPI(this);
        channelManager.addChannel(new Global());
        channelManager.addChannel(new Local());
        new LapisChatListeners(this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        for (ChatPlayer player : players.asMap().values()) {
            player.savePlayerData();
        }
    }

    public ChatPlayer getPlayer(UUID uuid) {
        if (players.getIfPresent(uuid) == null) {
            players.put(uuid, new ChatPlayer(uuid));
        }
        return players.getIfPresent(uuid);
    }

    private void registerCommands() {
        new LapisChatChannel(this);
    }
}
