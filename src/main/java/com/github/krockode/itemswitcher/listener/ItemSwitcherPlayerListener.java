package com.github.krockode.itemswitcher.listener;

import static com.github.krockode.itemswitcher.util.ItemSwitcherUtils.switchHeldItem;
import static com.github.krockode.itemswitcher.util.ItemSwitcherUtils.unswitchItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.github.krockode.itemswitcher.util.SwitcherStatus;

public class ItemSwitcherPlayerListener implements Listener {

    private static final String MATCHERS_CONFIG_FILE = "/com/github/krockode/itemswitcher/matchers.yml";
    private final Plugin plugin;
    private final Logger log;
    // item in hand to enable switching
    private final String enableSwitchingRegex;
    private final List<ItemSwitcherBlockMatcher> blockMatchers;
    private final Map<String, SwitcherStatus> enabledPlayers;

    public ItemSwitcherPlayerListener(final Plugin plugin, Map<String, SwitcherStatus> enabledPlayers) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        Configuration configuration = plugin.getConfig();
        this.enabledPlayers = enabledPlayers;

        // Load block matcher regex
        enableSwitchingRegex = configuration.getString("enable_switching_regex");
        blockMatchers = new ArrayList<ItemSwitcherBlockMatcher>();

        YamlConfiguration matchersConfig = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream(MATCHERS_CONFIG_FILE));
        for (Object matcherNode : matchersConfig.getList("block_matchers")) {
            @SuppressWarnings("unchecked")
            Map<String, String> matcherMap = (Map<String, String>) matcherNode;
            blockMatchers.add(new ItemSwitcherBlockMatcher(matcherMap.get("block_regex"), matcherMap.get("item_regex")));
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
                        if (blockMatcher.getBlockPattern().matcher(blockTypeString).matches()) {
                            switchHeldItem(event.getPlayer(), blockMatcher.getItemRegex(), enabledPlayers.get(event.getPlayer().getName()));
                            break;
                        }
                    }
                }
                break;
        }
    }

    // Handle block place switching
    // runs on high priority to let others cancel event
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPlace(final BlockPlaceEvent event) {
        if (!enabledPlayers.keySet().contains(event.getPlayer().getName())) {
            return;
        }
        if (!event.isCancelled() && event.getItemInHand().getAmount() == 1) {
            Player player = event.getPlayer();
            BlockSwitcher blockSwitcher = new BlockSwitcher(player, player.getInventory().getHeldItemSlot(), event.getItemInHand().getType());
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, blockSwitcher);
        }
    }

    private class BlockSwitcher implements Runnable {

        private Player player;
        private int itemIndex;
        private Material type;

        public BlockSwitcher(Player player, int itemIndex, Material type) {
            this.player = player;
            this.itemIndex = itemIndex;
            this.type = type;
        }

        public void run() {
            PlayerInventory inventory = player.getInventory();
            ItemStack itemInHand = player.getItemInHand();
            if ((inventory.getHeldItemSlot() == itemIndex) && itemInHand.getType() == Material.AIR) {
                ItemStack[] items = inventory.getContents();
                for (int i = 0; i < items.length; i++) {
                    ItemStack item = items[i];
                    if (item != null && item.getType() == type) {
                        player.setItemInHand(item);
                        inventory.setItem(i, itemInHand);
                        break;
                    }
                }
            }
        }
    }

    // Specific Handlers to revert switched items

    @EventHandler(ignoreCancelled = true)
    public void onHeldItemChanged(final PlayerItemHeldEvent event) {
        SwitcherStatus status = enabledPlayers.get(event.getPlayer().getName());
        if (status != null && status.hasSwitched()) {
            unswitchItems(event.getPlayer().getInventory(), status);
        }
    }
}
