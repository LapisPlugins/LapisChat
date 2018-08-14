package net.lapismc.lapischat.commands;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.utils.LapisChatCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class LapisChatPrivateMessage extends LapisChatCommand {

    public LapisChatPrivateMessage(LapisChat plugin) {
        //TODO load aliases from config
        super(plugin, "LapisChatPrivateMessage", "Send a message to another player", new ArrayList<>());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

    }
}
