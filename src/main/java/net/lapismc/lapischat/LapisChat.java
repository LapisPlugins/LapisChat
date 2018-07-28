package net.lapismc.lapischat;

import net.lapismc.lapischat.api.ChannelAPI;
import net.lapismc.lapischat.channels.Global;
import net.lapismc.lapischat.channels.Local;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class LapisChat extends JavaPlugin {

    private static LapisChat instance;
    public ChannelManager channelManager;
    private HashMap<UUID, ChatPlayer> players = new HashMap<>();

    public static LapisChat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        channelManager = new ChannelManager(this);
        new ChannelAPI(this);
        channelManager.addChannel(new Global());
        channelManager.addChannel(new Local());
        new LapisChatListeners(this);
    }

    @Override
    public void onDisable() {
        for (ChatPlayer player : players.values()) {
            player.savePlayerData();
        }
    }

    public ChatPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new ChatPlayer(uuid));
        }
        return players.get(uuid);
    }
}
