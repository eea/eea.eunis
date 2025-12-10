package ro.finsiel.eunis.search.users;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.jrfTables.users.*;
import ro.finsiel.eunis.jrfTables.users.UserDomain;
import ro.finsiel.eunis.jrfTables.users.UserPersist;
import ro.finsiel.eunis.session.ThemeManager;
import ro.finsiel.eunis.utilities.SQLUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.sql.*;


/**
 * Utility class for user management system.
 * @author finsiel
 */
public class UsersUtility {

    private static final Logger logger = Logger.getLogger(UsersUtility.class);

    /**
     * Retrieve roles for an user.
     * @param username username.
     * @return Vector of Strings with role names.
     */
    public static Vector getUsersRoles(String username) {
        if (username == null) {
            return new Vector();
        }
        Vector result = new Vector();

        try {
            List roles = new UsersRolesDomain().findWhere("A.USERNAME='" + username + "'");

            if (roles != null && roles.size() > 0) {
                for (int i = 0; i < roles.size(); i++) {
                    result.add(((UsersRolesPersist) roles.get(i)).getRoleName());
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Check if an String is found within an vector of Strings.
     * @param v Vector.
     * @param obj Object to found.
     * @return true if exists.
     */
    static public boolean ObjectIsInVector(Vector v, String obj) {
        if (v == null || obj == null) {
            return false;
        }
        boolean is = false;

        try {
            if (v != null && v.size() > 0 && obj != null) {
                for (int i = 0; i < v.size(); i++) {
                    if (((String) v.get(i)).equalsIgnoreCase(obj)) {
                        is = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return is;
    }

    /**
     * Return rights for an role as HTML compliant string (displayed within tooltip).
     * @param rolename rolanames.
     * @return String with HTML content.
     */
    public static String getRolesRightsAsString(String rolename) {
        if (rolename == null) {
            return "";
        }
        String st = "";

        try {
            List rights = new RolesRightsDomain().findWhere("A.ROLENAME = '" + rolename + "'  GROUP BY C.RIGHTNAME");

            if (rights != null && rights.size() > 0) {
                st = "&lt;UL&gt;";
                for (int i = 0; i < rights.size(); i++) {
                    st += "&lt;LI&gt;Right " + (i + 1) + " : "
                            + getNameNice(((RolesRightsPersist) rights.get(i)).getRightDescription());
                }
                st += "&lt;/UL&gt;";
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return st;
    }

    /**
     * Delete an user.
     *
     * @param username username.
     * @return true if succeed.
     */
    public static boolean deleteUser(String username) {
        boolean succes = false;
        String SQL = "";
        String SQL1 = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con1 = null;
        PreparedStatement ps1 = null;
        Statement st = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            SQL = "DELETE FROM eunis_users WHERE USERNAME='" + username + "'";

            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();
            SQL = "DELETE FROM eunis_users_roles WHERE USERNAME='" + username + "'";

            ps1 = con1.prepareStatement(SQL);
            ps1.execute();
            ps1.close();
            SQL = "SELECT * FROM eunis_save_advanced_search WHERE USERNAME='" + username + "'";
            st = con.createStatement();
            rs = st.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    SQL1 = "DELETE FROM eunis_save_advanced_search_criteria WHERE CRITERIA_NAME='" + rs.getString("CRITERIA_NAME")
                            + "' " + " AND NATURE_OBJECT = '" + rs.getString("NATURE_OBJECT") + "'" + " AND ID_NODE = '"
                            + rs.getString("ID_NODE") + "'";
                    ps1 = con1.prepareStatement(SQL1);
                    ps1.execute();
                    ps1.close();
                }
            }
            st.close();
            SQL = "DELETE FROM eunis_save_advanced_search WHERE USERNAME='" + username + "'";
            ps1 = con1.prepareStatement(SQL);
            ps1.execute();
            ps1.close();
            SQL = "SELECT * FROM eunis_group_search WHERE USERNAME='" + username + "'";
            st = con.createStatement();
            rs = st.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    SQL1 = "DELETE FROM eunis_group_search_criteria WHERE CRITERIA_NAME='" + rs.getString("CRITERIA_NAME") + "'";
                    ps1 = con1.prepareStatement(SQL1);
                    ps1.execute();
                    ps1.close();
                }
            }
            st.close();
            SQL = "DELETE FROM eunis_group_search WHERE USERNAME='" + username + "'";
            ps1 = con1.prepareStatement(SQL);
            ps1.execute();
            ps1.close();
            con1.close();
            con.close();
            rs.close();
            succes = true;
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return succes;
    }

    /**
     * Delete an right.
     *
     * @param rightname Name of the right.
     * @return true if succeed.
     */
    public static boolean deleteRights(String rightname) {
        boolean succes = false;
        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;
        Connection con1 = null;
        PreparedStatement ps1 = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            SQL = "DELETE FROM eunis_rights WHERE RIGHTNAME='" + rightname + "'";
            ps = con.prepareStatement(SQL);
            ps.execute();

            ps.close();
            con.close();
            SQL = "DELETE FROM eunis_roles_rights WHERE RIGHTNAME='" + rightname + "'";
            ps1 = con1.prepareStatement(SQL);
            ps1.execute();
            ps1.close();
            con1.close();
            succes = true;
        } catch (Exception e) {
            logger.debug(e, e);
        }
        if (succes && !existRightName(rightname) && !existRightNameInRolesRights(rightname)) {
            succes = true;
        } else {
            succes = false;
        }
        return succes;
    }

    /**
     * Check if an username exists within database.
     * @param username username.
     * @return true if exists.
     */
    public static boolean existUserName(String username) {
        if (username == null) {
            return false;
        }
        boolean is = false;

        try {
            List users = new UserDomain().findWhere("USERNAME='" + username + "'");

            if (users != null && users.size() > 0) {
                is = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }

        return is;
    }

    /**
     * Check if username exists in users roles table (eunis_users_roles).
     * @param username username.
     * @return true if username exists in that table.
     */
    public static boolean existUserNameInUsersRoles(String username) {
        if (username == null) {
            return false;
        }
        boolean is = false;

        try {
            List users = new UsersRolesDomain().findWhere("A.USERNAME='" + username + "'");

            if (users != null && users.size() > 0) {
                is = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return is;
    }

    /**
     * Check if an righ exists in eunis_roles_rights table.
     * @param rightname right.
     * @return true if exists.
     */
    public static boolean existRightNameInRolesRights(String rightname) {
        if (rightname == null) {
            return false;
        }
        boolean is = false;

        try {
            List rights = new RolesRightsSimpleDomain().findWhere("RIGHTNAME='" + rightname + "'");

            if (rights != null && rights.size() > 0) {
                is = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return is;
    }

    /**
     * Check if an role exists in users roles table (eunis_users_roles).
     * @param roleName role name.
     * @return true if exists.
     */
    public static boolean existRoleNameInUsersRoles(String roleName) {
        if (roleName == null) {
            return false;
        }
        boolean is = false;

        try {
            List roles = new UsersRolesDomain().findWhere("A.ROLENAME='" + roleName + "'");

            if (roles != null && roles.size() > 0) {
                is = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return is;
    }

    /**
     * Check if an role exists in role's rights table (eunis_roles_rights).
     * @param roleName role name.
     * @return true if exists.
     */
    public static boolean existRoleNameInRolesRights(String roleName) {
        if (roleName == null) {
            return false;
        }
        boolean is = false;

        try {
            List roles = new RolesRightsSimpleDomain().findWhere("ROLENAME='" + roleName + "'");

            if (roles != null && roles.size() > 0) {
                is = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }

        return is;
    }

    /**
     * Extract roles parameters from the request.
     * @param request
     * @return Vector of roles
     */
    public static Vector extractRolesParams(HttpServletRequest request) {
        Vector result = new Vector();

        if (null == request) {
            return result;
        }
        Enumeration en = request.getParameterNames();

        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();

            if (-1 != paramName.lastIndexOf("rolexxx")) {
                result.addElement(paramName);
            }
        }
        return result;
    }

    /**
     * Extract roles names from vector with roles parameters from the request.
     * @param request
     * @return Vector with roles names
     */
    public static Vector extractRoles(HttpServletRequest request) {
        Vector result = new Vector();
        Vector param = extractRolesParams(request);

        if (param != null) {
            for (int i = 0; i < param.size(); i++) {
                result.add(((String) param.get(i)).substring(8));
            }
        }

        return result;
    }

    /**
     * Extract rights parameters from the request.
     * @param request
     * @return Vector of rights
     */
    public static Vector extractRightsParams(HttpServletRequest request) {
        Vector result = new Vector();

        if (null == request) {
            return result;
        }
        Enumeration en = request.getParameterNames();

        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();

            if (-1 != paramName.lastIndexOf("rightxxx")) {
                result.addElement(paramName);
            }
        }
        return result;
    }

    /**
     * Extract rights names from vector with rights parameters from the request.
     * @param request
     * @return Vector with rights names
     */
    public static Vector extractRights(HttpServletRequest request) {
        Vector result = new Vector();
        Vector param = extractRightsParams(request);

        if (param != null) {
            for (int i = 0; i < param.size(); i++) {
                result.add(((String) param.get(i)).substring(9));
            }
        }

        return result;
    }

    /**
     * Extract rights parameters from the request.
     * @param request
     * @return Vector of rights
     */

    public static Vector extractRolesRightsParams(HttpServletRequest request) {
        Vector result = new Vector();

        if (null == request) {
            return result;
        }
        Enumeration en = request.getParameterNames();

        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();

            if (-1 != paramName.lastIndexOf("_rightxxx_")) {
                result.addElement(paramName);
            }
        }
        return result;
    }

    /**
     * Extract rights names from vector with rights parameters from the request.
     * @param roleName Name of the role for which rights to be retrieved.
     * @param request HTTP Request parameter, used to extract data
     * @return Vector with rights names
     */
    public static Vector extractRolesRights(String roleName, HttpServletRequest request) {
        Vector result = new Vector();
        Vector param = extractRolesRightsParams(request);

        if (param != null) {
            for (int i = 0; i < param.size(); i++) {
                if (((String) param.get(i)).indexOf(roleName) == 0) {
                    int index = ((String) param.get(i)).lastIndexOf("_rightxxx_");

                    if (index != -1) {
                        result.add(((String) param.get(i)).substring(index + 10));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Edit a user.
     *
     * @param manager who made this change
     * @param username user name
     * @param firstName user first name
     * @param lastName user last name
     * @param mail user mail
     * @param loginDate Login date.
     * @param newUserName  new user name
     * @param request request
     * @return true if operation was made with success
     */
    public static boolean editUser(String manager,
                                   String username,
                                   String firstName,
                                   String lastName,
                                   String mail,
                                   String loginDate,
                                   String newUserName,
                                   HttpServletRequest request) {
        boolean result = false;
        String updateSQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            // System.out.println("2----------loginDate="+loginDate+"+");
            updateSQL = "";
            updateSQL += " UPDATE eunis_users SET";
            updateSQL += " USERNAME=?,";
            updateSQL += " FIRST_NAME=?,";
            updateSQL += " LAST_NAME=?,";
            updateSQL += " EMAIL=?,";
            updateSQL += " LOGIN_DATE=str_to_date(?,'%d %b %Y %H:%i:%s')";
            updateSQL += " WHERE USERNAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, newUserName);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, mail);
            ps.setString(5, (loginDate == null ? null : loginDate.trim()));
            ps.setString(6, username);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // UPDATE USERS_ROLES
            updateSQL = "";
            updateSQL += " UPDATE eunis_users_roles SET";
            updateSQL += " USERNAME=?";
            updateSQL += " WHERE USERNAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, newUserName);
            ps.setString(2, username);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // UPDATE eunis_save_advanced_search
            updateSQL = "";
            updateSQL += " UPDATE eunis_save_advanced_search SET";
            updateSQL += " USERNAME=?";
            updateSQL += " WHERE USERNAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, newUserName);
            ps.setString(2, username);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // UPDATE eunis_group_search
            updateSQL = "";
            updateSQL += " UPDATE eunis_group_search SET";
            updateSQL += " USERNAME=?";
            updateSQL += " WHERE USERNAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, newUserName);
            ps.setString(2, username);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            Vector newRoles = extractRoles(request);
            Vector oldRoles = getUsersRoles(newUserName);

            if (newRoles != null && oldRoles != null && oldRoles.size() > 0) {
                for (int i = 0; i < oldRoles.size(); i++) {
                    if (!ObjectIsInVector(newRoles, (String) oldRoles.get(i))) {
                        updateSQL = "DELETE FROM eunis_users_roles WHERE USERNAME='" + newUserName + "' AND ROLENAME='"
                                + (String) oldRoles.get(i) + "'";
                        ps = con.prepareStatement(updateSQL);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (newRoles != null && oldRoles != null && newRoles.size() > 0) {
                for (int i = 0; i < newRoles.size(); i++) {
                    if (!ObjectIsInVector(oldRoles, (String) newRoles.get(i))) {
                        updateSQL = "INSERT INTO eunis_users_roles(USERNAME,ROLENAME) VALUES(";
                        updateSQL += "'" + newUserName + "',";
                        updateSQL += "'" + (String) newRoles.get(i) + "')";
                        ps = con.prepareStatement(updateSQL);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            result = true;
            con.close();
        } catch (Exception e) {
            logger.debug(e, e);
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException ex) {}
            }
        }
        return result;
    }

    /**
     * Edit a user role.
     *
     * @param manager who made this change
     * @param rolename role name
     * @param oldRoleName old role name
     * @param description role description
     * @param request request
     * @return true if operation was made with success
     */

    public static boolean editRoleName(String manager,
                                       String rolename,
                                       String oldRoleName,
                                       String description,
                                       HttpServletRequest request) {
        boolean result = false;
        String updateSQL = "";
        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            updateSQL = "";
            updateSQL += " UPDATE eunis_roles SET";
            updateSQL += " ROLENAME=?,";
            updateSQL += " DESCRIPTION=?";
            updateSQL += " WHERE ROLENAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, rolename);
            ps.setString(2, description);
            ps.setString(3, oldRoleName);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // UPDATE USERS_ROLES
            updateSQL = "";
            updateSQL += " UPDATE eunis_users_roles SET";
            updateSQL += " ROLENAME=?";
            updateSQL += " WHERE ROLENAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, rolename);
            ps.setString(2, oldRoleName);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // UPDATE ROLES_RIGHTS
            updateSQL = "";
            updateSQL += " UPDATE eunis_roles_rights SET";
            updateSQL += " ROLENAME=?";
            updateSQL += " WHERE ROLENAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, rolename);
            ps.setString(2, oldRoleName);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // DELETE ALL RIGHTS FOR ROLENAME
            SQL = "";
            SQL += " DELETE FROM eunis_roles_rights";
            SQL += " WHERE ROLENAME=?";
            ps = con.prepareStatement(SQL);
            ps.setString(1, rolename);
            ps.execute();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.execute();
            ps.close();

            // ADD NEW ROLES RIGHTS
            result = addRightsForRole(manager, request, rolename);

            con.close();
        } catch (Exception e) {
            logger.debug(e, e);
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException ex) {}
            }
        }
        return result;
    }

    /** Edit user rights.
     *
     * @param manager who made this change
     * @param rightname right name
     * @param description right description
     * @return true if operation was made with success
     */
    public static boolean editRights(String manager,
                                     String rightname,
                                     String description) {
        boolean result = false;
        String updateSQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            updateSQL = "UPDATE eunis_rights SET";
            updateSQL += " DESCRIPTION=?";
            updateSQL += " WHERE RIGHTNAME=?";
            ps = con.prepareStatement(updateSQL);
            ps.setString(1, description);
            ps.setString(2, rightname);
            ps.executeUpdate();
            ps.close();
            // Not sure if it's needed, but strange things happen when doing fast some operation on user from web page.
            ps = con.prepareStatement("COMMIT");
            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }

        return result;
    }

    /**
     * Get UserPersist object by user name.
     * @param username
     * @return UserPersit object
     */
    public static UserPersist getUserByUserName(String username) {
        if (username == null) {
            return null;
        }
        UserPersist result = null;

        try {
            List users = new UserDomain().findCustom(
                    "SELECT USERNAME,FIRST_NAME,LAST_NAME,LANG,EMAIL," + " THEME_INDEX,DATE_FORMAT(login_date,'%d %b %Y %H:%i:%s')"
                    + " FROM eunis_users" + " WHERE USERNAME='" + username + "'");

            if (users != null && users.size() > 0) {
                result = (UserPersist) users.get(0);
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Get rights descriptions list for a role name.
     * @param rolename Role for which rights to be extracted.
     * @return rights descriptions list
     */
    public static Vector getRolesRights(String rolename) {
        if (rolename == null) {
            return new Vector();
        }
        Vector result = new Vector();

        try {
            List rights = new RolesRightsDomain().findWhere("A.ROLENAME = '" + rolename + "'  GROUP BY C.RIGHTNAME");

            if (rights.size() > 0) {
                for (int i = 0; i < rights.size(); i++) {
                    result.add(((RolesRightsPersist) rights.get(i)).getRightDescription());
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Get rights names list for a role name.
     * @param rolename
     * @return rights names list
     */
    public static Vector getRolesRightsName(String rolename) {
        if (rolename == null) {
            return new Vector();
        }
        Vector result = new Vector();

        try {
            List rights = new RolesRightsDomain().findWhere("A.ROLENAME = '" + rolename + "'  GROUP BY C.RIGHTNAME");

            if (rights != null && rights.size() > 0) {
                for (int i = 0; i < rights.size(); i++) {
                    result.add(((RolesRightsPersist) rights.get(i)).getRightName());
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Add user roles.
     * @param manager who made this operation
     * @param roleName role name
     * @param description role description
     * @return true if operation was success
     */
    public static boolean addRoles(String manager, String roleName, String description) {
        boolean result = false;

        if (!existRoleName(roleName)) {
            try {
                RolesPersist role = new RolesPersist();

                role.setRoleName(roleName);
                role.setDescription(description);
                new RolesDomain().save(role);
                result = true;
            } catch (Exception e) {
                logger.debug(e, e);
            }
        }
        return result;
    }

    /**
     * Add user rights.
     * @param manager who made this operation
     * @param rightName right name
     * @param description right description
     * @return true if operation was success
     */
    public static boolean addRights(String manager, String rightName, String description) {
        boolean success = false;

        try {
            if (!existRightName(rightName)) {
                RightsPersist right = new RightsPersist();

                right.setRightName(rightName);
                right.setDescription(description);
                new RightsDomain().save(right);
                success = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return success;
    }

    /**
     * true if user role name exists in eunis_roles table.
     * @param rolename role name
     * @return true or false
     */
    public static boolean existRoleName(String rolename) {
        if (rolename == null) {
            return false;
        }
        boolean is = false;

        try {
            List roles = new RolesDomain().findWhere("ROLENAME='" + rolename + "'");

            if (roles != null && roles.size() > 0) {
                is = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return is;
    }

    /**
     * true if user right name exists in eunis_rights table.
     * @param rightname right name
     * @return true or false
     */
    public static boolean existRightName(String rightname) {
        if (rightname == null) {
            return false;
        }
        boolean result = false;

        try {
            List rights = new RightsDomain().findWhere("RIGHTNAME='" + rightname + "'");

            if (rights.size() > 0) {
                result = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Add user rights for a user role.
     *
     * @param manager who made this operation
     * @param request request
     * @param rolename role name
     * @return true if operation was made with success
     */
    public static boolean addRightsForRole(String manager, HttpServletRequest request, String rolename) {
        boolean success = false;

        try {
            Vector newRights = UsersUtility.extractRights(request);
            String SQL = "";

            if (newRights != null && newRights.size() > 0 && existRoleName(rolename)) {
                Connection[] con2 = new Connection[newRights.size()];
                PreparedStatement[] ps2 = new PreparedStatement[newRights.size()];
                int j = -1;

                for (int i = 0; i < newRights.size(); i++) {
                    j++;
                    con2[j] = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    SQL = "INSERT INTO eunis_roles_rights(ROLENAME,RIGHTNAME) VALUES(";
                    SQL += "'" + rolename + "',";
                    SQL += "'" + (String) newRights.get(i) + "')";
                    ps2[j] = con2[j].prepareStatement(SQL);
                    ps2[j].execute();
                    ps2[j].close();
                    con2[j].close();
                }
            }
            success = true;
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return success;
    }

    /**
     * Replace all "_" from string 'name' with " ".
     * @param name
     * @return return the new string
     */
    public static String getNameNice(String name) {
        String result = "";

        try {
            if (name != null) {
                result = name.replaceAll("_", " ");
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Get all user roles names.
     * @return all user roles names
     */
    public static Vector getAllRolesName() {
        Vector result = new Vector();

        try {
            List roles = new RolesDomain().findOrderBy("A.ROLENAME");

            if (roles.size() > 0) {
                for (int i = 0; i < roles.size(); i++) {
                    result.add(((RolesPersist) roles.get(i)).getRoleName());
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Delete a user role.
     *
     * @param roleName role name
     * @return true if operation was made with success
     */
    public static boolean deleteRole(String roleName) {
        boolean result = false;
        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            SQL = "DELETE FROM eunis_roles WHERE ROLENAME='" + roleName + "'";
            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "DELETE FROM eunis_roles_rights WHERE ROLENAME='" + roleName + "'";
            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();

            SQL = "DELETE FROM eunis_users_roles WHERE ROLENAME='" + roleName + "'";
            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();
            con.close();
            result = true;
        } catch (Exception e) {
            logger.debug(e, e);
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException ex) {}
            }
        }

        if (result && !existRoleName(roleName) && !existRoleNameInUsersRoles(roleName) && !existRoleNameInRolesRights(roleName)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Get RightsPersist object for a right name.
     * @param rightname right name
     * @return RightsPersist object
     */
    public static RightsPersist getRightsObject(String rightname) {
        if (rightname == null) {
            return null;
        }
        RightsPersist obj = null;

        try {
            List rights = new RightsDomain().findWhere("RIGHTNAME='" + rightname + "'");

            if (rights.size() > 0) {
                obj = (RightsPersist) rights.get(0);
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return obj;
    }

    /**
     * Get RolesPersist object for a role name.
     * @param roleName role name
     * @return RolesPersist object
     */
    public static RolesPersist getRoleObject(String roleName) {
        if (roleName == null) {
            return null;
        }
        RolesPersist result = null;

        try {
            List roles = new RolesDomain().findWhere("ROLENAME='" + roleName + "'");

            if (roles != null && roles.size() > 0) {
                result = (RolesPersist) roles.get(0);
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        ;
        return result;
    }

    /**
     * Check if a role_right object exist in eunis_roles_rights  table.
     * @param roleName role name
     * @param rightName right name
     * @return true if exist
     */
    public static boolean ifRoleRightObjectExist(String roleName, String rightName) {
        if (roleName == null || rightName == null) {
            return false;
        }
        boolean result = false;

        try {
            List rolesRights = new RolesRightsSimpleDomain().findWhere(
                    "ROLENAME='" + roleName + "' AND RIGHTNAME='" + rightName + "'");

            if (rolesRights != null && rolesRights.size() > 0) {
                result = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        ;
        return result;
    }

    /**
     * Delete saved easy search criteria.
     * @param username user name
     * @param pagename page name
     * @param criterianame criteria name
     */
    public static void deleteUserSaveCriteria(String username, String pagename, String criterianame) {
        String SQL = "";
        String SQL1 = "";
        ResultSet rs = null;
        Connection con1 = null;
        PreparedStatement ps1 = null;
        Statement st = null;

        try {
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "SELECT * FROM eunis_group_search WHERE USERNAME='" + username + "' AND CRITERIA_NAME='" + criterianame
                    + "' AND FROM_WHERE='" + pagename + "'";
            st = con1.createStatement();
            rs = st.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    SQL1 = "DELETE FROM eunis_group_search_criteria WHERE CRITERIA_NAME='" + rs.getString("CRITERIA_NAME") + "'";
                    ps1 = con1.prepareStatement(SQL1);
                    ps1.execute();
                    ps1.close();
                }
            }

            st.close();

            SQL = "DELETE FROM eunis_group_search WHERE USERNAME='" + username + "' AND CRITERIA_NAME='" + criterianame
                    + "' AND FROM_WHERE='" + pagename + "'";

            ps1 = con1.prepareStatement(SQL);
            ps1.execute();

            rs.close();
            ps1.close();
            con1.close();

        } catch (Exception e) {
            logger.debug(e, e);
        }
    }

    /**
     * Delete saved advanced search criteria.
     * @param natureobject nature object
     * @param pagename page name
     * @param criterianame criteria name
     */
    public static void deleteUserSaveAdvancedCriteria(String natureobject, String pagename, String criterianame) {
        String SQL = "";
        String SQL1 = "";
        ResultSet rs = null;
        Connection con1 = null;
        PreparedStatement ps1 = null;
        Statement st = null;

        try {
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "SELECT * FROM eunis_save_advanced_search WHERE NATURE_OBJECT='" + natureobject + "' AND CRITERIA_NAME='"
                    + criterianame + "' AND FROM_WHERE='" + pagename + "'";
            st = con1.createStatement();
            rs = st.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    SQL1 = "DELETE FROM eunis_save_advanced_search_criteria WHERE CRITERIA_NAME='" + rs.getString("CRITERIA_NAME")
                            + "' AND NATURE_OBJECT='" + natureobject + "'";
                    ps1 = con1.prepareStatement(SQL1);
                    ps1.execute();
                    ps1.close();
                }
            }

            st.close();

            SQL = "DELETE FROM eunis_save_advanced_search WHERE NATURE_OBJECT='" + natureobject + "' AND CRITERIA_NAME='"
                    + criterianame + "' AND FROM_WHERE='" + pagename + "'";

            ps1 = con1.prepareStatement(SQL);
            ps1.execute();

            rs.close();
            ps1.close();
            con1.close();

        } catch (Exception e) {
            logger.debug(e, e);
        }
    }

    /**
     * Delete saved combined search criteria.
     * @param pagename page name
     * @param criterianame criteria name
     */
    public static void deleteUserSaveCombinedCriteria(String pagename, String criterianame) {
        String SQL = "";
        String SQL1 = "";
        ResultSet rs = null;
        Connection con1 = null;
        PreparedStatement ps1 = null;
        Statement st = null;

        try {
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "SELECT * FROM EUNIS_SAVE_COMBINED_SEARCH WHERE CRITERIA_NAME='" + criterianame + "' AND FROM_WHERE='" + pagename
                    + "'";
            st = con1.createStatement();
            rs = st.executeQuery(SQL);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    SQL1 = "DELETE FROM EUNIS_SAVE_COMBINED_SEARCH_CRITERIA WHERE CRITERIA_NAME='" + rs.getString("CRITERIA_NAME")
                            + "'";
                    ps1 = con1.prepareStatement(SQL1);
                    ps1.execute();
                    ps1.close();
                }
            }

            st.close();

            SQL = "DELETE FROM EUNIS_SAVE_COMBINED_SEARCH WHERE CRITERIA_NAME='" + criterianame + "' AND FROM_WHERE='" + pagename
                    + "'";

            ps1 = con1.prepareStatement(SQL);
            ps1.execute();

            rs.close();
            ps1.close();
            con1.close();

        } catch (Exception e) {
            logger.debug(e, e);
        }
    }

    /**
     * Add users.
     *
     * @param username user name
     * @param firstname user first name
     * @param lastname user last name
     * @param mail user mail
     * @param loginDate Login date.
     * @param manager who made this operation
     * @return true if operation was made with success
     */
    public static boolean addUsers(String username,
                                   String firstname,
                                   String lastname,
                                   String mail,
                                   String loginDate,
                                   String manager) {
        boolean success = false;
        Connection con = null;
        PreparedStatement ps = null;
        String SQL = "";

        if (!existUserName(username)) {
            try {
                con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

                SQL = "INSERT INTO eunis_users(USERNAME,FIRST_NAME,LAST_NAME,EMAIL,THEME_INDEX,LOGIN_DATE) "
                        + " VALUES(?,?,?,?,?,str_to_date(?,'%d %b %Y %H:%i:%s'))";
                ps = con.prepareStatement(SQL);
                ps.setString(1, username);
                ps.setString(2, firstname);
                ps.setString(3, lastname);
                ps.setString(4, mail);
                ps.setInt(5, ThemeManager.THEME_SKY_BLUE);
                ps.setString(6, (loginDate == null ? null : loginDate.trim()));

                ps.execute();

                ps.close();
                con.close();

                success = true;
            } catch (Exception e) {
                logger.debug(e, e);
            }
        }
        return success;
    }

    /**
     * Add roles for users.
     *
     * @param username user name
     * @param request request
     * @param manager who made this operation
     * @return true if operation was made with success
     */
    public static boolean addRolesForUser(String username, HttpServletRequest request, String manager) {
        boolean success = false;

        try {
            Vector newRoles = UsersUtility.extractRoles(request);
            String SQL = "";

            if (newRoles != null && newRoles.size() > 0 && existUserName(username)) {
                Connection[] con2 = new Connection[newRoles.size()];
                PreparedStatement[] ps2 = new PreparedStatement[newRoles.size()];
                int j = -1;

                for (int i = 0; i < newRoles.size(); i++) {
                    j++;
                    con2[j] = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    SQL = "INSERT INTO eunis_users_roles(USERNAME,ROLENAME) VALUES(";
                    SQL += "'" + username + "',";
                    SQL += "'" + (String) newRoles.get(i) + "')";
                    ps2[j] = con2[j].prepareStatement(SQL);
                    ps2[j].execute();
                    ps2[j].close();
                    con2[j].close();
                }
            }
            success = true;
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return success;
    }
}
