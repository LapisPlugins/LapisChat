package net.lapismc.lapischat.commands;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapischat.utils.LapisChatCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LapisChatChannelList extends LapisChatCommand {

    public LapisChatChannelList(LapisChat plugin) {
        super(plugin, "channellist", "List all available channels", new ArrayList<>(Collections.singletonList("chlist")));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (isNotPlayer(sender, "Error.MustBePlayer")) return;
        ChatPlayer p = plugin.getPlayer(((Player) sender).getUniqueId());
        List<Channel> channels = plugin.channelManager.getChannels();
        List<Channel> availableChannels = new ArrayList<>();
        for (Channel ch : channels) {
            if (p.getPlayer().hasPermission(ch.getPermission()) && !p.isBannedFromChannel(ch)) {
                availableChannels.add(ch);
            }
        }
        if (availableChannels.isEmpty()) {
            p.sendMessage(plugin.config.getMessage("Channel.ChannelListEmpty"));
            return;
        }
        p.sendMessage(plugin.config.getMessage("Channel.ChannelList"));
        for (Channel ch : availableChannels) {
            p.sendMessage(ch.getName() + " (" + ch.getShortName() + ")");
        }
    }
}
