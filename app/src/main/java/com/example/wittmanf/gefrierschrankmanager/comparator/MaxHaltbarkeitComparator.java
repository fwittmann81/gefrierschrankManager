package com.example.wittmanf.gefrierschrankmanager.comparator;

import com.example.wittmanf.gefrierschrankmanager.Item;

import java.util.Comparator;

public class MaxHaltbarkeitComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.getMaxFreezeDate().compareTo(item2.getMaxFreezeDate());
    }
}
