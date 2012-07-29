package com.github.krockode.itemswitcher.listener;

import static com.github.krockode.itemswitcher.util.ItemSwitcherUtils.unswitchItems;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.github.krockode.itemswitcher.util.SwitcherStatus;

public class ItemSwitcherCommandExecutor implements CommandExecutor {

    private final Map<String, SwitcherStatus> enabledPlayers;
    private final Plugin plugin;
    private boolean debug = false;

    public ItemSwitcherCommandExecutor(Plugin plugin, Map<String, SwitcherStatus> enabledPlayers) {
        this.plugin = plugin;
        this.enabledPlayers = enabledPlayers;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        String option = args.length > 0 ? args[0] : "";
        if ((player == null) || ("list".equals(option))) {
            sender.sendMessage("Players using item switching (" + enabledPlayers.size() + "): " +
                    ChatColor.GREEN + StringUtils.join(enabledPlayers.keySet(), ChatColor.RESET + ", " + ChatColor.GREEN));
            return true;
        } else if (debug && "breaktools".equals(option)) {
            PlayerInventory inventory = player.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType().toString().matches(".*_PICKAXE")) {
                    plugin.getLogger().info("Breaking item " + item);
                    item.setDurability((short)(item.getType().getMaxDurability() - 3));
                }
            }
        } else if (debug && "x".equals(option)) {
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 1, 2);
            plugin.getServer().getPluginManager().callEvent(event);
        }else if (enabledPlayers.keySet().contains(player.getName())) {
            enabledPlayers.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Item switching Off");
        } else {
            enabledPlayers.put(player.getName(), new SwitcherStatus());
            player.sendMessage(ChatColor.YELLOW + "Item switching On");
            if (enabledPlayers.size() == 1) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SwitcherUpdater(), 20);
            }
        }
        return true;
    }

    private class SwitcherUpdater implements Runnable {
        public void run() {
            try {
                Iterator<String> playerNames = enabledPlayers.keySet().iterator();
                while (playerNames.hasNext()) {
                    String name = playerNames.next();
                    Player player = plugin.getServer().getPlayer(name);
                    SwitcherStatus status = enabledPlayers.get(name);
                    if (player == null || !player.isOnline()) {
                        playerNames.remove();
                        plugin.getLogger().fine("removing offline player: " + name);
                    } else if (status.shouldUnswitch()) {
                        unswitchItems(status, player.getInventory());
                        plugin.getLogger().fine("unswitching player " + name);
                    }
                }
            } finally {
                if (enabledPlayers.size() > 0) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SwitcherUpdater(), 20);
                }
            }
        }
    }
}
