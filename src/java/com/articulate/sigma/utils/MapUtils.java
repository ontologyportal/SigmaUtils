package com.articulate.sigma.utils;

/* This code is copyrighted by Articulate Software (c) 2003.
It is released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
Users of this code also consent, by use of this code, to credit Articulate Software in any
writings, briefings, publications, presentations, or other representations of any
software which incorporates, builds on, or uses this code.

Authors:
Adam Pease apease@articulatesoftware.com
*/

import java.util.*;

public class MapUtils {

    /** ***************************************************************
     */
    public static void addToMapMap(Map<String, Map<String, Set<String>>> mapmap, String superkey, String key, String element) {

        Map<String, Set<String>> map = mapmap.get(superkey);
        if (map == null)
            map = new HashMap<>();
        mapmap.put(superkey,map);
        Set<String> set = map.get(key);
        if (set == null)
            set = new HashSet<>();
        set.add(element);
        map.put(key,set);
    }

    /** ***************************************************************
     * utility method to add a String element to a HashMap of String
     * keys and a value of an HashSet of Strings
     */
    public static void addToMap(Map<String, Set<String>> map, String key, String element) {

        Set<String> al = map.get(key);
        if (al == null)
            al = new HashSet<>();
        al.add(element);
        map.put(key, al);
    }

    /** ***************************************************************
     * utility method to add frequency counts of keys
     */
    public static void addToFreqMap(Map<String, Integer> map, String key, int count) {

        int val = 0;
        if (map.containsKey(key))
            val = map.get(key);
        val = val + count;
        map.put(key, val);
    }

    /** ***************************************************************
     * utility method to merge frequency counts of keys
     */
    public static Map<String, Integer> mergeToFreqMap(Map<String, Integer> mapOld, Map<String, Integer> mapNew) {

        Map<String, Integer> result = new HashMap<>();
        result.putAll(mapOld);
        for (String key : mapNew.keySet())
            addToFreqMap(result,key,mapNew.get(key));
        return result;
    }

    /** ***************************************************************
     * utility method to add frequency counts of keys
     */
    public static void addToSortedFreqMap(Map<Integer, Set<String>> map, String key, int count) {

        Set<String> al = map.get(count);
        if (al == null)
            al = new HashSet<>();
        al.add(key);
        map.put(count, al);
    }

    /** ***************************************************************
     * utility method to merge frequency counts of keys
     */
    public static Map<Integer, Set<String>> toSortedFreqMap(Map<String, Integer> map) {

        Map<Integer, Set<String>> result = new TreeMap<>();
        for (String s : map.keySet()) {
            int val = map.get(s);
            addToSortedFreqMap(result,s,val);
        }
        return result;
    }

    /** ***************************************************************
     */
    public static String sortedFreqMapToString(Map<Integer, Set<String>> map) {

        StringBuilder sb = new StringBuilder();
        for (int i : map.keySet())
            sb.append(i).append("=").append(map.get(i)).append("\n");
        return sb.toString();
    }

    /** ***************************************************************
     * utility method to merge frequency counts of keys
     */
    public static Map<String, Map<Integer,Set<String>>> toKeyedSortedFreqMap(Map<String, Map<String, Integer>> map) {

        Map<String, Map<Integer,Set<String>>> result = new HashMap<>();
        Map<Integer, Set<String>> sorted;
        for (String key : map.keySet()) {
            sorted = toSortedFreqMap(map.get(key));
            result.put(key,sorted);
        }
        return result;
    }
}
