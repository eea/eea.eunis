package ro.finsiel.eunis.search.sites.advanced;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;

import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;


/**
 * Class used for sites advanced search.
 * @author finsiel
 */
public class SitesAdvancedSearch {

    private int SQL_LIMIT = 1000;

    private Vector Tables = new Vector();
    private Vector Operands = new Vector();
    private Vector MinValues = new Vector();
    private Vector MaxValues = new Vector();

    private static final Logger logger = Logger.getLogger(SitesAdvancedSearch.class);

    private int resultCount = 0;

    private String SourceDB = "''";

    /**
     * Ctor.
     */
    public SitesAdvancedSearch() {}

    /**
     * Setter for sourcedb property.
     * @param sourcedb sourcedb.
     */
    public void SetSourceDB(String sourcedb) {
        SourceDB = sourcedb;
    }

    /**
     * Initialization method.
     * @deprecated All DB connections should be taken from the pool
     */
    public void Init() {
    }

    /**
     * Limit the result output of the queries.
     * @param SQLLimit Limit.
     */
    public void SetSQLLimit(int SQLLimit) {
        SQL_LIMIT = SQLLimit;
    }

    private int FindCriteria(String Table, String Operand, String ValueMin, String ValueMax) {
        int pos = -1;
        int i;
        String sTable;
        String sOperand;
        String sValueMin;
        String sValueMax;

        if (!this.Tables.isEmpty()) {
            for (i = 0; i < this.Tables.size(); i++) {
                sTable = (String) this.Tables.elementAt(i);
                if (sTable.equalsIgnoreCase(Table)) {
                    sOperand = (String) this.Operands.elementAt(i);
                    sValueMin = (String) this.MinValues.elementAt(i);
                    sValueMax = (String) this.MaxValues.elementAt(i);
                    if (sOperand.equalsIgnoreCase(Operand) && sValueMin.equalsIgnoreCase(ValueMin)
                            && sValueMax.equalsIgnoreCase(ValueMax)) {
                        pos = i;
                    }
                }
            }
        }
        return pos;
    }

    /**
     * Add a new filter to the search criteria.
     * @param Table Table.
     * @param Operand Operand.
     * @param ValueMin Minimum value.
     * @param ValueMax Maximum value.
     */
    public void AddCriteria(String Table, String Operand, String ValueMin, String ValueMax) {
        int pos = -1;

        if (null == Table || null == Operand || null == ValueMin) {
            return;
        }
        if (Table.length() > 0 && Operand.length() > 0) {
            if (Operand.equalsIgnoreCase("Equal") && ValueMin.length() == 0) {
                return;
            }
            if (Operand.equalsIgnoreCase("Contains") && ValueMin.length() == 0) {
                return;
            }
            if (Operand.equalsIgnoreCase("Regex") && ValueMin.length() == 0) {
                return;
            }
            if (Operand.equalsIgnoreCase("Between") && ValueMin.length() == 0) {
                return;
            }
            if (Operand.equalsIgnoreCase("Between") && ValueMax.length() == 0) {
                return;
            }

            if (!this.Tables.isEmpty()) {
                pos = this.FindCriteria(Table, Operand, ValueMin, ValueMax);
            }
            if (pos == -1) {
                this.Tables.addElement(Table);
                this.Operands.addElement(Operand);
                this.MinValues.addElement(ValueMin);
                this.MaxValues.addElement(ValueMax);
            }
        }
    }

    /**
     * Remove a search filter.
     * @param Table Table.
     * @param Operand Relation operator.
     * @param ValueMin Minimum value.
     * @param ValueMax Maximum value.
     */
    public void RemoveCriteria(String Table, String Operand, String ValueMin, String ValueMax) {
        int pos;

        if (!this.Tables.isEmpty()) {
            pos = this.FindCriteria(Table, Operand, ValueMin, ValueMax);
            if (pos != -1) {
                this.Tables.remove(pos);
                this.Operands.remove(pos);
                this.MinValues.remove(pos);
                this.MaxValues.remove(pos);
            }
        }
    }

    /**
     * Clear all search filters.
     */
    public void ClearCriteria() {
        this.Tables.clear();
        this.Operands.clear();
        this.MinValues.clear();
        this.MaxValues.clear();
    }

    private String GetID(String value, String table, String idcolumn, String valuecolumn) {
        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "-1";

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (valuecolumn == null || valuecolumn.length() == 0) {
                valuecolumn = "NAME";
            }
            if (idcolumn == null || idcolumn.length() == 0) {
                idcolumn = "ID_" + table.toUpperCase();
            }
            SQL = "SELECT " + idcolumn + " FROM chm62edt_" + table.toLowerCase();
            SQL += " WHERE " + valuecolumn + "='" + value + "'";

            if (SQL.length() > 0) {
                ps = con.prepareStatement(SQL);
                rs = ps.executeQuery();

                if (rs.next()) {
                    result = rs.getString(1);
                }
                rs.close();
                ps.close();
            }
            con.close();
        } catch (Exception e) {
            logger.debug(e, e); 
            return result;
        }

        return result;
    }

    /**
     * Build the search filter.
     * @return WHERE search condition.
     */
    public String BuildFilter() {
        String filter = "";
        String attributesFilter = "";
        String sitesFilter = "";
        String sitesSQL = "";
        String sTable;
        String sOperand;
        String sValueMin;
        String sValueMax;
        int i;

        if (!this.Tables.isEmpty()) {

            for (i = 0; i < this.Tables.size(); i++) {
                sTable = (String) this.Tables.elementAt(i);
                sOperand = (String) this.Operands.elementAt(i);
                sValueMin = (String) this.MinValues.elementAt(i);
                sValueMax = (String) this.MaxValues.elementAt(i);

                if (sTable.equalsIgnoreCase("Name")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " WHERE NAME LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " WHERE NAME = '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " WHERE NAME REGEXP '" + sValueMin + "'";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Code")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites";
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " WHERE ID_SITE LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " WHERE ID_SITE = '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " WHERE ID_SITE REGEXP '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " WHERE ID_SITE > '" + sValueMin + "'";
                        sitesSQL += " AND ID_SITE < '" + sValueMax + "'";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("DesignationYear")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " LEFT(DESIGNATION_DATE,4) = '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " LEFT(DESIGNATION_DATE,4) REGEXP '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " LEFT(DESIGNATION_DATE,4) LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " RIGHT(DESIGNATION_DATE,4) >= " + sValueMin;
                        sitesSQL += " AND RIGHT(DESIGNATION_DATE,4) <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Area")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " AREA = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " AREA REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " AREA LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " AREA >= " + sValueMin;
                        sitesSQL += " AND AREA <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Size")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " AREA = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " AREA REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " AREA LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " AREA >= " + sValueMin;
                        sitesSQL += " AND AREA <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Length")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " 'LENGTH' = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " 'LENGTH' REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " 'LENGTH' LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " 'LENGTH' >= " + sValueMin;
                        sitesSQL += " AND 'LENGTH' <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Longitude")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " LONGITUDE = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " LONGITUDE REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " LONGITUDE LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " LONGITUDE >= " + sValueMin;
                        sitesSQL += " AND LONGITUDE <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Latitude")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " LATITUDE = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " LATITUDE REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " LATITUDE LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " LATITUDE >= " + sValueMin;
                        sitesSQL += " AND LATITUDE <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("MinimumAltitude")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " ALT_MIN = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " ALT_MIN REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " ALT_MIN LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " ALT_MIN >= " + sValueMin;
                        sitesSQL += " AND ALT_MIN <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("MaximumAltitude")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " ALT_MAX = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " ALT_MAX REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " ALT_MAX LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " ALT_MAX >= " + sValueMin;
                        sitesSQL += " AND ALT_MAX <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("MeanAltitude")) {
                    sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " ALT_MEAN = " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " ALT_MEAN REGEXP " + sValueMin;
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " ALT_MEAN LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " ALT_MEAN >= " + sValueMin;
                        sitesSQL += " AND ALT_MEAN <= " + sValueMax;
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Designation")) {
                    sitesSQL = "SELECT DISTINCT `chm62edt_sites`.`ID_NATURE_OBJECT`";
                    sitesSQL += " FROM chm62edt_sites ";
                    sitesSQL += " INNER JOIN `chm62edt_designations` ON (`chm62edt_designations`.`ID_DESIGNATION` = `chm62edt_sites`.`ID_DESIGNATION` AND `chm62edt_designations`.`ID_GEOSCOPE` = `chm62edt_sites`.`ID_GEOSCOPE`)";
                    sitesSQL += " WHERE";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " (`chm62edt_designations`.`DESCRIPTION` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " (`chm62edt_designations`.`DESCRIPTION` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " (`chm62edt_designations`.`DESCRIPTION` REGEXP '" + sValueMin + "')";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("HumanActivity")) {
                    sitesSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    sitesSQL += " FROM `chm62edt_nature_object_report_type`";
                    sitesSQL += " INNER JOIN `chm62edt_sites` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_sites`.`ID_NATURE_OBJECT`)";
                    sitesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    sitesSQL += " INNER JOIN `chm62edt_human_activity` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_human_activity`.`ID_HUMAN_ACTIVITY`)";
                    sitesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'HUMAN_ACTIVITY')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " AND (`chm62edt_human_activity`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " AND (`chm62edt_human_activity`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " AND (`chm62edt_human_activity`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " AND (`chm62edt_human_activity`.`ID_HUMAN_ACTIVITY` >= "
                                + GetID(sValueMin, "HUMAN_ACTIVITY", "", "") + ")";
                        sitesSQL += " AND (`chm62edt_human_activity`.`ID_HUMAN_ACTIVITY` <= "
                                + GetID(sValueMax, "HUMAN_ACTIVITY", "", "") + ")";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Motivation")) {
                    sitesSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    sitesSQL += " FROM `chm62edt_nature_object_report_type`";
                    sitesSQL += " INNER JOIN `chm62edt_sites` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_sites`.`ID_NATURE_OBJECT`)";
                    sitesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    sitesSQL += " INNER JOIN `chm62edt_motivation` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_motivation`.`ID_MOTIVATION`)";
                    sitesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'MOTIVATION')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " AND (`chm62edt_motivation`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " AND (`chm62edt_motivation`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " AND (`chm62edt_motivation`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sitesSQL += " AND (`chm62edt_motivation`.`ID_MOTIVATION` >= " + GetID(sValueMin, "MOTIVATION", "", "") + ")";
                        sitesSQL += " AND (`chm62edt_motivation`.`ID_MOTIVATION` <= " + GetID(sValueMax, "MOTIVATION", "", "") + ")";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Country")) {
                    sitesSQL = "SELECT DISTINCT `chm62edt_sites`.`ID_NATURE_OBJECT`";
                    sitesSQL += " FROM `chm62edt_sites`";
                    sitesSQL += " INNER JOIN `chm62edt_nature_object_geoscope` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `chm62edt_nature_object_geoscope`.`ID_NATURE_OBJECT`)";
                    sitesSQL += " INNER JOIN `chm62edt_country` ON (`chm62edt_nature_object_geoscope`.`ID_GEOSCOPE` = `chm62edt_country`.`ID_GEOSCOPE`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` LIKE '%" + sValueMin + "%')";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("RegionCode")) {
                    sitesSQL = "SELECT DISTINCT `chm62edt_sites`.`ID_NATURE_OBJECT`";
                    sitesSQL += " FROM `chm62edt_sites`";
                    sitesSQL += " INNER JOIN `chm62edt_site_attributes` ON (`chm62edt_sites`.`ID_SITE` = `chm62edt_site_attributes`.`ID_SITE`)";
                    sitesSQL += " INNER JOIN `chm62edt_region_codes` ON (`chm62edt_site_attributes`.`VALUE` = `chm62edt_region_codes`.`ID_REGION_CODE`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        sitesSQL += " WHERE (`chm62edt_region_codes`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        sitesSQL += " WHERE (`chm62edt_region_codes`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        sitesSQL += " WHERE (`chm62edt_region_codes`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (SourceDB != null && !SourceDB.equalsIgnoreCase("")) {
                        sitesSQL += " AND `chm62edt_sites`.`SOURCE_DB` IN (" + SourceDB + ")";
                    }
                }
                // execute every attribute query
                attributesFilter += " AND ID_NATURE_OBJECT IN (" + ExecuteSQL(sitesSQL, "'") + ")";
                // System.out.println("attributesFilter=" + attributesFilter);
            } // end for
            sitesFilter = " WHERE 1=1 " + attributesFilter;
            // execute final query
            sitesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_sites";
            sitesSQL += sitesFilter;
            // removed WHERE because is added automatically by JRF (findWere())
            filter += " ID_NATURE_OBJECT IN (" + ExecuteSQL(sitesSQL, "'") + ")";
        }
        // System.out.println("filter = " + filter);
        return filter;
    }

    private String ExecuteSQL(String SQL, String Delimiter) {
        String result = "";
        int resCount = 0;
        StringBuffer resultbuf = new StringBuffer();

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (SQL.length() > 0) {
                resultCount = 0;
                ResultSet rs = null;

                ps = con.prepareStatement(SQL);
                // System.out.println("Executing(SitesAdvancedSearch): "+SQL);
                rs = ps.executeQuery();

                if (rs.isBeforeFirst()) {
                    rs.last();
                    resCount = rs.getRow();
                    if (resCount > 0) {
                        rs.beforeFirst();
                        resultbuf.ensureCapacity(resCount * 6);
                    }
                }

                while (rs.next()) {
                    resultCount++;
                    if (resultCount <= SQL_LIMIT) {
                        // result+=Delimiter+rs.getString(1)+Delimiter;
                        // result+=",";
                        resultbuf.append(Delimiter).append(rs.getString(1)).append(Delimiter);
                        resultbuf.append(",");
                    }
                }
                if (resultCount >= SQL_LIMIT) {// System.out.println("<<< SQL LIMIT of "+SQL_LIMIT+" reached!. The results might not be concludent! >>>");
                }

                result = resultbuf.toString();
                if (result.length() > 0) {
                    if (result.substring(result.length() - 1).equalsIgnoreCase(",")) {
                        result = result.substring(0, result.length() - 1);
                    }
                } else {
                    result = "-1";
                }
                rs.close();
                ps.close();
            }
            con.close();
        } catch (Exception e) {
            logger.debug(e, e); 
            System.out.println("SQL = " + SQL);
            return "";
        }

        // System.out.println("Result: "+result);
        return result;
    }

    /**
     * Return the results count resulted from execution of this query.
     * @return Results count.
     */
    public int getResultCount() {
        return resultCount;
    }
}
