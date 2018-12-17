package com.example.wittmanf.gefrierschrankmanager.comparator;

import com.example.wittmanf.gefrierschrankmanager.Item;

import java.util.Comparator;

public class NameComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.getName().compareTo(item2.getName());
    }
}
