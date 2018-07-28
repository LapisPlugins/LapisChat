package net.lapismc.lapischat.framework;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.utils.PlaceholderAPIHook;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class Channel {

    private String name;
    private String prefix;
    private String format;
    private Chat vaultChat;
    private Permission perm;
    private List<ChatPlayer> players = new ArrayList<>();

    public Channel(String name, String prefix, Permission perm) {
        this.name = name;
        this.prefix = prefix;
        this.format = getDefaultFormat();
        this.perm = perm;
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        vaultChat = rsp.getProvider();
    }

    public Channel(String name, String prefix, Permission perm, String format) {
        this.name = name;
        this.prefix = prefix;
        this.format = format;
        this.perm = perm;
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        vaultChat = rsp.getProvider();
    }

    private String getDefaultFormat() {
        return LapisChat.getInstance().getConfig().getString("DefaultFormat");
    }

    void addPlayer(ChatPlayer p) {
        players.add(p);
    }

    void removePlayer(ChatPlayer p) {
        players.remove(p);
    }

    public List<ChatPlayer> getRecipients(ChatPlayer p) {
        return players;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Permission getPermission() {
        return perm;
    }

    public void sendMessage(ChatPlayer from, String msg) {
        msg = format(from, msg);
        for (ChatPlayer p : getRecipients(from)) {
            p.sendMessage(msg);
        }
    }

    protected String applyDefaultFormat(ChatPlayer from, String msg) {
        String finalMessage = format;
        String playerPrefix = vaultChat.getPlayerPrefix(from.getPlayer());
        String playerSuffix = vaultChat.getPlayerSuffix(from.getPlayer());
        finalMessage = finalMessage.replace("{CHANNEL_PREFIX}", getPrefix());
        finalMessage = finalMessage.replace("{PREFIX}", playerPrefix);
        finalMessage = finalMessage.replace("{NAME}", from.getPlayer().getDisplayName());
        finalMessage = finalMessage.replace("{SUFFIX}", playerSuffix);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            finalMessage = new PlaceholderAPIHook().format(format, from.getOfflinePlayer());
        } else {
            finalMessage = ChatColor.translateAlternateColorCodes('&', finalMessage);
        }
        finalMessage = finalMessage.replaceAll(" {2}", " ");
        finalMessage = finalMessage.replace("{MESSAGE}", msg);
        return finalMessage;
    }

    protected abstract String format(ChatPlayer from, String msg);
}
