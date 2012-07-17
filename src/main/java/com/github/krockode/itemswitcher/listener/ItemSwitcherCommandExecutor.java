package com.github.krockode.itemswitcher.listener;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemSwitcherCommandExecutor implements CommandExecutor {

    private final Set<String> enabledPlayers;
    public ItemSwitcherCommandExecutor(Set<String> enabledPlayers) {
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
        }
        if (enabledPlayers.contains(player.getName())) {
            enabledPlayers.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Item switching Off");
        } else {
            enabledPlayers.add(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Item switching On");
        }
        return true;
    }


}
