package com.example.demo.flyweight;

import java.util.HashMap;
import java.util.Map;

public class DonationItemTypeFactory {
    private static final Map<String, DonationItemType> flyweights = new HashMap<>();

    public static DonationItemType getItemType(String itemType, String subType) {
        String key = itemType + ":" + subType;
        if (!flyweights.containsKey(key)) {
            flyweights.put(key, new DonationItemType(itemType, subType));
        }
        return flyweights.get(key);
    }
}
