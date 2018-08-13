package net.lapismc.lapischat.events;

import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LapisChatEvent extends Event implements Cancellable {

    public static HandlerList handlers = new HandlerList();
    private Channel channel;
    private ChatPlayer sender;
    private String message;
    private String format;
    private boolean cancelled;

    public LapisChatEvent(Channel channel, ChatPlayer sender, String message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        format = channel.getFormat();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return Returns the {@link Channel} that the message is being sent to
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return Returns the {@link ChatPlayer} object for the player who has sent the message
     */
    public ChatPlayer getSender() {
        return sender;
    }

    /**
     * @return The raw message being sent by the player, this has no formatting tags
     */
    public String getMessage() {
        return message;
    }

    /**
     * Applies a format to a formatting tag
     *
     * @param placeholder The tag you wish to replace
     * @param value       The value that should be used in place of the tag
     */
    public void applyFormat(String placeholder, String value) {
        format = format.replace(placeholder, value);
    }

    /**
     * @return Returns the current format, This will not be the final format sent to players
     */
    public String getFormat() {
        return format;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
