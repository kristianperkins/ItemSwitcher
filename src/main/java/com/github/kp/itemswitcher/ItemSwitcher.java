package com.github.kp.itemswitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.kp.itemswitcher.listener.ItemSwitcherPlayerListener;

public class ItemSwitcher extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final YamlConfiguration configuration;
    private final Listener playerListener;

    public ItemSwitcher() throws FileNotFoundException, IOException, InvalidConfigurationException {
        // Load the configuration file
        File configFile = new File("plugins", "ItemSwitcherSettings.yml");
        log.info(configFile.getAbsolutePath());
        configuration = new YamlConfiguration();
        configuration.load(configFile);
        playerListener = new ItemSwitcherPlayerListener(configuration);
    }
    
    public void onDisable() {
        log.info("ItemSwitcher has been disabled");
    }

    public void onEnable() {
        log.info("loaded configuration: " + configuration);
        getServer().getPluginManager().registerEvents(playerListener, this);
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
}
