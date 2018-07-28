package net.lapismc.lapischat.channels;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class Local extends Channel {

    public Local() {
        super("Local", "l", LapisChat.getInstance().getConfig().getString("Channels.Local.Prefix"), new Permission("LapisChat.Local"));
    }

    @Override
    protected String format(ChatPlayer from, String msg) {
        return applyDefaultFormat(from, msg);
    }

    @Override
    public List<ChatPlayer> getRecipients(ChatPlayer p) {
        if (p.getOfflinePlayer().isOnline()) {
            Player player = p.getPlayer();
            ArrayList<ChatPlayer> nearby = new ArrayList<>();
            double range = LapisChat.getInstance().getConfig().getInt("Channels.Local.Range");
            for (Entity e : player.getNearbyEntities(range, range, range)) {
                if (e instanceof Player) {
                    nearby.add(LapisChat.getInstance().getPlayer(e.getUniqueId()));
                }
            }
            nearby.add(p);
            return nearby;
        }
        return new ArrayList<>();
    }
}
