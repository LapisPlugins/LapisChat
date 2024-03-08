package net.lapismc.lapischat.commands;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapischat.utils.LapisChatCommand;
import net.lapismc.lapiscore.utils.LapisItemBuilder;
import net.lapismc.lapisui.menu.SinglePage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (plugin.getConfig().getBoolean("ChannelListGUI")) {
            new ChannelGUI(p, availableChannels).showTo(p.getPlayer());
        } else {
            p.sendMessage(plugin.config.getMessage("Channel.ChannelList"));
            for (Channel ch : availableChannels) {
                p.sendMessage(ch.getName() + " (" + ch.getShortName() + ")");
            }
        }
    }

    private class ChannelGUI extends SinglePage<Channel> {

        ChatPlayer player;
        LapisItemBuilder.WoolColor joinedColor = LapisItemBuilder.WoolColor.GREEN;
        String joinedString = "You are listening to this channel";
        LapisItemBuilder.WoolColor mainColor = LapisItemBuilder.WoolColor.YELLOW;
        String mainString = "This is the channel you are talking in";
        LapisItemBuilder.WoolColor notJoinedColor = LapisItemBuilder.WoolColor.RED;
        String notJoinedString = "You are not in this channel";
        List<String> lore = new ArrayList<>(Arrays.asList("Left click to join/leave channel", "Right click to set as main"));


        public ChannelGUI(ChatPlayer player, List<Channel> list) {
            super(list);
            setTitle("Chat Channel Manager");
            this.player = player;
        }

        @Override
        protected ItemStack toItemStack(Channel channel) {
            LapisItemBuilder.WoolColor color = player.isInChannel(channel) ?
                    channel.equals(player.getMainChannel()) ? mainColor : joinedColor : notJoinedColor;
            String firstLoreLine = player.isInChannel(channel) ?
                    channel.equals(player.getMainChannel()) ? mainString : joinedString : notJoinedString;
            List<String> fullLore = new ArrayList<>();
            fullLore.add(firstLoreLine);
            fullLore.addAll(lore);
            return new LapisItemBuilder(Material.WHITE_WOOL).setWoolColor(color).setName(channel.getName())
                    .setLore(fullLore.toArray(new String[0])).build();
        }

        @Override
        protected void onItemClick(Player p, Channel channel, ClickType clickType) {
            ChatPlayer chatPlayer = plugin.getPlayer(p.getUniqueId());
            boolean inChannel = chatPlayer.isInChannel(channel);
            boolean mainChannel = channel.equals(chatPlayer.getMainChannel());
            if (clickType.equals(ClickType.LEFT)) {
                if (inChannel) {
                    chatPlayer.removeChannel(channel);
                } else {
                    chatPlayer.addChannel(channel);
                }
            } else if (clickType.equals(ClickType.RIGHT)) {
                if (mainChannel) {
                    chatPlayer.setMainChannel(null);
                } else {
                    chatPlayer.setMainChannel(channel);
                }
            }
            updateCachedItems();
            renderItems();
        }
    }
}
