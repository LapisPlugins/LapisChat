package net.lapismc.lapischat;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class LapisChatFileWatcher {

    private final LapisChat plugin;

    LapisChatFileWatcher(LapisChat plugin) {
        this.plugin = plugin;
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                watcher();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void watcher() throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(plugin.getDataFolder().getAbsolutePath());
        dir.register(watcher, ENTRY_DELETE, ENTRY_MODIFY);
        plugin.getLogger().info("LapisChat file watcher started!");
        WatchKey key = watcher.take();
        while (key != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                File f = fileName.toFile();
                if (kind == ENTRY_DELETE) {
                    if (f.getName().endsWith(".yml")) {
                        String name = f.getName().replace(".yml", "");
                        switch (name) {
                            case "config":
                                plugin.saveDefaultConfig();
                                plugin.reloadConfig();
                                break;
                            case "Messages":
                                plugin.config.generateConfigs();
                                break;
                        }
                    }
                } else if (kind == ENTRY_MODIFY) {
                    checkConfig(f);
                }
            }
            key.reset();
            key = watcher.take();
        }
        plugin.getLogger().severe("LapisChat file watcher has stopped, please report any errors to dart2112 if this was not intended");
    }

    private void checkConfig(File f) {
        String name = f.getName().replace(".yml", "");
        switch (name) {
            case "config":
                plugin.reloadConfig();
                plugin.getLogger().info("Changes made to LapisChat config have been loaded");
                break;
            case "Messages":
                plugin.config.reloadMessages(f);
                plugin.getLogger().info("Changes made to LapisChat Messages.yml have been loaded");
                break;
            default:
                break;
        }
    }

}
