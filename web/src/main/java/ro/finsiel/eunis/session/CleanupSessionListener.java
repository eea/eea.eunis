package ro.finsiel.eunis.session;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;

import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.Settings;
import ro.finsiel.eunis.search.Utilities;
import eionet.eunis.util.Constants;
import ro.finsiel.eunis.utilities.SQLUtilities;


/**
 * This class is used to clean up the files created in temporary directoy,
 * during a session's life. These files are the PDF, CSV reports
 * @author  finsiel
 */
public class CleanupSessionListener implements HttpSessionListener {

    /** Creates a new instance of CleanupSessionListener. */
    public CleanupSessionListener() {}

    private static final Logger logger = Logger.getLogger(CleanupSessionListener.class);

    /**
     * Method called by the container when the session is created.
     * @param httpSessionEvent Event with information about the session
     */
    public void sessionCreated(javax.servlet.http.HttpSessionEvent httpSessionEvent) {}

    /**
     * Method called by container when the session is expired. It contains the cleanup procedure
     * for the session's temporary files.
     * @param httpSessionEvent Event with information about the session
     */
    public void sessionDestroyed(javax.servlet.http.HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        String TEMP_PATH = session.getServletContext().getInitParameter(Constants.APP_HOME_INIT_PARAM);

        TEMP_PATH += Settings.getSetting("TEMP_DIR");
        String sessionID = session.getId();

        File tempDir = new File(TEMP_PATH);
        File[] files = tempDir.listFiles();

        if (null != files) {
            // Test each file from array if contains the session id in its name
            // and delete it does and if it's not an directory
            for (int i = 0; i < files.length; i++) {
                File aFile = files[i];

                if ((aFile.getName().lastIndexOf(sessionID) >= 0)
                        && aFile.isFile()) {
                    aFile.delete();
                }
            }
        }
        tempDir = new File(TEMP_PATH + "/no-cache/");
        files = tempDir.listFiles();
        if (null != files) {
            // Test each file from array if contains the session id in its name
            // and delete it does and if it's not an directory
            for (int i = 0; i < files.length; i++) {
                File aFile = files[i];

                if ((aFile.getName().lastIndexOf(sessionID) >= 0)
                        && aFile.isFile()) {
                    aFile.delete();
                }
            }
        }

        // Clear the tables used for advanced and combined search...
        // System.out.println("Clearing temporary tables...");

        Utilities.ClearSessionData(httpSessionEvent.getSession().getId());

        long longTime = new Date().getTime();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            // System.out.println("Updating eunis_session_log table.");
            ps = conn.prepareStatement(
                    "UPDATE eunis_session_log SET START=START, END=? WHERE ID_SESSION=?");

            // Check MySQL manual (START=START because MySQL updates *only* first occurring timestamp *if* the value differs)
            ps.setTimestamp(1, new java.sql.Timestamp(longTime));
            ps.setString(2, sessionID);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(conn, ps,  null);
        }
    }
}
