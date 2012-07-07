package com.github.kp.itemswitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.kp.itemswitcher.listener.ItemSwitcherCommandExecutor;
import com.github.kp.itemswitcher.listener.ItemSwitcherPlayerListener;

public class ItemSwitcher extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private final YamlConfiguration configuration;

    public ItemSwitcher() throws FileNotFoundException, IOException, InvalidConfigurationException {
        // Load the configuration file
        File configFile = new File("plugins", "ItemSwitcherSettings.yml");
        log.info(configFile.getAbsolutePath());
        configuration = new YamlConfiguration();
        configuration.load(configFile);
    }
    
    public void onDisable() {
        log.info("ItemSwitcher has been disabled");
        // save the configuration file, if there are no values, write the defaults.
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void onEnable() {
        log.info("loaded configuration: " + configuration);
        PluginDescriptionFile pdfFile = this.getDescription();

        Set<String> enabledPlayers = Collections.synchronizedSet(new HashSet<String>());
        Listener playerListener = new ItemSwitcherPlayerListener(configuration, enabledPlayers);
        getServer().getPluginManager().registerEvents(playerListener, this);

        ItemSwitcherCommandExecutor executor = new ItemSwitcherCommandExecutor(enabledPlayers);
        this.getCommand("switch").setExecutor(executor);
        log.info("players: " + enabledPlayers + "listener: " + playerListener + "exec: " + executor);
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
}
