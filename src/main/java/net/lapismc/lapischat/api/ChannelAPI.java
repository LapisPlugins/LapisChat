package net.lapismc.lapischat.api;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import org.bukkit.plugin.java.JavaPlugin;

public class ChannelAPI {

    public ChannelAPI(JavaPlugin plugin) {
        LapisChat.getInstance().getLogger().info("Plugin " + plugin.getName() + " has hooked into the Channel API");
    }

    public void addChannel(Channel channel) {
        LapisChat.getInstance().channelManager.addChannel(channel);
    }

    public void removeChannel(Channel channel) {
        LapisChat.getInstance().channelManager.removeChannel(channel);
    }

    public Channel getChannel(String name) {
        return LapisChat.getInstance().channelManager.getChannel(name);
    }

}
