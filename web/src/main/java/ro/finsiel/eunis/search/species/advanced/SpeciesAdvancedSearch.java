package ro.finsiel.eunis.search.species.advanced;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


/**
 * Class used for species advanced search.
 * @author finsiel
 */
public class SpeciesAdvancedSearch {
    private int SQL_LIMIT = 1000;

    private Vector Tables = new Vector();
    private Vector Operands = new Vector();
    private Vector MinValues = new Vector();
    private Vector MaxValues = new Vector();

    private static final Logger logger = Logger.getLogger(SpeciesAdvancedSearch.class);

    private int resultCount = 0;

    /**
     * Creates new SpeciesAdvancedSearch.
     */
    public SpeciesAdvancedSearch() {}

    /**
     * Initialization method.
     * @deprecated All DB connections should be taken from the pool
     */
    public void Init() {
    }

    /**
     * Limit the results output to the specified value.
     * @param SQLLimit Limit of returned rows.
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
     * Add new search criteria.
     * @param Table Table name.
     * @param Operand relation operator.
     * @param ValueMin Minimum value
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
                // System.out.println("Tables contain something");
                pos = this.FindCriteria(Table, Operand, ValueMin, ValueMax);
            }
            if (pos == -1) {
                // System.out.println("add criteria");
                this.Tables.addElement(Table);
                this.Operands.addElement(Operand);
                this.MinValues.addElement(ValueMin);
                this.MaxValues.addElement(ValueMax);
            }
        }
    }

    /**
     * Remove an search criteria.
     * @param Table Table name.
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
     * Remove all search criteria.
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
     * Build the condition for search.
     * @return SQL.
     * @throws ClassNotFoundException From JDBC.
     * @throws SQLException Feom JDBC.
     */
    public String BuildFilter() throws ClassNotFoundException, SQLException {
        String filter = "";
        String attributesFilter = "";
        String speciesFilter = "";
        String speciesSQL = "";
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
                    speciesSQL = "SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_species";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " WHERE SCIENTIFIC_NAME LIKE '%" + sValueMin + "%'";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " WHERE SCIENTIFIC_NAME = '" + sValueMin + "'";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " WHERE SCIENTIFIC_NAME REGEXP '" + sValueMin + "'";
                    }
                }

                if (sTable.equalsIgnoreCase("VernacularName")) {
                    speciesSQL = "SELECT DISTINCT chm62edt_reports.ID_NATURE_OBJECT FROM chm62edt_reports ";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
                    speciesSQL += " WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
                    speciesSQL += " AND";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " (chm62edt_report_attributes.VALUE LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " (chm62edt_report_attributes.VALUE = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regexp")) {
                        speciesSQL += " (chm62edt_report_attributes.VALUE REGEXP '" + sValueMin + "')";
                    }
                }

                if (sTable.equalsIgnoreCase("Group")) {
                    speciesSQL = "SELECT DISTINCT ID_NATURE_OBJECT";
                    speciesSQL += " FROM `chm62edt_species`";
                    speciesSQL += " INNER JOIN `chm62edt_group_species` ON (`chm62edt_species`.`ID_GROUP_SPECIES` = `chm62edt_group_species`.`ID_GROUP_SPECIES`)";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " WHERE (`chm62edt_group_species`.`COMMON_NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " WHERE (`chm62edt_group_species`.`COMMON_NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " WHERE (`chm62edt_group_species`.`COMMON_NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " WHERE ID_GROUP_SPECIES >= " + GetID(sValueMin, "GROUP_SPECIES", "", "COMMON_NAME");
                        speciesSQL += " AND ID_GROUP_SPECIES >= " + GetID(sValueMax, "GROUP_SPECIES", "", "COMMON_NAME");
                    }
                }

                if (sTable.equalsIgnoreCase("Author")) {
                    String SQLWhere = "";

                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        SQLWhere = " (`dc_index`.`SOURCE` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        SQLWhere = " (`dc_index`.`SOURCE` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        SQLWhere = " (`dc_index`.`SOURCE` LIKE '%" + sValueMin + "%')";
                    }
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `chm62edt_reports` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_reports`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_reports`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE";
                    speciesSQL += "      (`chm62edt_report_type`.`LOOKUP_TYPE` IN ('DISTRIBUTION_STATUS','LANGUAGE','CONSERVATION_STATUS','SPECIES_GEO','LEGAL_STATUS','SPECIES_STATUS','POPULATION_UNIT','TREND'))";
                    speciesSQL += "    AND " + SQLWhere;
                    speciesSQL += "    UNION";
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE " + SQLWhere;
                    speciesSQL += "    UNION";
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE " + SQLWhere;
                    speciesSQL += "    UNION";
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `chm62edt_taxonomy` ON (`chm62edt_species`.`ID_TAXONOMY` = `chm62edt_taxonomy`.`ID_TAXONOMY`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_taxonomy`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE " + SQLWhere;
                }

                if (sTable.equalsIgnoreCase("LegalInstrument")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_species`.`ID_NATURE_OBJECT` FROM `chm62edt_species`";
                    speciesSQL += " INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_reports` ON (`chm62edt_nature_object`.`ID_NATURE_OBJECT` = `chm62edt_reports`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `dc_index` ON (`chm62edt_reports`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += " WHERE `chm62edt_report_type`.LOOKUP_TYPE='LEGAL_STATUS'";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`dc_index`.`TITLE` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`dc_index`.`TITLE` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`dc_index`.`TITLE` LIKE '%" + sValueMin + "%')";
                    }
                }

                if (sTable.equalsIgnoreCase("Title")) {
                    String SQLWhere = "";

                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        SQLWhere = " (`dc_index`.`TITLE` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        SQLWhere = " (`dc_index`.`TITLE` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        SQLWhere = " (`dc_index`.`TITLE` LIKE '%" + sValueMin + "%')";
                    }
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `chm62edt_reports` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_reports`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_reports`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE";
                    speciesSQL += "      (`chm62edt_report_type`.`LOOKUP_TYPE` IN ('DISTRIBUTION_STATUS','LANGUAGE','CONSERVATION_STATUS','SPECIES_GEO','LEGAL_STATUS','SPECIES_STATUS','POPULATION_UNIT','TREND'))";
                    speciesSQL += "    AND " + SQLWhere;
                    speciesSQL += "    UNION";
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE " + SQLWhere;
                    speciesSQL += "    UNION";
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE " + SQLWhere;
                    speciesSQL += "    UNION";
                    speciesSQL += "    SELECT";
                    speciesSQL += "      `chm62edt_species`.`ID_NATURE_OBJECT`";
                    speciesSQL += "    FROM";
                    speciesSQL += "      `chm62edt_species`";
                    speciesSQL += "      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
                    speciesSQL += "      INNER JOIN `chm62edt_taxonomy` ON (`chm62edt_species`.`ID_TAXONOMY` = `chm62edt_taxonomy`.`ID_TAXONOMY`)";
                    speciesSQL += "      INNER JOIN `dc_index` ON (`chm62edt_taxonomy`.`ID_DC` = `dc_index`.`ID_DC`)";
                    speciesSQL += "    WHERE " + SQLWhere;
                }

                if (sTable.equalsIgnoreCase("Taxonomy")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_species`.`ID_NATURE_OBJECT` FROM `chm62edt_species`";
                    speciesSQL += " INNER JOIN `chm62edt_taxonomy` ON (`chm62edt_species`.`ID_TAXONOMY` = `chm62edt_taxonomy`.`ID_TAXONOMY`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " WHERE (`chm62edt_taxonomy`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " WHERE (`chm62edt_taxonomy`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " WHERE (`chm62edt_taxonomy`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                }

                if (sTable.equalsIgnoreCase("ThreatStatus")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_country` ON (`chm62edt_reports`.`ID_GEOSCOPE` = `chm62edt_country`.`ID_GEOSCOPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_conservation_status` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_conservation_status`.`ID_CONSERVATION_STATUS`)";
                    speciesSQL += " WHERE (`chm62edt_country`.`SELECTION` <> 0)";
                    speciesSQL += " AND (`chm62edt_country`.`ISO_2L` IS NOT NULL)";
                    speciesSQL += " AND (`chm62edt_report_type`.`LOOKUP_TYPE` = 'CONSERVATION_STATUS')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`ID_CONSERVATION_STATUS` >= "
                            + GetID(sValueMin, "CONSERVATION_STATUS", "", "") + ")";
                        speciesSQL += " AND (`chm62edt_conservation_status`.`ID_CONSERVATION_STATUS` <= "
                            + GetID(sValueMax, "CONSERVATION_STATUS", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("InternationalThreatStatus")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_country` ON (`chm62edt_reports`.`ID_GEOSCOPE` = `chm62edt_country`.`ID_GEOSCOPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_conservation_status` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_conservation_status`.`ID_CONSERVATION_STATUS`)";
                    speciesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` = 'Europe')";
                    speciesSQL += " AND (`chm62edt_report_type`.`LOOKUP_TYPE` = 'CONSERVATION_STATUS')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_conservation_status`.`ID_CONSERVATION_STATUS` >= "
                            + GetID(sValueMin, "CONSERVATION_STATUS", "", "") + ")";
                        speciesSQL += " AND (`chm62edt_conservation_status`.`ID_CONSERVATION_STATUS` <= "
                            + GetID(sValueMax, "CONSERVATION_STATUS", "", "") + ")";
                    }
                }
                if (sTable.equalsIgnoreCase("Country")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_country` ON (`chm62edt_reports`.`ID_GEOSCOPE` = `chm62edt_country`.`ID_GEOSCOPE`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " WHERE (`chm62edt_country`.`AREA_NAME_EN` REGEXP '" + sValueMin + "')";
                    }
                }
                if (sTable.equalsIgnoreCase("Biogeoregion")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_biogeoregion` ON (`chm62edt_reports`.`ID_GEOSCOPE_LINK` = `chm62edt_biogeoregion`.`ID_GEOSCOPE`)";
                    if (sOperand.equalsIgnoreCase("Between")) {
                        sOperand = "Equal";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " WHERE (`chm62edt_biogeoregion`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " WHERE (`chm62edt_biogeoregion`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " WHERE (`chm62edt_biogeoregion`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                }
                if (sTable.equalsIgnoreCase("Abundance")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_abundance` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_abundance`.`ID_ABUNDANCE`)";
                    speciesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'ABUNDANCE')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_abundance`.`DESCRIPTION` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_abundance`.`DESCRIPTION` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_abundance`.`DESCRIPTION` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_abundance`.`ID_ABUNDANCE` >= "
                            + GetID(sValueMin, "ABUNDANCE", "", "DESCRIPTION") + ")";
                        speciesSQL += " AND (`chm62edt_abundance`.`ID_ABUNDANCE` <= "
                            + GetID(sValueMax, "ABUNDANCE", "", "DESCRIPTION") + ")";
                    }
                }

                if (sTable.equalsIgnoreCase("LegalStatus")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_legal_status` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_legal_status`.`ID_LEGAL_STATUS`)";
                    speciesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'LEGAL_STATUS')";
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_legal_status`.`COMMENT` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_legal_status`.`COMMENT` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_legal_status`.`COMMENT` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_legal_status`.`ID_LEGAL_STATUS` >= "
                            + GetID(sValueMin, "LEGAL_STATUS", "", "COMMENT") + ")";
                        speciesSQL += " AND (`chm62edt_legal_status`.`ID_LEGAL_STATUS` <= "
                            + GetID(sValueMax, "LEGAL_STATUS", "", "COMMENT") + ")";
                    }
                }

                if (sTable.equalsIgnoreCase("Trend")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_trend` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_trend`.`ID_TREND`)";
                    speciesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'TREND')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_trend`.`STATUS` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_trend`.`STATUS` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_trend`.`STATUS` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_trend`.`ID_TREND` >= " + GetID(sValueMin, "TREND", "", "STATUS") + ")";
                        speciesSQL += " AND (`chm62edt_trend`.`ID_TREND` <= " + GetID(sValueMax, "TREND", "", "STATUS") + ")";
                    }
                }

                if (sTable.equalsIgnoreCase("DistributionStatus")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_distribution_status` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_distribution_status`.`ID_DISTRIBUTION_STATUS`)";
                    speciesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'DISTRIBUTION_STATUS')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_distribution_status`.`NAME` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_distribution_status`.`NAME` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_distribution_status`.`NAME` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_distribution_status`.`ID_DISTRIBUTION_STATUS` >= "
                            + GetID(sValueMin, "DISTRIBUTION_STATUS", "", "") + ")";
                        speciesSQL += " AND (`chm62edt_distribution_status`.`ID_DISTRIBUTION_STATUS` <= "
                            + GetID(sValueMax, "DISTRIBUTION_STATUS", "", "") + ")";
                    }
                }

                if (sTable.equalsIgnoreCase("SpeciesStatus")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_species_status` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_species_status`.`ID_SPECIES_STATUS`)";
                    speciesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'SPECIES_STATUS')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_species_status`.`DESCRIPTION` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_species_status`.`DESCRIPTION` REGEX '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_species_status`.`DESCRIPTION` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_species_status`.`ID_SPECIES_STATUS` >= "
                            + GetID(sValueMin, "SPECIES_STATUS", "", "DESCRIPTION") + ")";
                        speciesSQL += " AND (`chm62edt_species_status`.`ID_SPECIES_STATUS` <= "
                            + GetID(sValueMax, "SPECIES_STATUS", "", "DESCRIPTION") + ")";
                    }
                }

                if (sTable.equalsIgnoreCase("InfoQuality")) {
                    speciesSQL = "SELECT DISTINCT `chm62edt_reports`.`ID_NATURE_OBJECT`";
                    speciesSQL += " FROM `chm62edt_reports`";
                    speciesSQL += " INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
                    speciesSQL += " INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
                    speciesSQL += " INNER JOIN `chm62edt_info_quality` ON (`chm62edt_report_type`.`ID_LOOKUP` = `chm62edt_info_quality`.`ID_INFO_QUALITY`)";
                    speciesSQL += " WHERE (`chm62edt_report_type`.`LOOKUP_TYPE` = 'INFO_QUALITY')";
                    if (sOperand.equalsIgnoreCase("Equal")) {
                        speciesSQL += " AND (`chm62edt_info_quality`.`STATUS` = '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Regex")) {
                        speciesSQL += " AND (`chm62edt_info_quality`.`STATUS` REGEXP '" + sValueMin + "')";
                    }
                    if (sOperand.equalsIgnoreCase("Contains")) {
                        speciesSQL += " AND (`chm62edt_info_quality`.`STATUS` LIKE '%" + sValueMin + "%')";
                    }
                    if (sOperand.equalsIgnoreCase("Between")) {
                        speciesSQL += " AND (`chm62edt_info_quality`.`ID_INFO_QUALITY` >= "
                            + GetID(sValueMin, "INFO_QUALITY", "", "STATUS") + ")";
                        speciesSQL += " AND (`chm62edt_info_quality`.`ID_INFO_QUALITY` <= "
                            + GetID(sValueMin, "INFO_QUALITY", "", "STATUS") + ")";
                    }
                }
                // execute every attribute query
                // attributesFilter+=" AND ID_NATURE_OBJECT IN ("+ExecuteSQL(speciesSQL,"")+")";
            } // end for
            // execute final query

            // speciesSQL="SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_species";
            // speciesFilter=" WHERE 1=1 "+attributesFilter;
            // speciesSQL+=speciesFilter;
            // filter+=" ID_NATURE_OBJECT IN ("+ExecuteSQL(speciesSQL,"")+")";
            // System.out.println("filter build start");
            filter += " ID_NATURE_OBJECT IN (" + ExecuteSQL(speciesSQL, "") + ")";
            // System.out.println("filter build");
        }
        // System.out.println("filter = " + filter);
        // System.out.println("filter was build ");
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
                // System.out.println("resCount = " + resCount);

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
                // System.out.println("finished build delimited criteria");
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
            logger.debug(e, e); 
            return "";
        }

        // System.out.println("resultCount: "+resultCount);
        return result;
    }

    /**
     * Number of results for search.
     * @return Found rows.
     */
    public int getResultCount() {
        return resultCount;
    }
}
