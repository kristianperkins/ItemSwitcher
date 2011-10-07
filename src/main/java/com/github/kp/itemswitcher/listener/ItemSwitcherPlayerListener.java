package com.github.kp.itemswitcher.listener;

import static com.github.kp.itemswitcher.util.ItemSwitcherUtils.switchHeldItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class ItemSwitcherPlayerListener extends PlayerListener {

    private static final Logger log = Logger.getLogger("Minecraft");

    // air clicking vars
    private final boolean airClickSwitchingEnabled;
    private final String airClickItemRegex;

    // item in hand to enable switching
    private final String enableSwitchingRegex;
    private final List<ItemSwitcherBlockMatcher> blockMatchers;

    @SuppressWarnings("unchecked")
    public ItemSwitcherPlayerListener(final Configuration configuration) {

        ConfigurationNode node = configuration.getNode("air_click_switching");
        airClickSwitchingEnabled = node.getBoolean("enabled", false);
        airClickItemRegex = airClickSwitchingEnabled ? node.getString("item_regex") : null;

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

    @SuppressWarnings("unchecked")
    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {

        switch (event.getAction()) {

            case LEFT_CLICK_AIR:
                if (airClickSwitchingEnabled) {
                    switchHeldItem(event.getPlayer(), airClickItemRegex);
                }
                break;
            case LEFT_CLICK_BLOCK:

                ItemStack itemInHand = event.getPlayer().getItemInHand();
                if (String.valueOf(itemInHand.getType()).matches(enableSwitchingRegex)) {
                    // switch to something useful
                    String blockTypeString = event.getClickedBlock().getType().toString();
                    for (ItemSwitcherBlockMatcher blockMatcher : blockMatchers) {
                        if (blockTypeString.matches(blockMatcher.getBlockRegex())) {
                            switchHeldItem(event.getPlayer(), blockMatcher.getItemRegex());
                            break;
                        }
                    }                    
                }

                break;
        }
    }
}
