package com.articulate.sigma.utils;

/* This code is copyrighted by Articulate Software (c) 2003.
It is released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
Users of this code also consent, by use of this code, to credit Articulate Software in any
writings, briefings, publications, presentations, or other representations of any
software which incorporates, builds on, or uses this code.

Authors:
Adam Pease apease@articulatesoftware.com
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by apease on 7/23/16.
 */
public class MBoxReader {

    //Enumeration of the property names we'll use:
    public static String PROP_NAME_SENDER		= "Sender";
    public static String PROP_NAME_DATE			= "MessageDate";
    public static String PROP_NAME_SENDER_INFO          = "SenderInfo";
    public static String PROP_NAME_BODY			= "Body";
    public static String PROP_NAME_FACTS		= "Facts";
    public static Set<Map<String,String>> records = new HashSet<>();

    /** ***************************************************************
     * This regular expression will be used to extract fields from the From
     * line of each message. It matches the word "From" followed by a
     * space, followed by a sequence of non-whitespace characters which
     * constitute the sender, followed by some whitespace and a string of
     * exactly 24 characters which consitutes the date and possibly followed
     * by a string of characters containing other information about the
     * sender.
     */
    private static Pattern fromLineRegex
            = Pattern.compile("From (\\S*)\\s*(.{24})(.*)");

    /** ***************************************************************
     */
    public void execute(String path) {

        //Get the paths of the mbox files to process:
        List<String> mboxFiles = new ArrayList<>();
        File folder = new File(path);
        if (!folder.exists()) {
            System.out.println("Error in MBoxReader.execute(): '" + folder + "' doesn't exist ");
            return;
        }
        File[] listOfFiles;
        if (folder.isFile())
            mboxFiles.add(path);
        else {
            listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                if (!file.getName().contains("."))
                    mboxFiles.add(file.getName());
                if (file.getName().contains(".sbd"))
                    execute(folder.getName() + File.separator + file.getName());
            }

            if (mboxFiles == null)
                System.out.println("Error inMBoxReader.execute: You must specify at least one filename.");
        }
        // Now that we have processed the configuration, we're ready to
        // ;oop over each of the files to parse:
        for (String file : mboxFiles) {
            try
                //Open the current file:
                (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Process the current file. The first line of each message should be
                // "From <sender> <date> <more info>":
                String curFromLine = reader.readLine();
                if (curFromLine == null) {
                    System.out.println("mbox file '" + file + "' was empty.");
                    continue; //Continue on to next file.
                }

                //Loop over the messages in the file:
                while (curFromLine != null) {
                    //Report our progress:
                    System.out.println("Processing message: " + curFromLine + "...");

                    //Extract fields from the from line:
                    Matcher matcher = fromLineRegex.matcher(curFromLine);
                    if (!matcher.matches()) {
                        System.out.println("Invalid From line syntax in file '"
                                + file + "': " + curFromLine);
                        break; //Abort this file.
                    }

                    String sender = matcher.group(1);
                    String date = matcher.group(2);
                    String senderInfo = matcher.group(3);

                    //Create a new Record for this message and add the from line
                    //fields as properties:
                    Map<String,String> record = new HashMap();

                    record.put(PROP_NAME_SENDER, sender);
                    record.put(PROP_NAME_DATE, date);

                    if (!senderInfo.equals(""))
                        record.put(PROP_NAME_SENDER_INFO, senderInfo);

                    processHeaders(reader, record); //process the message headers.

                    //The rest of the message is the message body. This method will
                    //read that in, add it to the record and return the From line of
                    //the next message, if any:
                    curFromLine = processBody(reader, record);
                    records.add(record); //Emit the completed record.
                }
            }
            catch (IOException e) {
                //There was a problem processing the current file, but maybe the
                //others will work; we'll log an error and continue:
                System.out.println("Error processing mbox file '" + file + "': "
                        + e.getMessage());
            }
        }
    }

    /** ***************************************************************
     */
    private void processHeaders(BufferedReader reader, Map<String,String> record) {

        try {
            //Loop until we reach a blank line, which indicates the end of the
            //headers, or we reach the end of the input stream:
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.equals(""))
                    break;

                //Each header has the form "Name: value". Extract the name and
                //value from the current header:
                int colonPos = line.indexOf(':');
                if (colonPos == -1) {
                    System.out.println("Invalid message header format. Expected a colon in "
                            + "the line '" + line + "'");
                    continue; //Move on to next header.
                }
                record.put(line.substring(0,colonPos),line.substring(colonPos + 1));
            }
        }
        catch (IOException ioe) {
            System.err.println("Error in MBoxReader.processHeaders()");
            ioe.printStackTrace();
        }
    }

    /** ***************************************************************
     * Beginning at the current position of the reader, this method reads
     * in a message body until it reaches a blank line followed by a "From"
     * line indicating the start of the next message, or the stream runs out
     * of data. Once it is done reading in the body, it adds the body text
     * to the specified record as a property, and returns the "From" line of
     * the next message, if any.
     *
     * @param reader  Reader to read body from
     * @param record  Record to add body to
     *
     * @return  The "From" line of the next message in the reader stream, or
     *          <code>null</code> if there are no more messages
     *
     * @throws IOException
     */
    private String processBody(BufferedReader reader, Map<String,String> record) {

        String body = "";
        String fromLine = null;
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.equals("")) {
                    fromLine = reader.readLine();
                    if (fromLine == null)
                        break;
                    //If the line begins with "From " then it is a From line:
                    if (fromLine.regionMatches(true, 0, "From ", 0, 5))
                        break; //A new message was found.
                    //not a from line...
                    line += fromLine;
                    fromLine = null;
                }
                body += line; //Append line to body.
            }
            record.put(PROP_NAME_BODY, body); // Add the body to the record:
        }
        catch (IOException ioe) {
            System.err.println("Error in MBoxReader.processBody()");
            ioe.printStackTrace();
        }
        return fromLine;
    }

    /** ***************************************************************
     */
    public void extractInfo() {

        System.out.println("In MBoxReader.extractInfo()");
        for (Map<String,String> element : records) {
            if (element.keySet().contains(PROP_NAME_BODY)) {
                String body = com.articulate.sigma.utils.StringUtil.removeHTML(element.get(PROP_NAME_BODY));
                System.out.println("In MBoxReader.extractInfo() from " + body);
                //List<String> results = InterpretNumerics.getSumoTerms(body);
                //System.out.println("INFO in MBoxReader.extractInfo(): " + results);
            }
        }
    }

    /** ***************************************************************
     */
    public static void main(String[] args) {

        MBoxReader mbr = new MBoxReader();
        if (args.length > 0) {
            if (args[0].contains("-h")) {
                System.out.println("Usage: java -classpath . com.articulate.sigma.util.MBoxReader -f file");
            }
            else if (args[0].equals("-f") && args.length > 1) {
                mbr.execute(args[1]);
                mbr.extractInfo();
                //System.out.println(records);
            }
        }
    }
}
