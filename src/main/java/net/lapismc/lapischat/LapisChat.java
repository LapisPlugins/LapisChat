package net.lapismc.lapischat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.lapismc.lapischat.channels.Global;
import net.lapismc.lapischat.channels.Local;
import net.lapismc.lapischat.commands.LapisChatChannel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapischat.utils.LapisUpdater;
import net.lapismc.lapischat.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class LapisChat extends JavaPlugin {

    private static LapisChat instance;
    public ChannelManager channelManager;
    public LapisChatConfiguration config;
    String primaryColor = ChatColor.WHITE.toString();
    String secondaryColor = ChatColor.AQUA.toString();
    private Cache<UUID, ChatPlayer> players = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public static LapisChat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new LapisChatConfiguration(this);
        Bukkit.getScheduler().runTaskAsynchronously(this, this::updateCheck);
        channelManager = new ChannelManager();
        channelManager.addChannel(new Global());
        channelManager.addChannel(new Local());
        new LapisChatListeners(this);
        registerCommands();
        new LapisChatFileWatcher(this);
        new Metrics(this);
        getLogger().info(getName() + " v" + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        for (ChatPlayer player : players.asMap().values()) {
            player.savePlayerData();
        }
    }

    private void updateCheck() {
        LapisUpdater lp = new LapisUpdater(this);
        if (lp.checkUpdate()) {
            if (getConfig().getBoolean("Update.Download")) {
                getLogger().info("Update has been found and will be installed on the next restart");
                lp.downloadUpdate();
            } else if (getConfig().getBoolean("Update.Notify")) {
                getLogger().info("An update is available on spigot!");
            }
        }
    }

    public ChatPlayer getPlayer(UUID uuid) {
        if (players.getIfPresent(uuid) == null) {
            players.put(uuid, new ChatPlayer(uuid));
        }
        return players.getIfPresent(uuid);
    }

    private void registerCommands() {
        new LapisChatChannel(this);
    }
}
