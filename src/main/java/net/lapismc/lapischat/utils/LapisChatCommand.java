/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapismc.lapischat.utils;

import net.lapismc.lapischat.LapisChat;
import net.lapismc.lapischat.framework.Channel;
import net.lapismc.lapischat.framework.ChatPlayer;
import net.lapismc.lapiscore.LapisCoreCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class LapisChatCommand extends LapisCoreCommand {

    protected LapisChat plugin;

    protected LapisChatCommand(LapisChat plugin, String name, String desc, ArrayList<String> aliases) {
        super(plugin, name, desc, aliases, true);
        this.plugin = plugin;
    }

    protected ChatPlayer getChatPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return null;
        }
        return plugin.getPlayer(((Player) sender).getUniqueId());
    }

    protected void sendChannelMessage(CommandSender sender, String key, Channel channel) {
        sender.sendMessage(plugin.config.getMessage(key).replace("%CHANNEL%", channel.getName()));
    }

    protected void sendModeratorMessage(CommandSender sender, String key, Channel channel, ChatPlayer player) {
        sender.sendMessage(plugin.config.getMessage(key).replace("%CHANNEL%", channel.getName())
                .replace("%PLAYER%", player.getOfflinePlayer().getName()));
    }

    protected boolean isNotPermitted(CommandSender sender, String permission) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        return !p.hasPermission(permission);
    }

}
