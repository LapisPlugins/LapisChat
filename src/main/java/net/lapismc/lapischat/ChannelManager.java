package net.lapismc.lapischat;

import net.lapismc.lapischat.framework.Channel;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    private LapisChat plguin;
    private List<Channel> channels = new ArrayList<>();

    ChannelManager(LapisChat plugin) {
        this.plguin = plugin;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public boolean doesChannelExist(String name) {
        return getChannel(name) != null;
    }

    public Channel getChannel(String name) {
        for (Channel channel : channels) {
            if (channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }
        return null;
    }

}
