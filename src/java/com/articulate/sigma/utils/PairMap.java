package com.articulate.sigma.utils;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public static void addToMap(HashMap<String, HashMap<String,Integer>> map, String key, String value) {

        HashMap<String,Integer> ps = map.get(key);
        if (ps == null)
            ps = new HashMap<String,Integer>();
        if (ps.get(value) == null)
            ps.put(value,1);
        else
            ps.put(value,ps.get(value)+1);
        map.put(key,ps);
    }

    /** ***************************************************************
     * Convert to a sorted map
     */
    public static HashMap<String, TreeMap<Integer,HashSet<String>>> toSortedMap(HashMap<String, HashMap<String,Integer>> map) {

        HashMap<String, TreeMap<Integer,HashSet<String>>> result = new HashMap<>();
        return result;
    }

    /** ***************************************************************
     * Read a pair map from a text file
     */
    public static HashMap<String, HashMap<String,Integer>>  readMap(String fname) {

        HashMap<String, HashMap<String,Integer>> map = new HashMap<>();
        List<String> documents = Lists.newArrayList();
        File f = new File(fname);
        String line = null;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(f));
            while ((line = bf.readLine()) != null) {
                if (line == null || line.equals(""))
                    continue;
                HashMap<String,Integer> innerMap = new HashMap<>();
                String[] parts = line.split("\\|");
                String key = parts[0];
                String remain = parts[1];
                String[] pairs = remain.split(",");
                for (String pair : pairs) {
                    String[] p = pair.split(":");
                    String modifier = p[0];
                    String count = p[1];
                    int cint = Integer.parseInt(count);
                    innerMap.put(modifier,cint);
                }
                map.put(key,innerMap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("FileUtil.PairMap.readMap(): " +
                    "Unable to read line in file. Last line successfully read was: " + line);
        }
        return map;
    }

    /** ***************************************************************
     * Save a pair map to a text file
     */
    public static void saveMap(HashMap<String, HashMap<String,Integer>> map, String filename) {

        File f = new File(filename);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(f);
            for (String key : map.keySet()) {
                pw.print(key + "|");
                HashMap<String,Integer> hm = map.get(key);
                boolean first = true;
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
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("FileUtil.writeLines(): Unable to write line in file " + filename);
        }
        pw.flush();
        pw.close();
    }
}
