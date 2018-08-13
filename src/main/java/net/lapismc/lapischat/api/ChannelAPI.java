package net.lapismc.lapischat.api;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("unused")
public class ChannelAPI {

    public ChannelAPI(JavaPlugin plugin) {
        LapisChat.getInstance().getLogger().info("Plugin " + plugin.getName() + " has hooked into the Channel API");
    }

    /**
     * Adds a channel to the server
     *
     * @param channel The channel you wish to add
     */
    public void addChannel(Channel channel) {
        LapisChat.getInstance().channelManager.addChannel(channel);
    }

    /**
     * Removes a channel from the server
     *
     * @param channel The channel you wish to remove
     */
    public void removeChannel(Channel channel) {
        LapisChat.getInstance().channelManager.removeChannel(channel);
    }

    /**
     * @param name The name of the channel you wish to get
     * @return Returns the {@link Channel} object that has the name provided
     */
    public Channel getChannel(String name) {
        return LapisChat.getInstance().channelManager.getChannel(name);
    }

    /**
     * @return Returns a list of {@link Channel}s currently loaded on the server
     */
    public List<Channel> getChannels() {
        return LapisChat.getInstance().channelManager.getChannels();
    }

}
