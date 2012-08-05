package com.github.krockode.itemswitcher.util;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemSwitcherUtils {

    public static void switchHeldItem(final Player player, final Pattern itemPattern, final SwitcherStatus status) {
        PlayerInventory inventory = player.getInventory();

        int heldItemSlot = inventory.getHeldItemSlot();
        ItemStack itemInHand = inventory.getContents()[heldItemSlot];
        if ((itemInHand != null) && (itemPattern.matcher(itemInHand.getType().toString()).matches())) {
            // already has item of that type in hand
            status.updateInteractTime();
            return;
        }
        Integer unswitchedIndex = status.getUnswitchedIndex();
        System.out.println("unswitched index: " + unswitchedIndex);
        if (status.hasSwitched() && unswitchedIndex != null) {
            ItemStack unswitched = inventory.getItem(unswitchedIndex);
            if (unswitched != null && itemPattern.matcher(unswitched.getType().toString()).matches()) {
                // if reverting previous switch will do, do that.
                unswitchItems(inventory, status);
                return;
            }
        }

        // find an item matching all or part of the itemName param
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && itemPattern.matcher(item.getType().toString()).matches()) {
                // unswitch previous
                if (status.getUnswitchedIndex() != null) {
                    unswitchItems(inventory, status);
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

    public static void unswitchItems(PlayerInventory inventory, SwitcherStatus status) {
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
