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

    public Channel getChannel() {
        return channel;
    }

    public ChatPlayer getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void applyFormat(String placeholder, String value) {
        format = format.replace(placeholder, value);
    }

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
