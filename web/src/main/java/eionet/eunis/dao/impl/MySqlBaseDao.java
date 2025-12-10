package eionet.eunis.dao.impl;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import ro.finsiel.eunis.utilities.ResultSetBaseReader;

public abstract class MySqlBaseDao {

    private static final Logger logger = Logger.getLogger(MySqlBaseDao.class);

    /**
     * Returns a new database connection.
     */
    public static synchronized Connection getConnection() throws SQLException {

        Connection conn = null;

        try {
            conn = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
        } catch (Throwable e) {
            logger.error(e, e);
        }
        return conn;
    }

    /**
     * Cleanup resources ignoring SQL Exceptions.
     * @param conn  DB Connection
     * @param pstmt The prepared statement
     * @param rs The result set
     */
    public static void closeAllResources(Connection conn, Statement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ignored) {
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException ignored) {
        }
        try {
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
            }
        } catch (SQLException ignored) {
        }
    }

    public static void closeConnection(Connection conn) {
        closeAllResources(conn, null, null);
    }

    public static void commit(Connection conn) {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            logger.debug(e, e);
        }
    }

    public static void rollback(Connection conn) {
        try {
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            logger.debug(e, e); 
        }
    }

    protected java.sql.Date sqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    /**
     *
     * @param parameterizedSQL
     * @param values
     * @param conn
     * @return statement with parameters filled in.
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
     *
     * @param parameterizedSQL
     * @param values
     * @param rsReader
     * @throws SQLException
     */
    public <T> void executeQuery(String parameterizedSQL, List<Object> values, ResultSetBaseReader<T> rsReader)
        throws SQLException {
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
        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, pstmt, rs);
        }
    }

    /**
     * Executes a SELECT sql and returns the first value.
     *
     * @param SQL SQL.
     * @return First column.
     */
    public String ExecuteSQL(String SQL) {
        if (SQL == null || SQL.trim().length() <= 0) {
            return "";
        }

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
            return "";
        } finally {
            closeAllResources(con, ps, rs);
        }
        return result;
    }

    /**
     * Executes parameterized sql query and return list of results.
     * Note! only first column in query is returned.
     *
     * @param sql - sql string
     * @param params - sql parameters
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
                resultList.add((T) result.getObject(1));
            }
            return resultList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAllResources(connection, prepared, result);
        }
    }
}
