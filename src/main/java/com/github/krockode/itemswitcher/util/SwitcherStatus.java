package com.github.krockode.itemswitcher.util;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.inventory.ItemStack;

/**
 * Keep state of players switching
 */
public class SwitcherStatus {

    private long millisUntilUnswitch = 3000;

    // last time item switching was performed (including when no switch was required)
    private long lastInteract;

    private ItemStack unswitchedItem;
    private Integer unswitchedIndex;

    public void updateInteractTime() {
        lastInteract = System.currentTimeMillis();
    }

    public boolean shouldUnswitch() {
        return (unswitchedIndex != null) && lastInteract + millisUntilUnswitch < System.currentTimeMillis();
    }

    public ItemStack getUnswitchedItem() {
        return unswitchedItem;
    }

    public void setUnswitchedItem(ItemStack unswitchedItem) {
        this.unswitchedItem = unswitchedItem;
    }

    public boolean hasSwitched() {
        return unswitchedIndex != null;
    }

    public Integer getUnswitchedIndex() {
        return unswitchedIndex;
    }

    public void setUnswitchedIndex(Integer unswitchedIndex) {
        this.unswitchedIndex = unswitchedIndex;
    }

    public void setMillisUntilUnswitch(long millisUntilUnswitch) {
        this.millisUntilUnswitch = millisUntilUnswitch;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
