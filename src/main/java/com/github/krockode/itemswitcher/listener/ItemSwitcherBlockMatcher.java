package com.github.krockode.itemswitcher.listener;

import java.util.regex.Pattern;

public class ItemSwitcherBlockMatcher {

    // pattern to match a block that is clicked
    private Pattern blockPattern;

    // pattern to match an item in inventory
    private Pattern itemPattern;

    public ItemSwitcherBlockMatcher(String blockRegex, String itemRegex) {
        this.blockPattern = Pattern.compile(blockRegex);
        this.itemPattern = Pattern.compile(itemRegex);
    }

    public Pattern getBlockPattern() {
        return blockPattern;
    }
    public Pattern getItemRegex() {
        return itemPattern;
    }
}
