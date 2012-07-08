package com.github.kp.itemswitcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.kp.itemswitcher.listener.ItemSwitcherCommandExecutor;
import com.github.kp.itemswitcher.listener.ItemSwitcherPlayerListener;

public class ItemSwitcher extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private Set<String> enabledPlayers = Collections.synchronizedSet(new HashSet<String>());
    
    public void onDisable() {
        log.info("ItemSwitcher has been disabled");
        getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        Listener playerListener = new ItemSwitcherPlayerListener(getConfig(), enabledPlayers);
        getServer().getPluginManager().registerEvents(playerListener, this);

        ItemSwitcherCommandExecutor executor = new ItemSwitcherCommandExecutor(enabledPlayers);
        getCommand("switch").setExecutor(executor);
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
}
