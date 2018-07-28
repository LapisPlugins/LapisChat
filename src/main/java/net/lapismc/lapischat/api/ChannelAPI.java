package net.lapismc.lapischat.api;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import org.bukkit.plugin.java.JavaPlugin;

public class ChannelAPI {

    private static LapisChat plugin;

    public ChannelAPI(LapisChat plugin) {
        ChannelAPI.plugin = plugin;
    }

    public ChannelAPI(JavaPlugin plugin) {
        ChannelAPI.plugin.getLogger().info("Plugin " + plugin.getName() + " has hooked into the Channel API");
    }

    public void addChannel(Channel channel) {
        plugin.channelManager.addChannel(channel);
    }

    public Channel getChannel(String name) {
        return plugin.channelManager.getChannel(name);
    }

}
