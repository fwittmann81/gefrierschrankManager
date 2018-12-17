package com.example.wittmanf.gefrierschrankmanager.comparator;

import com.example.wittmanf.gefrierschrankmanager.Item;

import java.util.Comparator;

public class KategorieComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.getKategorie().compareTo(item2.getKategorie());
    }
}
