package ro.finsiel.eunis.utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import eionet.eunis.dao.impl.MySqlBaseDao;
import eionet.eunis.dto.TaxonomyTreeDTO;
import eionet.eunis.util.Constants;
import org.apache.log4j.Logger;

public class EunisUtil {

    /**
     * Map for default picture (if there is no other picture in the DB)
     */
    static final Map<String, String> defaultPictures = new HashMap<String, String>();
    public static HashMap<Integer, Integer> legalStatusOrder = new HashMap<>();

    private static final Logger logger = Logger.getLogger(EunisUtil.class);

    static {
        defaultPictures.put("Algae","Algae");
        defaultPictures.put("Amphibians","Amphibians");
        defaultPictures.put("Birds","Birds");
        defaultPictures.put("Conifers","Conifers");
        defaultPictures.put("Ferns","Ferns");
        defaultPictures.put("Fishes","Fishes");
        defaultPictures.put("Flowering Plants","Flowering-plants");
        defaultPictures.put("Fungi","Fungi");
        defaultPictures.put("Invertebrates","Invertebrates");
        defaultPictures.put("Mammals","Mammals");
        defaultPictures.put("Mosses & Liverworts","Mosses-and-liverworths");
        defaultPictures.put("Protists","Protists");
        defaultPictures.put("Reptiles","Reptiles");

        /**
         * 1. Habitats Directive  2324 2325 2326 2327
         2. Birds Directive 2440 2441 2456 2457
         3. Bern Convention  1564 1565 1566 1567
         3.1 Emerald Res. 4 2442 2467
         3.2 Emerald Res. 6 2443
         4. Bonn Convention 1799 1800
         4.1 ACAP 2468 2469
         4.2 ACCOBAMS  1802
         4.3 AEWA  2447
         4.4 ASCOBANS 1803
         4.5 EuroBats  1804
         4.6 AWarbler 2470 2471
         4.7 GBustard 2472 2473
         4.8 MSeal 2474 2475
         4.9 MoU Raptors 2476 2477
         4.10 SbCurlew 2478 2479
         4.11 Sharks MoU 2480 2481
         4.12 Wadden Sea Agreement 2451

         5. CITES  1791 1792 1793
         6. EU Trade  2444 2462  2445 2463  2446 2464  2458 2465  2459 2466
         7. SPA/BD Mediterranean 1818 1819
         8. OSPAR 1832
         9. HELCOM 2455

         */
        EunisUtil.legalStatusOrder.put(2324, 100); // Habitats I
        EunisUtil.legalStatusOrder.put(2325, 200); // Habitats II
        EunisUtil.legalStatusOrder.put(2326, 300); // Habitats III
        EunisUtil.legalStatusOrder.put(2327, 400); // Habitats IV

        EunisUtil.legalStatusOrder.put(2440, 500); // Birds
        EunisUtil.legalStatusOrder.put(2482, 550); // Birds GP
        EunisUtil.legalStatusOrder.put(2441, 600); // Birds I
        EunisUtil.legalStatusOrder.put(2456, 700); // Birds II
        EunisUtil.legalStatusOrder.put(2457, 800); // Birds III

        EunisUtil.legalStatusOrder.put(1564, 900); // Bern
        EunisUtil.legalStatusOrder.put(1565, 1000); // Bern I
        EunisUtil.legalStatusOrder.put(1566, 1100); // Bern II
        EunisUtil.legalStatusOrder.put(1567, 1200); // Bern III

        EunisUtil.legalStatusOrder.put(2442, 1300); // Emerald R4
        EunisUtil.legalStatusOrder.put(2467, 1400); // Emerald R4 (new)
        EunisUtil.legalStatusOrder.put(2443, 1500); // Emerald R6

        EunisUtil.legalStatusOrder.put(1799, 1600); // Bonn
        EunisUtil.legalStatusOrder.put(1800, 1700); // Bonn

        EunisUtil.legalStatusOrder.put(2468 , 1800); // ACAP
        EunisUtil.legalStatusOrder.put(2469 , 1810); // ACAP
        EunisUtil.legalStatusOrder.put(1802, 1900); // ACCOBAMS
        EunisUtil.legalStatusOrder.put(2447, 2000); // AEWA
        EunisUtil.legalStatusOrder.put(1803, 2100); // ASCOBANS
        EunisUtil.legalStatusOrder.put(1804, 2200); // EuroBats

        EunisUtil.legalStatusOrder.put(2470, 2220); // AWarbler
        EunisUtil.legalStatusOrder.put(2471, 2230); // AWarbler

        EunisUtil.legalStatusOrder.put(2472, 2240); // GBustard
        EunisUtil.legalStatusOrder.put(2473, 2250); // GBustard

        EunisUtil.legalStatusOrder.put(2474, 2260); //  MSeal
        EunisUtil.legalStatusOrder.put(2475, 2270); // MSeal

        EunisUtil.legalStatusOrder.put(2476, 2300); // Raptors
        EunisUtil.legalStatusOrder.put(2477, 2350); // Raptors

        EunisUtil.legalStatusOrder.put(2478, 2360); // SbCurlew
        EunisUtil.legalStatusOrder.put(2479, 2370); // SbCurlew

        EunisUtil.legalStatusOrder.put(2480, 2380); // Sharks MoU
        EunisUtil.legalStatusOrder.put(2481, 2390); // Sharks MoU

        EunisUtil.legalStatusOrder.put(2451, 2400); // Wadden Sea Agreement

        EunisUtil.legalStatusOrder.put(1791, 2500); // CITES
        EunisUtil.legalStatusOrder.put(1792, 2600); // CITES
        EunisUtil.legalStatusOrder.put(1793, 2700); // CITES

        EunisUtil.legalStatusOrder.put(2444, 2800); //  EU Trade
        EunisUtil.legalStatusOrder.put(2462, 2900); //  EU Trade
        EunisUtil.legalStatusOrder.put(2445, 3000); //  EU Trade
        EunisUtil.legalStatusOrder.put(2463, 3100); //  EU Trade
        EunisUtil.legalStatusOrder.put(2446, 3200); //  EU Trade
        EunisUtil.legalStatusOrder.put(2464, 3300); //  EU Trade
        EunisUtil.legalStatusOrder.put(2458, 3400); //  EU Trade
        EunisUtil.legalStatusOrder.put(2465, 3500); //  EU Trade
        EunisUtil.legalStatusOrder.put(2459, 3600); //  EU Trade
        EunisUtil.legalStatusOrder.put(2466, 3700); //  EU Trade

        EunisUtil.legalStatusOrder.put(1818, 3800); // SPA/BD Mediterranean
        EunisUtil.legalStatusOrder.put(1819, 3900); // SPA/BD Mediterranean

        EunisUtil.legalStatusOrder.put(1832, 4000); // OSPAR
        EunisUtil.legalStatusOrder.put(2455, 4100); // HELCOM
    }

    // species groups as a list for SQL IN, to determine if a species is in Natura or just other species
    public static String SPECIES_GROUPS;

    // initialize the species groups list
    static {
        StringBuilder groups = new StringBuilder();
        for(String s : Constants.N2000_ALL_SPECIES_GROUPS){
            groups.append("'").append(s).append("'");
            groups.append(", ");
        }

        if(groups.length()>2){
            groups.replace(groups.length()-2, groups.length(),"");
        }
        SPECIES_GROUPS = groups.toString();
    }


    /**
     * Encode a string for XML or HTML.
     *
     * @param in - input string
     * @return encoded string.
     */
    public static String replaceTagsExport(String in) {

        in = (in != null ? in : "");
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            if (c == '&') {
                ret.append("&amp;");
            } else if (c == '<') {
                ret.append("&lt;");
            } else if (c == '>') {
                ret.append("&gt;");
            } else if (c == '"') {
                ret.append("&quot;");
            } else if (c == '\'') {
                ret.append("&#039;");
            } else {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    /**
     * Adds escape characters to quotes
     * @param in
     * @return
     */
    public static String replaceTagsImport(String in) {

        in = (in != null ? in : "");
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            if (c == '"') {
                ret.append("\\\"");
            } else if (c == '\'') {
                ret.append("\\\'");
            } else {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    public static String replaceTagsAuthor(String in) {

        in = in.trim();

        if (in.startsWith("(")) {
            in = in.substring(1);
        }

        int index = in.indexOf(",");

        if (index != -1) {
            in = in.substring(0, index);
        }

        if (in.endsWith(")")) {
            in = in.substring(0, in.length() - 1);
        }

        return in.trim();
    }

    /**
     * Replace { with [ and } with ]. It is actually replacing braces with brackets.
     *
     * @param in - input string
     * @return - string with replacements.
     */
    public static String replaceBrackets(String in) {

        in = (in != null ? in : "");
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            if (c == '{') {
                ret.append("[");
            } else if (c == '}') {
                ret.append("]");
            } else {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    /**
     * Replace pattern within source with the replace string.
     * @param source Source string; if null returns empty string
     * @param pattern Pattern to be replaced; if null returns the source
     * @param replace String to replace with
     * @return Replaced string
     */
    public static String replace(String source, String pattern, String replace) {
        if (source != null) {
            if(replace != null){
                if(pattern!=null && pattern.length() != 0){
                    int len = pattern.length();
                    StringBuilder sb = new StringBuilder();
                    int found = -1;
                    int start = 0;

                    while ((found = source.indexOf(pattern, start)) != -1) {
                        sb.append(source.substring(start, found));
                        sb.append(replace);
                        start = found + len;
                    }

                    sb.append(source.substring(start));

                    return sb.toString();
                } else {
                    return source;
                }
            } else {
                return source;
            }
        } else {
            return "";
        }
    }

    /**
     * A method for creating a unique Hexa-Decimal digest of a String message.
     *
     * @param src String to be digested.
     * @param algorithm Digesting algorithm (please see Java documentation for allowable values).
     * @return A unique String-typed Hexa-Decimal digest of the input message.
     */
    public static String digestHexDec(String src, String algorithm) {

        byte[] srcBytes = src.getBytes();
        byte[] dstBytes;
        StringBuilder buf = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            md.update(srcBytes);
            dstBytes = md.digest();
            md.reset();

            for (Byte byteWrapper : dstBytes) {
                String s = Integer.toHexString(byteWrapper.intValue());

                if (s.length() == 1) {
                    s = "0" + s;
                }
                buf.append(s.substring(s.length() - 2));
            }
        } catch (GeneralSecurityException e) {
            logger.debug(e, e);
        }

        return buf.toString();
    }

    /**
     * @param in
     * @return new string with replacements.
     */
    public static String replaceTags(String in) {
        return replaceTags(in, false, false);
    }

    /**
     * @param in
     * @param dontCreateHTMLAnchors
     * @return new string with replacements.
     */
    public static String replaceTags(String in, boolean dontCreateHTMLAnchors) {
        return replaceTags(in, dontCreateHTMLAnchors, false);
    }

    /**
     * @param in
     * @param dontCreateHTMLAnchors
     * @param dontCreateHTMLLineBreaks - do not change \n into &lt;br/&gt;
     * @return new string with replacements.
     */
    public static String replaceTags(
            String in, boolean dontCreateHTMLAnchors, boolean dontCreateHTMLLineBreaks) {

        in = (in != null ? in : "");
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            if (c == '<') {
                ret.append("&lt;");
            } else if (c == '>') {
                ret.append("&gt;");
            } else if (c == '"') {
                ret.append("&quot;");
            } else if (c == '\'') {
                ret.append("&#039;");
            } else if (c == '\\') {
                ret.append("&#092;");
            } else if (c == '&') {
                boolean startsEscapeSequence = false;
                int j = in.indexOf(';', i);

                if (j > 0) {
                    String s = in.substring(i, j + 1);
                    UnicodeEscapes unicodeEscapes = new UnicodeEscapes();

                    if (unicodeEscapes.isXHTMLEntity(s)
                            || unicodeEscapes.isNumericHTMLEscapeCode(s)) {
                        startsEscapeSequence = true;
                    }
                }

                if (startsEscapeSequence) {
                    ret.append(c);
                } else {
                    ret.append("&amp;");
                }
            } else if (c == '\n' && dontCreateHTMLLineBreaks == false) {
                ret.append("<br/>");
            } else if (c == '\r' && in.charAt(i + 1) == '\n'
                && dontCreateHTMLLineBreaks == false) {
                ret.append("<br/>");
                i = i + 1;
            } else {
                ret.append(c);
            }
        }

        String retString = ret.toString();

        if (dontCreateHTMLAnchors == false) {
            retString = setAnchors(retString, false, 50);
        }

        return retString;
    }

    /**
     * Finds all urls in a given string and replaces them with HTML anchors. If boolean newWindow==true then target will be a new
     * window, else no. If boolean cutLink &gt; 0 then cut the displayed link length cutLink.
     */
    public static String setAnchors(String s, boolean newWindow, int cutLink) {

        StringBuilder buf = new StringBuilder();

        StringTokenizer st = new StringTokenizer(s, " \t\n\r\f", true);

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (!isURL(token)) {
                buf.append(token);
            } else {
                StringBuilder _buf = new StringBuilder("<a ");

                if (newWindow) {
                    _buf.append("target=\"_blank\" ");
                }
                _buf.append("href=\"");
                _buf.append(token);
                _buf.append("\">");

                if (cutLink < token.length()) {
                    _buf.append(token.substring(0, cutLink)).append("...");
                } else {
                    _buf.append(token);
                }

                _buf.append("</a>");
                buf.append(_buf.toString());
            }
        }

        return buf.toString();
    }

    /**
     * Finds all urls in a given string and replaces them with HTML anchors. If boolean newWindow==true then target will be a new
     * window, else no.
     */
    public static String setAnchors(String s, boolean newWindow) {

        return setAnchors(s, newWindow, 0);
    }

    /**
     * Finds all urls in a given string and replaces them with HTML anchors with target being a new window.
     */
    public static String setAnchors(String s) {

        return setAnchors(s, true);
    }

    /**
     * Checks if the given string is a well-formed URL
     */
    public static boolean isURL(String s) {
        try {
            URL url = new URL(s);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }

    /**
     * Cut a string to given length and add three dots. If length is negative,
     * then return the full string. If string is already shorter, then don't cut.
     *
     * @param s - string to cut
     * @param len - location to cut in
     * @return new string
     */
    public static String threeDots(String s, int len) {

        if (len <= 0) {
            return s;
        }
        if (s == null || s.length() == 0) {
            return s;
        }

        if (s.length() > len) {
            StringBuilder buf = new StringBuilder(s.substring(0, len));

            buf.append("...");
            return buf.toString();
        } else {
            return s;
        }
    }

    /**
     * Checks if the gven String is an integer
     * @param in String to check
     * @return true if no NumberFormatException is thrown while trying to convert
     */
    public static boolean isNumber(String in) {

        try {

            Integer.parseInt(in);

        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    public static void writeLogMessage(String msg, boolean cmd, SQLUtilities sql) {
        if (cmd) {
            System.out.println(msg);
        } else {
            sql.addImportLogMessage(msg);
        }
    }

    public static TaxonomyTreeDTO extractTaxonomyTree(String taxonomyTree) {

        TaxonomyTreeDTO ret = new TaxonomyTreeDTO();

        StringTokenizer st = new StringTokenizer(taxonomyTree, ",");

        while (st.hasMoreTokens()) {
            StringTokenizer sts = new StringTokenizer(st.nextToken(), "*");
            String classification_id = sts.nextToken();
            String classification_level = sts.nextToken();
            String classification_name = sts.nextToken();

            if (classification_level.equalsIgnoreCase("Kingdom")) {
                ret.setKingdom(classification_name);
            } else if (classification_level.equalsIgnoreCase("Phylum")) {
                ret.setPhylum(classification_name);
            } else if (classification_level.equalsIgnoreCase("Class")) {
                ret.setDwcClass(classification_name);
            } else if (classification_level.equalsIgnoreCase("Order")) {
                ret.setOrder(classification_name);
            } else if (classification_level.equalsIgnoreCase("Family")) {
                ret.setFamily(classification_name);
            }
        }

        return ret;
    }

    /**
     * Escapes MySQL strings
     * @param text
     * @return The text with MySQL-style escapes, if needed
     */
    public static String mysqlEscapes(String text)
    {
        if(text != null)
            text = text.replaceAll("'","''");

        return text;
    }

    /**
     * Returns the link to the default (abstract) picture of a species, by group
     * @param group The species group
     * @return The link to the abstract picture, relative path from /images/species/
     */
    public static String getDefaultPicture(String group){
        //http://taskman.eionet.europa.eu/issues/18873 : only use the "birds" photo
        return "default/" + defaultPictures.get("Birds") + ".jpg";
    }

}
