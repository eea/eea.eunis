package ro.finsiel.eunis.search.sites;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.jrfTables.*;
import ro.finsiel.eunis.jrfTables.sites.designation_code.DesignationDomain;
import ro.finsiel.eunis.jrfTables.sites.factsheet.SitesDesignationsDomain;
import ro.finsiel.eunis.search.Utilities;

import java.util.List;
import java.util.Vector;


/**
 * Utility class used in sites search.
 *
 * @author finsiel
 */
public class SitesSearchUtility {

    /**
     * Number for sites allowed to be displayed simultaneously in a map.
     */
    public static int SITES_PER_MAP = 300;

    private static final Logger logger = Logger.getLogger(SitesSearchUtility.class);

    /**
     * Parse date according to database type.
     *
     * @param date     Year to be parsed.
     * @param sourceDB Source DB.
     * @return Year.
     */
    public static String parseDesignationYear(String date, String sourceDB) {
        String ret = date;

        if (date == null) {
            return "";
        }
        if (sourceDB.equalsIgnoreCase("CDDA_NATIONAL")) {
            if (date.lastIndexOf("/") < 0 && date.length() > 4) {
                ret = date.substring(0, 4);
            }
        }
        if (sourceDB.equalsIgnoreCase("BIOGENETIC")) {
            if (date.length() >= 4) {
                ret = date.substring(0, 4);
            }
        }
        if (sourceDB.equalsIgnoreCase("CDDA_INTERNATIONAL")) {
            if (date.length() >= 4) {
                ret = date.substring(date.length() - 4, date.length());
            }
        }
        if (sourceDB.equalsIgnoreCase("CORINE")) {
            if (date.length() >= 4) {
                ret = date.substring(0, 4);
            }
        }
        if (sourceDB.equalsIgnoreCase("NATURA2000")) {
            if (date.length() >= 4) {
                ret = date.substring(0, 4);
            }
        }
        if (sourceDB.equalsIgnoreCase("DIPLOMA")) {
            if (date.length() >= 4) {
                ret = date.substring(0, 4);
            }
        }
        if (sourceDB.equalsIgnoreCase("NATURENET")) {
            if (date.length() >= 4) {
                ret = date.substring(0, 4);
            }
        }
        if (sourceDB.equalsIgnoreCase("EMERALD")) {
            if (date.length() >= 4) {
                ret = date.substring(0, 4);
            }
        }
        return ret;
    }

    /**
     * Retrieve the list of designation matching a criterion.<br />
     * This method implements: SELECT * FROM chm62edt_designations WHERE sql LIMIT 0, Utilities.MAX_POPUP_RESULTS.
     *
     * @param where SQL where.
     * @return A list of Chm62edtDesignationsPersist objects.
     */
    public static List findDesignationsWhere(String where) {
        List results = new Vector();
        String sql = where;

        sql += " LIMIT 0, " + Utilities.MAX_POPUP_RESULTS;


        try {
            results = new Chm62edtDesignationsDomain().findWhere(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
        }
        return results;
    }

    /**
     * Validate that site coordinates are correct in format.
     * @param longitude Longitude
     * @param longDeg Longitude
     * @param longMin Longitude
     * @param longSec Longitude
     * @param latitude Latitude
     * @param latDeg Latitude
     * @param latMin Latitude
     * @param latSec Latitude
     * @return true if coordinates are valid (not null, not -1 etc.)
     */
    public static boolean validateCoordinates(String longitude, String longDeg, String longMin, String longSec,
            String latitude, String latDeg, String latMin, String latSec) {
        boolean ret = false;

        if (longitude != null && longDeg != null && longMin != null && longSec != null && latitude != null && latDeg != null
                && latMin != null && latSec != null) {
            if (longitude.equalsIgnoreCase("E") || longitude.equalsIgnoreCase("W") && latitude.equalsIgnoreCase("N")
                    || latitude.equalsIgnoreCase("S")) {
                System.out.println("longitude/latitude are OK...");
                if (!longDeg.equalsIgnoreCase("-1") && !longMin.equalsIgnoreCase("-1") && !longSec.equalsIgnoreCase("-1")
                        && !latDeg.equalsIgnoreCase("-1") && !latMin.equalsIgnoreCase("-1") && !latSec.equalsIgnoreCase("-1")) {
                    int iLongDeg = Utilities.checkedStringToInt(longDeg, 0);
                    int iLongMin = Utilities.checkedStringToInt(longMin, 0);
                    int iLongSec = Utilities.checkedStringToInt(longSec, 0);

                    int iLatDeg = Utilities.checkedStringToInt(latDeg, 0);
                    int iLatMin = Utilities.checkedStringToInt(latMin, 0);
                    int iLatSec = Utilities.checkedStringToInt(latSec, 0);

                    if (iLongDeg != 0 && iLongMin != 0 && iLongSec != 0 && iLatDeg != 0 && iLatMin != 0 && iLatSec != 0) {
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Formats the latitude for display
     * @param latitude
     * @return The formatted latitude
     */
    public static String formatLatitude(String latitude){
        if(latitude != null && latitude.endsWith("000"))
            return latitude.substring(0, latitude.length()-3);
        return latitude;
    }

    /**
     * Formats the longitude for display
     * @param longitude
     * @return The formatted longitude
     */
    public static String formatLongitude(String longitude){
        if(longitude != null && longitude.endsWith("000"))
            return longitude.substring(0, longitude.length()-3);
        return longitude;
    }

    /**
     * Formats the given latitude for PDF output
     * @param latitude
     * @return The formatted latitude
     */
    public static String formatPDFLatitude(String latitude){
        // todo: check for detailed implementation
        return formatLatitude(latitude);
    }

    /**
     * Formats the given longitude for PDF output
     * @param longitude
     * @return The formatted longitude
     */
    public static String formatPDFLongitude(String longitude){
        // todo: check for detailed implementation
        return formatLongitude(longitude);
    }

    /**
     * Take the coordinates for a list of sites and put them in a string, separated by comma. Used for retrieve maps
     * from map server.
     *
     * @param sites List of sites (CoordinatesProvider objects).
     * @return String with coordinates separated by comma.
     */
    public static String computeCoordinatesForSites(List sites) {
        StringBuffer result = new StringBuffer();

        if (null == sites || sites.isEmpty()) {
            return "";
        }
        for (int i = 0; i < sites.size(); i++) {
            CoordinatesProvider site = (CoordinatesProvider) sites.get(i);
            String coordinate = getCoordinateForSite(site);

            if (null != coordinate) {
                result.append(coordinate);
                String siteName = site.getName();

                if (siteName.length() > 5) {
                    siteName = siteName.substring(0, 5) + "...";
                }
                result.append(":-2:" + siteName);
                if (i < sites.size() - 1) {
                    result.append(",");
                }
            }
        }
        return result.toString();
    }

    /**
     * format the coordinates for a single site (in the format Latitude:Longitude).
     *
     * @param site Site.
     * @return Formated coordinate.
     */
    public static String getCoordinateForSite(CoordinatesProvider site) {
        Utilities.startTimer();
        String result = null;
        String latDeg = "";
        String longDeg = "";

        if (null != site.getLongitude() && site.getLongitude().length() > 0 && !site.getLongitude().equalsIgnoreCase("n/a")) {
            longDeg = site.getLongitude();
            if (null != site.getLatitude() && site.getLatitude().length() > 0 && !site.getLatitude().equalsIgnoreCase("n/a")) {
                latDeg = site.getLatitude();
                result = "" + longDeg.substring(0, 5) + ":" + latDeg.substring(0, 5);
                return result;
            }
        }

        return result;
    }

    /**
     * Translate the SOURCE_DB field from chm62edt_sites in human readable language.
     *
     * @param sourceDB Source db.
     * @return Source database.
     */
    public static String translateSourceDB(String sourceDB) {
        if (null == sourceDB) {
            return "n/a";
        }
        String result = sourceDB.replaceAll("CDDA_NATIONAL", "CDDA National").replaceAll("CDDA_INTERNATIONAL", "CDDA International").replaceAll("NATURA2000", "Natura 2000").replaceAll("CORINE", "Corine").replaceAll("DIPLOMA", "European diploma").replaceAll("BIOGENETIC", "Biogenetic reserves").replaceAll("NATURENET", "NatureNet").replaceAll(
                "EMERALD", "Emerald");

        return result;
    }

    /**
     * Translate the SOURCE_DB field from chm62edt_sites in SQL compatible language.
     *
     * @param sourceDB The source database in human language
     * @return The string identifying the DB within database (ie. CDDA National corresponds to CDDA_NATIONAL).
     */
    public static String translateSourceDBInvert(String sourceDB) {
        if (null == sourceDB) {
            return sourceDB;
        }
        if (sourceDB.equalsIgnoreCase("CDDA National")) {
            return "CDDA_NATIONAL";
        }
        if (sourceDB.equalsIgnoreCase("CDDA International")) {
            return "CDDA_INTERNATIONAL";
        }
        if (sourceDB.equalsIgnoreCase("Natura 2000")) {
            return "NATURA2000";
        }
        if (sourceDB.equalsIgnoreCase("Corine biotopes")) {
            return "CORINE";
        }
        if (sourceDB.equalsIgnoreCase("European diploma")) {
            return "DIPLOMA";
        }
        if (sourceDB.equalsIgnoreCase("Biogenetic reserve")) {
            return "BIOGENETIC";
        }
        if (sourceDB.equalsIgnoreCase("NatureNet")) {
            return "NATURENET";
        }
        if (sourceDB.equalsIgnoreCase("Emerald")) {
            return "EMERALD";
        }
        return sourceDB;
    }

    /**
     * Compute the distance between two sites.
     *
     * @param site1 Site 1
     * @param site2 Site 2
     * @return Distance.
     */
    public static float distanceBetweenSites(Chm62edtSitesPersist site1, Chm62edtSitesPersist site2) {
        float site1X = Utilities.checkedStringToFloat(site1.getLongitude(), 0);
        float site2X = Utilities.checkedStringToFloat(site2.getLongitude(), 0);
        float site1Y = Utilities.checkedStringToFloat(site1.getLatitude(), 0);
        float site2Y = Utilities.checkedStringToFloat(site2.getLatitude(), 0);

        float xDelta = (Math.abs(site2X - site1X));
        float yDelta = (Math.abs(site2Y - site1Y));
        float distance = (xDelta * xDelta) + (yDelta * yDelta);

        distance = (float) Math.sqrt(distance);
        return distance * 115;
    }

    /**
     * This method finds the designation for a site, given its IdDesignation and IdGeoscope.<br />
     * This method also filters invalid designations names (those who have the DESCRIPTION column empty).
     * <br />
     * Implementing SQL is:
     * <br />
     * SELECT * FROM chm62edt_designations B" +
     * WHERE B.ID_DESIGNATION='" + idDesignation + "' AND B.ID_GEOSCOPE=" + idGeoscope + " ORDER BY B.DESCRIPTION, B.DESCRIPTION_EN, B.DESCRIPTION_FR";
     *
     * @param idDesignation idDesignation of his designation.
     * @param idGeoscope    idGeoscope of his designation.
     * @return A list of Chm62edtDesignationsPersist objects.
     */
    public static List findDesignationsForSite(String idDesignation, String idGeoscope) {
        List results = new Vector();
        String listFields = "ID_DESIGNATION,ID_GEOSCOPE,ID_DC,DESCRIPTION,DESCRIPTION_EN,DESCRIPTION_FR,ORIGINAL_DATASOURCE,"
                + "CDDA_SITES,REFERENCE_AREA,TOTAL_AREA,NATIONAL_LAW,NATIONAL_CATEGORY,NATIONAL_LAW_REFERENCE,NATIONAL_LAW_AGENCY,"
                + "DATA_SOURCE,TOTAL_NUMBER,REFERENCE_NUMBER,REFERENCE_DATE,REMARK,REMARK_SOURCE";
        String sql = "SELECT " + listFields + " FROM chm62edt_designations B" + " WHERE B.ID_DESIGNATION='" + idDesignation
                + "' AND B.ID_GEOSCOPE=" + idGeoscope + " ORDER BY B.DESCRIPTION, B.DESCRIPTION_EN, B.DESCRIPTION_FR";

        try {
            results = new Chm62edtDesignationsDomain().findCustom(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List tempList = new Vector();

        // Remove the invalid designations
        for (int i = 0; i < results.size(); i++) {
            Chm62edtDesignationsPersist designation = (Chm62edtDesignationsPersist) results.get(i);

            if (!designation.getDescription().equalsIgnoreCase("") || !designation.getDescriptionEn().equalsIgnoreCase("")
                    || !designation.getDescriptionFr().equalsIgnoreCase("")) {
                tempList.add(designation);
            }
        }
        results = tempList;
        return results;
    }

    /**
     * Retrieve site designations as comma separated string.
     * @param idDesignation ID_DESIGN
     * @param idGeoscope ID_GEOSCOPE
     * @return Designations
     */
    public static String siteDesignationsAsCommaSeparatedString(String idDesignation, String idGeoscope) {
        List results = new Vector();
        String listFields = "ID_DESIGNATION,ID_GEOSCOPE,ID_DC,DESCRIPTION,DESCRIPTION_EN,DESCRIPTION_FR,ORIGINAL_DATASOURCE,"
                + "CDDA_SITES,REFERENCE_AREA,TOTAL_AREA,NATIONAL_LAW,NATIONAL_CATEGORY,NATIONAL_LAW_REFERENCE,NATIONAL_LAW_AGENCY,"
                + "DATA_SOURCE,TOTAL_NUMBER,REFERENCE_NUMBER,REFERENCE_DATE,REMARK,REMARK_SOURCE";
        String sql = "SELECT " + listFields + " FROM chm62edt_designations B" + " WHERE B.ID_DESIGNATION='" + idDesignation
                + "' AND B.ID_GEOSCOPE=" + idGeoscope + " ORDER BY B.DESCRIPTION, B.DESCRIPTION_EN, B.DESCRIPTION_FR";

        try {
            results = new Chm62edtDesignationsDomain().findCustom(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String result = "";

        for (int i = 0; i < results.size(); i++) {
            Chm62edtDesignationsPersist designation = (Chm62edtDesignationsPersist) results.get(i);

            if (!designation.getDescription().equals("")) {
                result += designation.getDescription();
            } else if (!designation.getDescriptionEn().equalsIgnoreCase("")) {
                result += designation.getDescriptionEn();
            } else if (!designation.getDescriptionFr().equalsIgnoreCase("")) {
                result += designation.getDescriptionFr();
            }
            if (i < results.size() - 1) {
                result += ",";
            }
        }
        return result;
    }

    /**
     * Find sites specified by a designation.
     *
     * @param idDesignation ID_DESIGNATION.
     * @param geoscope      Country ID_GEOSCOPE (three letters).
     * @return List of DesignationPersist objects.
     */
    public static List findSitesForDesignation(String idDesignation, String geoscope) {
        List result = new Vector();

        try {
            List tempList = new DesignationDomain().findWhere(
                    " J.ID_DESIGNATION='" + idDesignation + "' AND J.ID_GEOSCOPE=" + geoscope + " AND J.ID_GEOSCOPE=C.ID_GEOSCOPE"
                    + " GROUP BY C.ID_NATURE_OBJECT,G.AREA_NAME_EN");

            if (tempList != null && tempList.size() > 0) {
                result = tempList;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Find designations for a site (displayed in factsheet).
     *
     * @param idSite ID_SITE.
     * @return List of SitesDesignationsPersist.
     */
    public static List findDesignationsForSitesFactsheet(String idSite) {
        List result = new Vector();

        try {
            List tempList = new SitesDesignationsDomain().findCustom(
                    "SELECT A.ID_SITE, A.ID_DESIGNATION,"
                            + " B.DESCRIPTION,B.DESCRIPTION_EN,B.DESCRIPTION_FR,C.AREA_NAME_EN,B.ORIGINAL_DATASOURCE,A.ID_GEOSCOPE, B.NATIONAL_CATEGORY"
                            + " FROM chm62edt_sites AS A"
                            + " INNER JOIN chm62edt_designations AS B ON (A.ID_DESIGNATION = B.ID_DESIGNATION AND A.ID_GEOSCOPE = B.ID_GEOSCOPE)"
                            + " INNER JOIN chm62edt_country AS C ON (A.ID_GEOSCOPE = C.ID_GEOSCOPE)" + " WHERE A.ID_SITE = '"
                            + idSite + "'" + " GROUP BY A.ID_DESIGNATION,C.AREA_NAME_EN,B.DESCRIPTION");

            if (tempList != null && tempList.size() > 0) {
                result = tempList;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Find designations 'INHD'.
     * @return List of Chm62edtDesignationsPersist objects
     */
    public static List findDesignationsTypeC() {
        List results = new Vector();

        try {
            results = new Chm62edtDesignationsDomain().findWhere("ID_DESIGNATION='INBD' OR ID_DESIGNATION='INHD'");
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
        }
        return results;
    }

    /**
     * Find site type.
     * @param idSite site id
     * @return Site type in english
     */
    public static String getSiteType(String idSite) {
        List results = new Vector();
        String sType = "";

        try {
            results = new Chm62edtSitesAttributesDomain().findWhere(" NAME='TYPE' AND ID_SITE = '" + idSite + "'");
            if (results.size() > 0) {
                Chm62edtSitesAttributesPersist t = (Chm62edtSitesAttributesPersist) results.get(0);

                sType = t.getValue();
            }
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
        }
        return sType;
    }

}
