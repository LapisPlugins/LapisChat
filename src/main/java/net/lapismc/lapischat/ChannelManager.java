package net.lapismc.lapischat;

import net.lapismc.lapischat.framework.Channel;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    private final List<Channel> channels = new ArrayList<>();

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
        for (Channel channel : getChannels()) {
            if (channel.getName().equalsIgnoreCase(name) || channel.getShortName().equalsIgnoreCase(name)) {
                return channel;
            }
        }
        return null;
    }

}
