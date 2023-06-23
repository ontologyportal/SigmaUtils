package com.articulate.sigma.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PairMap {

    /** ***************************************************************
     * utility method to add a Pair element to a HashMap of String
     * keys and a value of Pair, incrementing the count of Pair if it is
     * already a value
     */
    public static void addToMap(HashMap<String, Pair> map, String key, Pair element) {

        Pair p = map.get(key);
        if (p == null)
            p = new Pair(0,element.str);
        p.count = p.count + 1;
        map.put(key,element);
    }
}
