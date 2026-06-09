package com.articulate.sigma.utils;

/** This is a class includes input validation/sanitization functions. */
public final class ValidationUtils {

    private ValidationUtils() {}

    /********************************************************************
     * Escapes text for safe HTML display.
     * @param o object to escape.
     * @return HTML-escaped string.
     */
    public static String escapeHtml(Object o) {

        if (o == null) return "";
        return String.valueOf(o)
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    /********************************************************************
     * Escapes text for safe HTML display.
     * @param o object to escape.
     * @return HTML-escaped string.
     */
    public static String jsonEsc(Object o) {

        if (o == null) return "";
        return String.valueOf(o)
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t");
    }

    /****************************************************************
     * Wrapper for the sanitizeInteger function that allows a default value.
     * @param s A String
     * @return Validated integer value
     */
    public static int sanitizeInteger(String s, int defaultInt) {
        if (s == null || StringUtil.emptyString(s) || !StringUtil.isInteger(s)) {
            return defaultInt;
        } else return sanitizeInteger(s);
    }

    /****************************************************************
     * Returns the integer value of a string after validating that it is
     * an integer and removing any HTML.
     * @param s A String
     * @return Validated integer value
     */
    public static int sanitizeInteger(String s) {

        if (StringUtil.emptyString(s)) return 1;
        s = StringUtil.removeHTML(s);
        if (!StringUtil.isInteger(s)) return 1;
        return Integer.parseInt(s);
    }

    /****************************************************************
     * Wrapper for the sanitizeInteger function that allows a default value.
     * @param s A String
     * @return Validated integer value
     */
    public static String sanitizeIntegerString(String s, String defaultInt) {
        
        if (s == null || StringUtil.emptyString(s) || !StringUtil.isInteger(s)) return StringUtil.removeHTML(defaultInt);
        else return sanitizeIntegerString(s);
    }

    /****************************************************************
     * Returns validated SUMO term string.
     * @param s A String
     * @return Validated SUMO term
     */
    public static String sanitizeSumoTerm(String s, String defaultString) {
        
        if (s == null || StringUtil.emptyString(s)) return StringUtil.removeHTML(defaultString);
        else return StringUtil.replaceNonIdChars(sanitizeString(s));
    }

    /****************************************************************
     * Returns the integer of a string after removing any HTML.
     * @param s A String
     * @return Validated integer value
     */
    public static String sanitizeIntegerString(String s) {

        if (StringUtil.emptyString(s) || !StringUtil.isInteger(s)) return "";
        return StringUtil.removeHTML(s);
    }

    /****************************************************************
     * Wrapper for the sanitize string function that allows a default if the string is empty.
     * @param s A String
     * @return Validated String
     */
    public static String sanitizeString(String s, String defaultString) {

        if (StringUtil.emptyString(s)) return StringUtil.removeHTML(defaultString);
        return StringUtil.removeHTML(s);
    }

    /****************************************************************
     * Returns validated String to ensure it doesn't contain any HTML or jsp tags.
     * @param s A String
     * @return Validated String
     */
    public static String sanitizeString(String s) {

        if (StringUtil.emptyString(s)) return "";
        return StringUtil.removeHTML(s);
    }

    /****************************************************************
     * Returns validated SUMO term string.
     * @param s A String
     * @return Validated SUMO term
     */
    public static String sanitizeSumoTerm(String s) {
        
        return StringUtil.replaceNonIdChars(sanitizeString(s));
    }

    /****************************************************************
     * Returns whether the given file name is a valid .kif filename.
     * @param s A String
     * @return true if valid
     */
    public static boolean isValidKifFileName(String s) {

        if (StringUtil.emptyString(s)) return false;
        return s.matches("[a-zA-Z0-9._-]+") && s.endsWith(".kif");
    }

    /****************************************************************
     * Sanitizes a KIF file name and ensures the extension is .kif.
     * @param s A String
     * @return Sanitized KIF filename
     */
    public static String santizeKifFileName(String s) {

        String cleaned = sanitizeString(s);
        if (StringUtil.emptyString(cleaned)) return ".kif";
        if (cleaned.endsWith(".kif")) return cleaned;
        int dotIndex = cleaned.lastIndexOf('.');
        if (dotIndex > 0) cleaned = cleaned.substring(0, dotIndex);
        return cleaned + ".kif";
    }
}