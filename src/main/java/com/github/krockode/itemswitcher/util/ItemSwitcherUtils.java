package com.github.krockode.itemswitcher.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemSwitcherUtils {

    public static void switchHeldItem(final Player player, final String itemPattern) {
        PlayerInventory inventory = player.getInventory();

        int heldItemSlot = inventory.getHeldItemSlot();
        ItemStack itemInHand = inventory.getContents()[heldItemSlot];
        if ((itemInHand != null) && (itemInHand.getType().toString().matches(itemPattern))) {
            // already has item of that type in hand
            return;
        }

        // find an item matching all or part of the itemName param
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.getType().toString().matches(itemPattern)) {
                // switch the two items
                inventory.setItem(heldItemSlot, item);
                inventory.setItem(i, itemInHand);
                return;
            }
        }
    }
}
