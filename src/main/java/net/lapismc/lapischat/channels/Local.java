package net.lapismc.lapischat.channels;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.HashSet;
import java.util.Set;

public class Local extends Channel {

    public Local() {
        super("Local", "l", LapisChat.getInstance().getConfig().getString("Channels.Local.Prefix"), new Permission("LapisChat.Local"));
    }

    @Override
    protected String format(ChatPlayer from, String msg, String format) {
        return applyDefaultFormat(from, msg, format);
    }

    @Override
    public Set<ChatPlayer> getRecipients(ChatPlayer p) {
        if (p.getOfflinePlayer().isOnline()) {
            Player player = p.getPlayer();
            Set<ChatPlayer> nearby = new HashSet<>();
            double range = LapisChat.getInstance().getConfig().getInt("Channels.Local.Range");
            for (Entity e : player.getNearbyEntities(range, range, range)) {
                if (e instanceof Player) {
                    nearby.add(LapisChat.getInstance().getPlayer(e.getUniqueId()));
                }
            }
            nearby.add(p);
            return nearby;
        }
        return new HashSet<>();
    }
}
