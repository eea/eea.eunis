package ro.finsiel.eunis.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import ro.finsiel.eunis.dataimport.ColumnDTO;
import ro.finsiel.eunis.dataimport.ImportLogDTO;
import eionet.eunis.dto.DoubleDTO;

/**
 * User: ancai Date: 03.03.2005 Time: 15:35:37.
 */
public class SQLUtilities {

    private static final Logger logger = Logger.getLogger(SQLUtilities.class);

    private int SQL_LIMIT = 1000;
    private int resultCount = 0;
    private static final String INSERT_BOOKMARK =
        "INSERT INTO eunis_bookmarks( USERNAME, BOOKMARK, DESCRIPTION ) VALUES( ?, ?, ?)";

    /**
     * SQL used for soundex search
     */
    public static String smartSoundex = "" + " select name,phonetic,object_type" + " from `chm62edt_soundex`"
    + " where object_type = '<object_type>'" + " and phonetic = soundex('<name>')"
    + " and left(name,6) = left('<name>',6)" + " union" + " select name,phonetic,object_type" + " from `chm62edt_soundex`"
    + " where object_type = '<object_type>'" + " and phonetic = soundex('<name>')"
    + " and left(name,5) = left('<name>',5)" + " union" + " select name,phonetic,object_type" + " from `chm62edt_soundex`"
    + " where object_type = '<object_type>'" + " and phonetic = soundex('<name>')"
    + " and left(name,4) = left('<name>',4)" + " union" + " select name,phonetic,object_type" + " from `chm62edt_soundex`"
    + " where object_type = '<object_type>'" + " and phonetic = soundex('<name>')"
    + " and left(name,3) = left('<name>',3)" + " union" + " select name,phonetic,object_type" + " from `chm62edt_soundex`"
    + " where object_type = '<object_type>'" + " and phonetic = soundex('<name>')"
    + " and left(name,2) = left('<name>',2)" + " union" + " select name,phonetic,object_type" + " from `chm62edt_soundex`"
    + " where object_type = '<object_type>'" + " and left(phonetic,3) = left(soundex('<name>'),3)";

    /**
     * Creates a new SQLUtilities object.
     */
    public SQLUtilities() {
    }

    /**
     * Initialization method for this object.
     *
     * @deprecated Use the connection pool instead
     */
    public void Init() {
    }

    /**
     * Limit the results computed.
     *
     * @param SQLLimit
     *            Limit.
     */
    public void SetSQLLimit(int SQLLimit) {
        SQL_LIMIT = SQLLimit;
    }

    /**
     * Executes parameterized sql query and return list of results. Note! only first column in query is returned.
     *
     * @param sql
     *            - sql string
     * @param params
     *            - sql parameters
     * @return list of results
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> executeQuery(String sql, List<Object> params) throws SQLException {
        Connection connection = null;
        PreparedStatement prepared = null;
        ResultSet result = null;

        try {
            connection = getConnection();
            List<T> resultList = new LinkedList<T>();

            prepared = prepareStatement(sql, params, connection);
            result = prepared.executeQuery();
            while (result.next()) {
                resultList.add((T)result.getObject(1));
            }
            return resultList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(connection, prepared, result);
        }
    }

    /**
     * Execute an sql.
     *
     * @param SQL
     *            SQL.
     * @param Delimiter
     *            LIMIT
     * @return result
     */
    public String ExecuteFilterSQL(String SQL, String Delimiter) {

        if (SQL == null || Delimiter == null || SQL.trim().length() <= 0) {
            return "";
        }

        String result = "";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            resultCount = 0;

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();
            result = "";
            while (rs.next()) {
                resultCount++;
                if (resultCount <= SQL_LIMIT) {
                    result += Delimiter + rs.getString(1) + Delimiter;
                    result += ",";
                }
            }

            if (result.length() > 0) {
                if (result.substring(result.length() - 1).equalsIgnoreCase(",")) {
                    result = result.substring(0, result.length() - 1);
                }
            }

        } catch (Exception e) {
            logger.debug(e, e);
            result = "";
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    public Connection getConnection() {
        Connection con = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
        } catch (Exception e) {
            logger.debug(e, e);
            return null;
        }
        return con;
    }

    /**
     * Executes a SELECT sql and returns the first value.
     *
     * @param SQL
     *            SQL.
     * @return First column.
     */
    public ArrayList<String> SQL2Array(String SQL) {

        if (SQL == null || SQL.trim().length() <= 0) {
            return null;
        }

        ArrayList<String> result = new ArrayList<String>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString(1));
            }

            closeAll(con, ps, rs);
        } catch (Exception e) {
            logger.debug(e, e);
            result = null;
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Executes a SELECT sql and returns the given two columns as object.
     *
     * @param SQL
     * @param firstColumn
     * @param secondColumn
     * @return list
     */
    public ArrayList<DoubleDTO> SQL2ListOfDoubles(String SQL, String firstColumn, String secondColumn) {

        if (SQL == null || SQL.trim().length() <= 0 || firstColumn == null || secondColumn == null) {
            return null;
        }

        ArrayList<DoubleDTO> result = new ArrayList<DoubleDTO>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            while (rs.next()) {
                DoubleDTO d = new DoubleDTO();
                d.setOne(rs.getString(firstColumn));
                d.setTwo(rs.getString(secondColumn));
                result.add(d);
            }
        } catch (Exception e) {
            logger.debug(e, e);
            result = null;
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Executes a SELECT sql and returns the first value.
     *
     * @param SQL
     *            SQL.
     * @return First column.
     */
    public String ExecuteSQL(String SQL) {

        if (SQL == null || SQL.trim().length() <= 0) {
            return "";
        }

        logger.debug("SQL = " + SQL);

        String result = "";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            if (rs.next()) {
                result = rs.getString(1);
            }

        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }


    /**
     * Executes a CREATE sql.
     *
     * @param SQL
     */
    public void UpdateSQL(String SQL) {

        if (SQL == null || SQL.trim().length() <= 0) {
            logger.info("SQL is empty!");
        }
        // logger.debug("SQL is empty!");

        String result = "";

        Connection con = null;
        Statement statement = null;
        int updateQuery = 0;

        try {
            con = getConnection();

            statement = con.createStatement();
            updateQuery = statement.executeUpdate(SQL);

            if (updateQuery != 0) {
                logger.info("table is created successfully and " + updateQuery
                        + " row is inserted.");
            }

        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            closeAll(con, statement, null);
        }
    }



    /**
     * Executes a sql.
     *
     * @param SQL
     *            SQL.
     */
    public void ExecuteDirectSQL(String SQL) {

        if (SQL == null || SQL.trim().length() <= 0) {
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            ps = con.prepareStatement(SQL);
            ps.execute();
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            closeAll(con, ps, null);
        }
    }

    /**
     * @param parameterizedSQL The prepared statement SQL
     * @param values The list of values, in the PS order
     * @param conn Connection to run the PS
     * @return prepared sql statement
     * @throws SQLException
     */
    private PreparedStatement prepareStatement(String parameterizedSQL, List<Object> values, Connection conn) throws SQLException {

        PreparedStatement pstmt = conn.prepareStatement(parameterizedSQL);

        for (int i = 0; values != null && i < values.size(); i++) {
            try {
                String val = (String) values.get(i);

                if (val != null && val.equals("NULL")) {
                    pstmt.setNull(i + 1, Types.NULL);
                } else {
                    pstmt.setObject(i + 1, values.get(i));
                }
            } catch (ClassCastException e) {
                pstmt.setObject(i + 1, values.get(i));
            }
        }
        return pstmt;
    }

    /**
     * Executes given parameterized SQL statement with the given parameter values, passing the result set rows to the given reader.
     * Uses connection created internally from arguments supplied via {@link #Init()}.
     *
     * @param parameterizedSQL The given parameterized SQL.
     * @param values The given SQL parameter values.
     * @param rsReader The given result set reader.
     * @throws SQLException When executing the query and traversing its result.
     */
    public void executeQuery(String parameterizedSQL, List<Object> values, ResultSetBaseReader rsReader) throws SQLException {

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Connection con = null;
        try {
            con = getConnection();
            pstmt = prepareStatement(parameterizedSQL, values, con);
            rs = pstmt.executeQuery();
            if (rs != null) {
                ResultSetMetaData rsMd = rs.getMetaData();

                if (rsMd != null && rsMd.getColumnCount() > 0) {
                    rsReader.setResultSetMetaData(rsMd);
                    while (rs.next()) {
                        rsReader.readRow(rs);
                    }
                }
            }
        } finally {
            closeAll(con, pstmt, rs);
        }

    }

    /**
     * Execute an sql.
     *
     * @param SQL
     *            SQL.
     * @param noColumns
     *            Number of columns
     * @return list of sql results.
     */
    public List ExecuteSQLReturnList(String SQL, int noColumns) {

        if (SQL == null || SQL.trim().length() <= 0 || noColumns <= 0) {
            return new ArrayList();
        }

        List result = new ArrayList();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();

            while (rs.next()) {
                TableColumns columns = new TableColumns();
                List columnsValues = new ArrayList();

                for (int i = 1; i <= noColumns; i++) {
                    columnsValues.add(rs.getString(i));
                }
                columns.setColumnsValues(columnsValues);
                result.add(columns);
            }
        } catch (Exception e) {
            logger.debug(e, e);
            result = new ArrayList();
        } finally {
            closeAll(con, ps, rs);
        }
        return result;
    }

    /**
     * Executes the statement and returns the result as a hashtable.
     * @param sql_stmt SLQ to run
     * @return Hashtable containing results, keyed by column name; only the <i>last</i> row is returned
     */
    public Hashtable<String, String> getHashtable(String sql_stmt) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        Hashtable<String, String> h = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement(sql_stmt);
            rset = stmt.executeQuery(sql_stmt);
            ResultSetMetaData md = rset.getMetaData();

            int colCnt = md.getColumnCount();

            while (rset.next()) {
                h = new Hashtable<String, String>();
                for (int i = 0; i < colCnt; ++i) {
                    String name = md.getColumnName(i + 1);
                    String value = rset.getString(i + 1);

                    if (value == null) {
                        value = "";
                    }
                    h.put(name, value);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            closeAll(con, stmt, rset);
        }
        return h;
    }

    /**
     * Count search results.
     *
     * @return reusults count.
     */
    public int getResultCount() {
        return resultCount;
    }

    /**
     * Execute UPDATE statement
     *
     * @param tableName
     *            table name
     * @param columnName
     *            column update
     * @param columnValue
     *            new value for column
     * @param whereCondition
     *            WHERE condition
     * @return operation status
     */
    public boolean ExecuteUpdate(String tableName, String columnName, String columnValue, String whereCondition) {

        boolean result = true;

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            ps = con.prepareStatement("UPDATE " + tableName + " SET " + columnName + " = '" + columnValue + "' WHERE 1=1"
                        + (whereCondition == null || whereCondition.trim().length() <= 0 ? "" : " AND " + whereCondition));
            ps.execute();
        } catch (Exception e) {
            logger.debug(e, e);
            result = false;
        } finally {
            closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Execute DELETE statement.
     *
     * @param tableName
     *            table name
     * @param whereCondition
     *            WHERE
     * @return operation status
     */
    public boolean ExecuteDelete(String tableName, String whereCondition) {

        boolean result = true;

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            ps =
                con.prepareStatement("DELETE FROM " + tableName + " WHERE 1=1"
                        + (whereCondition == null || whereCondition.trim().length() <= 0 ? "" : " AND " + whereCondition));
            ps.execute();
        } catch (Exception e) {
            logger.debug(e, e);
            result = false;
        } finally {
            closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Insert bookmark functionality.
     *
     * @param username
     *            username associated with that bookmark
     * @param bookmarkURL
     *            URL
     * @param description
     *            Short description displayed to the user
     * @return operation status
     */
    public boolean insertBookmark(String username, String bookmarkURL, String description) {
        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(INSERT_BOOKMARK);
            ps.setString(1, username);
            ps.setString(2, bookmarkURL);
            ps.setString(3, description);
            ps.execute();
            result = true;
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Execute INSERT statement.
     *
     * @param tableName
     *            table name
     * @param tableColumns
     *            columns
     * @return operation status
     */
    public boolean ExecuteInsert(String tableName, TableColumns tableColumns) {

        if (tableName == null || tableName.trim().length() <= 0 || tableColumns == null || tableColumns.getColumnsNames() == null
                || tableColumns.getColumnsNames().size() <= 0 || tableColumns.getColumnsValues() == null
                || tableColumns.getColumnsValues().size() <= 0) {
            return false;
        }

        boolean result = true;

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            String namesList = "";
            String valuesList = "";

            for (int i = 0; i < tableColumns.getColumnsNames().size(); i++) {
                namesList +=
                     tableColumns.getColumnsNames().get(i)
                    + (i < tableColumns.getColumnsNames().size() - 1 ? "," : "");
                valuesList +=
                    "'" + tableColumns.getColumnsValues().get(i) + "'"
                    + (i < tableColumns.getColumnsNames().size() - 1 ? "," : "");
            }

            ps = con.prepareStatement("INSERT INTO " + tableName + " ( " + namesList + " ) values ( " + valuesList + " ) ");

            ps.execute();
        } catch (Exception e) {
            logger.debug(e, e);
            result = false;
        } finally {
            closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Execute INSERT statement.
     *
     * @param tableName
     *            table name
     * @param tableRows
     *            columns
     * @return operation status
     */
    public List<String> ExecuteMultipleInsert(String tableName, List<TableColumns> tableRows) throws Exception {

        if (tableName == null || tableName.trim().length() <= 0 || tableRows == null || tableRows.size() <= 0) {
            return null;
        }

        List<String> result = new ArrayList<String>();

        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        String query = "";

        try {
            con = getConnection();
            con.setAutoCommit(false);

            st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData rsMeta = rs.getMetaData();

            List<String> mysqlColumnNames = new ArrayList<String>();
            int numberOfColumns = rsMeta.getColumnCount();

            for (int x = 1; x <= numberOfColumns; x++) {
                String columnName = rsMeta.getColumnName(x);

                mysqlColumnNames.add(columnName);
            }

            for (Iterator<TableColumns> it = tableRows.iterator(); it.hasNext();) {

                TableColumns tableColumns = it.next();
                String namesList = "";
                String valuesList = "";

                for (int i = 0; i < tableColumns.getColumnsNames().size(); i++) {
                    String columnName = (String) tableColumns.getColumnsNames().get(i);

                    if (columnName != null && !columnName.equals("")) {
                        columnName = "`" + columnName + "`";
                    }
                    String columnValue = (String) tableColumns.getColumnsValues().get(i);

                    namesList += columnName + (i < tableColumns.getColumnsNames().size() - 1 ? "," : "");
                    valuesList += "'" + columnValue + "'" + (i < tableColumns.getColumnsNames().size() - 1 ? "," : "");
                }

                List xmlColumnNames = tableColumns.getColumnsNames();

                for (Iterator<String> it2 = mysqlColumnNames.iterator(); it2.hasNext();) {
                    boolean exist = false;
                    String mysqlColumnName = it2.next();

                    for (Iterator it3 = xmlColumnNames.iterator(); it3.hasNext();) {
                        String xmlColumnName = (String) it3.next();

                        if (mysqlColumnName.equalsIgnoreCase(xmlColumnName)) {
                            exist = true;
                        }
                    }
                    if (!exist) {
                        namesList += ",`" + mysqlColumnName + "`";
                        valuesList += ", NULL";
                    }
                }

                query = "INSERT INTO " + tableName + " ( " + namesList + " ) values ( " + valuesList + " ) ";
                ps = con.prepareStatement(query);
                ps.execute();
            }
            con.commit();
        } catch (Exception e) {
            if(con != null){
                con.rollback();
            }
            throw new IllegalArgumentException(e.getMessage() + " for statement: " + query, e);
            // result.add(e.getMessage()+"<br/> SQL statement: "+query);
        } finally {
            closeAll(null, st, null);
            closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Determines what tabs will be shown for given factsheet.
     *
     * @param idNatureObject
     *            object from database
     * @param NatureObjectType
     *            type of object (species, habitats, sites)
     * @return List<String> list of column names that exist
     */
    public List<String> getExistingTabPages(String idNatureObject, String NatureObjectType) {

        List<String> existingTabs = new ArrayList<String>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String SQL = "SELECT *";
        SQL += " FROM chm62edt_tab_page_" + NatureObjectType.toLowerCase();
        SQL += " WHERE ID_NATURE_OBJECT=" + idNatureObject;

        try {
            con = getConnection();

            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int colCnt = meta.getColumnCount();

            if (rs.next()) {
                for (int i = 1; i <= colCnt; i++) {
                    String colName = meta.getColumnName(i);
                    if (colName != null && !colName.equalsIgnoreCase("ID_NATURE_OBJECT")) {
                        boolean exists = rs.getString(colName).equalsIgnoreCase("Y");
                        if (exists) {
                            existingTabs.add(colName);
                        }
                    }
                }
            }
        } catch (Exception e) {// logger.debug(e, e);
            logger.debug(e, e);
        } finally {
            closeAll(con, ps, rs);
        }

        return existingTabs;
        // quick hack to display all tabs in factsheet
        // return false;
    }

    /**
     * Closes up the given resources, if they are not null.
     * @param con The DB connection
     * @param ps The Statement or PreparedStatement
     * @param rs The ResultSet
     */
    public static void closeAll(Connection con, Statement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean DesignationHasSites(String idDesignation, String idGeoscope) {
        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String strSQL = "SELECT COUNT(*) AS RECORD_COUNT FROM `chm62edt_sites` INNER JOIN `chm62edt_designations` " +
                "ON (`chm62edt_sites`.ID_DESIGNATION = `chm62edt_designations`.ID_DESIGNATION AND " +
                "`chm62edt_sites`.ID_GEOSCOPE = `chm62edt_designations`.ID_GEOSCOPE) " +
                "WHERE `chm62edt_sites`.ID_DESIGNATION = ?  AND `chm62edt_sites`.ID_GEOSCOPE = ?";
        try {
            con = getConnection();

            ps = con.prepareStatement(strSQL);
            ps.setString(1, idDesignation);
            ps.setString(2, idGeoscope);
            rs = ps.executeQuery();

            rs.next();
            result = rs.getInt(1) > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    public boolean EunisHabitatHasChilds(String idCode) {
        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String strSQL = "SELECT ID_HABITAT FROM chm62edt_habitat";
        strSQL = strSQL + " WHERE EUNIS_HABITAT_CODE LIKE ?";
        strSQL = strSQL + " AND LENGTH(EUNIS_HABITAT_CODE)>?";

        try {
            con = getConnection();

            ps = con.prepareStatement(strSQL);
            ps.setString(1, idCode+"%");
            ps.setLong(2, idCode.length());
            rs = ps.executeQuery();

            if (rs.next()) {
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    public boolean Annex1HabitatHasChilds(String idCode, String idCodeParent) {
        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String strSQL = "SELECT ID_HABITAT FROM chm62edt_habitat";
        strSQL = strSQL + " WHERE CODE_2000 LIKE '" + idCode + "%'";
        strSQL = strSQL + " AND CODE_2000<>'" + idCodeParent + "'";

        try {
            con = getConnection();

            ps = con.prepareStatement(strSQL);
            rs = ps.executeQuery();

            if (rs.next()) {
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Checks that a taxonomy has child taxonomies (any taxonomy's parent id is the given id code).
     * @param idCode Taxonomy id
     * @return True if there are child taxonomies.
     */
    public boolean SpeciesHasChildTaxonomies(String idCode) {
        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String strSQL = "SELECT ID_TAXONOMY FROM chm62edt_taxonomy";
        strSQL = strSQL + " WHERE ID_TAXONOMY_PARENT = ?";
        strSQL = strSQL + " AND ID_TAXONOMY<>?";

        try {
            con = getConnection();

            ps = con.prepareStatement(strSQL);
            ps.setString(1, idCode);
            ps.setString(2, idCode);
            rs = ps.executeQuery();

            if (rs.next()) {
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Checks that the given species has child species (there is any species with the taxonomy id equal with the given id code).
     * @param idCode Taxonomy id
     * @return True if at least one record is found
     */
    public boolean SpeciesHasChildSpecies(String idCode) {
        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String strSQL = "SELECT ID_SPECIES FROM chm62edt_species";
        strSQL = strSQL + " WHERE ID_TAXONOMY = ?";

        try {
            con = getConnection();

            ps = con.prepareStatement(strSQL);
            ps.setString(1, idCode);
            rs = ps.executeQuery();

            if (rs.next()) {
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Lists all the table names starting with chm62edt or dc_.
     * @return The list of table names
     */
    public List<String> getAllChm62edtTableNames() {

        List<String> ret = new ArrayList<String>();
        Connection con = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            DatabaseMetaData meta = con.getMetaData();
            rs = meta.getTables(null, null, null, new String[] {"TABLE"});

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");

                if (tableName != null && (tableName.startsWith("chm62edt") || tableName.startsWith("dc_"))) {
                    ret.add(tableName);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, null,  rs);
        }

        return ret;
    }

    /**
     * Reads the table info (column metadata).
     * @param tableName The table to return metadata for
     * @return A map contatining the column metadata keyed by column name.
     */
    public HashMap<String, ColumnDTO> getTableInfo(String tableName) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        HashMap<String, ColumnDTO> columns = new HashMap<String, ColumnDTO>();

        try {
            con = getConnection();

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData rsMeta = rs.getMetaData();

            int numberOfColumns = rsMeta.getColumnCount();

            for (int x = 1; x <= numberOfColumns; x++) {
                String columnName = rsMeta.getColumnName(x);
                int columnType = rsMeta.getColumnType(x);
                int size = rsMeta.getColumnDisplaySize(x);
                int precision = rsMeta.getPrecision(x);
                int scale = rsMeta.getScale(x);
                boolean isSigned = rsMeta.isSigned(x);
                int isNullable = rsMeta.isNullable(x);

                ColumnDTO column = new ColumnDTO();

                column.setColumnName(columnName);
                column.setColumnType(columnType);
                column.setColumnSize(size);
                column.setPrecision(precision);
                column.setScale(scale);
                column.setSigned(isSigned);
                column.setNullable(isNullable);

                columns.put(columnName.toLowerCase(), column);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, st, rs);
        }

        return columns;
    }

    public List<ColumnDTO> getTableInfoList(String tableName) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        List<ColumnDTO> columns = new ArrayList<ColumnDTO>();

        try {
            con = getConnection();

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData rsMeta = rs.getMetaData();

            int numberOfColumns = rsMeta.getColumnCount();

            for (int x = 1; x <= numberOfColumns; x++) {
                String columnName = rsMeta.getColumnName(x);
                int columnType = rsMeta.getColumnType(x);
                int size = rsMeta.getColumnDisplaySize(x);
                int precision = rsMeta.getPrecision(x);
                int scale = rsMeta.getScale(x);
                boolean isSigned = rsMeta.isSigned(x);
                int isNullable = rsMeta.isNullable(x);

                ColumnDTO column = new ColumnDTO();

                column.setColumnName(columnName);
                column.setColumnType(columnType);
                column.setColumnSize(size);
                column.setPrecision(precision);
                column.setScale(scale);
                column.setSigned(isSigned);
                column.setNullable(isNullable);

                columns.add(column);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, st, rs);
        }

        return columns;
    }

    public String getTableContentAsXML(String tableName) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        StringBuilder ret = new StringBuilder();

        String nl = "\n";

        try {
            con = getConnection();

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData rsMeta = rs.getMetaData();
            int numberOfColumns = rsMeta.getColumnCount();

            while (rs.next()) {
                ret.append("<ROW>").append(nl);
                for (int x = 1; x <= numberOfColumns; x++) {
                    String columnName = rsMeta.getColumnName(x);
                    String value = rs.getString(columnName);
                    int columnType = rsMeta.getColumnType(x);
                    int size = rsMeta.getColumnDisplaySize(x);

                    if (columnType == Types.DATE) {
                        if (size == 4) {
                            if (value != null && value.length() > 4) {
                                value = value.substring(0, 4);
                            }
                        }
                    }
                    if (value == null) {
                        value = "NULL";
                    }

                    if (!value.equalsIgnoreCase("NULL") && !value.equals("")) {
                        ret.append("<").append(columnName).append(">").append(EunisUtil.replaceTagsExport(value)).append("</")
                        .append(columnName).append(">").append(nl);
                    }
                }
                ret.append("</ROW>").append(nl);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeAll(con, st, rs);
        }

        return ret.toString();
    }

    /**
     * Execute INSERT statement
     *
     * @param message
     * @return operation status
     */
    public boolean addImportLogMessage(String message) {

        if (message == null) {
            return false;
        }

        boolean result = true;

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            message = EunisUtil.replaceTagsImport(message);
            ps =
                con.prepareStatement("INSERT INTO eunis_import_log (MESSAGE, CUR_TIMESTAMP) values ( '" + message
                        + "', CURRENT_TIMESTAMP() ) ");
            ps.execute();
        } catch (Exception e) {
            logger.debug(e, e);
            result = false;
        } finally {
            closeAll(con, ps, null);
        }
        return result;
    }

    public List<ImportLogDTO> getImportLogMessages() {

        List<ImportLogDTO> result = new ArrayList<ImportLogDTO>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            ps =
                con.prepareStatement("SELECT LOG_ID, MESSAGE, CUR_TIMESTAMP FROM eunis_import_log ORDER BY LOG_ID DESC LIMIT 100");
            rs = ps.executeQuery();

            while (rs.next()) {
                ImportLogDTO dto = new ImportLogDTO();

                dto.setId(rs.getString("LOG_ID"));
                dto.setMessage(rs.getString("MESSAGE"));
                dto.setTimestamp(rs.getString("CUR_TIMESTAMP"));
                result.add(dto);
            }

        } catch (Exception e) {
            logger.debug(e, e);
            return null;
        } finally {
            closeAll(con, ps, rs);
        }

        return result;
    }

    /**
     * Returns all URLs in the DB.
     * @return URLs from dc_index, site_attributes, designations
     */
    public List<String> getUrls() {
        List<String> ret = new ArrayList<String>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> statements = new ArrayList<String>();

        statements.add("SELECT URL FROM dc_index");
        // can also be https!
        statements.add("SELECT VALUE FROM chm62edt_site_attributes WHERE VALUE LIKE 'http%'");
        statements.add("SELECT DATA_SOURCE FROM chm62edt_designations WHERE DATA_SOURCE LIKE 'http%'");

        try {
            con = getConnection();

            for (String stmt : statements) {
                ps = con.prepareStatement(stmt);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String url = rs.getString(1);

                    if (url != null && url.length() > 0) {
                        int space = url.indexOf(" ");

                        if (space != -1) {
                            url = url.substring(0, space);
                        }

                        int br = url.indexOf("\n");

                        if (br != -1) {
                            url = url.substring(0, br);
                        }

                        ret.add(url);
                    }
                }
            }

        } catch (Exception e) {
            logger.debug(e, e);
            ret = null;
        } finally {
            closeAll(con, ps, rs);
        }

        return ret;
    }

    /**
     * Execute INSERT statement.
     * @deprecated The fields that were updated by these scripts were deleted http://taskman.eionet.europa.eu/issues/17806
     */
    public void runPostImportSitesScript(boolean cmd) {

        Connection con = null;
        PreparedStatement ps = null;
        SQLUtilities sqlc = new SQLUtilities();

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
        } catch (Exception e) {
            EunisUtil.writeLogMessage("ERROR occured while generating sites latitude/longitude values: " + e.getMessage(), cmd,
                    sqlc);
            logger.debug(e, e);
        } finally {
            closeAll(con, ps, null);
        }
    }

    /**
     * Reconstruct the TAXONOMY_TREE.
     */
    public void reconstructTaxonomyTree() {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String tree = "";
        // ID of taxonomy that is updated
        String origId = "";

        try {
            con = getConnection();

            ps = con.prepareStatement("SELECT ID_TAXONOMY, ID_TAXONOMY_PARENT, LEVEL, NAME FROM chm62edt_taxonomy LIMIT 100");
            rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("ID_TAXONOMY");
                origId = id;
                String parent = rs.getString("ID_TAXONOMY_PARENT");
                String level = rs.getString("LEVEL");
                String name = rs.getString("NAME");
                tree = id + "*" + level + "*" + name;
                do {
                    ps = con.prepareStatement(
                    "SELECT ID_TAXONOMY, ID_TAXONOMY_PARENT, LEVEL, NAME FROM chm62edt_taxonomy WHERE ID_TAXONOMY = ?");
                    ps.setString(1, parent);
                    ResultSet rs2 = ps.executeQuery();
                    while (rs2.next()) {
                        id = rs2.getString("ID_TAXONOMY");
                        parent = rs2.getString("ID_TAXONOMY_PARENT");
                        level = rs2.getString("LEVEL");
                        name = rs2.getString("NAME");
                        tree = id + "*" + level + "*" + name + "," + tree;
                    }
                    rs2.close();
                } while (!id.equals(parent));

                // Update TAXONOMY_TREE
                ps = con.prepareStatement("UPDATE chm62edt_taxonomy SET TAXONOMY_TREE = ? WHERE ID_TAXONOMY = ?");
                ps.setString(1, tree);
                ps.setString(2, origId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            closeAll(con, ps, rs);
        }
    }

}
