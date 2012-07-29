package com.github.krockode.itemswitcher.listener;

import static com.github.krockode.itemswitcher.util.ItemSwitcherUtils.switchHeldItem;
import static com.github.krockode.itemswitcher.util.ItemSwitcherUtils.unswitchItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.krockode.itemswitcher.util.SwitcherStatus;

public class ItemSwitcherPlayerListener implements Listener {

    private final Logger log;
    // item in hand to enable switching
    private final String enableSwitchingRegex;
    private final List<ItemSwitcherBlockMatcher> blockMatchers;
    private final Map<String, SwitcherStatus> enabledPlayers;

    @SuppressWarnings("unchecked")
    public ItemSwitcherPlayerListener(final Plugin plugin, Map<String, SwitcherStatus> enabledPlayers) {
        this.log = plugin.getLogger();
        Configuration configuration = plugin.getConfig();
        this.enabledPlayers = enabledPlayers;

        // Load block matcher regex
        enableSwitchingRegex = configuration.getString("enable_switching_regex");
        blockMatchers = new ArrayList<ItemSwitcherBlockMatcher>();

        for (Object matcherNode : configuration.getList("block_matchers")) {
            Map<String, String> matcherMap = (Map<String, String>) matcherNode;
            blockMatchers.add(new ItemSwitcherBlockMatcher(
                    matcherMap.get("block_regex"), matcherMap.get("item_regex")));
        }
        log.info("ItemSwitcher loaded " + blockMatchers.size() + " block matching rules.");
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unchecked")
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!enabledPlayers.keySet().contains(event.getPlayer().getName())) {
            return;
        }
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                ItemStack itemInHand = event.getPlayer().getItemInHand();
                if (String.valueOf(itemInHand.getType()).matches(enableSwitchingRegex)) {
                    // switch to something useful
                    String blockTypeString = event.getClickedBlock().getType().toString();
                    for (ItemSwitcherBlockMatcher blockMatcher : blockMatchers) {
                        if (blockTypeString.matches(blockMatcher.getBlockRegex())) {
                            switchHeldItem(event.getPlayer(), blockMatcher.getItemRegex(), enabledPlayers.get(event.getPlayer().getName()));
                            break;
                        }
                    }
                }
                break;
        }
    }

    // Specific Handlers to revert switched items

    @EventHandler(ignoreCancelled = true)
    public void onHeldItemChanged(final PlayerItemHeldEvent event) {
        SwitcherStatus status = enabledPlayers.get(event.getPlayer().getName());
        if (status != null && status.hasSwitched()) {
            unswitchItems(status, event.getPlayer().getInventory());
        }
    }
}
