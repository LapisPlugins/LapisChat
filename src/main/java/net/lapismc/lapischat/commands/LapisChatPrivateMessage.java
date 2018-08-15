package net.lapismc.lapischat.commands;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.utils.LapisChatCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class LapisChatPrivateMessage extends LapisChatCommand {

    public LapisChatPrivateMessage(LapisChat plugin) {
        super(plugin, "LapisChatPrivateMessage", "Send a message to another player",
                new ArrayList<>(plugin.getConfig().getStringList("PrivateMessageCommands")));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        //tell dart2112|console a message I don't want the world to see
        if (args.length >= 2) {
            UUID receiverUUID;
            UUID senderUUID;
            if (args[0].equalsIgnoreCase("console")) {
                receiverUUID = plugin.consoleUUID;
            } else {
                //noinspection deprecation
                if (!Bukkit.getOfflinePlayer(args[0]).isOnline()) {
                    sendMessage(sender, "PrivateMessage.PlayerNotFound");
                    return;
                }
                receiverUUID = Bukkit.getPlayer(args[0]).getUniqueId();
            }
            if (sender instanceof Player) {
                senderUUID = ((Player) sender).getUniqueId();
            } else {
                senderUUID = plugin.consoleUUID;
            }
            String msg = getMessageFromArray(args);
            plugin.messageManager.sendMessage(senderUUID, receiverUUID, msg);
        } else {
            sendMessage(sender, "Help.PrivateMessage");
        }
    }

    private String getMessageFromArray(String[] args) {
        StringBuilder msg = new StringBuilder();
        int i = 0;
        for (String s : args) {
            if (i != 0)
                msg.append(s).append(" ");
            i++;
        }
        return msg.toString();
    }
}
