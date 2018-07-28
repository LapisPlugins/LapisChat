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

public class ChatPlayer {

    private UUID uuid;
    private Channel mainChannel;
    private List<Channel> channels = new ArrayList<>();
    private List<Channel> bannedChannels = new ArrayList<>();

    public ChatPlayer(UUID uuid) {
        this.uuid = uuid;
        loadPlayerData();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Channel getMainChannel() {
        return mainChannel;
    }

    public void setMainChannel(Channel mainChannel) {
        this.mainChannel = mainChannel;
    }

    public void addChannel(Channel channel) {
        channel.addPlayer(this);
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channel.removePlayer(this);
        channels.remove(channel);
    }

    public void banFromChannel(Channel channel) {
        removeChannel(channel);
        bannedChannels.add(channel);
    }

    public boolean isBannedFromChannel(Channel channel) {
        return bannedChannels.contains(channel);
    }

    public void unBanFromChannel(Channel channel) {
        bannedChannels.remove(channel);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public boolean isInChannel(Channel channel) {
        return getChannels().contains(channel);
    }

    void sendMessage(String msg) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (op.isOnline()) {
            op.getPlayer().sendMessage(msg);
        }
    }

    public void savePlayerData() {
        File file = new File(LapisChat.getInstance().getDataFolder() + File.separator +
                "Players" + File.separator + uuid.toString() + ".yml");
        if (createFileIfNotExists(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> list = new ArrayList<>();
        for (Channel channel : channels) {
            list.add(channel.getName());
        }
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
        if (yaml.contains("Channel"))
            setMainChannel(LapisChat.getInstance().channelManager.getChannel(yaml.getString("Channel")));
        if (yaml.contains("Channels") && !yaml.getStringList("Channels").isEmpty()) {
            for (String name : yaml.getStringList("Channels")) {
                addChannel(LapisChat.getInstance().channelManager.getChannel(name));
            }
        }
    }

    private boolean createFileIfNotExists(File file) {
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
