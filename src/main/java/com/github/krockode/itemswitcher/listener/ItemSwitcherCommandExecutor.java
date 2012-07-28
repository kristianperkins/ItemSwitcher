package com.github.krockode.itemswitcher.listener;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class ItemSwitcherCommandExecutor implements CommandExecutor {

    private final Set<String> enabledPlayers;
    private final Plugin plugin;
    private boolean debug = false;

    public ItemSwitcherCommandExecutor(Plugin plugin, Set<String> enabledPlayers) {
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
                    ChatColor.GREEN + StringUtils.join(enabledPlayers, ChatColor.RESET + ", " + ChatColor.GREEN));
            return true;
        } else if (debug && "breaktools".equals(option)) {
            PlayerInventory inventory = player.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType().toString().matches(".*_PICKAXE")) {
                    plugin.getLogger().info("Breaking item " + item);
                    item.setDurability((short)(item.getType().getMaxDurability() - 3));
                }
            }
        } else if (enabledPlayers.contains(player.getName())) {
            enabledPlayers.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Item switching Off");
        } else {
            enabledPlayers.add(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Item switching On");
        }
        return true;
    }


}
