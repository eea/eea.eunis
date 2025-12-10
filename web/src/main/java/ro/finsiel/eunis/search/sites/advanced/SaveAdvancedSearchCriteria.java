package ro.finsiel.eunis.search.sites.advanced;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * This class is used to save advanced search criteria.
 * @author finsiel.
 */
public class SaveAdvancedSearchCriteria {

    /** id of session. */
    private String idSession = null;

    /** nature object for witch the search was made. */
    private String natureObject = null;

    /** name of the user who made this operation. */
    private String userName = null;

    /** description of the saved search. */
    private String description = null;

    /** name of the jsp page where operation was made. */
    private String fromWhere = null;

    /** Sites source data set. */
    private String SourceDB = null;

    private static final Logger logger = Logger.getLogger(SaveAdvancedSearchCriteria.class);

    /**
     * Class constructor.
     * @param idsession id session
     * @param natureobject nature object
     * @param username user name
     * @param description search description
     * @param fromWhere page name
     * @param SourceDB sites source data set.
     */
    public SaveAdvancedSearchCriteria(String idsession,
                                      String natureobject,
                                      String username,
                                      String description,
                                      String fromWhere,
                                      String SourceDB) {
        this.idSession = idsession;
        this.natureObject = natureobject;
        this.userName = username;
        this.description = description;
        this.fromWhere = fromWhere;
        this.SourceDB = SourceDB;
    }

    /**
     * Save advanced search criteria.
     * @return true if save operation was made with success.
     */
    public boolean SaveCriteria() {
        String SQL1 = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;
        boolean saveWithSuccess = false;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL1 = "SELECT * " + " FROM eunis_advanced_search " + " WHERE ID_SESSION = '" + idSession + "' "
                    + " AND NATURE_OBJECT = '" + natureObject + "'";
            rs = ps.executeQuery(SQL1);

            String name = userName + CriteriaMaxNumber(userName);
            String d = (description != null && !description.equalsIgnoreCase("") ? description : getDescription());

            d = Utilities.removeQuotes(d);
            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    Connection con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    Statement ps1 = con1.createStatement();

                    String SQL = "INSERT INTO eunis_save_advanced_search";

                    SQL += "(CRITERIA_NAME,NATURE_OBJECT,ID_NODE,NODE_TYPE,DESCRIPTION,USERNAME,FROM_WHERE)";
                    SQL += " VALUES(";
                    SQL += "'" + name + "',";
                    SQL += "'" + natureObject + "',";
                    SQL += "'" + rs.getString("ID_NODE") + "',";
                    SQL += "'" + rs.getString("NODE_TYPE") + "',";
                    SQL += "'" + d + "',";
                    SQL += "'" + userName + "',";
                    SQL += "'" + fromWhere + "')";

                    ps1.executeUpdate(SQL);
                    ps1.close();
                    con1.close();
                }
            }

            rs.close();
            ps.close();
            con.close();

            // inser criteria

            Connection con3 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            Statement ps3 = con3.createStatement();

            SQL1 = "SELECT * " + " FROM eunis_advanced_search_criteria " + " WHERE ID_SESSION = '" + idSession + "' "
                    + " AND NATURE_OBJECT = '" + natureObject + "' " + " ORDER BY ID_NODE";
            ResultSet rs3 = ps3.executeQuery(SQL1);

            if (rs3.isBeforeFirst()) {
                while (!rs3.isLast()) {
                    rs3.next();
                    Connection con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    Statement ps1 = con1.createStatement();

                    String SQL = "INSERT INTO eunis_save_advanced_search_criteria";

                    SQL += "(CRITERIA_NAME,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
                    SQL += " VALUES(";
                    SQL += "'" + name + "',";
                    SQL += "'" + natureObject + "',";
                    SQL += "'" + rs3.getString("ID_NODE") + "',";
                    SQL += "'" + rs3.getString("ATTRIBUTE") + "',";
                    SQL += "'" + rs3.getString("OPERATOR") + "',";
                    SQL += "'" + rs3.getString("FIRST_VALUE") + "',";
                    SQL += "'" + rs3.getString("LAST_VALUE") + "')";
                    ps1.executeUpdate(SQL);

                    ps1.close();
                    con1.close();
                }
            }

            rs3.close();
            ps3.close();
            con3.close();

            long maxIdNode = getMaxIdNode(name, natureObject);

            // insert SourceDB

            Connection con2 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            Statement ps2 = con2.createStatement();

            String SQL = "INSERT INTO eunis_save_advanced_search";

            SQL += "(CRITERIA_NAME,NATURE_OBJECT,ID_NODE,NODE_TYPE,DESCRIPTION,USERNAME,FROM_WHERE)";
            SQL += " VALUES(";
            SQL += "'" + name + "',";
            SQL += "'" + natureObject + "',";
            SQL += "'" + maxIdNode + "',";
            SQL += "'Criteria',";
            SQL += "'" + d + "',";
            SQL += "'" + userName + "',";
            SQL += "'" + fromWhere + "')";
            ps2.executeUpdate(SQL);

            ps2.close();
            con2.close();

            Connection con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            Statement ps1 = con1.createStatement();

            SQL = "INSERT INTO eunis_save_advanced_search_criteria";
            SQL += "(CRITERIA_NAME,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
            SQL += " VALUES(";
            SQL += "'" + name + "',";
            SQL += "'" + natureObject + "',";
            SQL += "'" + maxIdNode + "',";
            SQL += "'SourceDB',";
            SQL += "'is',";
            SQL += "\"" + SourceDB + "\",";
            SQL += "'')";
            ps1.executeUpdate(SQL);

            ps1.close();
            con1.close();
            saveWithSuccess = true;
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return saveWithSuccess;
    }

    /**
     * Get sites source data set.
     *
     * @param criteria criteria name
     * @param natureObject nature object
     * @return sites source data set.
     */
    public static String getSourceDB(String criteria,
                                     String natureObject) {
        String sourceDB = "";
        long maxIdNode = MaxIdNode(criteria, natureObject);

        try {
            Connection con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            Statement ps = con.createStatement();

            String SQL = "SELECT FIRST_VALUE " + " FROM eunis_save_advanced_search_criteria " + " WHERE CRITERIA_NAME ='" + criteria
                    + "' " + " AND NATURE_OBJECT='" + natureObject + "' " + " AND ID_NODE = '" + maxIdNode + "'";
            ResultSet rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                rs.next();
                sourceDB = rs.getString(1);
            }

            ps.close();
            con.close();
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return sourceDB;
    }

    /**
     * Return max value of ID_NODE field from eunis_save_advanced_search table for a criteria name and a nature object.
     * @param criteria criteria name
     * @param natureObject nature object
     * @return max value.
     */
    private long getMaxIdNode(String criteria, String natureObject) {
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;
        long result = 0;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT MAX(ID_NODE) " + " FROM eunis_save_advanced_search " + " WHERE CRITERIA_NAME ='" + criteria + "' "
                    + " AND NATURE_OBJECT='" + natureObject + "'";
            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                rs.next();
                result = rs.getLong(1);
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }
        return result + 1;
    }

    /**
     * Return max value of ID_NODE field from eunis_save_advanced_search table for a criteria name and a nature object.
     *
     * @param criteria criteria name
     * @param natureObject nature object
     * @return max value.
     */
    private static long MaxIdNode(String criteria,
                                  String natureObject) {
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;
        long result = 0;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT MAX(ID_NODE) " + " FROM eunis_save_advanced_search " + " WHERE CRITERIA_NAME ='" + criteria + "' "
                    + " AND NATURE_OBJECT='" + natureObject + "'";
            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                rs.next();
                result = rs.getLong(1);
            }
            ps.close();
            con.close();
        } catch (Exception e) {
            logger.debug(e, e);
        }

        return result;
    }

    /**
     * Return value of node type field from eunis_advanced_search table.
     * @param idsession id session
     * @param natureobject nature object
     * @param idnode id node
     * @return node type field value.
     */
    private String getNodeType(String idsession, String natureobject, String idnode) {
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;
        String result = "";

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT NODE_TYPE " + " FROM eunis_advanced_search " + " WHERE ID_SESSION='" + idsession + "' "
                    + " AND NATURE_OBJECT='" + natureobject + "' " + " AND ID_NODE='" + idnode + "'";
            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                rs.next();
                result = rs.getString(1);
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Return description of saved advanced search.
     * @return  search description
     */
    private String getDescription() {
        String descr = "";
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT * " + " FROM eunis_advanced_search_criteria " + " WHERE ID_SESSION='" + idSession + "' "
                    + " AND NATURE_OBJECT='" + natureObject + "' " + " ORDER BY ID_NODE";

            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                descr = "ADVANCED SEACH, searching " + natureObject + " having next criteria: ";
                String op = "";

                while (!rs.isLast()) {
                    rs.next();
                    if (rs.isFirst()) {
                        String nodeType = getNodeType(rs.getString("ID_SESSION"), rs.getString("NATURE_OBJECT"), "0");

                        op = (nodeType != null && nodeType.equalsIgnoreCase("any") ? "OR" : "AND");
                    }
                    descr += " " + rs.getString("ATTRIBUTE") + " " + rs.getString("OPERATOR") + " " + rs.getString("FIRST_VALUE");
                    if (rs.getString("OPERATOR").equalsIgnoreCase("Between")) {
                        descr += " and " + rs.getString("LAST_VALUE");
                    }
                    if (!rs.isLast()) {
                        descr += " " + op;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
           SQLUtilities.closeAll(con, ps, rs);
        }
        return descr;
    }

    /**
     * Expand, in a jsp page, list of saved advanced searches made by a user.
     *
     * @param userName user name
     * @param fromWhere page name
     * @return list of saved searches
     */
    public static String ExpandSaveCriteriaForThisPage(String userName,
                                                       String fromWhere) {
        String result = "";
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT * " + " FROM eunis_save_advanced_search " + " WHERE USERNAME='" + userName + "' " + " AND FROM_WHERE='"
                    + fromWhere + "' " + " GROUP BY CRITERIA_NAME, NATURE_OBJECT";

            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                result = "<TABLE width=\"100%\" border=\"0\">";
                while (!rs.isLast()) {
                    rs.next();
                    String sourceDB = getSourceDB(rs.getString("CRITERIA_NAME"), rs.getString("NATURE_OBJECT")
                    );

                    sourceDB = Utilities.removeQuotes(sourceDB);
                    sourceDB = sourceDB.replaceAll(" ", "");
                    result += "<tr>";
                    // result+="<TD><IMG src=\"images/mini/bulletb.gif\" width=\"6\" height=\"6\" align=\"middle\" />&nbsp;<a href=\"javascript:openQuestion('select-columns.jsp?saveThisCriteriaCriteria=yes&fromWhere="+fromWhere+"&criterianame="+rs.getString("CRITERIA_NAME")+"&natureobject="+rs.getString("NATURE_OBJECT")+"&searchedNatureObject=Sites&searchType=Advanced&Proceed to next step=Proceed to next step');\">"+rs.getString("DESCRIPTION")+"</a></TD>";
                    result += "<td><a title=\"Load criteria\" href=\"javascript:setFormDeleteSaveCriteria('" + fromWhere + "','"
                            + rs.getString("CRITERIA_NAME") + "','" + rs.getString("NATURE_OBJECT")
                            + "');\"><img alt=\"Delete\" src=\"images/mini/delete.jpg\" border=\"0\" align=\"absmiddle\"></a>&nbsp;&nbsp;<a title=\"Load criteria\" href=\"javascript:setFormLoadSaveCriteria('"
                            + fromWhere + "','" + rs.getString("CRITERIA_NAME") + "','" + rs.getString("NATURE_OBJECT") + "','"
                            + sourceDB + "');\">" + rs.getString("DESCRIPTION") + "</a></td>";
                    result += "</tr>";
                }
                result += "</table>";
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }
        return result;
    }

    /**
     * Insert data from eunis_save_advanced_search table in eunis_advanced_search table;
     * used to load data from saved searches in input fields from jsp page.
     * @param idsession id session
     * @param criterianame criteria name
     * @param natureobject nature object
     */
    public static void insertEunisAdvancedSearch(String idsession,
                                                 String criterianame,
                                                 String natureobject) {
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT * " + " FROM eunis_save_advanced_search " + " WHERE CRITERIA_NAME='" + criterianame + "' "
                    + " AND NATURE_OBJECT='" + natureobject + "'";

            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    // is not SourceDB
                    if (!rs.getString("ID_NODE").equalsIgnoreCase(
                            new Long(MaxIdNode(rs.getString("criteria_name"), rs.getString("nature_object"))).toString())) {
                        Connection con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        Statement ps1 = con1.createStatement();

                        String SQL1 = "INSERT INTO eunis_advanced_search";

                        SQL1 += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
                        SQL1 += " VALUES(";
                        SQL1 += "'" + idsession + "',";
                        SQL1 += "'" + natureobject + "',";
                        SQL1 += "'" + rs.getString("ID_NODE") + "',";
                        SQL1 += "'" + rs.getString("NODE_TYPE") + "')";

                        ps1.executeUpdate(SQL1);
                        ps1.close();
                        con1.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }
    }

    /**
     * Insert data from eunis_save_advanced_search_criteria table in eunis_advanced_search_criteria table;
     * used to load data from saved searches in input fields from jsp page.
     * @param idsession id session
     * @param criterianame criteria name
     * @param natureobject nature object
     */
    public static void insertEunisAdvancedSearchCriteria(String idsession,
                                                         String criterianame,
                                                         String natureobject) {
        String SQL = "";
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            SQL = "SELECT * " + " FROM eunis_save_advanced_search_criteria " + " WHERE CRITERIA_NAME='" + criterianame + "' "
                    + " AND NATURE_OBJECT='" + natureobject + "'";

            rs = ps.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    // is not SourceDB
                    if (!rs.getString("ID_NODE").equalsIgnoreCase(
                            new Long(MaxIdNode(rs.getString("criteria_name"), rs.getString("nature_object"))).toString())) {
                        Connection con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        Statement ps1 = con1.createStatement();

                        String SQL1 = "INSERT INTO eunis_advanced_search_criteria";

                        SQL1 += "(ID_SESSION,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
                        SQL1 += " VALUES(";
                        SQL1 += "'" + idsession + "',";
                        SQL1 += "'" + natureobject + "',";
                        SQL1 += "'" + rs.getString("ID_NODE") + "',";
                        SQL1 += "'" + rs.getString("ATTRIBUTE") + "',";
                        SQL1 += "'" + rs.getString("OPERATOR") + "',";
                        SQL1 += "'" + rs.getString("FIRST_VALUE") + "',";
                        SQL1 += "'" + rs.getString("LAST_VALUE") + "')";

                        ps1.executeUpdate(SQL1);
                        ps1.close();
                        con1.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }

    }

    /**
     * Return max number from values of CRITERIA_NAME fields for a user (Ex : for 'root0','root1','root2' return 2 for user 'root').
     * @param user user name
     * @return max number
     */
    private Long CriteriaMaxNumber(String user) {
        String SQL1 = "";
        ResultSet rs = null;
        Connection con1 = null;
        Statement st = null;

        Long result = (long) 0;

        try {
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL1 = " SELECT MAX(CAST(SUBSTRING(CRITERIA_NAME,LENGTH('" + user + "')+1,LENGTH(CRITERIA_NAME)) AS SIGNED))"
                    + " FROM eunis_save_advanced_search" + " WHERE USERNAME = '" + user + "'";

            st = con1.createStatement();
            rs = st.executeQuery(SQL1);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    result = rs.getLong(1) + 1;
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con1, st, rs);
        }

        return result;
    }
}
