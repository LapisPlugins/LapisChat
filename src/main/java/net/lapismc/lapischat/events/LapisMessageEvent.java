package net.lapismc.lapischat.events;

import net.lapismc.lapiscore.events.LapisCoreCancellableEvent;

import java.util.UUID;

/**
 * This event is fired when a player sends a private message to another player on the server
 */
public class LapisMessageEvent extends LapisCoreCancellableEvent {

    private final UUID sender;
    private final UUID receiver;
    private final String message;
    private String format;

    /**
     * Prepare to send a message
     *
     * @param sender   The sender of the message
     * @param receiver The recipient of the message
     * @param message  The message content
     * @param format   The format being used for displaying in chat
     */
    public LapisMessageEvent(UUID sender, UUID receiver, String message, String format) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.format = format;
    }

    /**
     * @return Returns the {@link UUID}  for the player who has sent the message
     */
    public UUID getSender() {
        return sender;
    }

    /**
     * @return Returns the {@link UUID}  for the player who has sent the message
     */
    public UUID getReceiver() {
        return receiver;
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
