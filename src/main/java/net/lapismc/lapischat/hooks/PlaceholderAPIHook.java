package net.lapismc.lapischat.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook {

    public String format(String s, OfflinePlayer op) {
        return PlaceholderAPI.setPlaceholders(op, s);
    }

}
