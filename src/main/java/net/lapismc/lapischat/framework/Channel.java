package net.lapismc.lapischat.framework;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.hooks.DiscordSRVHook;
import net.lapismc.lapischat.hooks.PlaceholderAPIHook;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a chat channel and handles every interaction with this channel
 */
public abstract class Channel {

    private final String name;
    private final String prefix;
    private final Permission perm;
    private final String shortName;
    private final Permission setMainPerm;
    private final Permission autoJoinPerm;
    /**
     * List of players who are currently in this channel
     */
    protected Set<ChatPlayer> players = new HashSet<>();
    private String format;
    private final Chat vaultChat;

    /**
     * Use this constructor if you wish to use the default format
     *
     * @param name      The name of the channel
     * @param shortName The name that can be used to join the channel
     * @param prefix    The prefix shown in chat
     * @param perm      The permission required to join the channel
     */
    protected Channel(String name, String shortName, String prefix, Permission perm) {
        this.name = name;
        this.shortName = shortName;
        this.prefix = prefix;
        this.format = getDefaultFormat();
        this.perm = perm;
        this.perm.setDefault(PermissionDefault.FALSE);
        this.autoJoinPerm = new Permission("LapisChat.AutoJoin." + name, PermissionDefault.FALSE);
        this.setMainPerm = new Permission("LapisChat.SetMain." + name, PermissionDefault.FALSE);
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        vaultChat = rsp.getProvider();
    }

    /**
     * Use this constructor if you wish to set your own format for this channel
     *
     * @param name      The name of the channel
     * @param shortName The name that can be used to join the channel
     * @param prefix    The prefix shown in chat
     * @param perm      The permission required to join the channel
     * @param format    The format that you wish to use for this channel
     */
    public Channel(String name, String shortName, String prefix, Permission perm, String format) {
        this(name, shortName, prefix, perm);
        this.format = format;
    }

    private String getDefaultFormat() {
        return LapisChat.getInstance().getConfig().getString("DefaultFormat");
    }

    /**
     * @return Returns the current format for this channel
     */
    public String getFormat() {
        if (format == null) {
            return getDefaultFormat();
        }
        return format;
    }

    /**
     * This method wil call {@link #forceAddPlayer(ChatPlayer)}, it is primarily here to be overridden for things like
     * password checks
     *
     * @param p The player to add to this channel
     */
    protected void addPlayer(ChatPlayer p) {
        forceAddPlayer(p);
    }

    /**
     * This should only be called if you want to add the player based on a permission or other system rather then a
     * player joining a channel of their own accord
     *
     * @param p The player to add to this channel
     */
    protected void forceAddPlayer(ChatPlayer p) {
        players.add(p);
    }

    /**
     * Removes the player from the channels player list meaning this player wont receive messages from this channel
     * NOTE: this doesn't stop the player sending messages to this channel, it only stops them seeing messages from it
     *
     * @param p The player to be removed from this channel
     */
    void removePlayer(ChatPlayer p) {
        players.remove(p);
    }

    /**
     * Returns the players that will receive a message if it was sent by p
     * By default this returns the list of players in the channel
     * this is overridden by other channels to specify distance or faction association
     *
     * @param p the player sending a message to the channel
     * @return Returns a list of ChatPlayers that will receive the message
     */
    protected Set<ChatPlayer> getRecipients(ChatPlayer p) {
        return players;
    }

    /**
     * @return Returns the full name of the channel
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the short name of the channel
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return Returns the prefix of the channel
     */
    private String getPrefix() {
        return prefix;
    }

    /**
     * @return Returns the permission required to join this channel
     */
    public Permission getPermission() {
        return perm;
    }

    /**
     * @return Returns the permission required to be added to this channel on first join
     */
    public Permission getAutoJoinPermission() {
        return autoJoinPerm;
    }

    /**
     * @return Returns the permission required to have this channel set as your main channel on every join
     */
    public Permission getSetMainPermission() {
        return setMainPerm;
    }

    /**
     * Sends a message to the recipients of the channel
     *
     * @param from   The {@link ChatPlayer} that has sent the message
     * @param msg    The message being sent
     * @param format The finalised format
     */
    public void sendMessage(ChatPlayer from, String msg, String format) {
        //Send to players
        msg = format(from, msg, format);
        for (ChatPlayer p : getRecipients(from)) {
            p.sendMessage(msg);
        }
        //send to console
        String consoleMsg;
        if (LapisChat.getInstance().getConfig().getBoolean("StripColorFromConsole")) {
            consoleMsg = ChatColor.stripColor(msg);
        } else {
            consoleMsg = msg;
        }
        //Only send the console message if HideChatFromConsole is false
        if (!LapisChat.getInstance().getConfig().getBoolean("HideChatFromConsole"))
            Bukkit.getServer().getLogger().info(consoleMsg);
        //Send to discord
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV") && LapisChat.getInstance().getConfig().getStringList("ChannelsForDiscord").contains(getName())) {
            DiscordSRVHook.logToDiscord(from.getPlayer(), msg);
        }
    }

    /**
     * @param from   The {@link ChatPlayer} sending the message
     * @param msg    The message being sent
     * @param format The format being used
     * @return Returns the formatted message to be sent to recipients
     */
    protected String applyDefaultFormat(ChatPlayer from, String msg, String format) {
        String finalMessage = format;
        String playerPrefix = vaultChat.getPlayerPrefix(from.getPlayer());
        String playerSuffix = vaultChat.getPlayerSuffix(from.getPlayer());
        finalMessage = finalMessage.replace("{CHANNEL_PREFIX}", getPrefix());
        finalMessage = finalMessage.replace("{PREFIX}", playerPrefix);
        finalMessage = finalMessage.replace("{NAME}", from.getPlayer().getDisplayName());
        finalMessage = finalMessage.replace("{SUFFIX}", playerSuffix);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            finalMessage = new PlaceholderAPIHook().format(finalMessage, from.getOfflinePlayer());
        }
        finalMessage = ChatColor.translateAlternateColorCodes('&', finalMessage);
        finalMessage = finalMessage.replaceAll(" {2}", " ");
        finalMessage = finalMessage.replace("{MESSAGE}", msg);
        return finalMessage;
    }

    /**
     * This must be implemented by all channels, by default it should return {@link #applyDefaultFormat(ChatPlayer, String, String)}
     * This is can be used to apply extra per channel formatting tags
     *
     * @param from   The {@link ChatPlayer} sending the message
     * @param msg    The message being sent
     * @param format The raw format to be applied
     * @return Returns the formatted message for this channel
     */
    protected abstract String format(ChatPlayer from, String msg, String format);
}
