package com.articulate.sigma.utils;

/* This code is copyrighted by Articulate Software (c) 2003.
It is released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
Users of this code also consent, by use of this code, to credit Articulate Software in any
writings, briefings, publications, presentations, or other representations of any
software which incorporates, builds on, or uses this code.

Authors:
Adam Pease apease@articulatesoftware.com
*/

import com.google.common.collect.Lists;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtil {

    public static boolean includeBlanks = false;

    /****************************************************************
     */
    public static List<String> readLines(String filename) {

        return readLines(filename,false);
    }

    /****************************************************************
     * This method reads in a text file, breaking it into single line documents
     * Currently, sentences are not separated if they occur on the same line.
     *
     * @param filename          file to be read
     * @param separateSentences should sentences be separated if they occur on one line
     * @return list of strings from each line of the document
     */
    public static List<String> readLines(String filename, boolean separateSentences) {

        List<String> documents = Lists.newArrayList();
        File f = new File(filename);
        String line = null;
        try (BufferedReader bf = new BufferedReader(new FileReader(f))) {
            while ((line = bf.readLine()) != null) {
                if (!includeBlanks && (line == null || line.equals("")))
                    continue;
                documents.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("FileUtil.readLines(): " +
                    "Unable to read line in file. Last line successfully read was: " + line);
        }
        return documents;
    }

    /****************************************************************
     */
    public static void delete(String filename) {

        try {
            File f = new File(filename);
            f.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("FileUtil.delete(): Unable to delete: " + filename);
        }
    }

    /****************************************************************
     */
    public static void writeLines(String filename, Collection<String> lines) {

        System.out.println("FileUtil.writeLines(): filename: " + filename);
        System.out.println("FileUtil.writeLines(): " + lines.size());
        File f = new File(filename);
        try (PrintWriter pw = new PrintWriter(f)) {
            for (String line : lines) {
                pw.println(line);
                //System.out.println("FileUtil.writeLines(): line: " + line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("FileUtil.writeLines(): Unable to write line in file " + filename);
        }
    }

    /****************************************************************
     * Read a KIF file extracting of a certain type in one
     * and putting them in another.  Write a new copy of the original
     * file without the statements, as well as the new file, leaving
     * the original untouched.
     */
    public static void splitStatements(String filename, String pattern) {

        String fnameMinus = filename + ".minus";
        String fnameNew = filename + ".new";
        includeBlanks = true;
        try (PrintWriter pwNew = new PrintWriter(new FileWriter(fnameNew, false));
            PrintWriter pwMinus = new PrintWriter(new FileWriter(fnameMinus, false))) {
            List<String> lines = (ArrayList) readLines(filename, false);
            System.out.println("splitSttaements(): read file " + filename + " with " + lines.size() + " lines");
            String s;
            int parenLevel;
            boolean inQuote;
            for (int i = 0; i < lines.size(); i++) {
                s = lines.get(i);
                System.out.println(s);
                if (s.contains(pattern) && !s.startsWith(";")) {
                    pwNew.println(s);
                    parenLevel = 0;
                    inQuote = false;
                    do {
                        for (int j = 0; j < s.length(); j++) {
                            if (s.charAt(j) == '(' && !inQuote)
                                parenLevel++;
                            if (s.charAt(j) == ')' && !inQuote)
                                parenLevel--;
                            if (s.charAt(j) == '"')
                                inQuote = !inQuote;
                        }
                        if (parenLevel > 0) {
                            i++;
                            s = lines.get(i);
                            pwNew.println(s);
                        }
                    } while (parenLevel > 0);
                }
                else
                    pwMinus.println(s);
            }
        }
        catch (Exception ex) {
            System.err.println("Error with " + filename + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /****************************************************************
     * Removes the path from a file specifier and returns just the
     * file name
     */
    public static String noPath(String s) {

        if (!s.contains(File.separator) || s.indexOf(File.separator) == s.length()-1)
            return s;
        return s.substring(s.lastIndexOf(File.separator)+1);
    }

    /****************************************************************
     * Removes the extension and just returns the file name
     */
    public static String noExt(String s) {

        if (s == null || !s.contains("."))
            return s;
        return s.substring(0,s.lastIndexOf("."));
    }

    /****************************************************************
     * Recursively get all file paths in a directory and subdirectories
     */
    public static Collection<String> walk(String path) {

        File root = new File(path);
        File[] list = root.listFiles();
        List<String> result = new ArrayList<>();
        if (list == null) return result;
        for (File f : list) {
            if (f.isDirectory())
                result.addAll(walk(f.getAbsolutePath()));
            else
                result.add(f.getAbsolutePath());
        }
        return result;
    }

    /** ***************************************************************
     * A test method.
     */
    public static void printHelp() {

        System.out.println("Sigma Knowledge Engineering Environment - FileUtil");
        System.out.println("  options:");
        System.out.println("  -h - show this help screen");
        System.out.println("  -s <fname> \"<pattern>\" - split statements");
    }

    /** ***************************************************************
     */
    public static void main(String[] args) {

        if (args == null) {
            printHelp();
        }
        else {
            if (args != null && args.length > 0 && args[0].equals("-h")) {
                printHelp();
            }
            if (args != null && args.length > 2 && args[0].equals("-s")) {
                splitStatements(args[1],args[2]);
            }
        }
    }
}
