/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        jaanus
 */

package eionet.eunis.stripes.actions;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import ro.finsiel.eunis.jrfTables.Chm62edtCountryPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtDesignationsPersist;
import ro.finsiel.eunis.jrfTables.sites.statistics.CountrySitesFactsheetDomain;
import ro.finsiel.eunis.jrfTables.sites.statistics.CountrySitesFactsheetPersist;
import ro.finsiel.eunis.search.CountryUtil;
import ro.finsiel.eunis.search.SourceDb;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.sites.statistics.StatisticsBean;
import ro.finsiel.eunis.utilities.SQLUtilities;

/**
 * Stripes action bean for a country factsheet.
 *
 * @author jaanus
 */
@UrlBinding("/countries/{eunisAreaCode}/{tab}")
@SuppressWarnings({"rawtypes", "unchecked"})
public class CountryFactsheetActionBean extends AbstractStripesAction {

    /**  */
    private static final String FACTSHEET_LAYOUT_JSP = "/stripes/countries-factsheet/country-factsheet.layout.jsp";

    /** The tables listed on this factsheet. */
    public static final Tab[] TABS = {Tab.GENERAL, Tab.DESIG_TYPES, Tab.SPECIES, Tab.HABITAT_TYPES};

    /** The country code coming from URL binding. */
    private String eunisAreaCode;

    /** The current tab of the factsheet. */
    private Tab currTab = Tab.GENERAL;

    /** The persistent country object representing the country requested. */
    private Chm62edtCountryPersist country;

    /** Regions found in this country. */
    private Vector countryRegions;

    /** Looks like a record in chm62edt_country is not always a country, so this flag is true if it's indeed a country. */
    private boolean isIndeedCountry;

    /** List of designations found for this country. */
    private List designations;

    /** Values for the designations listed in {@link #designations}, exactly in the same order. */
    private ArrayList designationsValues = new ArrayList();

    /** An instance of {@link StatisticsBean} that is populated from request parameters. Used as helper for various stuff below. */
    private StatisticsBean statisticsBean = new StatisticsBean();

    /** List of the country's sites factsheets. */
    private List countrySitesFactsheets = new ArrayList();

    /** Number of sites in this country. */
    private int nrOfSites;

    /** HTML that lists the country's sites factsheets. */
    private String sitesCountryFactsheetRowsHtml;

    /** The current tab's friendly name. */
    private String tab = Tab.GENERAL.getDisplayName();

    /**
     * Default action handler.
     *
     * @return
     */
    @DefaultHandler
    public Resolution view() {

        return new ForwardResolution(FACTSHEET_LAYOUT_JSP);
    }

    @Before(on = {"view"})
    public void beforeView() {

        // Ensure the tab is set.
        if (tab == null) {
            tab = Tab.GENERAL.getDisplayName();
        }

        // Set the current tab from its friendly string representation.
        for (int i = 0; i < TABS.length; i++) {
            if (TABS[i].getDisplayName().equals(tab)) {
                currTab = TABS[i];
                break;
            }
        }

        // If the country's area-code not given, return right away, as we have nothing to do here.
        if (StringUtils.isBlank(eunisAreaCode)) {
            return;
        }

        // Find country object by this area-code, return right away if it's not found.
        country = CountryUtil.findCountryByAreaCode(eunisAreaCode);
        if (country == null) {
            return;
        }

        // Necessary assignments for the "general" and "desig-types" tabs.
        if (currTab.equals(Tab.GENERAL) || currTab.equals(Tab.DESIG_TYPES)) {

            populateStatisticsBean();
            countryRegions = CountryUtil.findRegionsFromCountry(country.getEunisAreaCode());
            isIndeedCountry = Utilities.isCountry(country.getAreaNameEnglish());
            // Necessary assignments for the "desig-types" tab.
            if (isIndeedCountry && currTab.equals(Tab.DESIG_TYPES)) {
                loadDesignations();
            }

            loadCountrySitesFactsheets();

        }
    }

    /**
     * Loads the country's sites factsheets.
     */
    private void loadCountrySitesFactsheets() {

        StringBuffer sql = new StringBuffer();
        sql.append(" AREA_NAME_EN = '").append(country.getAreaNameEnglish()).append("'");

        SourceDb sourceDb = SourceDb.noDatabase();

        sourceDb.add(SourceDb.Database.NATURA2000,
                getContext().getRequest().getParameter("DB_NATURA2000") != null);
        sourceDb.add(SourceDb.Database.CORINE,
                getContext().getRequest().getParameter("DB_CORINE") != null);
        sourceDb.add(SourceDb.Database.DIPLOMA,
                getContext().getRequest().getParameter("DB_DIPLOMA") != null);
        sourceDb.add(SourceDb.Database.CDDA_NATIONAL,
                getContext().getRequest().getParameter("DB_CDDA_NATIONAL") != null);
        sourceDb.add(SourceDb.Database.CDDA_INTERNATIONAL,
                getContext().getRequest().getParameter("DB_CDDA_INTERNATIONAL") != null);
        sourceDb.add(SourceDb.Database.BIOGENETIC,
                getContext().getRequest().getParameter("DB_BIOGENETIC") != null);
        sourceDb.add(SourceDb.Database.EMERALD,
                getContext().getRequest().getParameter("DB_EMERALD") != null);

        if(sourceDb.isEmpty()) {
            sourceDb = SourceDb.allDatabases().remove(SourceDb.Database.NATURENET);
        }

        sql = Utilities.getConditionForSourceDB(sql, sourceDb, "chm62edt_country_sites_factsheet");

        try {
            countrySitesFactsheets = new CountrySitesFactsheetDomain().findWhere(sql.toString());
            if (countrySitesFactsheets != null && !countrySitesFactsheets.isEmpty()) {
                for (Iterator iter = countrySitesFactsheets.iterator(); iter.hasNext();) {
                    CountrySitesFactsheetPersist sitesFactsheet = (CountrySitesFactsheetPersist) iter.next();
                    nrOfSites = nrOfSites + NumberUtils.toInt(sitesFactsheet.getNumberOfSites());
                }
            }

            sitesCountryFactsheetRowsHtml =
                    Utilities.getSitesCountryFactsheetInTable(countrySitesFactsheets, getContentManagement());
        } catch (Exception e) {
            logger.debug(e, e); 
        }
    }

    /**
     * Populates from request parameters the statistics bean that will be used as helper for various operations.
     */
    private void populateStatisticsBean() {

        try {
            Map map = new HashMap();
            map.putAll(getContext().getRequest().getParameterMap());
            map.put("country", country.getAreaNameEnglish());

            putIfNotExists(map, "yearMin", "null");
            putIfNotExists(map, "yearMax", "null");
            putIfNotExists(map, "designationCat", "null");
            putIfNotExists(map, "designation", "null");
            putIfNotExists(map, "DB_NATURA2000", "null");
            putIfNotExists(map, "DB_CORINE", "null");
            putIfNotExists(map, "DB_DIPLOMA", "null");
            putIfNotExists(map, "DB_CDDA_NATIONAL", "null");
            putIfNotExists(map, "DB_BIOGENETIC", "null");
            putIfNotExists(map, "DB_EMERALD", "null");
            putIfNotExists(map, "DB_CDDA_INTERNATIONAL", "null");

            BeanUtils.populate(statisticsBean, map);

        } catch (IllegalAccessException e) {
            logger.debug(e, e); 
        } catch (InvocationTargetException e) {
            logger.debug(e, e); 
        }
    }

    /**
     *
     * @param map
     * @param key
     * @param value
     */
    private void putIfNotExists(Map map, Object key, Object value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

    /**
     * Returns the country's name as should be for display in the page title.
     *
     * @return
     */
    public String getNameForTitle() {
        if (currTab.equals(Tab.GENERAL) || currTab.equals(Tab.DESIG_TYPES)) {
            return statisticsBean == null ? "" : statisticsBean.toHumanString();
        } else {
            return country == null ? "" : country.getAreaNameEnglish();
        }
    }



    /**
     *
     *
     */
    private void loadDesignations() {

        ArrayList<Long> noSitesA = new ArrayList<Long>();
        ArrayList<Long> areaTotalA = new ArrayList<Long>();
        ArrayList<Long> areaTotalOverlapA = new ArrayList<Long>();

        Connection con = null;

        SQLUtilities sqlUtilities = getContext().getSqlUtilities();
        try {

            con = sqlUtilities.getConnection();

            populateDesignations(con, noSitesA, areaTotalA);
            populateOverlaps(con, areaTotalOverlapA);

            for (int j = 0; j < noSitesA.size(); j++) {

                Vector values = new Vector();

                if (noSitesA.get(j).longValue() != 0) {
                    values.addElement(noSitesA.get(j));
                } else {
                    values.addElement(new Long(0));
                }

                if (!areaTotalA.isEmpty() && areaTotalA.get(j).longValue() != 0) {

                    if (!areaTotalOverlapA.isEmpty() && areaTotalOverlapA.get(j).longValue() != 0) {

                        values.addElement((areaTotalA.get(j) - areaTotalOverlapA.get(j) < 0 ? new Long(0)
                        : (areaTotalA.get(j) - areaTotalOverlapA.get(j))));
                    } else {
                        values.addElement(areaTotalA.get(j));
                    }
                } else {
                    values.addElement(new Long(0));
                }

                designationsValues.add(values);
            }

        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            SQLUtilities.closeAll(con, null, null);

        }

    }

    /**
     * @param con Connection
     * @param noSitesA List of 'Total number of sites'
     * @param areaTotalA List of 'Total area(ha)'
     * @throws SQLException
     */
    private void populateDesignations(Connection con, List noSitesA, List areaTotalA) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            String sql =
                    "SELECT DESIG.ID_DESIGNATION as DESIG_ID, "
                            + " DESIG.ID_GEOSCOPE as GEO, "
                            + " DESIG.DESCRIPTION_EN as TITLE, "
                            + " count(distinct SITES.ID_SITE) as SITE_COUNT, "
                            + " sum(SITES.AREA) as TOT_AREA , "
                            + " DESIG.* "
                            + " from "
                            + " chm62edt_designations as DESIG "
                            + " inner join chm62edt_sites as SITES on (DESIG.ID_DESIGNATION=SITES.ID_DESIGNATION and DESIG.ID_GEOSCOPE=SITES.ID_GEOSCOPE) "
                            + " inner join chm62edt_nature_object_geoscope as GEO on (SITES.ID_NATURE_OBJECT=GEO.ID_NATURE_OBJECT) "
                            + " inner join chm62edt_country as CNTRY on (GEO.ID_GEOSCOPE = CNTRY.ID_GEOSCOPE) " + " where "
                            + statisticsBean.prepareSQLForFindSites() + " group by " + "DESIG.ID_DESIGNATION, DESIG.ID_GEOSCOPE;";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                noSitesA.add(rs.getLong("SITE_COUNT"));
                areaTotalA.add(rs.getLong("TOT_AREA"));
                Chm62edtDesignationsPersist persist = new Chm62edtDesignationsPersist();

                persist.setDescriptionEn(rs.getString("TITLE"));
                persist.setIdGeoscope(rs.getString("GEO"));
                persist.setDescription(rs.getString("DESCRIPTION"));
                persist.setNationalCategory(rs.getString("NATIONAL_CATEGORY"));
                persist.setIdDc(rs.getInt("ID_DC"));
                persist.setCddaSites(rs.getString("CDDA_SITES"));
                persist.setOriginalDataSource(rs.getString("ORIGINAL_DATASOURCE"));
                persist.setReferenceArea(rs.getBigDecimal("REFERENCE_AREA"));
                persist.setNationalLaw(rs.getString("NATIONAL_LAW"));
                persist.setNationalLawReference(rs.getString("NATIONAL_LAW_REFERENCE"));
                persist.setNationalLawAgency(rs.getString("NATIONAL_LAW_AGENCY"));
                persist.setDataSource(rs.getString("DATA_SOURCE"));
                persist.setReferenceNumber(rs.getBigDecimal("REFERENCE_NUMBER"));
                persist.setReferenceDate(rs.getString("REFERENCE_DATE"));
                persist.setRemark(rs.getString("REMARK"));
                persist.setRemarkSource(rs.getString("REMARK_SOURCE"));
                persist.setTotalArea(rs.getBigDecimal("TOTAL_AREA"));
                persist.setIdDesignation(rs.getString("DESIG_ID"));

                if (designations == null) {
                    designations = new ArrayList();
                }
                designations.add(persist);

            }
        } finally {
            SQLUtilities.closeAll(null, ps, rs);
        }
    }

    /**
     * @param con Connection
     * @param areaTotalOverlapA area total overlap list
     * @throws SQLException
     */
    private void populateOverlaps(Connection con, List areaTotalOverlapA) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select OVERLAP from chm62edt_sites_sites WHERE OVERLAP > 0 LIMIT 1";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            boolean hasOverlaps = rs.next();

            SQLUtilities.closeAll(null, ps, rs);
            rs = null;
            ps = null;

            if (hasOverlaps) {

                for (int i = 0; i < designations.size(); i++) {

                    Chm62edtDesignationsPersist design = (Chm62edtDesignationsPersist) designations.get(i);

                    sql =
                            "select sum(gr.overlap) from "
                                    + " (SELECT SITES_SITES.OVERLAP FROM "
                                    + " chm62edt_designations as DESIG "
                                    + " INNER JOIN chm62edt_sites AS SITES ON (SITES.ID_DESIGNATION=DESIG.ID_DESIGNATION and SITES.ID_GEOSCOPE = DESIG.ID_GEOSCOPE AND SITES.AREA>0) "
                                    + " INNER JOIN chm62edt_nature_object_geoscope as GEO on (SITES.ID_NATURE_OBJECT=GEO.ID_NATURE_OBJECT) "
                                    + " INNER JOIN chm62edt_country as CNTRY on (GEO.ID_GEOSCOPE = CNTRY.ID_GEOSCOPE) "
                                    + " INNER JOIN chm62edt_sites_sites AS SITES_SITES ON (SITES.ID_SITE = SITES_SITES.ID_SITE AND SITES_SITES.OVERLAP>0) "
                                    + " INNER JOIN chm62edt_sites AS SITES2 ON (SITES_SITES.ID_SITE_LINK = SITES2.ID_SITE AND SITES2.AREA>0) "
                                    + " where DESIG.ID_DESIGNATION='" + design.getIdDesignation() + "' "
                                    + " AND  DESIG.ID_GEOSCOPE = " + design.getIdGeoscope() + " AND "
                                    + statisticsBean.prepareSQLForFindSites()
                                    + " group by SITES.ID_SITE, SITES_SITES.ID_SITE_LINK, SITES_SITES.OVERLAP) as gr;";

                    ps = con.prepareStatement(sql);
                    rs = ps.executeQuery();
                    areaTotalOverlapA.add(rs.next() ? rs.getLong(1) : Long.valueOf(0));
                }
            }
        } finally {
            SQLUtilities.closeAll(null, ps, rs);
        }
    }

    /**
     *
     * @return
     */
    public String getEunisAreaCode() {
        return eunisAreaCode;
    }

    /**
     *
     * @param eunisAreaCode
     */
    public void setEunisAreaCode(String eunisAreaCode) {
        this.eunisAreaCode = eunisAreaCode;
    }

    /**
     *
     * @return
     */
    public Tab[] getTabs() {
        return CountryFactsheetActionBean.TABS;
    }

    /**
     *
     * @return
     */
    public Tab getCurrTab() {
        return currTab;
    }

    /**
     * @return the country
     */
    public Chm62edtCountryPersist getCountry() {
        return country;
    }

    /**
     * @return the countryRegions
     */
    public Vector getCountryRegions() {
        return countryRegions;
    }

    /**
     * @return the isIndeedCountry
     */
    public boolean isIndeedCountry() {
        return isIndeedCountry;
    }

    /**
     * @return the designations
     */
    public List getDesignations() {
        return designations;
    }

    /**
     * @return the statisticsBean
     */
    public StatisticsBean getStatisticsBean() {
        return statisticsBean;
    }

    /**
     * @return the designationsValues
     */
    public ArrayList getDesignationsValues() {
        return designationsValues;
    }

    /**
     *
     * @return
     */
    public List getCountrySitesFactsheets() {
        return countrySitesFactsheets;
    }

    /**
     *
     * @return
     */
    public int getNrOfSites() {
        return nrOfSites;
    }

    /**
     * @return the sitesCountryFactsheetRowsHtml
     */
    public String getSitesCountryFactsheetRowsHtml() {
        return sitesCountryFactsheetRowsHtml;
    }

    /**
     *
     * @param tab
     */
    public void setTab(String tab) {
        this.tab = tab;
    }

    /**
     * @author jaanus
     */
    public enum Tab {

        /** Enumerations. */
        GENERAL("General information"), DESIG_TYPES("Sites designation types"), SPECIES("Species"), HABITAT_TYPES("Habitat types");

        /** The tab's displayed title. */
        String title;

        /** The enum's camel-case name where underscores replaced by empty string, and the very first letter is lower case. */
        String displayName;

        /**
         * Constructor.
         *
         * @param title
         */
        Tab(String title) {
            this.title = title;
            displayName = name().toLowerCase().replaceAll("_", "");
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         *
         * @return
         */
        public String getDisplayName() {
            return displayName;
        }
    }

}
