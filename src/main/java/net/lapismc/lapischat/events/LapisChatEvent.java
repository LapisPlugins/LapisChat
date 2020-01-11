package net.lapismc.lapischat.events;

import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapiscore.LapisCoreCancellableEvent;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LapisChatEvent extends LapisCoreCancellableEvent {

    private Channel channel;
    private ChatPlayer sender;
    private String message;
    private String format;

    public LapisChatEvent(Channel channel, ChatPlayer sender, String message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        format = channel.getFormat();
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
}
