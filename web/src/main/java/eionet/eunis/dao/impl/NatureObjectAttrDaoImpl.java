package eionet.eunis.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.lang.StringUtils;

import eionet.eunis.dao.INatureObjectAttrDao;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link INatureObjectAttrDao}.
 * 
 * @author jaanus
 */
public class NatureObjectAttrDaoImpl extends MySqlBaseDao implements INatureObjectAttrDao {


    private static final Logger logger = Logger.getLogger(NatureObjectAttrDaoImpl.class);
    /*
     * (non-Javadoc)
     * 
     * @see eionet.eunis.dao.INatureObjectAttrDao#getNatObjAttribute(java.lang.Integer, java.lang.String)
     */
    @Override
    public String getNatObjAttribute(Integer natObjId, String name) {
        String query =
                "SELECT OBJECT FROM chm62edt_nature_object_attributes WHERE NAME = '" + name + "' AND ID_NATURE_OBJECT = "
                        + natObjId;
        return ExecuteSQL(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.eunis.dao.INatureObjectAttrDao#queryResultExists(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public boolean queryResultExists(Integer natObjId, String queryId, String queriesName) {
        boolean ret = false;
        String query =
                "SELECT OBJECT FROM chm62edt_nature_object_attributes WHERE NAME = '" + queriesName + "' "
                        + "AND ID_NATURE_OBJECT = " + natObjId;
        String result = ExecuteSQL(query);
        if (!StringUtils.isBlank(result) && result.contains(queryId)) {
            ret = true;
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.eunis.dao.INatureObjectAttrDao#insertNatureObjAttrForAll(eionet.eunis.dao.INatureObjectAttrDao.ObjectType,
     * java.lang.String)
     */
    @Override
    public void insertNatureObjAttrForAll(ObjectType objectType, String attrName) {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            if (ObjectType.SPECIES.equals(objectType)) {
                ps =
                        con.prepareStatement("insert into chm62edt_nature_object_attributes (ID_NATURE_OBJECT, NAME) "
                                + "(SELECT ID_NATURE_OBJECT,'" + attrName + "' FROM chm62edt_species)");
            } else if (ObjectType.HABITATS.equals(objectType)) {
                ps =
                        con.prepareStatement("insert into chm62edt_nature_object_attributes (ID_NATURE_OBJECT, NAME) "
                                + "(SELECT ID_NATURE_OBJECT,'" + attrName + "' FROM chm62edt_habitat)");
            } else {
                // Unsupported object type
                return;
            }

            ps.executeUpdate();

        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, ps, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.eunis.dao.INatureObjectAttrDao#appendToNatureObjAttr(eionet.eunis.dao.INatureObjectAttrDao.ObjectType,
     * java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void appendToNatureObjAttr(ObjectType objectType, String objectIds, String attrName, String attrValue) {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            if (ObjectType.SPECIES.equals(objectType)) {
                ps =
                        con.prepareStatement("UPDATE chm62edt_nature_object_attributes " + "SET OBJECT=CONCAT_WS(',',OBJECT,'"
                                + attrValue + "') WHERE NAME='" + attrName + "' AND ID_NATURE_OBJECT IN "
                                + "(SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_species WHERE ID_SPECIES IN (" + objectIds
                                + ")) "
                                + "AND FIND_IN_SET('" + attrValue + "', OBJECT)=0");
            } else if (ObjectType.HABITATS.equals(objectType)) {
                ps =
                        con.prepareStatement("UPDATE chm62edt_nature_object_attributes " + "SET OBJECT=CONCAT_WS(',',OBJECT,'"
                                + attrValue + "') WHERE NAME='" + attrName + "' AND ID_NATURE_OBJECT IN "
                                + "(SELECT DISTINCT ID_NATURE_OBJECT FROM chm62edt_habitat WHERE ID_HABITAT IN (" + objectIds
                                + ")) "
                                + "AND FIND_IN_SET('" + attrValue + "', OBJECT)=0");
            } else {
                // Unsupported object type
                return;
            }

            ps.executeUpdate();

        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, ps, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.eunis.dao.INatureObjectAttrDao#deleteEmptyNatureObjAttrsForAll(eionet.eunis.dao.INatureObjectAttrDao.ObjectType,
     * java.lang.String)
     */
    @Override
    public void deleteEmptyNatureObjAttrsForAll(ObjectType objectType, String attrName) {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            if (ObjectType.SPECIES.equals(objectType)) {
                ps =
                        con.prepareStatement("delete from chm62edt_nature_object_attributes where NAME = '" + attrName
                                + "' and LENGTH(OBJECT) = 0 and ID_NATURE_OBJECT"
                                + " in (select ID_NATURE_OBJECT from chm62edt_species where ID_NATURE_OBJECT > 0)");

            } else if (ObjectType.HABITATS.equals(objectType)) {
                ps =
                        con.prepareStatement("delete from chm62edt_nature_object_attributes where NAME = '" + attrName
                                + "' and LENGTH(OBJECT) = 0 and ID_NATURE_OBJECT"
                                + " in (select ID_NATURE_OBJECT from chm62edt_habitat where ID_NATURE_OBJECT > 0)");
            } else {
                // Unsupported object type.
                return;
            }

            ps.executeUpdate();

        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, ps, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.eunis.dao.INatureObjectAttrDao#deleteNatureObjAttrsForAll(eionet.eunis.dao.INatureObjectAttrDao.ObjectType,
     * java.lang.String)
     */
    @Override
    public void deleteNatureObjAttrsForAll(ObjectType objectType, String attrName) {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            if (ObjectType.SPECIES.equals(objectType)) {
                ps =
                        con.prepareStatement("delete from chm62edt_nature_object_attributes where NAME = '"
                                + attrName
                                + "' and ID_NATURE_OBJECT in (select ID_NATURE_OBJECT from chm62edt_species where ID_NATURE_OBJECT > 0)");
            } else if (ObjectType.HABITATS.equals(objectType)) {
                ps =
                        con.prepareStatement("delete from chm62edt_nature_object_attributes where NAME = '"
                                + attrName
                                + "' and ID_NATURE_OBJECT in (select ID_NATURE_OBJECT from chm62edt_habitat where ID_NATURE_OBJECT > 0)");
            } else {
                // Unsupported object type.
                return;
            }
            ps.executeUpdate();

        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, ps, null);
        }
    }
}
