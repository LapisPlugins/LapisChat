package net.lapismc.lapischat.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.logging.Logger;

public class LapisUpdater {

    private final String jarName;
    private final String username;
    private final String repoName;
    private final String branch;
    private final JavaPlugin plugin;
    private final Logger logger;
    private String ID;
    private Boolean force;
    private String newVersionRawString;

    public LapisUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jarName = "LapisChat";
        this.username = "LapisPlugins";
        this.repoName = "LapisChat";
        this.branch = "master";
        this.logger = plugin.getLogger();
    }

    public boolean checkUpdate() {
        this.ID = "LapisChat";
        this.force = false;
        return updateCheck();
    }

    public void downloadUpdate() {
        this.ID = "LapisChat";
        this.force = true;
        downloadUpdateJar();
    }

    private void downloadUpdateJar() {
        if (updateCheck()) {
            try {
                URL changeLogURL = new URL(
                        "https://raw.githubusercontent.com/" + username + "/" + repoName + "/" + branch + "/updater" +
                                "/changelog.yml");
                ReadableByteChannel changelogByteChannel = Channels.newChannel(changeLogURL.openStream());
                File changeLogFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator +
                        "changelog.yml");
                URL jarURL = new URL(
                        "https://raw.githubusercontent.com/" + username + "/" + repoName + "/" + branch + "/updater/"
                                + ID + "/" + jarName + ".jar");
                ReadableByteChannel jarByteChannel = Channels.newChannel(jarURL.openStream());
                File update = plugin.getServer().getUpdateFolderFile();
                if (!update.exists()) {
                    if (!update.mkdirs()) {
                        logger.info(plugin.getName() + " failed to make update folder");
                    }
                }
                File jar = new File(update.getAbsolutePath() + File.separator + jarName + ".jar");
                if (!jar.exists()) {
                    if (!jar.createNewFile()) {
                        logger.info(plugin.getName() + " updater Failed to save the updated Jar");
                        return;
                    }
                }
                FileOutputStream jarOutputStream = new FileOutputStream(jar);
                jarOutputStream.getChannel().transferFrom(jarByteChannel, 0, Long.MAX_VALUE);
                jarByteChannel.close();
                jarOutputStream.flush();
                jarOutputStream.close();

                FileOutputStream changeLogOutputStream = new FileOutputStream(changeLogFile);
                changeLogOutputStream.getChannel().transferFrom(changelogByteChannel, 0, Long.MAX_VALUE);
                changelogByteChannel.close();
                changeLogOutputStream.flush();
                changeLogOutputStream.close();
                YamlConfiguration changeLog = YamlConfiguration.loadConfiguration(changeLogFile);
                logger.info("Changes in newest Version \n" +
                        changeLog.getStringList("ChangeLog." + newVersionRawString).toString().replace("[", "").replace("]", ""));
            } catch (IOException e) {
                e.printStackTrace();
                logger.severe(plugin.getName() + " updater failed to download updates!");
                logger.severe("Please check your internet connection and" +
                        " firewall settings and try again later");
            }
        }
    }

    private boolean updateCheck() {
        Integer oldVersion;
        Integer newVersion;
        File f;
        YamlConfiguration yaml;
        try {
            URL website = new URL(
                    "https://raw.githubusercontent.com/" + username + "/" + repoName + "/" + branch + "/updater/update.yml");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator +
                    "update.yml");
            Date d = new Date(f.lastModified());
            Date d0 = new Date();
            d0.setTime(d0.getTime() - 3600);
            if (!f.exists() || force || d.before(d0)) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.flush();
                fos.close();
                if (!f.setLastModified(d0.getTime())) {
                    logger.info(plugin.getName() + " failed to update last time modified on update.yml");
                }
            }
        } catch (IOException e) {
            logger.severe(plugin.getName() + " updater failed to check for updates!");
            logger.severe("Please check your internet connection and" +
                    " firewall settings and try again later");
            return false;
        }
        try {
            yaml = YamlConfiguration.loadConfiguration(f);
            if (!yaml.contains(ID)) {
                return false;
            }
            String oldVersionString = plugin.getDescription().getVersion()
                    .replace(".", "");
            newVersionRawString = yaml.getString(ID);
            String newVersionString = yaml.getString(ID).replace(".", "");
            oldVersion = Integer.parseInt(oldVersionString);
            newVersion = Integer.parseInt(newVersionString);
        } catch (Exception e) {
            logger.severe(plugin.getName() + " failed to load update.yml or parse the values!" +
                    " It may be corrupt!");
            logger.severe("Please try again later");
            if (!f.delete()) {
                logger.info(plugin.getName() + " failed to delete update.yml, please delete it your self");
            }
            return false;
        }
        return !oldVersion.equals(newVersion);
    }
}