package com.github.kp.itemswitcher.listener;

import java.util.Set;

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
     
        if (cmd.getName().equalsIgnoreCase("switch")) {
            if (player == null) {
                sender.sendMessage("this command can only be run by a player");
            } else if (args.length < 1) {
                sender.sendMessage("specify on or off");
            } else {
                if ("on".equals(args[0])) {
                    enabledPlayers.add(player.getName());
                    sender.sendMessage("Tool switching On");
                } else if ("off".equals(args[0])) {
                    enabledPlayers.remove(player.getName());
                    sender.sendMessage("Tool switching Off");
                }
            }
            return true;
        }
        return false;
    }


}
