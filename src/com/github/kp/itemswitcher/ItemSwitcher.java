package com.github.kp.itemswitcher;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.github.kp.itemswitcher.listener.ItemSwitcherPlayerListener;

public class ItemSwitcher extends JavaPlugin {

	private final Configuration configuration;

	private static final Logger log = Logger.getLogger("Minecraft");

	private final PlayerListener playerListener;

	public ItemSwitcher() {
		// Load the configuration file
		File configFile = new File("plugins", "ItemSwitcherSettings.yml");
		log.info(configFile.getAbsolutePath());
		configuration = new Configuration(configFile);
		configuration.load();

		playerListener = new ItemSwitcherPlayerListener(configuration);
	}
	
	public void onDisable() {
		log.info("ItemSwitcher has been disabled");
	}

	public void onEnable() {

		log.info("loaded configuration: " + configuration);

        // Register our events
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
