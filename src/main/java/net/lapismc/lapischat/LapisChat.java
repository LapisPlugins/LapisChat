package net.lapismc.lapischat;

import net.lapismc.lapischat.channels.Global;
import net.lapismc.lapischat.channels.Local;
import net.lapismc.lapischat.commands.*;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapiscore.utils.LapisCoreFileWatcher;
import net.lapismc.lapiscore.utils.LapisUpdater;
import net.lapismc.lapiscore.utils.Metrics;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public final class LapisChat extends LapisCorePlugin {

    private static LapisChat instance;
    private final HashMap<UUID, ChatPlayer> players = new HashMap<>();
    public ChannelManager channelManager;
    public MessageManager messageManager;
    public UUID consoleUUID = UUID.nameUUIDFromBytes("Console".getBytes());
    private LapisCoreFileWatcher fileWatcher;

    public static LapisChat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerConfiguration(new LapisCoreConfiguration(this, 4, 3));
        Bukkit.getScheduler().runTaskAsynchronously(this, this::updateCheck);
        channelManager = new ChannelManager();
        channelManager.addChannel(new Global());
        channelManager.addChannel(new Local());
        messageManager = new MessageManager(this);
        new LapisChatListeners(this);
        registerCommands();
        fileWatcher = new LapisCoreFileWatcher(this);
        new Metrics(this);
        getLogger().info(getName() + " v" + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        for (ChatPlayer player : players.values()) {
            player.savePlayerData();
        }
        fileWatcher.stop();
    }

    private void updateCheck() {
        LapisUpdater lp = new LapisUpdater(this, "LapisChat", "LapisPlugins", "LapisChat", "master");
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
        if (players.get(uuid) == null) {
            players.put(uuid, new ChatPlayer(uuid));
        }
        return players.get(uuid);
    }

    private void registerCommands() {
        new LapisChatChannel(this);
        new LapisChatChannelList(this);
        new LapisChatCommand(this);
        new LapisChatPrivateMessage(this);
        new LapisChatReply(this);
    }
}
