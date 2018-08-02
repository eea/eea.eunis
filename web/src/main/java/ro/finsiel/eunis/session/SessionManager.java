package ro.finsiel.eunis.session;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import eionet.directory.DirServiceException;
import eionet.directory.DirectoryService;
import ro.finsiel.eunis.Settings;
import ro.finsiel.eunis.WebContentManagement;
import ro.finsiel.eunis.auth.EncryptPassword;
import ro.finsiel.eunis.jrfTables.users.*;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.SQLUtilities;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;


/**
 * This is the main session manager object which keeps all the 'global' variables for the session. So if you want to
 * keep something on the session global, please use this object.
 * @author finsiel
 */
public final class SessionManager implements java.io.Serializable {

    private static Logger logger = Logger.getLogger(SessionManager.class);

    private WebContentManagement webContent = new WebContentManagement();

    private String currentLanguage = "en";
    private static HashMap<String, WebContentManagement> languages = new HashMap();
    boolean editContentMode = false;
    boolean advancedEditContentMode = false;

    /** This is the username for the current session. If user didn't logon, then this field is null */
    private String username = null;

    /** This is the password for the current session. If user didn't logon, then this field is null */
    private String password = null;

    /** Keeps the track of user login/logout. When true, an user was authorized. */
    private boolean authenticated = false;

    /** Keeps the user preferences. */
    private UserPersist userPrefs = new UserPersist();

    /** Show / HIDE EUNIS not(!) validated species to authenticated users. */
    private boolean showEUNISInvalidatedSpecies = false;

    private boolean login_RIGHT = false;
    private boolean upload_reports_RIGHT = false;
    private boolean services_RIGHT = false;
    private boolean save_search_criteria_RIGHT = false;
    private boolean content_management_RIGHT = false;
    private boolean import_export_data_RIGHT = false;
    private boolean upload_pictures_RIGHT = false;
    private boolean role_management_RIGHT = false;
    private boolean admin_ROLE = false;
    private boolean manage_users = false;

    private ThemeManager themeManager = null;

    private String explainedcriteria = "";
    private String listcriteria = "";
    private String sourcedb = "";
    private String combinedexplainedcriteria1 = "";
    private String combinedlistcriteria1 = "";
    private String combinednatureobject1 = "";
    private String combinedexplainedcriteria2 = "";
    private String combinedlistcriteria2 = "";
    private String combinednatureobject2 = "";
    private String combinedexplainedcriteria3 = "";
    private String combinedlistcriteria3 = "";
    private String combinednatureobject3 = "";
    private String combinedcombinationtype = "";

    private String cacheReportEmailAddress = "";

    private String userFullName = "";

    boolean languageDetected = false;

    static {
        // When server is initializing, load the default language.
        WebContentManagement cm = new WebContentManagement();

        cm.setLanguage("en");
        languages.put("en", cm);
    }

    /**
     * Used for Combined Search function.
     * @return Source DB.
     */
    public String getSourcedb() {
        return sourcedb;
    }

    /**
     * Used for Combined Search function.
     * @param sourcedb New Source DB.
     */
    public void setSourcedb(String sourcedb) {
        this.sourcedb = sourcedb;
    }

    /**
     * Used for Combined Search function.
     * @return First nature object from which search was started. (Possible values: "Species", "Habitats", "Sites").
     */
    public String getCombinednatureobject1() {
        return combinednatureobject1;
    }

    /**
     * Used for Combined Search function.
     * @param combinednatureobject1 New nature object value.
     */
    public void setCombinednatureobject1(String combinednatureobject1) {
        this.combinednatureobject1 = combinednatureobject1;
    }

    /**
     * Used for Combined Search function.
     * @return Human readable string explaining search criteria.
     */
    public String getCombinedexplainedcriteria2() {
        return combinedexplainedcriteria2;
    }

    /**
     * Used for Combined Search function.
     * @param combinedexplainedcriteria2 Explanation of search criteria.
     */
    public void setCombinedexplainedcriteria2(String combinedexplainedcriteria2) {
        this.combinedexplainedcriteria2 = combinedexplainedcriteria2;
    }

    /**
     * Used for Combined Search function.
     * @return List of search criteria in human readable form.
     */
    public String getCombinedlistcriteria2() {
        return combinedlistcriteria2;
    }

    /**
     * Used for Combined Search function.
     * @param combinedlistcriteria2 New list of search criteria.
     */
    public void setCombinedlistcriteria2(String combinedlistcriteria2) {
        this.combinedlistcriteria2 = combinedlistcriteria2;
    }

    /**
     * Used for Combined Search function.
     * @return Second nature object choosed for searching. (Possible values: "Species", "Habitats", "Sites").
     */
    public String getCombinednatureobject2() {
        return combinednatureobject2;
    }

    /**
     * Used for Combined Search function.
     * @param combinednatureobject2 Nature object used for searching.
     */
    public void setCombinednatureobject2(String combinednatureobject2) {
        this.combinednatureobject2 = combinednatureobject2;
    }

    /**
     * Used for Combined Search function.
     * @return Human readable string explaining search criteria.
     */
    public String getCombinedexplainedcriteria3() {
        return combinedexplainedcriteria3;
    }

    /**
     * Used for Combined Search function.
     * @param combinedexplainedcriteria3 New string explaining search criteria.
     */
    public void setCombinedexplainedcriteria3(String combinedexplainedcriteria3) {
        this.combinedexplainedcriteria3 = combinedexplainedcriteria3;
    }

    /**
     * Used for Combined Search function.
     * @return List of search criteria in human readable form.
     */
    public String getCombinedlistcriteria3() {
        return combinedlistcriteria3;
    }

    /**
     * Used for Combined Search function.
     * @param combinedlistcriteria3 New list of search criteria.
     */
    public void setCombinedlistcriteria3(String combinedlistcriteria3) {
        this.combinedlistcriteria3 = combinedlistcriteria3;
    }

    /**
     * Used for Combined Search function.
     * @return Third nature object used for searching. (Possible values: "Species", "Habitats", "Sites").
     */
    public String getCombinednatureobject3() {
        return combinednatureobject3;
    }

    /**
     * Used for Combined Search function.
     * @param combinednatureobject3 New nature object used for searching.
     */
    public void setCombinednatureobject3(String combinednatureobject3) {
        this.combinednatureobject3 = combinednatureobject3;
    }

    /**
     * Used for Combined Search function.
     * @return Human readable string explaining combination type between the three possible nature objects.
     */
    public String getCombinedcombinationtype() {
        return combinedcombinationtype;
    }

    /**
     * Used for Combined Search function.
     * @param combinedcombinationtype Type of combination.
     */
    public void setCombinedcombinationtype(String combinedcombinationtype) {
        this.combinedcombinationtype = combinedcombinationtype;
    }

    /**
     * Used for Combined Search function.
     * @return Human readable string explaining search criteria.
     */
    public String getCombinedexplainedcriteria1() {
        return combinedexplainedcriteria1;
    }

    /**
     * Used for Combined Search function.
     * @param combinedexplainedcriteria Human readable string explaining search criteria.
     */
    public void setCombinedexplainedcriteria1(String combinedexplainedcriteria) {
        this.combinedexplainedcriteria1 = combinedexplainedcriteria;
    }

    /**
     * Used for Combined Search function.
     * @return List of search criteria in human readable form.
     */
    public String getCombinedlistcriteria1() {
        return combinedlistcriteria1;
    }

    /**
     * Used for Combined Search function.
     * @param combinedlistcriteria New list of search criteria.
     */
    public void setCombinedlistcriteria1(String combinedlistcriteria) {
        this.combinedlistcriteria1 = combinedlistcriteria;
    }

    /**
     * Used for Combined Search function.
     * @return Explained search criteria in human readable form.
     */
    public String getExplainedcriteria() {
        return explainedcriteria;
    }

    /**
     * Used for Combined Search function.
     * @param explainedcriteria New explanation of criteria.
     */
    public void setExplainedcriteria(String explainedcriteria) {
        this.explainedcriteria = explainedcriteria;
    }

    /**
     * Used for Advanced Search function.
     * @return Criterias used for searching in human readable form.
     */
    public String getListcriteria() {
        return listcriteria;
    }

    /**
     * Used for Advanced Search function.
     * @param listcriteria New value for search criteria.
     */
    public void setListcriteria(String listcriteria) {
        this.listcriteria = listcriteria;
    }

    // Configure the Logging system.
    static {
        BasicConfigurator.configure();
        logger.info("Log4j system initialized.");
    }

    /**
     * Constructs an new instance of SessionManager object.
     */
    public SessionManager() {
        loadUserPreferences();
    }

    /**
     * This method is used for error / information purposes. JSP file can't have an logging system on their own (or maybe
     * will have in the future) and use now the logging features of SessionManager.<br />
     * This method logs only WARNING MESSAGES. Hierarchy is:<br />
     * <UL>
     *  <LI>INFO - most generic, informational messages</LI>
     *  <LI>WARN - Warning, possible errors etc.</LI>
     *  <LI>ERROR - Errors, Database connection errors, exceptions etc..</LI>
     *  <LI>FATAL - I wouldn't like that in my code :)</LI>
     * @param message Message to output in logs
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * This method is used for error / information purposes. JSP file can't have an logging system on their own (or maybe
     * will have in the future) and use now the logging features of SessionManager.<br />
     * This method logs only INFO MESSAGES. Hierarchy is:<br />
     * <UL>
     *  <LI>INFO - most generic, informational messages</LI>
     *  <LI>WARN - Warning, possible errors etc.</LI>
     *  <LI>ERROR - Errors, Database connection errors, exceptions etc..</LI>
     *  <LI>FATAL - I wouldn't like that in my code :)</LI>
     * @param message Message to output in logs
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * This method is used for error / information purposes. JSP file can't have an logging system on their own (or maybe
     * will have in the future) and use now the logging features of SessionManager.<br />
     * This method logs only ERROR MESSAGES. Hierarchy is:<br />
     * <UL>
     *  <LI>INFO - most generic, informational messages</LI>
     *  <LI>WARN - Warning, possible errors etc.</LI>
     *  <LI>ERROR - Errors, Database connection errors, exceptions etc..</LI>
     *  <LI>FATAL - I wouldn't like that in my code :)</LI>
     * @param message Message to output in logs
     */
    public static void error(String message) {
        logger.error(message);
    }

    /**
     * This method is used for error / information purposes. JSP file can't have an logging system on their own (or maybe
     * will have in the future) and use now the logging features of SessionManager.<br />
     * This method logs only FATAL MESSAGES. Hierarchy is:<br />
     * <UL>
     *  <LI>INFO - most generic, informational messages</LI>
     *  <LI>WARN - Warning, possible errors etc.</LI>
     *  <LI>ERROR - Errors, Database connection errors, exceptions etc..</LI>
     *  <LI>FATAL - I wouldn't like that in my code :)</LI>
     * @param message Message to output in logs
     */
    public static void fatal(String message) {
        logger.fatal(message);
    }

    /**
     * Login method. This method looks into database and searches for the username and password and checkes for a match
     * @param username Username
     * @param password Password in clear (this method will use encription to check...)
     * @param request HTTP ServletRequest used for accessing the ID of the session.
     * @return True if username/password pair is valid, false otherwise
     * Note: Also if the is logged on, his/her preferences are automatically loaded.
     */
    public boolean login(String username, String password, HttpServletRequest request) {
        logout();
        this.username = username;
        this.password = password;
        this.authenticated = true;
        loadUserPreferences();
        // log the login process to database

        Connection conn = null;
        PreparedStatement ps = null;
        // update last login date
        try {
            conn = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            UserPersist user = findUser(username);

            if (user == null) {
                ps = conn.prepareStatement(
                        "INSERT INTO eunis_users (USERNAME) VALUES ('"
                        + username + "')");
                ps.execute();
                ps.close();
                ps = conn.prepareStatement(
                        "INSERT INTO eunis_users_roles (USERNAME, ROLENAME) VALUES ('"
                        + username + "','Guest')");
                ps.execute();
                ps.close();
            }

            ps = conn.prepareStatement(
            "UPDATE eunis_users SET LOGIN_DATE=? WHERE USERNAME=?");
            ps.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
            ps.setString(2, username);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeAll(conn, ps, null);
        }

        if (null != request) {
            String sessionID = request.getSession().getId();
            String ipAddr = request.getRemoteAddr();
            long longTime = new Date().getTime();

            try {
                conn = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                ps = conn.prepareStatement(
                "INSERT INTO eunis_session_log (ID_SESSION, USERNAME, START, END, IP_ADDRESS) VALUES (?, ?, ?, ?, ?)");

                ps.setString(1, sessionID);
                ps.setString(2, username);
                ps.setTimestamp(3, new java.sql.Timestamp(longTime));
                ps.setTimestamp(4, new java.sql.Timestamp(longTime));
                ps.setString(5, ipAddr);
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SQLUtilities.closeAll(conn, ps, null);
            }
        } else {
            System.out.println(
            "SessionManager::login(...) - request object was null.");
        }
        return authenticated;
    }

    /** Performs a logout of the user, so the username/password/authenticated fields becomes invalid. */
    public void logout() {
        this.username = null;
        this.password = null;
        this.authenticated = false;
        this.userPrefs = new UserPersist();
        this.showEUNISInvalidatedSpecies = false;
        this.login_RIGHT = false;
        this.upload_reports_RIGHT = false;
        this.services_RIGHT = false;
        this.save_search_criteria_RIGHT = false;
        this.content_management_RIGHT = false;
        this.import_export_data_RIGHT = false;
        this.upload_pictures_RIGHT = false;
        this.manage_users = false;
        this.role_management_RIGHT = false;
        this.admin_ROLE = false;
        setEditContentMode(false);
        setAdvancedEditContentMode(false);
    }

    /**
     * Method to find an user in database after its username (PK).
     * @param username Username
     * @return null if user was not found or username is null. UserPersist object associated with that user.
     */
    private UserPersist findUser(String username) {
        if (null == username) {
            return null;
        }
        UserPersist user = null;
        List list = new Vector();

        try {
            list = new UserDomain().findWhere("username='" + username + "'");
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            user = (UserPersist) list.get(0);
        }
        return user;
    }

    /**
     * This method is used to save the user preferences into database (only for authenticated users). Prior to call this
     * method, username/password pairs must be valid (current session contains an user that was authenticated)
     */
    public void saveUserPreferences() {
        if (isAuthenticated()) {
            // First load preferences for that user from database
            // Save into database efectively
            try {
                if (null != userPrefs) {
                    new UserDomain().save(userPrefs);
                }
            } catch (Exception ex) {
                System.err.println(
                "Could not save into database user preferences:");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Load user preferences from database. Prior to call this method, username/password pairs must be valid (current
     * session contains an user that was authenticated)
     */
    public void loadUserPreferences() {
        UserPersist user = findUser(username);
        userFullName = username;

        if (null == user) {
            authenticated = false;
            username = null;
            password = null;
            this.userPrefs = new UserPersist();
            // If user is normal (not authenticated) default preferences.
            userPrefs.setThemeIndex(ThemeManager.THEME_SKY_BLUE);
        } else {
            this.userPrefs = user;
            authenticated = true;
            username = user.getUsername();
            if (!Utilities.isEmptyString(user.getFirstName()) && !Utilities.isEmptyString(user.getLastName())){
                userFullName = user.getFirstName() + " " + user.getLastName();
            }
            // showEUNISInvalidatedSpecies = Utilities.checkedStringToBoolean(user.getShowInvalidatedSpecies().toString(), false);
            themeManager = new ThemeManager(this);
            themeManager.switchTheme(user.getThemeIndex());

            Vector userRights = findUserRights(username);

            if (userRights != null && userRights.size() > 0) {
                for (int i = 0; i < userRights.size(); i++) {
                    // System.out.println("rigth="+userRights.get(i));
                    if (((String) userRights.get(i)).equalsIgnoreCase("login")) {
                        login_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("upload_reports")) {
                        upload_reports_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("services")) {
                        services_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("save_search_criteria")) {
//                        save_search_criteria_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("content_management")) {
                        content_management_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("import/export_data")) {
                        import_export_data_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("user_management")) {
                        manage_users = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("role_management")) {
                        role_management_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("upload_pictures")) {
                        upload_pictures_RIGHT = true;
                    }
                    if (((String) userRights.get(i)).equalsIgnoreCase("show_novalidated_species")) {
//                        showEUNISInvalidatedSpecies = true;
                    }
                }
            }

            Vector userRoles = findUserRoles(username);

            if (userRoles != null && userRoles.size() > 0) {
                for (int i = 0; i < userRoles.size(); i++) {
                    // System.out.println("rigth="+userRoles.get(i));
                    if (((String) userRoles.get(i)).equalsIgnoreCase(
                    "administrator")) {
                        admin_ROLE = true;
                    }
                }
            }

        }
    }

    /**
     * Retrieve the rights for an user.
     * @param username Username.
     * @return Vector of UsersRightsPersist objects, one for each right.
     */
    public Vector findUserRights(String username) {
        if (null == username) {
            return new Vector();
        }
        Vector<String> userRights = new Vector<String>();

        try {
            List list = new UsersRightsDomain().findWhere(
                    "A.username='" + username
                    + "' GROUP BY A.USERNAME,C.RIGHTNAME");

            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    userRights.add(
                            ((UsersRightsPersist) list.get(i)).getRightName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRights;
    }

    /**
     * Retrieve the role for an user.
     * @param username Username.
     * @return Vector of UsersRolesPersist objects, one for each right.
     */
    public Vector findUserRoles(String username) {
        if (null == username) {
            return new Vector();
        }
        Vector<String> userRoles = new Vector<String>();

        try {
            List list = new UsersRolesDomain().findWhere(
                    "A.username='" + username
                    + "' GROUP BY A.USERNAME,E.ROLENAME");

            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    userRoles.add(
                            ((UsersRolesPersist) list.get(i)).getRoleName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRoles;
    }

    /**
     * Provides access to the current theme.
     * @return Current theme used in pages
     */
    public ThemeManager getThemeManager() {
        if (null == themeManager) {
            loadUserPreferences();
        }
        if (null == themeManager) {
            themeManager = new ThemeManager(this);
            themeManager.switchTheme(ThemeManager.THEME_SKY_BLUE);
        }
        return themeManager;
    }

    /**
     * Changes the current theme of the user.
     * @param index Index of theme (defined in ThemeManager).
     */
    public void setThemeIndex(int index) {
        if (null != themeManager) {
            themeManager.switchTheme(index);
        }
    }

    /**
     * Getter for username property.
     * @return The value of username. If user didn't logon, this method will return null.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username property.
     * @param username New value for username. If null, then there is no user currently logged.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for password property.
     * @return The value of password. If user didn't logon, this method will return null.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for passowrd property.
     * @param password New value for the passowrd. If null, then there is no user currently logged.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for authenticated property.
     * @return True if user is logged on, false otherwise.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Getter for user preferences object (userPrefs).
     * @return User preferences.
     */
    public UserPersist getUserPrefs() {
        return userPrefs;
    }

    /**
     * Getter for the showEUINSInvalidatedSpecies property.
     * @return If user should see or not the species not validated by EUNIS.
     */
    public boolean getShowEUNISInvalidatedSpecies() {
        return showEUNISInvalidatedSpecies;
    }

    /**
     * Provides access to the content management system which manages HTML pages stored in database
     * (WEB_CONTENT_MANAGEMENT table).
     * @return content management object.
     */
    public WebContentManagement getWebContent() {
        return getWebContent(currentLanguage);
    }

    /**
     * Return the current web content 'dictionary' for specified language.
     * @param language Language code
     * @return Content for that language, if not loaded application will try to
     * load the specified language. If code not found in eunis_web_content table
     * result will be null.
     */
    public WebContentManagement getWebContent(String language) {
        WebContentManagement cm = new WebContentManagement();

        try {
            if (languages.keySet().contains(language)) {
                cm = languages.get(language);
                cm.setEditMode(editContentMode);
                cm.setAdvancedEditMode(advancedEditContentMode);
            } else {
                cm = new WebContentManagement();
                cm.setLanguage(language);
                cm.setEditMode(false);
                languages.put(language, cm);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cm;
    }

    /**
     * Getter for login right.
     * @return Trues if user has the right to login within application.
     */
    public boolean isLogin_RIGHT() {
        return login_RIGHT;
    }

    /**
     * Getter for upload reports right.
     * @return True if user has the right to upload reports to server (Related Reports).
     */
    public boolean isUpload_reports_RIGHT() {
        return upload_reports_RIGHT;
    }

    /**
     * Getter for services right.
     * @return True if user has the right to access the services page.
     */
    public boolean isServices_RIGHT() {
        return services_RIGHT;
    }

    /**
     * Getter for save search criteria right.
     * @return True if user has the right to save searched criteria.
     */
    public boolean isSave_search_criteria_RIGHT() {
        return save_search_criteria_RIGHT;
    }

    /**
     * Getter for content management right.
     * @return True is user has the right to edit content of web pages.
     */
    public boolean isContent_management_RIGHT() {
        return content_management_RIGHT;
    }

    /**
     * Getter for Import/Export Data.
     * @return True is user has the right to import or export data.
     */
    public boolean isImportExportData_RIGHT() {
        return import_export_data_RIGHT;
    }

    /**
     * Getter for user management right.
     * @return True if user has the right to manage users.
     */
    public boolean isUser_management_RIGHT() {
        return manage_users;
    }

    /**
     * Getter for upload pictures right.
     * @return True if user has the right to upload pictures (for factsheets).
     */
    public boolean isUpload_pictures_RIGHT() {
        return upload_pictures_RIGHT;
    }

    /**
     * Getter for role management right.
     * @return True if user has the right to manage user's roles.
     */
    public boolean isRole_management_RIGHT() {
        return role_management_RIGHT;
    }

    /**
     * Getter for mysql administration right.
     * @return True if user has the right to execute queries/updates to database (access the application which manages db server).
     */
    public boolean isAdmin() {
        return admin_ROLE;
    }


    /**
     * Current application language specified by the user
     * @return currentLanguage
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sets application current language for an user
     * @param currentLanguage New code for language.
     */
    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    /**
     * Getter for editContentMode. If application is in edit content mode
     * @return editContentMode
     */
    public boolean isEditContentMode() {
        return editContentMode;
    }

    /**
     * Setter for editContentMode property. Activate inline editor
     * @param editContentMode New value
     */
    public void setEditContentMode(boolean editContentMode) {
        this.editContentMode = editContentMode;
    }

    /**
     * Getter for advancedEditContentMode property. If advanced inline editor
     * is activated
     * @return advancedEditContentMode
     */
    public boolean isAdvancedEditContentMode() {
        return advancedEditContentMode;
    }

    /**
     * Setter for advancedEditContentMode property. Activate advanced inline
     * editor
     * @param advancedEditContentMode
     */
    public void setAdvancedEditContentMode(boolean advancedEditContentMode) {
        this.advancedEditContentMode = advancedEditContentMode;
    }

    /**
     * Cache the e-mail address property. This e-mail is where generated reports
     * are sent.
     * @return e-mail address entered by user, where reports will be sent.
     */
    public String getCacheReportEmailAddress() {
        return cacheReportEmailAddress;
    }

    /**
     * Setter for e-mail address where reports are sent
     * @param cacheReportEmailAddress New value
     */
    public void setCacheReportEmailAddress(String cacheReportEmailAddress) {
        this.cacheReportEmailAddress = cacheReportEmailAddress;
    }

    /**
     * Specifies if language was detected from browser accept-language header
     * If detected once stop subsequent detections
     * @return languageDetected status
     */
    public boolean isLanguageDetected() {
        return languageDetected;
    }

    /**
     * Setter for languageDetected property
     * @param languageDetected
     */
    public void setLanguageDetected(boolean languageDetected) {
        this.languageDetected = languageDetected;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
