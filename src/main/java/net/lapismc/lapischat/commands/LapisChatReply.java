package net.lapismc.lapischat.commands;


import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.utils.LapisChatCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class LapisChatReply extends LapisChatCommand {

    public LapisChatReply(LapisChat plugin) {
        super(plugin, "reply", "Send a message to the last player who you talked to", new ArrayList<>(Collections.singletonList("r")));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        //r this is a message
        String msg = getMessageFromArray(args);
        UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : plugin.consoleUUID;
        if (!plugin.messageManager.sendReply(uuid, msg)) {
            sendMessage(sender, "PrivateMessage.NoConversation");
        }
    }

    private String getMessageFromArray(String[] args) {
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        return msg.toString();
    }
}
