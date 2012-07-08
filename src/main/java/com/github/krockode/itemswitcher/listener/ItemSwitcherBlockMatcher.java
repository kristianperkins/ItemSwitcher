package com.github.krockode.itemswitcher.listener;

public class ItemSwitcherBlockMatcher {

    // pattern to match a block that is clicked 
    private String blockRegex;

    // pattern to match an item in inventory
    private String itemRegex;

    public ItemSwitcherBlockMatcher(String blockRegex, String itemRegex) {
        this.blockRegex = blockRegex;
        this.itemRegex = itemRegex;
    }

    public String getBlockRegex() {
        return blockRegex;
    }
    public String getItemRegex() {
        return itemRegex;
    }
}
