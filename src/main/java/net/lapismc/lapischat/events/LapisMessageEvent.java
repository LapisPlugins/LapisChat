package net.lapismc.lapischat.events;

import net.lapismc.lapiscore.events.LapisCoreCancellableEvent;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LapisMessageEvent extends LapisCoreCancellableEvent {

    private UUID sender;
    private UUID receiver;
    private String message;
    private String format;

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
