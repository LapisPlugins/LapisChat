package net.lapismc.lapischat.utils;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;

public class DiscordSRVHook {

    public static void logToDiscord(Player sender, String message) {
        DiscordSRV.getPlugin().processChatMessage(sender, message, null, false);
    }

}
