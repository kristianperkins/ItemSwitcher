package com.github.krockode.itemswitcher;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.krockode.itemswitcher.listener.ItemSwitcherCommandExecutor;
import com.github.krockode.itemswitcher.listener.ItemSwitcherPlayerListener;
import com.github.krockode.itemswitcher.util.SwitcherStatus;

public class ItemSwitcher extends JavaPlugin {

    private Map<String, SwitcherStatus> enabledPlayers = new HashMap<String, SwitcherStatus>();

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
