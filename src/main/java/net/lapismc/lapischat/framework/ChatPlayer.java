package net.lapismc.lapischat.framework;

import net.lapismc.lapischat.LapisChat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class represents a player using LapisChat
 */
public class ChatPlayer {

    private final UUID uuid;
    private Channel mainChannel;
    private final List<Channel> channels = new ArrayList<>();
    private final List<Channel> bannedChannels = new ArrayList<>();

    /**
     * @param uuid The Unique ID of the player you wish to access
     */
    public ChatPlayer(UUID uuid) {
        this.uuid = uuid;
        loadPlayerData();
    }

    /**
     * @return Returns the {@link OfflinePlayer} object for this player
     */
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    /**
     * @return Returns the {@link Player} object for this player
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * @return Returns the {@link Channel} that the player is currently sending messages to
     */
    public Channel getMainChannel() {
        return mainChannel;
    }

    /**
     * Sets the channel the player will send messages to
     *
     * @param mainChannel The channel you wish to have the player use
     */
    public void setMainChannel(Channel mainChannel) {
        this.mainChannel = mainChannel;
    }

    /**
     * Adds a player to a channel, this means the player will receive messages from this channel
     * This may involve entering a password, if you wish to add a player regardless of protections use {@link #forceAddChannel(Channel)}
     *
     * @param channel The channel you wish to add
     */
    public void addChannel(Channel channel) {
        forceAddChannel(channel);
    }

    /**
     * Forces a player into a channel, this overrides things like channel passwords
     *
     * @param channel The channel you wish to add
     */
    public void forceAddChannel(Channel channel) {
        channel.forceAddPlayer(this);
        if (!channels.contains(channel))
            channels.add(channel);
    }

    /**
     * Removing a player form a channel means they will no longer receive any messages sent in that channel
     * MainChannel will be set to null if the channel you are removing is currently the main channel
     *
     * @param channel The channel you want the player to be removed from
     */
    public void removeChannel(Channel channel) {
        if (channel.equals(mainChannel)) {
            mainChannel = null;
        }
        channel.removePlayer(this);
        channels.remove(channel);
    }

    /**
     * This will remove the player from the channel and stop them from joining until they are unbanned
     *
     * @param channel The channel you wish to ban this player from joining
     */
    public void banFromChannel(Channel channel) {
        removeChannel(channel);
        bannedChannels.add(channel);
    }

    /**
     * @param channel The channel you wish to check
     * @return Returns true if the player is currently banned from joining this channel
     */
    public boolean isBannedFromChannel(Channel channel) {
        return bannedChannels.contains(channel);
    }

    /**
     * Un-bans a player from the channel, this will allow them to join the channel again
     *
     * @param channel The channel you wish to unban the player from
     */
    public void unBanFromChannel(Channel channel) {
        bannedChannels.remove(channel);
    }

    /**
     * @return Returns a list of {@link Channel}'s that the player is in
     */
    public List<Channel> getChannels() {
        return channels;
    }

    /**
     * @param channel The channel you wish to check
     * @return Returns true if the player is in the channel provided
     */
    public boolean isInChannel(Channel channel) {
        return getChannels().contains(channel);
    }

    /**
     * Send the string as a message to the player
     * This is used by channels when they are sending messages
     * This method will not format the message, it just sends it to the player
     *
     * @param msg The string to be sent to the player
     */
    public void sendMessage(String msg) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (op.isOnline()) {
            op.getPlayer().sendMessage(msg);
        }
    }

    /**
     * Saves the players channel data to their configuration file
     * This includes the channels they are in, the channel they are chatting in and any banned channels
     */
    public void savePlayerData() {
        File file = new File(LapisChat.getInstance().getDataFolder() + File.separator +
                "Players" + File.separator + uuid.toString() + ".yml");
        if (createFileIfNotExists(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> list = new ArrayList<>();
        for (Channel channel : channels) {
            list.add(channel.getName());
        }
        if (getMainChannel() != null)
            yaml.set("Channel", getMainChannel().getName());
        yaml.set("Channels", list);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerData() {
        File file = new File(LapisChat.getInstance().getDataFolder() + File.separator +
                "Players" + File.separator + uuid.toString() + ".yml");
        if (createFileIfNotExists(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (yaml.contains("Channel")) {
            Channel channel = LapisChat.getInstance().channelManager.getChannel(yaml.getString("Channel"));
            if (channel != null)
                setMainChannel(channel);
        }
        if (yaml.contains("Channels") && !yaml.getStringList("Channels").isEmpty()) {
            for (String name : yaml.getStringList("Channels")) {
                Channel channel = LapisChat.getInstance().channelManager.getChannel(name);
                if (channel != null)
                    forceAddChannel(channel);
            }
        }
    }

    private boolean createFileIfNotExists(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }
}
