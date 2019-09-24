package ro.finsiel.eunis.search;


import org.apache.commons.lang.StringEscapeUtils;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.StringTokenizer;


/**
 * Class used for advanced search.
 * @author finsiel
 */
public class AdvancedSearch {

    private int SQL_LIMIT = 1000;
    private String SourceDB = "''";

    private int resultCount = 0;

    /**
     * Ctor.
     */
    public AdvancedSearch() {}

    /**
     * Setter for sourcedb property.
     * @param sourcedb sourcedb.
     */
    public void SetSourceDB(String sourcedb) {
        SourceDB = sourcedb;
    }

    /**
     * Initializations.
     * @deprecated All DB connections should be taken from the pool
     */
    public void Init() {

    }

    public void SetSQLLimit(int SQLLimit) {
        SQL_LIMIT = SQLLimit;
    }

    public boolean DeleteSessionData(String IdSession) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        Statement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = " DELETE FROM eunis_advanced_search_results";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_temp";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_criteria";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_criteria_temp";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            ps = con.createStatement();
            ps.execute(SQL);

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }
        return result;
    }

    public boolean DeleteSessionDataForNatureObject(String IdSession, String natureObject) {
        boolean result = false;
        String SQL = "";
        Connection con = null;
        Statement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = " DELETE FROM eunis_advanced_search_results";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            SQL += " AND NATURE_OBJECT = '" + natureObject + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            SQL += " AND NATURE_OBJECT = '" + natureObject + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_temp";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            SQL += " AND NATURE_OBJECT = '" + natureObject + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_criteria";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            SQL += " AND NATURE_OBJECT = '" + natureObject + "'";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_criteria_temp";
            SQL += " WHERE ID_SESSION = '" + IdSession + "'";
            SQL += " AND NATURE_OBJECT = '" + natureObject + "'";
            ps = con.createStatement();
            ps.execute(SQL);

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Clear all tables used in adv/combined search.
     * @return true if cleared.
     */
    public boolean DeleteAllTemporaryData() {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        Statement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = " DELETE FROM eunis_advanced_search_results";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_temp";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_criteria";
            ps = con.createStatement();
            ps.execute(SQL);
            ps.close();

            SQL = " DELETE FROM eunis_advanced_search_criteria_temp";
            ps = con.createStatement();
            ps.execute(SQL);

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Clear the results of a search.
     * @param IdSession Session iD.
     * @param NatureObject Nature object (species/habs/sites).
     * @return true if cleared.
     */
    public boolean DeleteResults(String IdSession, String NatureObject) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "DELETE FROM eunis_advanced_search_results";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }

    private Connection getCon() {
        Connection con = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
        } catch (Exception e) {
            return null;
        }
        return con;
    }

    /**
     * add a result of search in results table.
     * @param IdSession SESSION ID.
     * @param NatureObject Nature object (species/hab/sites)
     * @param IdNatureObject ID of the searched nature object.
     * @return true if saved.
     */
    public boolean AddResult(String IdSession, String NatureObject, String IdNatureObject) {
        boolean result = false;

        // String SQL="";
        StringBuffer SQL = new StringBuffer();
        Connection con = null;
        Statement ps = null;

        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);
        IdNatureObject = StringEscapeUtils.escapeXml(IdNatureObject);

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            ps = con.createStatement();

            // while(tokenizer.hasMoreElements()) {
            // SQL="INSERT INTO eunis_advanced_search_results";
            // SQL+="(ID_SESSION,NATURE_OBJECT,ID_NATURE_OBJECT)";
            // SQL+=" VALUES(";
            // SQL+="'"+IdSession+"',";
            // SQL+="'"+NatureObject+"',";
            // SQL+="'"+tokenizer.nextElement().toString().trim()+"')";
            //
            // ps.executeUpdate(SQL);
            // }

            StringTokenizer tokenizer = new StringTokenizer(IdNatureObject, ",");

            int poscount = tokenizer.countTokens();
            // System.out.println("poscount = " + poscount);
            int pos = 0;
            int max_results = poscount;

            // int max_results=3;

            SQL.ensureCapacity(65000);
            SQL.append("INSERT INTO eunis_advanced_search_results");
            SQL.append("(ID_SESSION,NATURE_OBJECT,ID_NATURE_OBJECT)");
            SQL.append(" VALUES ");
            while (tokenizer.hasMoreElements() && pos < max_results) {
                pos++;
                SQL.append("(");
                SQL.append("'" + IdSession + "',");
                SQL.append("'" + NatureObject + "',");
                if (SQL.length() > 64000) {
                    SQL.append(
                            "'" + tokenizer.nextElement().toString().trim()
                            + "')");
                    ps.executeUpdate(SQL.toString());
                    SQL = new StringBuffer();
                    SQL.ensureCapacity(65000);
                    SQL.append("INSERT INTO eunis_advanced_search_results");
                    SQL.append("(ID_SESSION,NATURE_OBJECT,ID_NATURE_OBJECT)");
                    SQL.append(" VALUES ");
                } else {
                    if (pos < max_results) {
                        SQL.append(
                                "'" + tokenizer.nextElement().toString().trim()
                                + "'),");
                    } else {
                        SQL.append(
                                "'" + tokenizer.nextElement().toString().trim()
                                + "')");
                    }
                }
            }
            if (SQL.length() > 0) {
                ps.executeUpdate(SQL.toString());
            }

            ps.close();

            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Change a search criteria.
     * @param IdNode ID of the node.
     * @param IdSession Session ID.
     * @param NatureObject Nature object (species/habs/sites).
     * @param Criteria New criteria.
     * @return true if changed.
     */
    public boolean ChangeCriteria(String IdNode, String IdSession, String NatureObject, String Criteria) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "UPDATE eunis_advanced_search";
            SQL += " SET NODE_TYPE='" + Criteria + "'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }

    /**
     * Change a search criteria.
     * @param IdNode node to change.
     * @param IdSession Session id.
     * @param NatureObject nature object(species/habs/sites).
     * @param Attribute new attribute.
     * @return true if changed.
     */
    public boolean ChangeAttribute(String IdNode, String IdSession, String NatureObject, String Attribute) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "UPDATE eunis_advanced_search_criteria";
            SQL += " SET ATTRIBUTE='" + Attribute + "'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }

    public boolean ChangeOperator(String IdNode, String IdSession, String NatureObject, String Operator) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        IdNode = StringEscapeUtils.escapeXml(IdNode);
        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);


        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "UPDATE eunis_advanced_search_criteria";
            SQL += " SET OPERATOR='" + Operator + "'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            if (Operator.equalsIgnoreCase("Between")) {
                SQL = "UPDATE eunis_advanced_search_criteria";
                SQL += " SET LAST_VALUE='enter value here...'";
                SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
                SQL += " AND ID_SESSION = '" + IdSession + "'";
                SQL += " AND ID_NODE = '" + IdNode + "'";

                ps = con.prepareStatement(SQL);
                ps.execute();
            } else {
                SQL = "UPDATE eunis_advanced_search_criteria";
                SQL += " SET LAST_VALUE=''";
                SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
                SQL += " AND ID_SESSION = '" + IdSession + "'";
                SQL += " AND ID_NODE = '" + IdNode + "'";

                ps = con.prepareStatement(SQL);
                ps.execute();
            }

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    public boolean ChangeFirstValue(String IdNode, String IdSession, String NatureObject, String FirstValue) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        IdNode = StringEscapeUtils.escapeXml(IdNode);
        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);
        FirstValue = StringEscapeUtils.escapeXml(FirstValue);

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "UPDATE eunis_advanced_search_criteria";
            SQL += " SET FIRST_VALUE='" + FirstValue + "'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }

    public boolean ChangeLastValue(String IdNode, String IdSession, String NatureObject, String LastValue) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        IdNode = StringEscapeUtils.escapeXml(IdNode);
        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);
        LastValue = StringEscapeUtils.escapeXml(LastValue);

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "UPDATE eunis_advanced_search_criteria";
            SQL += " SET LAST_VALUE='" + LastValue + "'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }

    /**
     * Build search filter.
     * @param IdNode Node from the search tree.
     * @param IdSession ID of the session.
     * @param NatureObject Nature object (species/habs/sites).
     * @return WHERE string.
     */
    public String BuildFilter(String IdNode, String IdSession, String NatureObject) {
        String result = "";

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "SELECT * FROM eunis_advanced_search_criteria";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            // System.out.println("SQL = " + SQL);

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            if (rs.isBeforeFirst()) {
                rs.next();
                if (NatureObject.equalsIgnoreCase("Species")) {
                    ro.finsiel.eunis.search.species.advanced.SpeciesAdvancedSearch sas;

                    sas = new ro.finsiel.eunis.search.species.advanced.SpeciesAdvancedSearch();
                    sas.SetSQLLimit(SQL_LIMIT);
                    sas.AddCriteria(rs.getString("ATTRIBUTE"),
                            rs.getString("OPERATOR"),
                            rs.getString("FIRST_VALUE"),
                            rs.getString("LAST_VALUE"));
                    result = sas.BuildFilter();
                    resultCount = sas.getResultCount();
                    // System.out.println("resultCount = " + resultCount);
                }
                if (NatureObject.equalsIgnoreCase("Habitat")) {
                    ro.finsiel.eunis.search.habitats.advanced.HabitatsAdvancedSearch has;

                    has = new ro.finsiel.eunis.search.habitats.advanced.HabitatsAdvancedSearch();
                    has.SetSQLLimit(SQL_LIMIT);
                    has.AddCriteria(rs.getString("ATTRIBUTE"),
                            rs.getString("OPERATOR"),
                            rs.getString("FIRST_VALUE"),
                            rs.getString("LAST_VALUE"));
                    result = has.BuildFilter();
                    resultCount = has.getResultCount();
                    // System.out.println("resultCount = " + resultCount);
                }
                if (NatureObject.equalsIgnoreCase("Sites")) {
                    ro.finsiel.eunis.search.sites.advanced.SitesAdvancedSearch sas;

                    sas = new ro.finsiel.eunis.search.sites.advanced.SitesAdvancedSearch();
                    sas.SetSourceDB(SourceDB);
                    sas.SetSQLLimit(SQL_LIMIT);
                    sas.AddCriteria(rs.getString("ATTRIBUTE"),
                            rs.getString("OPERATOR"),
                            rs.getString("FIRST_VALUE"),
                            rs.getString("LAST_VALUE"));
                    result = sas.BuildFilter();
                    resultCount = sas.getResultCount();
                    // System.out.println("resultCount = " + resultCount);
                }
            }

            rs.close();

            ps.close();
            con.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * Build filter for save criteria.
     * @param IdNode ID of the node from tree.
     * @param IdSession ID session.
     * @param NatureObject nature object (species/habs/sites).
     * @return WHERE filter.
     */
    public String BuildFilterSave(String IdNode, String IdSession, String NatureObject) {
        String result = "";

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String natObj = NatureObject.substring(0, NatureObject.indexOf("Save"));

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "SELECT * FROM eunis_advanced_search_criteria";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            // System.out.println("SQL = " + SQL);

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            if (rs.isBeforeFirst()) {
                rs.next();
                if (natObj.equalsIgnoreCase("Species")) {
                    ro.finsiel.eunis.search.species.advanced.SpeciesAdvancedSearch sas;

                    sas = new ro.finsiel.eunis.search.species.advanced.SpeciesAdvancedSearch();
                    sas.SetSQLLimit(SQL_LIMIT);
//                    sas.Init(SQL_DRV, SQL_URL, SQL_USR, SQL_PWD);
                    sas.AddCriteria(rs.getString("ATTRIBUTE"),
                            rs.getString("OPERATOR"),
                            rs.getString("FIRST_VALUE"),
                            rs.getString("LAST_VALUE"));
                    result = sas.BuildFilter();
                    resultCount = sas.getResultCount();
                    // System.out.println("resultCount = " + resultCount);
                }
                if (natObj.equalsIgnoreCase("Habitat")) {
                    ro.finsiel.eunis.search.habitats.advanced.HabitatsAdvancedSearch has;

                    has = new ro.finsiel.eunis.search.habitats.advanced.HabitatsAdvancedSearch();
                    has.SetSQLLimit(SQL_LIMIT);
//                    has.Init(SQL_DRV, SQL_URL, SQL_USR, SQL_PWD);
                    has.AddCriteria(rs.getString("ATTRIBUTE"),
                            rs.getString("OPERATOR"),
                            rs.getString("FIRST_VALUE"),
                            rs.getString("LAST_VALUE"));
                    result = has.BuildFilter();
                    resultCount = has.getResultCount();
                    // System.out.println("resultCount = " + resultCount);
                }
                if (natObj.equalsIgnoreCase("Sites")) {
                    ro.finsiel.eunis.search.sites.advanced.SitesAdvancedSearch sas;

                    sas = new ro.finsiel.eunis.search.sites.advanced.SitesAdvancedSearch();
                    sas.SetSourceDB(SourceDB);
                    sas.SetSQLLimit(SQL_LIMIT);
//                    sas.Init(SQL_DRV, SQL_URL, SQL_USR, SQL_PWD);
                    sas.AddCriteria(rs.getString("ATTRIBUTE"),
                            rs.getString("OPERATOR"),
                            rs.getString("FIRST_VALUE"),
                            rs.getString("LAST_VALUE"));
                    result = sas.BuildFilter();
                    resultCount = sas.getResultCount();
                    // System.out.println("resultCount = " + resultCount);
                }
            }

            rs.close();

            ps.close();
            con.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public String InterpretCriteria(String IdNode, String IdSession, String NatureObject) {
        String result = "";

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "SELECT * FROM eunis_advanced_search_criteria";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            // System.out.println("SQL = " + SQL);

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            if (rs.isBeforeFirst()) {
                rs.next();
                result = "<strong>"
                        + ro.finsiel.eunis.search.Utilities.SplitString(
                                rs.getString("ATTRIBUTE"))
                                + "</strong> ";
                result += "<em>" + rs.getString("OPERATOR") + "</em> ";
                result += "<u>" + rs.getString("FIRST_VALUE") + "</u>";
                if (rs.getString("OPERATOR").equalsIgnoreCase("Between")) {
                    result += " <em>AND</em> <u>" +  rs.getString("LAST_VALUE")
                            + "</u>";
                }
            }

            rs.close();

            ps.close();
            con.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public boolean DeleteBranch(String IdNode, String IdSession, String NatureObject) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "DELETE FROM eunis_advanced_search_criteria";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE LIKE '" + IdNode + "%'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "DELETE FROM eunis_advanced_search";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE LIKE '" + IdNode + "%'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    public boolean DeleteRoot(String IdSession, String NatureObject, String Attribute) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "DELETE FROM eunis_advanced_search_criteria";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "DELETE FROM eunis_advanced_search";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = this.CreateInitialBranch(IdSession, NatureObject, Attribute);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    public boolean DeleteRootNoInitialize(String IdSession, String NatureObject, String Attribute) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "DELETE FROM eunis_advanced_search_criteria";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "DELETE FROM eunis_advanced_search";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    public boolean InsertBranch(String IdNode, String IdSession, String NatureObject, String Attribute) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String IdNodeNew = "";

        IdNode = StringEscapeUtils.escapeXml(IdNode);
        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);
        Attribute = StringEscapeUtils.escapeXml(Attribute);

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            // obtin ultimul nod child al nodului curent
            if (IdNode.length() == 1) {
                SQL = "SELECT * FROM eunis_advanced_search";
                SQL += " WHERE ID_SESSION = '" + IdSession + "'";
                SQL += " AND NATURE_OBJECT = '" + NatureObject + "'";
                SQL += " AND LENGTH(ID_NODE) = " + IdNode.length();
                SQL += " ORDER BY ID_NODE DESC";
            } else {
                SQL = "SELECT * FROM eunis_advanced_search";
                SQL += " WHERE ID_NODE LIKE '"
                        + IdNode.substring(0, IdNode.length() - 1) + "%'";
                SQL += " AND ID_SESSION = '" + IdSession + "'";
                SQL += " AND NATURE_OBJECT = '" + NatureObject + "'";
                SQL += " AND LENGTH(ID_NODE) = " + IdNode.length();
                SQL += " ORDER BY ID_NODE DESC";
            }

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            Integer LastNumber = new Integer(0);

            if (rs.next()) {
                if (IdNode.length() == 1) {
                    LastNumber = new Integer(rs.getString("ID_NODE"));
                    LastNumber = new Integer(LastNumber.intValue() + 1);
                } else {
                    // System.out.println("LastNumber = " + rs.getString("ID_NODE").substring(rs.getString("ID_NODE").length()-1));
                    LastNumber = new Integer(
                            rs.getString("ID_NODE").substring(
                                    rs.getString("ID_NODE").length() - 1));
                    // System.out.println("LastNumber = " + LastNumber);
                    LastNumber = new Integer(LastNumber.intValue() + 1);
                }

                rs.close();
                ps.close();

                if (LastNumber.intValue() > 9) {
                    return result;
                }

                if (IdNode.length() == 1) {
                    IdNodeNew = LastNumber.toString();
                } else {
                    IdNodeNew = IdNode.substring(0, IdNode.length() - 1)
                            + LastNumber.toString();
                }

                SQL = "INSERT INTO eunis_advanced_search";
                SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
                SQL += " VALUES(";
                SQL += "'" + IdSession + "',";
                SQL += "'" + NatureObject + "',";
                SQL += "'" + IdNodeNew + "',";
                SQL += "'Criteria')";

                ps = con.prepareStatement(SQL);
                ps.execute();
                ps.close();

                SQL = "INSERT INTO eunis_advanced_search_criteria";
                SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
                SQL += " VALUES(";
                SQL += "'" + IdSession + "',";
                SQL += "'" + NatureObject + "',";
                SQL += "'" + IdNodeNew + "',";
                SQL += "'" + Attribute + "',";
                SQL += "'Equal',";
                SQL += "'enter value here...',";
                SQL += "'')";

                ps = con.prepareStatement(SQL);
                ps.execute();

                ps.close();
                con.close();

                result = true;
            } else {
                ps.close();
                con.close();
                // System.out.println("Unexpected error adding branch!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }

        return result;
    }

    public boolean ComposeBranch(String IdNode, String IdSession, String NatureObject) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        IdNode = StringEscapeUtils.escapeXml(IdNode);
        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "UPDATE eunis_advanced_search";
            SQL += " SET NODE_TYPE='All'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "INSERT INTO eunis_advanced_search";
            SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
            SQL += " VALUES(";
            SQL += "'" + IdSession + "',";
            SQL += "'" + NatureObject + "',";
            SQL += "'" + IdNode + ".1',";
            SQL += "'Criteria')";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "UPDATE eunis_advanced_search_criteria";
            SQL += " SET ID_NODE='" + IdNode + ".1'";
            SQL += " WHERE NATURE_OBJECT = '" + NatureObject + "'";
            SQL += " AND ID_SESSION = '" + IdSession + "'";
            SQL += " AND ID_NODE = '" + IdNode + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    public boolean CreateInitialBranch(String IdSession, String NatureObject, String Attribute) {
        boolean result = false;

        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        IdSession = StringEscapeUtils.escapeXml(IdSession);
        NatureObject = StringEscapeUtils.escapeXml(NatureObject);
        Attribute = StringEscapeUtils.escapeXml(Attribute);

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "DELETE FROM eunis_advanced_search";
            SQL += " WHERE ID_SESSION='" + IdSession + "'";
            SQL += " AND NATURE_OBJECT='" + NatureObject + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "DELETE FROM eunis_advanced_search_criteria";
            SQL += " WHERE ID_SESSION='" + IdSession + "'";
            SQL += " AND NATURE_OBJECT='" + NatureObject + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "INSERT INTO eunis_advanced_search";
            SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
            SQL += " VALUES(";
            SQL += "'" + IdSession + "',";
            SQL += "'" + NatureObject + "',";
            SQL += "'0',";
            SQL += "'All')";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "INSERT INTO eunis_advanced_search";
            SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
            SQL += " VALUES(";
            SQL += "'" + IdSession + "',";
            SQL += "'" + NatureObject + "',";
            SQL += "'1',";
            SQL += "'Criteria')";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "INSERT INTO eunis_advanced_search_criteria";
            SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
            SQL += " VALUES(";
            SQL += "'" + IdSession + "',";
            SQL += "'" + NatureObject + "',";
            SQL += "'1',";
            SQL += "'" + Attribute + "',";
            SQL += "'Equal',";
            SQL += "'enter value here...',";
            SQL += "'')";

            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    public String createCriteria(String IdSession, String NatureObject) {
        String where = "";
        String SQL = "";
        String p_operator = "";
        String SQLModelStart = "";
        String SQLModelEnd = "";

        String NodeType = "";
        String IdNode = "";

        Connection con = null;
        PreparedStatement ps = null;

        ResultSet rs = null;
        ResultSet rsa = null;
        ResultSet rsb = null;
        ResultSet rsc = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQLModelStart = "SELECT ";
            SQLModelStart += "`eunis_advanced_search`.`ID_NODE`,";
            SQLModelStart += "`eunis_advanced_search`.`NODE_TYPE`,";
            SQLModelStart += "`eunis_advanced_search_criteria`.`ATTRIBUTE`,";
            SQLModelStart += "`eunis_advanced_search_criteria`.`OPERATOR`,";
            SQLModelStart += "`eunis_advanced_search_criteria`.`FIRST_VALUE`,";
            SQLModelStart += "`eunis_advanced_search_criteria`.`LAST_VALUE` ";
            SQLModelStart += "FROM ";
            SQLModelStart += "`eunis_advanced_search` ";
            SQLModelStart += "LEFT OUTER JOIN `eunis_advanced_search_criteria` ON (`eunis_advanced_search`.`ID_SESSION` = `eunis_advanced_search_criteria`.`ID_SESSION`) AND (`eunis_advanced_search`.`NATURE_OBJECT` = `eunis_advanced_search_criteria`.`NATURE_OBJECT`) AND (`eunis_advanced_search`.`ID_NODE` = `eunis_advanced_search_criteria`.`ID_NODE`) ";
            SQLModelStart += "WHERE (`eunis_advanced_search`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += "AND (`eunis_advanced_search`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";
            SQLModelEnd += "ORDER BY ";
            SQLModelEnd += "`eunis_advanced_search`.`ID_NODE` ";

            SQL = SQLModelStart;
            SQL += "AND (`eunis_advanced_search`.`ID_NODE`='0') ";
            SQL += SQLModelEnd;
            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();
            if (rs.next()) {
                p_operator = rs.getString("NODE_TYPE");
            }
            rs.close();
            ps.close();

            SQL = SQLModelStart;
            SQL += "AND (LENGTH(`eunis_advanced_search`.`ID_NODE`)=1) ";
            SQL += "AND (`eunis_advanced_search`.`ID_NODE`<>'0') ";
            SQL += SQLModelEnd;
            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            where += "[ ";
            while (rs.next()) {
                IdNode = rs.getString("ID_NODE");
                NodeType = rs.getString("NODE_TYPE");
                if (NodeType.equalsIgnoreCase("Criteria")) {
                    where += "#" + IdNode + "#";
                } else {
                    SQL = SQLModelStart;
                    SQL += "AND (LENGTH(`eunis_advanced_search`.`ID_NODE`)=3) ";
                    SQL += "AND (`eunis_advanced_search`.`ID_NODE` LIKE '"
                            + IdNode + ".%') ";
                    SQL += SQLModelEnd;
                    ps = con.prepareStatement(SQL);
                    rsa = ps.executeQuery();

                    if (rsa.isBeforeFirst()) {
                        where += "[ ";
                        while (rsa.next()) {
                            IdNode = rsa.getString("ID_NODE");
                            NodeType = rsa.getString("NODE_TYPE");
                            if (NodeType.equalsIgnoreCase("Criteria")) {
                                where += "#" + IdNode + "#";
                            } else {
                                SQL = SQLModelStart;
                                SQL += "AND (LENGTH(`eunis_advanced_search`.`ID_NODE`)=5) ";
                                SQL += "AND (`eunis_advanced_search`.`ID_NODE` LIKE '"
                                        + IdNode + ".%') ";
                                SQL += SQLModelEnd;
                                ps = con.prepareStatement(SQL);
                                rsb = ps.executeQuery();

                                if (rsb.isBeforeFirst()) {
                                    where += "[ ";
                                    while (rsb.next()) {
                                        IdNode = rsb.getString("ID_NODE");
                                        NodeType = rsb.getString("NODE_TYPE");
                                        if (NodeType.equalsIgnoreCase("Criteria")) {
                                            where += "#" + IdNode + "#";
                                        } else {
                                            SQL = SQLModelStart;
                                            SQL += "AND (LENGTH(`eunis_advanced_search`.`ID_NODE`)=7) ";
                                            SQL += "AND (`eunis_advanced_search`.`ID_NODE` LIKE '"
                                                    + IdNode + ".%') ";
                                            SQL += SQLModelEnd;
                                            ps = con.prepareStatement(SQL);
                                            rsc = ps.executeQuery();

                                            if (rsc.isBeforeFirst()) {
                                                where += "[ ";
                                                while (rsc.next()) {
                                                    IdNode = rsc.getString(
                                                            "ID_NODE");

                                                    where += "#" + IdNode + "#";

                                                    if (!rsc.isLast()) {
                                                        if (rsb.getString("NODE_TYPE").equalsIgnoreCase(
                                                                "Any")) {
                                                            where += " OR ";
                                                        }
                                                        if (rsb.getString("NODE_TYPE").equalsIgnoreCase(
                                                                "All")) {
                                                            where += " AND ";
                                                        }
                                                    }
                                                }
                                                where += " ]";
                                            }
                                            rsc.close();
                                        }

                                        if (!rsb.isLast()) {
                                            if (rsa.getString("NODE_TYPE").equalsIgnoreCase(
                                                    "Any")) {
                                                where += " OR ";
                                            }
                                            if (rsa.getString("NODE_TYPE").equalsIgnoreCase(
                                                    "All")) {
                                                where += " AND ";
                                            }
                                        }
                                    }
                                    where += " ]";
                                }
                                rsb.close();
                            }

                            if (!rsa.isLast()) {
                                if (rs.getString("NODE_TYPE").equalsIgnoreCase(
                                        "Any")) {
                                    where += " OR ";
                                }
                                if (rs.getString("NODE_TYPE").equalsIgnoreCase(
                                        "All")) {
                                    where += " AND ";
                                }
                            }
                        }
                        where += " ]";
                    }
                    rsa.close();
                }
                if (!rs.isLast()) {
                    if (p_operator.equalsIgnoreCase("Any")) {
                        where += " OR ";
                    }
                    if (p_operator.equalsIgnoreCase("All")) {
                        where += " AND ";
                    }
                }
            }
            where += " ]";

            rs.close();

            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            return where;
        } finally {
            SQLUtilities.closeAll(null, null, rsa);
            SQLUtilities.closeAll(null, null, rsb);
            SQLUtilities.closeAll(null, null, rsc);
            SQLUtilities.closeAll(con, ps, rs);
        }

        return where;
    }

    /**
     * Compute search criteria.
     * @param IdSession Session id.
     * @param NatureObject Nature object (species/habs/sites).
     * @return Criteria as sql.
     */
    public String calculateCriteria(String IdSession, String NatureObject) {
        String where = "";
        String SQL = "";
        String p_operator = "";
        String SQLModelStart = "";
        String SQLModelEnd = "";

        String NodeType = "";
        String IdNode = "";

        Connection con = null;
        PreparedStatement ps = null;

        ResultSet rs = null;
        ResultSet rsa = null;
        ResultSet rsb = null;
        ResultSet rsc = null;

        String SQLnodes = "";
        String snode = "";
        String snodes = "";
        String snodea = "";
        String snodesa = "";
        String snodeb = "";
        String snodesb = "";
        String snodec = "";
        String snodesc = "";

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQLModelStart = "DELETE FROM eunis_advanced_search_temp";
            SQLModelStart += " WHERE (`eunis_advanced_search_temp`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search_temp`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            ps.execute();
            ps.close();

            SQLModelStart = "DELETE FROM eunis_advanced_search_criteria_temp";
            SQLModelStart += " WHERE (`eunis_advanced_search_criteria_temp`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search_criteria_temp`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            ps.execute();
            ps.close();

            // System.out.println("delete done");

            SQLModelStart = "SELECT * FROM eunis_advanced_search";
            SQLModelStart += " WHERE (`eunis_advanced_search`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            rs = ps.executeQuery();

            while (rs.next()) {
                SQL = "INSERT INTO eunis_advanced_search_temp";
                SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
                SQL += " VALUES(";
                SQL += "'" + rs.getString("ID_SESSION") + "',";
                SQL += "'" + rs.getString("NATURE_OBJECT") + "',";
                SQL += "'" + rs.getString("ID_NODE") + "',";
                SQL += "'" + rs.getString("NODE_TYPE") + "'";
                SQL += ")";
                ps = con.prepareStatement(SQL);
                ps.execute();
            }
            rs.close();
            ps.close();

            SQLModelStart = "SELECT * FROM eunis_advanced_search_criteria";
            SQLModelStart += " WHERE (`eunis_advanced_search_criteria`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search_criteria`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            rs = ps.executeQuery();

            while (rs.next()) {
                SQL = "INSERT INTO eunis_advanced_search_criteria_temp";
                SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
                SQL += " VALUES(";
                SQL += "'" + rs.getString("ID_SESSION") + "',";
                SQL += "'" + rs.getString("NATURE_OBJECT") + "',";
                SQL += "'" + rs.getString("ID_NODE") + "',";
                SQL += "'" + rs.getString("ATTRIBUTE") + "',";
                SQL += "'" + rs.getString("OPERATOR") + "',";
                SQL += "'" + rs.getString("FIRST_VALUE") + "',";
                SQL += "'" + rs.getString("LAST_VALUE") + "'";
                SQL += ")";
                ps = con.prepareStatement(SQL);
                ps.execute();
            }
            rs.close();
            ps.close();

            // System.out.println("populate done");

            SQLModelStart = "SELECT ";
            SQLModelStart += "`eunis_advanced_search_temp`.`ID_NODE`,";
            SQLModelStart += "`eunis_advanced_search_temp`.`NODE_TYPE`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`ATTRIBUTE`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`OPERATOR`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`FIRST_VALUE`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`LAST_VALUE` ";
            SQLModelStart += "FROM ";
            SQLModelStart += "`eunis_advanced_search_temp` ";
            SQLModelStart += "LEFT OUTER JOIN `eunis_advanced_search_criteria_temp` ON (`eunis_advanced_search_temp`.`ID_SESSION` = `eunis_advanced_search_criteria_temp`.`ID_SESSION`) AND (`eunis_advanced_search_temp`.`NATURE_OBJECT` = `eunis_advanced_search_criteria_temp`.`NATURE_OBJECT`) AND (`eunis_advanced_search_temp`.`ID_NODE` = `eunis_advanced_search_criteria_temp`.`ID_NODE`) ";
            SQLModelStart += "WHERE (`eunis_advanced_search_temp`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += "AND (`eunis_advanced_search_temp`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";
            SQLModelEnd += "ORDER BY ";
            SQLModelEnd += "`eunis_advanced_search_temp`.`ID_NODE` ";

            SQL = SQLModelStart;
            SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE`='0') ";
            SQL += SQLModelEnd;

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            if (rs.next()) {
                p_operator = rs.getString("NODE_TYPE");
            }
            rs.close();
            ps.close();

            SQL = SQLModelStart;
            SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=1) ";
            SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE`<>'0') ";
            SQL += SQLModelEnd;

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            where += "[ ";
            snodes = "";
            while (rs.next()) {
                snode = "";
                IdNode = rs.getString("ID_NODE");
                NodeType = rs.getString("NODE_TYPE");
                if (NodeType.equalsIgnoreCase("Criteria")) {
                    where += "#" + IdNode + "#";
                    snode = BuildFilter(IdNode, IdSession, NatureObject);
                    // System.out.println("snode = " + snode);
                } else {
                    SQL = SQLModelStart;
                    SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=3) ";
                    SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE` LIKE '"
                            + IdNode + ".%') ";
                    SQL += SQLModelEnd;
                    ps = con.prepareStatement(SQL);
                    rsa = ps.executeQuery();

                    if (rsa.isBeforeFirst()) {
                        where += "[ ";
                        snodesa = "";
                        while (rsa.next()) {
                            snodea = "";
                            IdNode = rsa.getString("ID_NODE");
                            NodeType = rsa.getString("NODE_TYPE");
                            if (NodeType.equalsIgnoreCase("Criteria")) {
                                where += "#" + IdNode + "#";
                                snodea = BuildFilter(IdNode, IdSession,
                                        NatureObject);
                                // System.out.println("snodea = " + snodea);
                            } else {
                                SQL = SQLModelStart;
                                SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=5) ";
                                SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE` LIKE '"
                                        + IdNode + ".%') ";
                                SQL += SQLModelEnd;
                                ps = con.prepareStatement(SQL);
                                rsb = ps.executeQuery();

                                if (rsb.isBeforeFirst()) {
                                    where += "[ ";
                                    snodesb = "";
                                    while (rsb.next()) {
                                        snodeb = "";
                                        IdNode = rsb.getString("ID_NODE");
                                        NodeType = rsb.getString("NODE_TYPE");
                                        if (NodeType.equalsIgnoreCase("Criteria")) {
                                            where += "#" + IdNode + "#";
                                            snodeb = BuildFilter(IdNode,
                                                    IdSession, NatureObject);
                                            // System.out.println("snodeb = " + snodeb);
                                        } else {
                                            SQL = SQLModelStart;
                                            SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=7) ";
                                            SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE` LIKE '"
                                                    + IdNode + ".%') ";
                                            SQL += SQLModelEnd;
                                            ps = con.prepareStatement(SQL);
                                            rsc = ps.executeQuery();

                                            if (rsc.isBeforeFirst()) {
                                                where += "[ ";
                                                snodesc = "";
                                                while (rsc.next()) {
                                                    snodec = "";
                                                    IdNode = rsc.getString(
                                                            "ID_NODE");

                                                    where += "#" + IdNode + "#";
                                                    snodec = BuildFilter(IdNode,
                                                            IdSession,
                                                            NatureObject);
                                                    // System.out.println("snodec = " + snodec);

                                                    snodesc += snodec;
                                                    if (!rsc.isLast()) {
                                                        if (rsb.getString("NODE_TYPE").equalsIgnoreCase(
                                                                "Any")) {
                                                            where += " OR ";
                                                            snodesc += " OR ";
                                                        }
                                                        if (rsb.getString("NODE_TYPE").equalsIgnoreCase(
                                                                "All")) {
                                                            where += " AND ";
                                                            snodesc += " AND ";
                                                        }
                                                    }
                                                }
                                                where += " ]";
                                            }
                                            rsc.close();
                                            // System.out.println("snodesc = " + snodesc);
                                            // create filter from this level criterias
                                            SQLnodes = "SELECT ID_NATURE_OBJECT FROM chm62edt_" + NatureObject.toLowerCase();
                                            SQLnodes += " WHERE (" + snodesc
                                                    + ")";
                                            // System.out.println("SQLnodes = " + SQLnodes);
                                            snodeb = "ID_NATURE_OBJECT IN ("
                                                    + ExecuteFilterSQL(SQLnodes,
                                                    "")
                                                    + ")";
                                            // System.out.println("snodeb (facut din snodesc) = " + snodeb);
                                        }
                                        snodesb += snodeb;
                                        if (!rsb.isLast()) {
                                            if (rsa.getString("NODE_TYPE").equalsIgnoreCase(
                                                    "Any")) {
                                                where += " OR ";
                                                snodesb += " OR ";
                                            }
                                            if (rsa.getString("NODE_TYPE").equalsIgnoreCase(
                                                    "All")) {
                                                where += " AND ";
                                                snodesb += " AND ";
                                            }
                                        }
                                    }
                                    where += " ]";
                                }
                                rsb.close();
                                // System.out.println("snodesb = " + snodesb);
                                // create filter from this level criterias
                                SQLnodes = "SELECT ID_NATURE_OBJECT FROM chm62edt_" + NatureObject.toLowerCase();
                                SQLnodes += " WHERE (" + snodesb + ")";
                                // System.out.println("SQLnodes = " + SQLnodes);
                                snodea = "ID_NATURE_OBJECT IN ("
                                        + ExecuteFilterSQL(SQLnodes, "") + ")";
                                // System.out.println("snodea (facut din snodesb) = " + snodea);
                            }
                            snodesa += snodea;
                            if (!rsa.isLast()) {
                                if (rs.getString("NODE_TYPE").equalsIgnoreCase(
                                        "Any")) {
                                    where += " OR ";
                                    snodesa += " OR ";
                                }
                                if (rs.getString("NODE_TYPE").equalsIgnoreCase(
                                        "All")) {
                                    where += " AND ";
                                    snodesa += " AND ";
                                }
                            }
                        }
                        where += " ]";
                    }
                    rsa.close();
                    // System.out.println("snodesa = " + snodesa);
                    // create filter from this level criterias
                    SQLnodes = "SELECT ID_NATURE_OBJECT FROM chm62edt_" + NatureObject.toLowerCase();
                    SQLnodes += " WHERE (" + snodesa + ")";
                    // System.out.println("SQLnodes = " + SQLnodes);
                    // System.out.println("ce se executa ca sa obtin snode = " + ExecuteFilterSQL(SQLnodes,""));
                    snode = "ID_NATURE_OBJECT IN ("
                            + ExecuteFilterSQL(SQLnodes, "") + ")";
                    // System.out.println("snode (facut di snodesa) = " + snode);
                }
                snodes += snode;

                if (!rs.isLast()) {
                    if (p_operator.equalsIgnoreCase("Any")) {
                        where += " OR ";
                        snodes += " OR ";
                    }
                    if (p_operator.equalsIgnoreCase("All")) {
                        where += " AND ";
                        snodes += " AND ";
                    }
                }
            }
            where += " ]";
            rs.close();

            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            return where;
        } finally {
            SQLUtilities.closeAll(null, null, rsa);
            SQLUtilities.closeAll(null, null, rsb);
            SQLUtilities.closeAll(null, null, rsc);
            SQLUtilities.closeAll(con, ps, rs);
        }

        return snodes;
    }

    /**
     * Calculate search criteria for save.
     * @param IdSession Session ID.
     * @param NatureObject nature object (species/habs/sites).
     * @return Criteria.
     */
    public String calculateCriteriaSave(String IdSession, String NatureObject) {
        String where = "";
        String SQL = "";
        String p_operator = "";
        String SQLModelStart = "";
        String SQLModelEnd = "";

        String NodeType = "";
        String IdNode = "";

        Connection con = null;
        PreparedStatement ps = null;

        ResultSet rs = null;
        ResultSet rsa = null;
        ResultSet rsb = null;
        ResultSet rsc = null;

        String SQLnodes = "";
        String snode = "";
        String snodes = "";
        String snodea = "";
        String snodesa = "";
        String snodeb = "";
        String snodesb = "";
        String snodec = "";
        String snodesc = "";

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQLModelStart = "DELETE FROM eunis_advanced_search_temp";
            SQLModelStart += " WHERE (`eunis_advanced_search_temp`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search_temp`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            ps.execute();
            ps.close();

            SQLModelStart = "DELETE FROM eunis_advanced_search_criteria_temp";
            SQLModelStart += " WHERE (`eunis_advanced_search_criteria_temp`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search_criteria_temp`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            ps.execute();
            ps.close();

            // System.out.println("delete done");

            SQLModelStart = "SELECT * FROM eunis_advanced_search";
            SQLModelStart += " WHERE (`eunis_advanced_search`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            rs = ps.executeQuery();

            while (rs.next()) {
                SQL = "INSERT INTO eunis_advanced_search_temp";
                SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,NODE_TYPE)";
                SQL += " VALUES(";
                SQL += "'" + rs.getString("ID_SESSION") + "',";
                SQL += "'" + rs.getString("NATURE_OBJECT") + "',";
                SQL += "'" + rs.getString("ID_NODE") + "',";
                SQL += "'" + rs.getString("NODE_TYPE") + "'";
                SQL += ")";
                ps = con.prepareStatement(SQL);
                ps.execute();
            }
            rs.close();
            ps.close();

            SQLModelStart = "SELECT * FROM eunis_advanced_search_criteria";
            SQLModelStart += " WHERE (`eunis_advanced_search_criteria`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += " AND (`eunis_advanced_search_criteria`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";

            ps = con.prepareStatement(SQLModelStart);
            rs = ps.executeQuery();

            while (rs.next()) {
                SQL = "INSERT INTO eunis_advanced_search_criteria_temp";
                SQL += "(ID_SESSION,NATURE_OBJECT,ID_NODE,ATTRIBUTE,OPERATOR,FIRST_VALUE,LAST_VALUE)";
                SQL += " VALUES(";
                SQL += "'" + rs.getString("ID_SESSION") + "',";
                SQL += "'" + rs.getString("NATURE_OBJECT") + "',";
                SQL += "'" + rs.getString("ID_NODE") + "',";
                SQL += "'" + rs.getString("ATTRIBUTE") + "',";
                SQL += "'" + rs.getString("OPERATOR") + "',";
                SQL += "'" + rs.getString("FIRST_VALUE") + "',";
                SQL += "'" + rs.getString("LAST_VALUE") + "'";
                SQL += ")";
                ps = con.prepareStatement(SQL);
                ps.execute();
            }
            rs.close();
            ps.close();

            // System.out.println("populate done");

            SQLModelStart = "SELECT ";
            SQLModelStart += "`eunis_advanced_search_temp`.`ID_NODE`,";
            SQLModelStart += "`eunis_advanced_search_temp`.`NODE_TYPE`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`ATTRIBUTE`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`OPERATOR`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`FIRST_VALUE`,";
            SQLModelStart += "`eunis_advanced_search_criteria_temp`.`LAST_VALUE` ";
            SQLModelStart += "FROM ";
            SQLModelStart += "`eunis_advanced_search_temp` ";
            SQLModelStart += "LEFT OUTER JOIN `eunis_advanced_search_criteria_temp` ON (`eunis_advanced_search_temp`.`ID_SESSION` = `eunis_advanced_search_criteria_temp`.`ID_SESSION`) AND (`eunis_advanced_search_temp`.`NATURE_OBJECT` = `eunis_advanced_search_criteria_temp`.`NATURE_OBJECT`) AND (`eunis_advanced_search_temp`.`ID_NODE` = `eunis_advanced_search_criteria_temp`.`ID_NODE`) ";
            SQLModelStart += "WHERE (`eunis_advanced_search_temp`.`ID_SESSION`='"
                    + IdSession + "') ";
            SQLModelStart += "AND (`eunis_advanced_search_temp`.`NATURE_OBJECT`='"
                    + NatureObject + "') ";
            SQLModelEnd += "ORDER BY ";
            SQLModelEnd += "`eunis_advanced_search_temp`.`ID_NODE` ";

            SQL = SQLModelStart;
            SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE`='0') ";
            SQL += SQLModelEnd;

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            if (rs.next()) {
                p_operator = rs.getString("NODE_TYPE");
            }
            rs.close();
            ps.close();

            SQL = SQLModelStart;
            SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=1) ";
            SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE`<>'0') ";
            SQL += SQLModelEnd;

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            where += "[ ";
            snodes = "";
            while (rs.next()) {

                snode = "";
                IdNode = rs.getString("ID_NODE");
                NodeType = rs.getString("NODE_TYPE");
                if (NodeType.equalsIgnoreCase("Criteria")) {
                    where += "#" + IdNode + "#";
                    snode = BuildFilterSave(IdNode, IdSession, NatureObject);
                    // System.out.println(" snode = " + snode);
                } else {
                    SQL = SQLModelStart;
                    SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=3) ";
                    SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE` LIKE '"
                            + IdNode + ".%') ";
                    SQL += SQLModelEnd;
                    ps = con.prepareStatement(SQL);
                    rsa = ps.executeQuery();

                    if (rsa.isBeforeFirst()) {
                        where += "[ ";
                        snodesa = "";
                        while (rsa.next()) {
                            snodea = "";
                            IdNode = rsa.getString("ID_NODE");
                            NodeType = rsa.getString("NODE_TYPE");
                            if (NodeType.equalsIgnoreCase("Criteria")) {
                                where += "#" + IdNode + "#";
                                snodea = BuildFilterSave(IdNode, IdSession,
                                        NatureObject);
                                // System.out.println("snodea = " + snodea);
                            } else {
                                SQL = SQLModelStart;
                                SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=5) ";
                                SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE` LIKE '"
                                        + IdNode + ".%') ";
                                SQL += SQLModelEnd;
                                ps = con.prepareStatement(SQL);
                                rsb = ps.executeQuery();

                                if (rsb.isBeforeFirst()) {
                                    where += "[ ";
                                    snodesb = "";
                                    while (rsb.next()) {
                                        snodeb = "";
                                        IdNode = rsb.getString("ID_NODE");
                                        NodeType = rsb.getString("NODE_TYPE");
                                        if (NodeType.equalsIgnoreCase("Criteria")) {
                                            where += "#" + IdNode + "#";
                                            snodeb = BuildFilterSave(IdNode,
                                                    IdSession, NatureObject);
                                            // System.out.println("snodeb = " + snodeb);
                                        } else {
                                            SQL = SQLModelStart;
                                            SQL += "AND (LENGTH(`eunis_advanced_search_temp`.`ID_NODE`)=7) ";
                                            SQL += "AND (`eunis_advanced_search_temp`.`ID_NODE` LIKE '"
                                                    + IdNode + ".%') ";
                                            SQL += SQLModelEnd;
                                            ps = con.prepareStatement(SQL);
                                            rsc = ps.executeQuery();

                                            if (rsc.isBeforeFirst()) {
                                                where += "[ ";
                                                snodesc = "";
                                                while (rsc.next()) {
                                                    snodec = "";
                                                    IdNode = rsc.getString(
                                                            "ID_NODE");

                                                    where += "#" + IdNode + "#";
                                                    snodec = BuildFilterSave(
                                                            IdNode, IdSession,
                                                            NatureObject);
                                                    // System.out.println("snodec = " + snodec);

                                                    snodesc += snodec;
                                                    if (!rsc.isLast()) {
                                                        if (rsb.getString("NODE_TYPE").equalsIgnoreCase(
                                                                "Any")) {
                                                            where += " OR ";
                                                            snodesc += " OR ";
                                                        }
                                                        if (rsb.getString("NODE_TYPE").equalsIgnoreCase(
                                                                "All")) {
                                                            where += " AND ";
                                                            snodesc += " AND ";
                                                        }
                                                    }
                                                }
                                                where += " ]";
                                            }
                                            rsc.close();
                                            // System.out.println("snodesc = " + snodesc);
                                            // create filter from this level criterias
                                            String natObj = NatureObject.substring(
                                                    0,
                                                    NatureObject.indexOf("Save"));

                                            SQLnodes = "SELECT ID_NATURE_OBJECT FROM chm62edt_" + natObj.toLowerCase();
                                            SQLnodes += " WHERE (" + snodesc
                                                    + ")";
                                            // System.out.println("SQLnodes = " + SQLnodes);
                                            snodeb = "ID_NATURE_OBJECT IN ("
                                                    + ExecuteFilterSQL(SQLnodes,
                                                    "")
                                                    + ")";
                                            // System.out.println("snodeb (facut din snodesc) = " + snodeb);
                                        }
                                        snodesb += snodeb;
                                        if (!rsb.isLast()) {
                                            if (rsa.getString("NODE_TYPE").equalsIgnoreCase(
                                                    "Any")) {
                                                where += " OR ";
                                                snodesb += " OR ";
                                            }
                                            if (rsa.getString("NODE_TYPE").equalsIgnoreCase(
                                                    "All")) {
                                                where += " AND ";
                                                snodesb += " AND ";
                                            }
                                        }
                                    }
                                    where += " ]";
                                }
                                rsb.close();
                                // System.out.println("snodesb = " + snodesb);
                                // create filter from this level criterias
                                String natObj = NatureObject.substring(0,
                                        NatureObject.indexOf("Save"));

                                SQLnodes = "SELECT ID_NATURE_OBJECT FROM chm62edt_" + natObj.toLowerCase();
                                SQLnodes += " WHERE (" + snodesb + ")";
                                // System.out.println("SQLnodes = " + SQLnodes);
                                snodea = "ID_NATURE_OBJECT IN ("
                                        + ExecuteFilterSQL(SQLnodes, "") + ")";
                                // System.out.println("snodea (facut din snodesb) = " + snodea);
                            }
                            snodesa += snodea;
                            if (!rsa.isLast()) {
                                if (rs.getString("NODE_TYPE").equalsIgnoreCase(
                                        "Any")) {
                                    where += " OR ";
                                    snodesa += " OR ";
                                }
                                if (rs.getString("NODE_TYPE").equalsIgnoreCase(
                                        "All")) {
                                    where += " AND ";
                                    snodesa += " AND ";
                                }
                            }
                        }
                        where += " ]";
                    }
                    rsa.close();
                    // System.out.println("snodesa = " + snodesa);
                    // create filter from this level criterias
                    String natObj = NatureObject.substring(0,
                            NatureObject.indexOf("Save"));

                    SQLnodes = "SELECT ID_NATURE_OBJECT FROM chm62edt_" + natObj.toLowerCase();
                    SQLnodes += " WHERE (" + snodesa + ")";
                    // System.out.println("SQLnodes = " + SQLnodes);
                    // System.out.println("ce se executa ca sa obtin snode = " + ExecuteFilterSQL(SQLnodes,""));
                    snode = "ID_NATURE_OBJECT IN ("
                            + ExecuteFilterSQL(SQLnodes, "") + ")";
                    // System.out.println("snode (facut di snodesa) = " + snode);
                }
                snodes += snode;

                if (!rs.isLast()) {
                    if (p_operator.equalsIgnoreCase("Any")) {
                        where += " OR ";
                        snodes += " OR ";
                    }
                    if (p_operator.equalsIgnoreCase("All")) {
                        where += " AND ";
                        snodes += " AND ";
                    }
                }
            }
            where += " ]";
            rs.close();

            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            return where;
        }  finally {
            SQLUtilities.closeAll(null, null, rsa);
            SQLUtilities.closeAll(null, null, rsb);
            SQLUtilities.closeAll(null, null, rsc);
            SQLUtilities.closeAll(con, ps, rs);
        }

        return snodes;
    }

    private String Spaces(int spaces) {
        String result = "";

        for (int i = 0; i < spaces; i++) {
            result = result + "&nbsp;";
        }

        return result;
    }

    public String FormatCriteria(String where) {
        String result = "";
        int ident = -1;
        int i = 0;
        char c;

        for (i = 0; i < where.length(); i++) {
            c = where.charAt(i);
            if (c == '(') {
                ident++;
                result += c;
                result += "<br />" + Spaces(ident * 3);
            } else {
                if (c == ')') {
                    result += "<br />" + Spaces(ident * 3);
                    result += c;
                    ident--;
                    result += "<br />" + Spaces(ident * 3);
                } else {
                    result += c;
                }
            }
        }

        return result;
    }

    public String ExecuteFilterSQL(String SQL, String Delimiter) {
        String result = "";
        int resCount = 0;
        StringBuilder resultbuf = new StringBuilder();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (SQL.length() > 0) {
                resultCount = 0;

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
                    if (resultCount < SQL_LIMIT) {
                        // result+=Delimiter+rs.getString(1)+Delimiter;
                        // result+=",";
                        resultbuf.append(Delimiter).append(rs.getString(1)).append(
                                Delimiter);
                        resultbuf.append(",");
                    }
                }
                if (resultCount >= SQL_LIMIT) {// System.out.println("<<< SQL LIMIT of "+SQL_LIMIT+" reached!. The results might not be concludent! >>>");
                }

                result = resultbuf.toString();

                if (result.length() > 0) {
                    if (result.substring(result.length() - 1).equalsIgnoreCase(
                            ",")) {
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
            e.printStackTrace();
            return result;
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }
        return result;
    }

    public String ExecuteSQL(String SQL) {
        String result = "";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (SQL.length() > 0) {
                ResultSet rs = null;

                ps = con.prepareStatement(SQL);
                // System.out.println("Executing: "+SQL);
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
            return "";
        }

        return result;
    }

    public int getResultCount() {
        return resultCount;
    }
}
