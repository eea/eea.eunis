package ro.finsiel.eunis.search.habitats.advanced;


import eionet.eunis.util.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;


/**
 * Class used for habitats->advanced search.
 * @author finsiel
 */
public class HabitatsAdvancedSearch {
    private int SQL_LIMIT = 1000;

    private Vector Tables = new Vector();
    private Vector Operands = new Vector();
    private Vector MinValues = new Vector();
    private Vector MaxValues = new Vector();
    ;

    private int resultCount = 0;

    /**
     * Ctor.
     */
    public HabitatsAdvancedSearch() {}

    /**
     * Initializations.
     * @deprecated All DB connections should be taken from the pool
     */
    public void Init() {
    }

    /**
     * Set the limit of retrieved results.
     * @param SQLLimit Limit of results
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
     * Add a new search criteria.
     * @param Table Name of the table.
     * @param Operand Relation operator.
     * @param ValueMin Min value.
     * @param ValueMax Max value.
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
     * Remove a search criteria.
     * @param Table Table name
     * @param Operand Relation operator.
     * @param ValueMin Min value
     * @param ValueMax Max value.
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
     * Clear all search criteria.
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
            e.printStackTrace();
            return result;
        }

        return result;
    }

    /**
     * Build where filter for SQL query.
     * @return WHERE filter.
     */
    public String BuildFilter() {
        String filter = "";
        String attributesFilter = "";
        String habitatsFilter = "";
        String habitatsSQL = "";
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

                if (sTable.equalsIgnoreCase("ScientificName")) {
                    habitatsSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_habitat";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE SCIENTIFIC_NAME LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE SCIENTIFIC_NAME = '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE SCIENTIFIC_NAME REGEXP '" + sValueMin + "'";
                    }
                }
                if (sTable.equalsIgnoreCase("Code")) {
                    habitatsSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_habitat";
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE EUNIS_HABITAT_CODE LIKE '%" + sValueMin + "%' OR CODE_ANNEX1 LIKE '%" + sValueMin
                        + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE EUNIS_HABITAT_CODE = '" + sValueMin + "' OR CODE_ANNEX1 = '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE EUNIS_HABITAT_CODE REGEXP '" + sValueMin + "' OR CODE_ANNEX1 REGEXP '" + sValueMin
                        + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " WHERE (EUNIS_HABITAT_CODE BETWEEN '%" + sValueMin + "%' AND '%" + sValueMax + "%')";
                        habitatsSQL += " OR (CODE_ANNEX1 BETWEEN '%" + sValueMin + "%' AND '%" + sValueMax + "%')";
                    }
                }
                if (sTable.equalsIgnoreCase("LegalInstruments")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_habitat`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat_class_code` ON (`chm62edt_habitat`.`ID_HABITAT` = `chm62edt_habitat_class_code`.`ID_HABITAT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_class_code` ON (`chm62edt_habitat_class_code`.`ID_CLASS_CODE` = `chm62edt_class_code`.`ID_CLASS_CODE`)";
                    habitatsSQL += " WHERE (`chm62edt_class_code`.`LEGAL` = 1)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_class_code`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_class_code`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_class_code`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                }
                if (sTable.equalsIgnoreCase("SourceDatabase")) {
                    habitatsSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_habitat WHERE ";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        if (sValueMin.equalsIgnoreCase("EUNIS")) {
                            // habitatsSQL+="CODE_ANNEX1 IS NULL";
                            habitatsSQL += "chm62edt_habitat.ID_HABITAT>=1 and  chm62edt_habitat.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'";
                        } else {
                            // habitatsSQL+="CODE_ANNEX1 IS NOT NULL";
                            habitatsSQL += "chm62edt_habitat.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'";
                        }
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        if (sValueMin.equalsIgnoreCase("EUNIS")) {
                            // habitatsSQL+="CODE_ANNEX1 IS NULL";
                            habitatsSQL += "chm62edt_habitat.ID_HABITAT >=1 and chm62edt_habitat.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'";
                        } else {
                            // habitatsSQL+="CODE_ANNEX1 IS NOT NULL";
                            habitatsSQL += "chm62edt_habitat.HABITAT_TYPE = '" + Constants.HABITAT_ANNEX1 + "' ";
                        }
                    }
                }
                if (sTable.equalsIgnoreCase("Country")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_reports`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_country` ON (`chm62edt_reports`.`ID_GEOSCOPE` = `chm62edt_country`.`ID_GEOSCOPE`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` REGEXP '" + sValueMin + "')";
                    }
                }
                if (sTable.equalsIgnoreCase("Biogeoregion")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_reports`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_biogeoregion` ON (`chm62edt_reports`.`ID_GEOSCOPE_LINK` = `chm62edt_biogeoregion`.`ID_GEOSCOPE`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE (`chm62edt_biogeoregion`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE (`chm62edt_biogeoregion`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE (`chm62edt_biogeoregion`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                }

                if (sTable.equalsIgnoreCase("LegalInstrument")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT` FROM `chm62edt_habitat`";
                    habitatsSQL += " INNER JOIN `chm62edt_nature_object` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    habitatsSQL += " INNER JOIN `dc_index` `dc_index_REFERENCE` ON (`dc_index`.`REFERENCE` = `dc_index_REFERENCE`.`ID_DC`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE (`dc_index`.`TITLE` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE (`dc_index`.`TITLE` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE (`dc_index`.`TITLE` LIKE '%" + sValueMin + "%')";
                    }
                }

                if (sTable.equalsIgnoreCase("Author")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT` FROM `chm62edt_habitat`";
                    habitatsSQL += " INNER JOIN `chm62edt_nature_object` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE (`dc_index`.`SOURCE` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE (`dc_index`.`SOURCE` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE (`dc_index`.`SOURCE` LIKE '%" + sValueMin + "%')";
                    }
                }

                if (sTable.equalsIgnoreCase("Title")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT` FROM `chm62edt_habitat`";
                    habitatsSQL += " INNER JOIN `chm62edt_nature_object` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " WHERE (`dc_index`.`TITLE` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " WHERE (`dc_index`.`TITLE` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " WHERE (`dc_index`.`TITLE` LIKE '%" + sValueMin + "%')";
                    }
                }

                if (sTable.equalsIgnoreCase("Altitude")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_altitude` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_altitude`.`ID_ALTITUDE`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'ALTITUDE')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_altitude`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_altitude`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_altitude`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_altitude`.`ID_ALTITUDE` >= " + GetID(sValueMin, "ALTITUDE", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_altitude`.`ID_ALTITUDE` <= " + GetID(sValueMax, "ALTITUDE", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Chemistry")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_chemistry` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_chemistry`.`ID_CHEMISTRY`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'CHEMISTRY')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_chemistry`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_chemistry`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_chemistry`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_chemistry`.`ID_CHEMISTRY` >= " + GetID(sValueMin, "CHEMISTRY", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_chemistry`.`ID_CHEMISTRY` <= " + GetID(sValueMax, "CHEMISTRY", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Climate")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_climate` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_climate`.`ID_CLIMATE`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'CLIMATE')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_climate`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_climate`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_climate`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_climate`.`ID_CLIMATE` >= " + GetID(sValueMin, "CLIMATE", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_climate`.`ID_CLIMATE` <= " + GetID(sValueMax, "CLIMATE", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Cover")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_cover` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_cover`.`ID_COVER`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'COVER')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_cover`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_cover`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_cover`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_cover`.`ID_COVER` >= " + GetID(sValueMin, "COVER", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_cover`.`ID_COVER` <= " + GetID(sValueMax, "COVER", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Depth")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_depth` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_depth`.`ID_DEPTH`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'DEPTH')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_depth`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_depth`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_depth`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_depth`.`ID_DEPTH` >= " + GetID(sValueMin, "DEPTH", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_depth`.`ID_DEPTH` <= " + GetID(sValueMax, "DEPTH", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Geomorph")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_geomorph` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_geomorph`.`ID_GEOMORPH`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'GEOMORPH')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_geomorph`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_geomorph`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_geomorph`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_geomorph`.`ID_GEOMORPH` >= " + GetID(sValueMin, "GEOMORPH", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_geomorph`.`ID_GEOMORPH` <= " + GetID(sValueMax, "GEOMORPH", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Humidity")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_humidity` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_humidity`.`ID_HUMIDITY`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'HUMIDITY')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_humidity`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_humidity`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_humidity`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_humidity`.`ID_HUMIDITY` >= " + GetID(sValueMin, "HUMIDITY", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_humidity`.`ID_HUMIDITY` <= " + GetID(sValueMax, "HUMIDITY", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("LifeForm")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_life_form` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_life_form`.`ID_LIFE_FORM`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'LIFE_FORM')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_life_form`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_life_form`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_life_form`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_life_form`.`ID_LIFE_FORM` >= " + GetID(sValueMin, "ID_LIFE_FORM", "", "")
                        + ")";
                        habitatsSQL += " AND (`chm62edt_life_form`.`ID_LIFE_FORM` <= " + GetID(sValueMax, "ID_LIFE_FORM", "", "")
                        + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("LightIntensity")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_light_intensity` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_light_intensity`.`ID_LIGHT_INTENSITY`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'LIGHT_INTENSITY')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_light_intensity`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_light_intensity`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_light_intensity`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_light_intensity`.`ID_LIGHT_INTENSITY` >= "
                            + GetID(sValueMin, "LIGHT_INTENSITY", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_light_intensity`.`ID_LIGHT_INTENSITY` <= "
                            + GetID(sValueMax, "LIGHT_INTENSITY", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Marine")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_marine` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_marine`.`ID_MARINE`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'MARINE')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_marine`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_marine`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_marine`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_marine`.`ID_MARINE` >= " + GetID(sValueMin, "MARINE", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_marine`.`ID_MARINE` <= " + GetID(sValueMax, "MARINE", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Salinity")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_salinity` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_salinity`.`ID_SALINITY`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'SALINITY')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_salinity`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_salinity`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_salinity`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_salinity`.`ID_SALINITY` >= " + GetID(sValueMin, "SALINITY", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_salinity`.`ID_SALINITY` <= " + GetID(sValueMax, "SALINITY", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Spatial")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_spatial` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_spatial`.`ID_SPATIAL`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'SPATIAL')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_spatial`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_spatial`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_spatial`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_spatial`.`ID_SPATIAL` >= " + GetID(sValueMin, "SPATIAL", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_spatial`.`ID_SPATIAL` <= " + GetID(sValueMax, "SPATIAL", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Substrate")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_substrate` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_substrate`.`ID_SUBSTRATE`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'SUBSTRATE')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_substrate`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_substrate`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_substrate`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_substrate`.`ID_SUBSTRATE` >= " + GetID(sValueMin, "SUBSTRATE", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_substrate`.`ID_SUBSTRATE` <= " + GetID(sValueMax, "SUBSTRATE", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Temporal")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_temporal` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_temporal`.`ID_TEMPORAL`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'TEMPORAL')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_temporal`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_temporal`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_temporal`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_temporal`.`ID_TEMPORAL` >= " + GetID(sValueMin, "TEMPORAL", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_temporal`.`ID_TEMPORAL` <= " + GetID(sValueMax, "TEMPORAL", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Tidal")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_tidal` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_tidal`.`ID_TIDAL`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'TIDAL')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_tidal`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_tidal`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_tidal`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_tidal`.`ID_TIDAL` >= " + GetID(sValueMin, "TIDAL", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_tidal`.`ID_TIDAL` <= " + GetID(sValueMax, "TIDAL", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Water")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_water` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_water`.`ID_WATER`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'WATER')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_water`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_water`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_water`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_water`.`ID_WATER` >= " + GetID(sValueMin, "WATER", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_water`.`ID_WATER` <= " + GetID(sValueMax, "WATER", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Usage")) {
                    habitatsSQL = "SELECT DISTINCT `chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT`";
                    habitatsSQL += " FROM `chm62edt_nature_object_report_type`";
                    habitatsSQL += " INNER JOIN `chm62edt_habitat` ON (`chm62edt_nature_object_report_type`.`ID_NATURE_OBJECT` = `chm62edt_habitat`.`ID_NATURE_OBJECT`)";
                    habitatsSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_nature_object_report_type`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    habitatsSQL += " INNER JOIN `chm62edt_usage` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_usage`.`ID_USAGE`)";
                    habitatsSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'USAGE')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        habitatsSQL += " AND (`chm62edt_usage`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        habitatsSQL += " AND (`chm62edt_usage`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        habitatsSQL += " AND (`chm62edt_usage`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        habitatsSQL += " AND (`chm62edt_usage`.`ID_USAGE` >= " + GetID(sValueMin, "USAGE", "", "") + ")";
                        habitatsSQL += " AND (`chm62edt_usage`.`ID_USAGE` <= " + GetID(sValueMax, "USAGE", "", "") + ")";
                    }
                }

                habitatsSQL += " AND (chm62edt_habitat.ID_HABITAT<>'-1' AND chm62edt_habitat.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "') ";
                // execute every attribute query
                attributesFilter += " AND ID_NATURE_OBJECT IN (" + ExecuteSQL(habitatsSQL, "") + ")";
            } // end for
            habitatsFilter = " WHERE 1=1 " + attributesFilter;
            // execute final query
            habitatsSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_habitat";
            habitatsSQL += habitatsFilter;
            // removed WHERE because is added automatically by JRF (findWere())
            filter += " ID_NATURE_OBJECT IN (" + ExecuteSQL(habitatsSQL, "") + ")";
        }
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
                // System.out.println("Executing: "+SQL);
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
            System.out.println("SQL = " + SQL);
            e.printStackTrace();
            return "";
        }

        return result;
    }

    /**
     * Count the number of found habitats.
     * @return Results of count.
     */
    public int getResultCount() {
        return resultCount;
    }
}
