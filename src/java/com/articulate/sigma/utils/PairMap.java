package com.articulate.sigma.utils;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PairMap {

    /** ***************************************************************
     * utility method to add a Pair element to a HashMap of String
     * keys and a value of Pair, incrementing the count of Pair if it is
     * already a value.  Maps can look like
     * man->{tall->5, short->4, grizzled->1}
     * walk->{strenuous->3,short->3}
     */
    public static void addToMap(Map<String, Map<String,Integer>> map, String key, String value) {

        Map<String,Integer> ps = map.get(key);
        if (ps == null)
            ps = new HashMap<>();
        if (ps.get(value) == null)
            ps.put(value,1);
        else
            ps.put(value,ps.get(value)+1);
        map.put(key,ps);
    }

    /** ***************************************************************
     * Convert to a sorted map
     */
    public static Map<String, Map<Integer,Set<String>>> toSortedMap(Map<String, Map<String,Integer>> map) {

        Map<String, Map<Integer,Set<String>>> result = new HashMap<>();
        return result;
    }

    /** ***************************************************************
     * Read a pair map from a text file
     */
    public static Map<String, Map<Integer,Set<String>>> readMap(String fname) {

        Map<String, Map<String,Integer>> map = new HashMap<>();
        List<String> documents = Lists.newArrayList();
        File f = new File(fname);
        String line = null;
        Map<String,Integer> innerMap;
        String[] parts, pairs, p;
        String key, remain, modifier, count;
        int cint;
        try (BufferedReader bf = new BufferedReader(new FileReader(f))) {
            while ((line = bf.readLine()) != null) {
                if (line == null || line.equals(""))
                    continue;
                innerMap = new HashMap<>();
                parts = line.split("\\|");
                key = parts[0];
                remain = parts[1];
                pairs = remain.split(",");
                for (String pair : pairs) {
                    p = pair.split(":");
                    if (p.length != 2) {
                        System.out.println("FileUtil.PairMap.readMap(): " +
                                "bad format in file " + fname + ". Last line successfully read was: " + line);
                        System.out.println("pair: " + pair);
                        System.out.println("key: " + key);
                        System.out.println("remain: " + remain);
                    }
                    modifier = p[0];
                    count = p[1];
                    cint = Integer.parseInt(count);
                    innerMap.put(modifier,cint);
                }
                map.put(key,innerMap);
            }
        }
        catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.err.println("FileUtil.PairMap.readMap(): " +
                    "Unable to read line in file " + fname + ". Last line successfully read was: " + line);
        }
        return MapUtils.toKeyedSortedFreqMap(map);
    }

    /** ***************************************************************
     * Save a pair map to a text file (in the current directory if
     * just a bare filename is supplied)
     */
    public static void saveMap(HashMap<String, HashMap<String,Integer>> map, String filename) {

        File f = new File(filename);
        try (PrintWriter pw = new PrintWriter(f)) {
            Map<String,Integer> hm;
            boolean first;
            for (String key : map.keySet()) {
                pw.print(key + "|");
                hm = map.get(key);
                first = true;
                for (String s : hm.keySet()) {
                    if (!first)
                        pw.print(",");
                    else
                        first = false;
                    pw.print(s + ":" + hm.get(s));
                }
                pw.println();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("FileUtil.writeLines(): Unable to write line in file " + filename);
        }
    }
}
