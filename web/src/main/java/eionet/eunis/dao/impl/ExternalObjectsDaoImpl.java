package eionet.eunis.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import eionet.eunis.dao.IExternalObjectsDao;
import eionet.eunis.dto.AttributeDto;
import eionet.eunis.dto.ExternalObjectDTO;
import eionet.eunis.dto.LinkDTO;
import org.apache.log4j.Logger;

/**
 * @author Risto Alt <a href="mailto:risto.alt@tieto.com">contact</a>
 */
public class ExternalObjectsDaoImpl extends MySqlBaseDao implements IExternalObjectsDao {

    private static final Logger logger = Logger.getLogger(ExternalObjectsDaoImpl.class);

    public ExternalObjectsDaoImpl() {
    }

    /**
     * @see eionet.eunis.dao.IExternalObjectsDao#getMaybeSameObjects() {@inheritDoc}
     */
    public List<ExternalObjectDTO> getMaybeSameObjects() {

        List<ExternalObjectDTO> ret = new ArrayList<ExternalObjectDTO>();

        // String query =
        // "SELECT CONCAT(S.SCIENTIFIC_NAME, ' ', S.AUTHOR) AS SQL_NAME, S.ID_SPECIES, S.ID_NATURE_OBJECT, E.RESOURCE, E.NAME FROM chm62edt_species S, externalobjects E WHERE E.ID_NATURE_OBJECT = S.ID_NATURE_OBJECT AND RELATION = 'maybesame' ORDER BY E.RESOURCE LIMIT 300";
        String query =
                "SELECT CONCAT(S.SCIENTIFIC_NAME, ' ', S.AUTHOR) AS SQL_NAME, S.ID_SPECIES, S.ID_NATURE_OBJECT, A1.OBJECT AS IDENTIFIER, A2.OBJECT AS GEO_NAME FROM chm62edt_species S, chm62edt_nature_object_attributes A1, "
                        + "chm62edt_nature_object_attributes A2 WHERE A1.ID_NATURE_OBJECT = S.ID_NATURE_OBJECT AND A1.NAME = 'maybeSameSynonym' AND A2.ID_NATURE_OBJECT=A1.ID_NATURE_OBJECT AND A2.NAME = '_geospeciesScientificName' ORDER BY A1.OBJECT LIMIT 300";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ExternalObjectDTO dto = new ExternalObjectDTO();

                dto.setIdentifier(rs.getString("IDENTIFIER"));
                dto.setName(rs.getString("GEO_NAME"));
                dto.setNameSql(rs.getString("SQL_NAME"));
                dto.setNatureObjectId(rs.getString("ID_NATURE_OBJECT"));
                dto.setSpecieId(rs.getString("ID_SPECIES"));
                ret.add(dto);
            }

        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return ret;
    }

    public void updateExternalObject(String identifier, String val) {
        if (identifier != null && val != null) {
            String query = "UPDATE chm62edt_nature_object_attributes SET NAME=? WHERE OBJECT=? AND NAME = 'maybeSameSynonym'";

            Connection con = null;
            PreparedStatement ps = null;

            try {
                con = getConnection();
                ps = con.prepareStatement(query);

                ps.setString(1, val);
                ps.setString(2, identifier);
                ps.executeUpdate();

            } catch (Exception e) {
                logger.debug(e, e); 
            } finally {
                closeAllResources(con, ps, null);
            }
        }
    }

    public Hashtable<String, AttributeDto> getNatureObjectAttributes(Integer id) {
        Hashtable<String, AttributeDto> ret = new Hashtable<String, AttributeDto>();

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            preparedStatement =
                    con.prepareStatement("SELECT NAME, OBJECT, TYPE FROM chm62edt_nature_object_attributes WHERE ID_NATURE_OBJECT=?");
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("NAME");
                String object = rs.getString("OBJECT");
                String type = rs.getString("TYPE");                
                AttributeDto attr = new AttributeDto(name, type, object, name);
                ret.put(name, attr);
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return ret;
    }

    public ArrayList<LinkDTO> getNatureObjectLinks(Integer idNatOb) {
        ArrayList<LinkDTO> ret = new ArrayList<LinkDTO>();

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            preparedStatement =
                    con.prepareStatement("SELECT LINK, LINKTEXT FROM chm62edt_nature_object_links WHERE ID_NATURE_OBJECT=?");
            preparedStatement.setInt(1, idNatOb);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                LinkDTO dto = new LinkDTO();
                dto.setUrl(rs.getString("LINK"));
                dto.setName(rs.getString("LINKTEXT"));
                ret.add(dto);
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return ret;
    }

}
