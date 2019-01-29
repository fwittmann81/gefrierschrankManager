package com.example.wittmanf.gefrierschrankmanager.comparator;

import com.example.wittmanf.gefrierschrankmanager.Item;

import java.util.Comparator;

public class InitialComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        int maxFreezeDateComparison = item1.getMaxFreezeDate().compareTo(item2.getMaxFreezeDate());
        if(maxFreezeDateComparison!=0){
            return maxFreezeDateComparison;
        } else {
            return item1.getName().toLowerCase().compareTo(item2.getName().toLowerCase());
        }
    }
}
