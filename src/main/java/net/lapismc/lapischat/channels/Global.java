package net.lapismc.lapischat.channels;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.permissions.Permission;

public class Global extends Channel {

    public Global() {
        super("Global", "g", LapisChat.getInstance().getConfig().getString("Channels.Global.Prefix"), new Permission("LapisChat.Global"));
    }

    @Override
    public String format(ChatPlayer from, String msg) {
        return applyDefaultFormat(from, msg);
    }
}
