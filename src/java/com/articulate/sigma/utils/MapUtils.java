package com.articulate.sigma.utils;

/* This code is copyrighted by Articulate Software (c) 2003.
It is released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
Users of this code also consent, by use of this code, to credit Articulate Software in any
writings, briefings, publications, presentations, or other representations of any
software which incorporates, builds on, or uses this code.

Authors:
Adam Pease apease@articulatesoftware.com
*/

import com.google.common.collect.Sets;

import java.util.*;

public class MapUtils {

    /** ***************************************************************
     */
    public static void addToMapMap(HashMap<String, HashMap<String, HashSet<String>>> mapmap, String superkey, String key, String element) {

        HashMap<String, HashSet<String>> map = mapmap.get(superkey);
        if (map == null)
            map = new HashMap<String, HashSet<String>>();
        mapmap.put(superkey,map);
        HashSet<String> set = map.get(key);
        if (set == null)
            set = new HashSet<String>();
        set.add(element);
        map.put(key,set);
    }

    /** ***************************************************************
     * utility method to add a String element to a HashMap of String
     * keys and a value of an HashSet of Strings
     */
    public static void addToMap(Map<String, HashSet<String>> map, String key, String element) {

        HashSet<String> al = map.get(key);
        if (al == null)
            al = new HashSet<String>();
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
    public static void addToSortedFreqMap(Map<Integer, HashSet<String>> map, String key, int count) {

        HashSet<String> al = map.get(count);
        if (al == null)
            al = new HashSet<String>();
        al.add(key);
        map.put(count, al);
    }

    /** ***************************************************************
     * utility method to merge frequency counts of keys
     */
    public static Map<Integer, HashSet<String>> toSortedFreqMap(Map<String, Integer> map) {

        TreeMap<Integer, HashSet<String>> result = new TreeMap<>();
        for (String s : map.keySet()) {
            int val = map.get(s);
            addToSortedFreqMap(result,s,val);
        }
        return result;
    }

    /** ***************************************************************
     */
    public static String sortedFreqMapToString(Map<Integer, HashSet<String>> map) {

        StringBuffer sb = new StringBuffer();
        for (int i : map.keySet())
            sb.append(i + "=" + map.get(i) + "\n");
        return sb.toString();
    }

}
