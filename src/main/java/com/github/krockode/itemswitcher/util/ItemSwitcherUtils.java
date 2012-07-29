package com.github.krockode.itemswitcher.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemSwitcherUtils {

    public static void switchHeldItem(final Player player, final String itemPattern, final SwitcherStatus status) {
        PlayerInventory inventory = player.getInventory();

        int heldItemSlot = inventory.getHeldItemSlot();
        ItemStack itemInHand = inventory.getContents()[heldItemSlot];
        if ((itemInHand != null) && (itemInHand.getType().toString().matches(itemPattern))) {
            // already has item of that type in hand
            return;
        }
        if (status.hasSwitched() && inventory.getItem(status.getUnswitchedIndex()).getType().toString().matches(itemPattern)) {
            // if reverting previous switch will do, do that.
            unswitchItems(status, inventory);
            return;
        }

        // find an item matching all or part of the itemName param
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.getType().toString().matches(itemPattern)) {
                // unswitch previous
                if (status.getUnswitchedIndex() != null) {
                    unswitchItems(status, inventory);
                    itemInHand = inventory.getItemInHand();
                }
                inventory.setItem(i, itemInHand);
                inventory.setItem(heldItemSlot, item);
                status.setUnswitchedIndex(i);
                status.setUnswitchedItem(itemInHand);
                status.updateInteractTime();
                return;
            }
        }
        return;
    }

    public static void unswitchItems(SwitcherStatus status, PlayerInventory inventory) {
        int heldItemSlot = inventory.getHeldItemSlot();
        ItemStack itemInHand = inventory.getContents()[heldItemSlot];
        Integer unswitchedSlot = status.getUnswitchedIndex();
        ItemStack originalHeldItem = inventory.getItem(unswitchedSlot);
        inventory.setItem(heldItemSlot, originalHeldItem);
        inventory.setItem(unswitchedSlot, itemInHand);
        status.setUnswitchedIndex(null);
        status.setUnswitchedItem(null);
    }
}
