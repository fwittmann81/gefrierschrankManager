package com.example.wittmanf.gefrierschrankmanager.comparator;

import com.example.wittmanf.gefrierschrankmanager.Item;

import java.util.Comparator;

public class FachComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        return String.valueOf(item1.getFach()).compareTo(String.valueOf(item2.getFach()));
    }
}
