package com.github.krockode.itemswitcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.krockode.itemswitcher.listener.ItemSwitcherCommandExecutor;
import com.github.krockode.itemswitcher.listener.ItemSwitcherPlayerListener;

public class ItemSwitcher extends JavaPlugin {

    private Set<String> enabledPlayers = Collections.synchronizedSet(new HashSet<String>());

    public void onDisable() {
        getLogger().info("ItemSwitcher has been disabled");
        getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void onEnable() {
        Listener playerListener = new ItemSwitcherPlayerListener(this, enabledPlayers);
        getServer().getPluginManager().registerEvents(playerListener, this);
        ItemSwitcherCommandExecutor executor = new ItemSwitcherCommandExecutor(this, enabledPlayers);
        getCommand("switcher").setExecutor(executor);
    }
}
