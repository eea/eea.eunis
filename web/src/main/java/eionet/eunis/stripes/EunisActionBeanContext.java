package eionet.eunis.stripes;


import java.util.Properties;

import net.sourceforge.stripes.action.ActionBeanContext;

import org.apache.log4j.Logger;

import ro.finsiel.eunis.session.SessionManager;
import ro.finsiel.eunis.utilities.SQLUtilities;


/**
 * Eunis application context.
 *
 * @author Aleksandr Ivanov
 * <a href="mailto:aleksandr.ivanov@tietoenator.com">contact</a>
 */
public class EunisActionBeanContext extends ActionBeanContext {

    private static SQLUtilities sqlUtil;
    /** Storage of values from eunis.properties file. */
    private static Properties eunisProperties;
    private int severity;
    private static final Logger logger = Logger.getLogger(EunisActionBeanContext.class);

    /**
     * get application property from eunis.properties file.
     *
     * @param key key to fetch.
     * @return application property value
     */
    public synchronized String getApplicationProperty(String key) {
        if (eunisProperties == null) {
            try {
                eunisProperties = new Properties();
                eunisProperties.load(getClass().getClassLoader().getResourceAsStream("eunis.properties"));
            } catch (Exception fatal) {
                logger.error(fatal);
                throw new RuntimeException(fatal);
            }
        }
        return eunisProperties.getProperty(key);
    }

    /**
     * Gets an object from session.
     *
     * @param key
     * @return object
     */
    public Object getFromSession(String key) {
        return getRequest().getSession().getAttribute(key);
    }

    /**
     * Add an object to session.
     *
     * @param key
     * @param value
     */
    public void addToSession(String key, Object value) {
        getRequest().getSession().setAttribute(key, value);
    }

    /**
     * Removes object from session.
     *
     * @param key
     */
    public void removeFromSession(String key) {
        getRequest().getSession().removeAttribute(key);
    }

    /**
     * Gets application init parameter.
     *
     * @param key
     * @return application init parameter
     */
    public String getInitParameter(String key) {
        return getServletContext().getInitParameter(key);
    }

    /**
     * @return jdbc user
     */
    public String getJdbcUser() {
        return getInitParameter("JDBC_USR");
    }

    /**
     * @return jdbc password
     */
    public String getJdbcPassword() {
        return getInitParameter("JDBC_PWD");
    }

    /**
     * @return jdbc url
     */
    public String getJdbcUrl() {
        return getInitParameter("JDBC_URL");
    }

    /**
     * @return jdbc driver
     */
    public String getJdbcDriver() {
        return getInitParameter("JDBC_DRV");
    }

    /**
     * @return session manager
     */
    public SessionManager getSessionManager() {
        SessionManager result = (SessionManager) getFromSession("SessionManager");

        if (result == null) {
            result = new SessionManager();
            addToSession("SessionManager", result);
        }
        return result;
    }

    /**
     * @return sql utils
     */
    public synchronized SQLUtilities getSqlUtilities() {
        if (sqlUtil == null) {
            sqlUtil = new SQLUtilities();
            sqlUtil.Init();
        }
        return sqlUtil;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDomainName() {
        return getInitParameter("DOMAIN_NAME");
    }

    public String getDistributionCDDALayer() {
        return getApplicationProperty("DIST_CDDA_LAYER");
    }

    public String getDistributionBioRegionsLayer() {
        return getApplicationProperty("DIST_BIO_REGIONS_LAYER");
    }

    public String getDistributionRiverBasinLayer() {
        return getApplicationProperty("DIST_RIVER_BASIN_LAYER");
    }

    public String getDistributionSpeciesLayer() {
        return getApplicationProperty("DIST_SPECIES_LAYER");
    }

    public String getDistributionHabitatsLayer() {
        return getApplicationProperty("DIST_HABITATS_LAYER");
    }

    public String getDistributionHabitatsInitDistLayer() {
        return getApplicationProperty("DIST_HABITATS_INIT_DIST_LAYER");
    }

    public String getDistributionHabitatsInitRangeLayer() {
        return getApplicationProperty("DIST_HABITATS_INIT_RANGE_LAYER");
    }

    public String getDistributionHabitatsN2000Layer() {
        return getApplicationProperty("DIST_HABITATS_N2000_LAYER");
    }

    public String getDistributionHabitatsN2000QueryLayer() {
        return getApplicationProperty("DIST_HABITATS_N2000_QUERY_LAYER");
    }

    public String getDistributionSpeciesN2000Layer() {
        return getApplicationProperty("DIST_SPECIES_N2000_LAYER");
    }

    public String getDistributionSpeciesN2000QueryLayer() {
        return getApplicationProperty("DIST_SPECIES_N2000_QUERY_LAYER");
    }

    public String getDistributionArcgisScript(){
        return getApplicationProperty("DIST_ARCGIS_SCRIPT");
    }

    public String getDistributionArcgisCSS(){
        return getApplicationProperty("DIST_ARCGIS_CSS");
    }

    public String getNatura2000MapId(){ return getApplicationProperty("NATURA2000_MAP_ID"); }

}
