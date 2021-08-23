package eionet.eunis.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eionet.eunis.dao.ISitesDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ro.finsiel.eunis.dataimport.parsers.CddaSitesImportParser;


/**
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
public class SitesDaoImpl extends MySqlBaseDao implements ISitesDao {

    private static Log logger = LogFactory.getLog(SitesDaoImpl.class);

    public SitesDaoImpl() {}

    public void deleteSites(Map<String, String> sites) throws SQLException {
        Connection con = null;
        Statement st = null;
        PreparedStatement ps7 = null;
        PreparedStatement ps8 = null;
        PreparedStatement ps9 = null;
        PreparedStatement ps10 = null;
        PreparedStatement ps11 = null;
        PreparedStatement ps12 = null;
        PreparedStatement ps13 = null;
        PreparedStatement ps14 = null;
        PreparedStatement ps15 = null;
        PreparedStatement ps16 = null;
        PreparedStatement ps17 = null;
        PreparedStatement ps18 = null;
        PreparedStatement ps19 = null;

        try {
            con = getConnection();

            st = con.createStatement();

            ps7 = con.prepareStatement(
            "DELETE FROM chm62edt_nature_object_geoscope WHERE ID_NATURE_OBJECT_LINK=?");
            ps8 = con.prepareStatement(
            "DELETE FROM chm62edt_reports WHERE ID_NATURE_OBJECT=?");
            ps9 = con.prepareStatement(
            "DELETE FROM chm62edt_nature_object_report_type WHERE ID_NATURE_OBJECT=?");
            ps10 = con.prepareStatement(
            "DELETE FROM chm62edt_nature_object_report_type WHERE ID_NATURE_OBJECT_LINK=?");
            ps11 = con.prepareStatement(
            "DELETE FROM chm62edt_nature_object_attributes WHERE ID_NATURE_OBJECT=?");
            ps12 = con.prepareStatement(
            "DELETE FROM chm62edt_tab_page_sites WHERE ID_NATURE_OBJECT=?");

            ps13 = con.prepareStatement(
            "DELETE FROM chm62edt_sites_sites WHERE ID_SITE=?");
            ps14 = con.prepareStatement(
            "DELETE FROM chm62edt_sites_sites WHERE ID_SITE_LINK=?");
            ps15 = con.prepareStatement(
            "DELETE FROM chm62edt_sites_related_designations WHERE ID_SITE=?");
            ps16 = con.prepareStatement(
            "DELETE FROM chm62edt_site_attributes WHERE ID_SITE=?");

            ps17 = con.prepareStatement(
            "DELETE FROM chm62edt_nature_object_picture WHERE ID_OBJECT=? AND NATURE_OBJECT_TYPE='Sites'");

            ps18 = con.prepareStatement(
            "DELETE FROM chm62edt_sites WHERE ID_SITE=?");
            ps19 = con.prepareStatement(
            "DELETE FROM chm62edt_nature_object WHERE ID_NATURE_OBJECT=?");

            int counter = 0;

            if (sites != null) {
                for (Iterator<String> it = sites.keySet().iterator(); it.hasNext();) {

                    String siteId = it.next();
                    logger.info("Deleting site: " + siteId);
                    String idNatureObject = sites.get(siteId);

                    if (idNatureObject == null) {
                        idNatureObject = getNatObjectId(siteId);
                    }

                    String idReportAttributesGeoscope = getReportAttributeIds(
                            idNatureObject, con,
                    "SELECT ID_REPORT_ATTRIBUTES FROM chm62edt_nature_object_geoscope WHERE ID_NATURE_OBJECT=?");
                    String idReportAttributesReports = getReportAttributeIds(
                            idNatureObject, con,
                    "SELECT ID_REPORT_ATTRIBUTES FROM chm62edt_reports WHERE ID_NATURE_OBJECT=?");
                    String idReportAttributesReportType = getReportAttributeIds(
                            idNatureObject, con,
                    "SELECT ID_REPORT_ATTRIBUTES FROM chm62edt_nature_object_report_type WHERE ID_NATURE_OBJECT=?");

                    String idReportTypeReports = getReportTypeIds(idNatureObject,
                            con,
                    "SELECT ID_REPORT_TYPE FROM chm62edt_reports WHERE ID_NATURE_OBJECT=?");
                    String idReportTypeReportType = getReportTypeIds(
                            idNatureObject, con,
                    "SELECT ID_REPORT_TYPE FROM chm62edt_nature_object_report_type WHERE ID_NATURE_OBJECT=?");

                    counter++;

                    if (idReportAttributesGeoscope != null) {
                        st.addBatch(
                                "DELETE FROM chm62edt_report_attributes WHERE ID_REPORT_ATTRIBUTES IN ("
                                + idReportAttributesGeoscope + ")");
                    }

                    if (idReportAttributesReports != null) {
                        st.addBatch(
                                "DELETE FROM chm62edt_report_attributes WHERE ID_REPORT_ATTRIBUTES IN ("
                                + idReportAttributesReports + ")");
                    }

                    if (idReportAttributesReportType != null) {
                        st.addBatch(
                                "DELETE FROM chm62edt_report_attributes WHERE ID_REPORT_ATTRIBUTES IN ("
                                + idReportAttributesReportType + ")");
                    }

                    if (idReportTypeReports != null) {
                        st.addBatch(
                                "DELETE FROM chm62edt_report_type WHERE ID_REPORT_TYPE IN ("
                                + idReportTypeReports + ")");
                    }

                    if (idReportTypeReportType != null) {
                        st.addBatch(
                                "DELETE FROM chm62edt_report_type WHERE ID_REPORT_TYPE IN ("
                                + idReportTypeReportType + ")");
                    }

                    ps7.setString(1, idNatureObject);
                    ps7.addBatch();

                    ps8.setString(1, idNatureObject);
                    ps8.addBatch();

                    ps9.setString(1, idNatureObject);
                    ps9.addBatch();

                    ps10.setString(1, idNatureObject);
                    ps10.addBatch();

                    ps11.setString(1, idNatureObject);
                    ps11.addBatch();

                    ps12.setString(1, idNatureObject);
                    ps12.addBatch();

                    ps13.setString(1, siteId);
                    ps13.addBatch();

                    ps14.setString(1, siteId);
                    ps14.addBatch();

                    ps15.setString(1, siteId);
                    ps15.addBatch();

                    ps16.setString(1, siteId);
                    ps16.addBatch();

                    ps17.setString(1, idNatureObject);
                    ps17.addBatch();

                    ps18.setString(1, siteId);
                    ps18.addBatch();

                    ps19.setString(1, idNatureObject);
                    ps19.addBatch();

                    if (counter % 10000 == 0) {
                        st.executeBatch();
                        st.clearBatch();
                        ps7.executeBatch();
                        ps7.clearParameters();
                        ps8.executeBatch();
                        ps8.clearParameters();
                        ps9.executeBatch();
                        ps9.clearParameters();
                        ps10.executeBatch();
                        ps10.clearParameters();
                        ps11.executeBatch();
                        ps11.clearParameters();
                        ps12.executeBatch();
                        ps12.clearParameters();
                        ps13.executeBatch();
                        ps13.clearParameters();
                        ps14.executeBatch();
                        ps14.clearParameters();
                        ps15.executeBatch();
                        ps15.clearParameters();
                        ps16.executeBatch();
                        ps16.clearParameters();
                        ps17.executeBatch();
                        ps17.clearParameters();
                        ps18.executeBatch();
                        ps18.clearParameters();
                        ps19.executeBatch();
                        ps19.clearParameters();
                    }
                }
            }

            if (!(counter % 10000 == 0)) {
                st.executeBatch();
                st.clearBatch();
                ps7.executeBatch();
                ps7.clearParameters();
                ps8.executeBatch();
                ps8.clearParameters();
                ps9.executeBatch();
                ps9.clearParameters();
                ps10.executeBatch();
                ps10.clearParameters();
                ps11.executeBatch();
                ps11.clearParameters();
                ps12.executeBatch();
                ps12.clearParameters();
                ps13.executeBatch();
                ps13.clearParameters();
                ps14.executeBatch();
                ps14.clearParameters();
                ps15.executeBatch();
                ps15.clearParameters();
                ps16.executeBatch();
                ps16.clearParameters();
                ps17.executeBatch();
                ps17.clearParameters();
                ps18.executeBatch();
                ps18.clearParameters();
                ps19.executeBatch();
                ps19.clearParameters();
            }

        } catch (Exception e) {
            logger.warn(e, e);
        } finally {
            closeAllResources(null, ps7, null);
            closeAllResources(null, ps8, null);
            closeAllResources(null, ps9, null);
            closeAllResources(null, ps10, null);
            closeAllResources(null, ps11, null);
            closeAllResources(null, ps12, null);
            closeAllResources(null, ps13, null);
            closeAllResources(null, ps14, null);
            closeAllResources(null, ps15, null);
            closeAllResources(null, ps16, null);
            closeAllResources(null, ps16, null);
            closeAllResources(null, ps17, null);
            closeAllResources(null, ps18, null);
            closeAllResources(null, ps19, null);
            closeAllResources(con, st, null);
        }
    }

    private String getNatObjectId(String siteId) throws SQLException {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ID_SITE = '"
            + siteId + "'";
        String natId = ExecuteSQL(query);

        return natId;
    }

    /**
     * Removes the CDDA_NATIONAL sites that are NOT in the current map
     */
    public void deleteSitesCdda(Map<String, String> sites) throws SQLException {

        Connection con = null;
        PreparedStatement ps = null;

        ResultSet rs = null;

        try {
            con = getConnection();

            String query = "SELECT ID_SITE, ID_NATURE_OBJECT FROM chm62edt_sites WHERE SOURCE_DB = 'CDDA_NATIONAL'";

            ps = con.prepareStatement(query);

            rs = ps.executeQuery();
            Map<String, String> siteIds = new HashMap<String, String>();

            while (rs.next()) {
                String idNatureObject = rs.getString("ID_NATURE_OBJECT");
                String idSite = rs.getString("ID_SITE");

                if (idNatureObject != null && idSite != null) {
                    if (sites != null && !sites.containsKey(idSite)) {
                        siteIds.put(idSite, idNatureObject);
                    }
                }
            }
            deleteSites(siteIds);

        } catch (Exception e) {
            logger.warn(e, e);
        } finally {
            closeAllResources(con, ps, rs);
        }

    }

    private String getReportAttributeIds(String objectId, Connection con, String query) throws Exception {

        String result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, objectId);
            rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("ID_REPORT_ATTRIBUTES");

                if (id != null && !id.equals("-1") && id.length() > 0) {
                    if (result != null && result.length() > 0) {
                        result += ",";
                    } else {
                        result = "";
                    }
                    result += id;
                }
            }
        } catch (Exception e) {
            logger.warn(e, e);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return result;
    }

    private String getReportTypeIds(String objectId, Connection con, String query) throws Exception {

        String result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, objectId);
            rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("ID_REPORT_TYPE");

                if (id != null && !id.equals("-1") && id.length() > 0) {
                    if (result != null && result.length() > 0) {
                        result += ",";
                    } else {
                        result = "";
                    }
                    result += id;
                }
            }
        } catch (Exception e) {
            logger.warn(e, e);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return result;
    }

    public void updateCountrySitesFactsheet() throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        try {
            con = getConnection();
            // Empty chm62edt_country_sites_factsheet table before update
            ps = con.prepareStatement(
            "DELETE FROM chm62edt_country_sites_factsheet");
            ps.executeUpdate();

            ps = con.prepareStatement(
            "SELECT DISTINCT SOURCE_DB FROM chm62edt_sites WHERE SOURCE_DB IS NOT NULL");
            rs = ps.executeQuery();
            List<String> sources = new ArrayList<String>();

            while (rs.next()) {
                String sourceDb = rs.getString("SOURCE_DB");

                if (sourceDb != null) {
                    sources.add(sourceDb);
                }
            }

            ps1 = con.prepareStatement(
            "SELECT DISTINCT AREA_NAME_EN,SURFACE FROM chm62edt_country WHERE ISO_2L<>'' AND ISO_2L IS NOT NULL AND AREA_NAME_EN IS NOT NULL");
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                String areaName = rs1.getString("AREA_NAME_EN");

                if (areaName == null) {
                    areaName = "";
                }
                double surface = rs1.getDouble("SURFACE");

                for (Iterator<String> it = sources.iterator(); it.hasNext();) {
                    String sourceDb = it.next();

                    // here we calculate no of sites
                    String sql = "SELECT Count(DISTINCT C.ID_NATURE_OBJECT) AS cnt "
                        + "FROM chm62edt_country AS A "
                        + "INNER JOIN chm62edt_nature_object_geoscope AS B ON A.ID_GEOSCOPE = B.ID_GEOSCOPE "
                        + "INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT "
                        + "WHERE A.AREA_NAME_EN = ? AND C.SOURCE_DB = ?";

                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    int noSites = 0;

                    while (rs2.next()) {
                        noSites = rs2.getInt("cnt");
                    }

                    // here we calculate no of species
                    sql = "SELECT COUNT(DISTINCT H.ID_NATURE_OBJECT) AS cnt FROM chm62edt_country AS E INNER JOIN chm62edt_nature_object_geoscope AS D ON D.ID_GEOSCOPE = E.ID_GEOSCOPE INNER JOIN chm62edt_sites AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT INNER JOIN chm62edt_species AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT "
                        + "WHERE E.AREA_NAME_EN = ? AND C.SOURCE_DB = ?";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    int noSpecies = 0;

                    while (rs2.next()) {
                        noSpecies = rs2.getInt("cnt");
                    }

                    // here we calculate no of habitats
                    sql = "SELECT COUNT(DISTINCT H.ID_NATURE_OBJECT) AS cnt FROM chm62edt_country AS E INNER JOIN chm62edt_nature_object_geoscope AS D ON D.ID_GEOSCOPE = E.ID_GEOSCOPE INNER JOIN chm62edt_sites AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT INNER JOIN chm62edt_habitat AS H "
                        + "ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT WHERE E.AREA_NAME_EN = ? AND C.SOURCE_DB = ?";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    int noHabitat = 0;

                    while (rs2.next()) {
                        noHabitat = rs2.getInt("cnt");
                    }

                    // calculate area
                    sql = "SELECT C.AREA AS AREA FROM chm62edt_country AS A "
                        + "INNER JOIN chm62edt_nature_object_geoscope AS B ON A.ID_GEOSCOPE = B.ID_GEOSCOPE "
                        + "INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT "
                        + "WHERE A.AREA_NAME_EN = ? AND C.SOURCE_DB = ? AND C.AREA IS NOT NULL AND C.AREA>0 "
                        + "GROUP BY C.ID_NATURE_OBJECT";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    double totalSizePartial = 0;
                    int no = 0;

                    while (rs2.next()) {
                        totalSizePartial = totalSizePartial
                        + rs2.getDouble("AREA");
                        no = no + 1;
                    }

                    sql = "SELECT H.OVERLAP AS OVERLAP FROM chm62edt_country AS A"
                        + " INNER JOIN chm62edt_nature_object_geoscope AS B ON A.ID_GEOSCOPE = B.ID_GEOSCOPE"
                        + " INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT"
                        + " INNER JOIN chm62edt_sites_sites AS H ON C.ID_SITE = H.ID_SITE"
                        + " INNER JOIN chm62edt_sites AS I ON H.ID_SITE_LINK = I.ID_SITE"
                        + " WHERE A.AREA_NAME_EN = ? AND C.SOURCE_DB =?"
                        + " AND H.OVERLAP>0 AND C.AREA>0 AND I.AREA>0 GROUP BY C.ID_SITE,I.ID_SITE,H.OVERLAP";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    double totalSizeOverlap = 0;

                    while (rs2.next()) {
                        totalSizeOverlap = totalSizeOverlap
                        + rs2.getDouble("OVERLAP");
                        no = no + 1;
                    }

                    double totalSize = totalSizePartial - totalSizeOverlap;
                    double avgSize = 0;

                    if (no != 0) {
                        avgSize = totalSize / no;
                    }

                    double noSitesPerKm = 0;

                    if (noSites != 0) {
                        if (surface != 0) {
                            noSitesPerKm = noSites / surface;
                        }
                    }

                    sql = "SELECT COUNT(DISTINCT C.ID_NATURE_OBJECT) AS cnt FROM chm62edt_country AS A "
                        + "INNER JOIN chm62edt_nature_object_geoscope AS B ON A.ID_GEOSCOPE = B.ID_GEOSCOPE "
                        + "INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT "
                        + "WHERE A.AREA_NAME_EN = ? AND C.SOURCE_DB = ? AND "
                        + "((C.AREA IS NOT NULL AND C.AREA>0) OR (C.LENGTH IS NOT NULL AND C.LENGTH>0))";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    double noSitesWithSurfaceAvailable = 0;

                    while (rs2.next()) {
                        noSitesWithSurfaceAvailable = rs2.getDouble("cnt");
                    }

                    double procent = 0;

                    if (noSites != 0) {
                        procent = (noSitesWithSurfaceAvailable * 100) / noSites;
                    }

                    sql = "SELECT C.AREA AS AREA FROM chm62edt_country AS A "
                        + "INNER JOIN chm62edt_nature_object_geoscope AS B ON A.ID_GEOSCOPE = B.ID_GEOSCOPE "
                        + "INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT "
                        + "WHERE A.AREA_NAME_EN = ? AND C.SOURCE_DB = ? AND C.AREA IS NOT NULL AND C.AREA>0 "
                        + "GROUP BY C.ID_NATURE_OBJECT";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    rs2 = ps2.executeQuery();
                    double sum = 0;

                    while (rs2.next()) {
                        double area = rs2.getDouble("AREA");

                        sum = sum + (area - avgSize) * (area - avgSize);
                    }

                    double standardDeviation = 0;

                    if (noSites != 0) {
                        standardDeviation = Math.sqrt(sum / noSites);
                    }

                    sql = "INSERT INTO chm62edt_country_sites_factsheet (AREA_NAME_EN, SOURCE_DB, NUMBER_OF_SITES, NUMBER_OF_SPECIES, "
                        + "NUMBER_OF_HABITATS, TOTAL_SIZE, NO_SITES_PER_SQUARE_KM, PROCENT_NO_SITES_WITH_SURFACE_AVAILABLE, AVG_SIZE, STANDARD_DEVIATION) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?)";
                    ps2 = con.prepareStatement(sql);
                    ps2.setString(1, areaName);
                    ps2.setString(2, sourceDb);
                    ps2.setInt(3, noSites);
                    ps2.setInt(4, noSpecies);
                    ps2.setInt(5, noHabitat);
                    ps2.setDouble(6, totalSize);
                    ps2.setDouble(7, noSitesPerKm);
                    ps2.setDouble(8, procent);
                    ps2.setDouble(9, avgSize);
                    ps2.setDouble(10, standardDeviation);
                    ps2.executeUpdate();
                }
            }

        } catch (Exception e) {
            logger.warn(e, e);
            throw new SQLException(e.getMessage(), e);
        } finally {
            closeAllResources(null, ps2, rs2);
            closeAllResources(null, ps1, rs1);
            closeAllResources(con, ps, rs);
        }
    }

    public void updateDesignationsTable() throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement psUpdateDesignation = null;

        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            con = getConnection();

            String updateDesignation = "UPDATE chm62edt_designations SET CDDA_SITES='Y', TOTAL_NUMBER=? WHERE ID_DESIGNATION=?";

            psUpdateDesignation = con.prepareStatement(updateDesignation);

            String query = "SELECT ID_DESIGNATION FROM chm62edt_designations WHERE SOURCE_DB = 'CDDA_NATIONAL'";
            String query2 = "";

            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                String idDesignation = rs.getString("ID_DESIGNATION");

                if (idDesignation != null && idDesignation.length() > 0) {
                    query2 = "SELECT COUNT(ID_SITE) AS CNT FROM chm62edt_sites WHERE ID_DESIGNATION=?";
                    ps2 = con.prepareStatement(query2);
                    ps2.setString(1, idDesignation);
                    rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        int cnt = rs2.getInt("CNT");

                        if (cnt > 0) {
                            psUpdateDesignation.setInt(1, cnt);
                            psUpdateDesignation.setString(2, idDesignation);
                            psUpdateDesignation.addBatch();
                        }
                    }
                }
            }
            psUpdateDesignation.executeBatch();
            psUpdateDesignation.clearParameters();

        } catch (Exception e) {
            logger.warn(e, e);
            throw new SQLException(e.getMessage(), e);
        } finally {
            closeAllResources(con, ps, rs);
            if (ps2 != null) {
                ps2.close();
            }
            if (psUpdateDesignation != null) {
                psUpdateDesignation.close();
            }
            if (rs2 != null) {
                rs.close();
            }
        }
    }
}
