package net.lapismc.lapischat;

import net.lapismc.lapischat.channels.Global;
import net.lapismc.lapischat.channels.Local;
import net.lapismc.lapischat.commands.LapisChatChannel;
import net.lapismc.lapischat.commands.LapisChatCommand;
import net.lapismc.lapischat.commands.LapisChatPrivateMessage;
import net.lapismc.lapischat.commands.LapisChatReply;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapischat.utils.LapisUpdater;
import net.lapismc.lapischat.utils.Metrics;
import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapiscore.utils.LapisCoreFileWatcher;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public final class LapisChat extends LapisCorePlugin {

    private static LapisChat instance;
    public ChannelManager channelManager;
    public MessageManager messageManager;
    private HashMap<UUID, ChatPlayer> players = new HashMap<>();
    public UUID consoleUUID = UUID.nameUUIDFromBytes("Console".getBytes());

    public static LapisChat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerConfiguration(new LapisCoreConfiguration(this, 3, 3));
        Bukkit.getScheduler().runTaskAsynchronously(this, this::updateCheck);
        channelManager = new ChannelManager();
        channelManager.addChannel(new Global());
        channelManager.addChannel(new Local());
        messageManager = new MessageManager(this);
        new LapisChatListeners(this);
        registerCommands();
        new LapisCoreFileWatcher(this);
        new Metrics(this);
        getLogger().info(getName() + " v" + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        for (ChatPlayer player : players.values()) {
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
        if (players.get(uuid) == null) {
            players.put(uuid, new ChatPlayer(uuid));
        }
        return players.get(uuid);
    }

    private void registerCommands() {
        new LapisChatChannel(this);
        new LapisChatCommand(this);
        new LapisChatPrivateMessage(this);
        new LapisChatReply(this);
    }
}
