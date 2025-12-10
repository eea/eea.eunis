package ro.finsiel.eunis.search.advanced;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.sql.*;


/**
 * Class used for combined search.
 * @author finsiel
 */
public class CombinedSearch {
    private int SQL_LIMIT = 1000;

    private static final Logger logger = Logger.getLogger(CombinedSearch.class);

    /**
     * Ctor.
     */
    public CombinedSearch() {
    }

    /**
     * Initializations.
     * @deprecated
     */
    public void Init() {
    }

    /**
     * Limit the results searched.
     * @param SQLLimit Limit.
     */
    public void SetSQLLimit(int SQLLimit) {
        SQL_LIMIT = SQLLimit;
    }

    /**
     * Execute an sql.
     * @param SQL SQL.
     * @param Delimiter Delimiter (LIMIT).
     * @return Criteria.
     * @throws ClassNotFoundException JDBC.
     * @throws SQLException JDBC.
     */
    public String ExecuteSQL(String SQL, String Delimiter) throws ClassNotFoundException, SQLException {
        String result = "";
        int resCount = 0;
        StringBuffer resultbuf = new StringBuffer();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (SQL.length() > 0) {

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

                while (rs.next() && rs.getRow() <= SQL_LIMIT) {
                    // result+=Delimiter+rs.getString(1)+Delimiter;
                    // result+=",";
                    resultbuf.append(Delimiter).append(rs.getString(1)).append(Delimiter);
                    resultbuf.append(",");
                }

                result = resultbuf.toString();

                if (result.length() > 0) {
                    if (result.substring(result.length() - 1).equalsIgnoreCase(",")) {
                        result = result.substring(0, result.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
            return "";
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }
        // System.out.println("Result: "+result);
        return result;
    }

}
