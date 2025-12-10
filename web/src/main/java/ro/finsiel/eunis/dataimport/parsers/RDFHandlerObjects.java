package ro.finsiel.eunis.dataimport.parsers;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ro.finsiel.eunis.utilities.EunisUtil;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.StatementHandler;
import com.hp.hpl.jena.vocabulary.RDF;

import eionet.eunis.dto.LinkInfoDTO;
import eionet.eunis.stripes.extensions.LoadException;


/**
 *
 * @author Risto Alt, e-mail: <a href="mailto:risto.alt@tieto.com">risto.alt@tieto.com</a>
 *
 */
public class RDFHandlerObjects implements StatementHandler, ErrorHandler {

    /** */
    private boolean saxErrorSet = false;
    private boolean saxWarningSet = false;

    private Logger logger = Logger.getLogger(RDFHandlerObjects.class);

    private static final String geo_ns = "http://rdf.geospecies.org/ont/geospecies#";

    /** */
    private static final String EMPTY_STRING = "";

    private LinkInfoDTO dto = null;

    private Connection con = null;

    private List<String> errors = null;

    private PreparedStatement preparedStatement;
    private int counter = 0;

    /**
     * @throws SQLException
     *
     */
    public RDFHandlerObjects(Connection con) throws SQLException {
        this.con = con;

        String query = "INSERT INTO chm62edt_nature_object_attributes (ID_NATURE_OBJECT, NAME, OBJECT, TYPE) VALUES (?,?,?,?)";

        preparedStatement = con.prepareStatement(query);

        errors = new ArrayList<String>();

    }

    /*
     *  (non-Javadoc)
     * @see com.hp.hpl.jena.rdf.arp.StatementHandler#statement(com.hp.hpl.jena.rdf.arp.AResource, com.hp.hpl.jena.rdf.arp.AResource, com.hp.hpl.jena.rdf.arp.AResource)
     */
    public void statement(AResource subject, AResource predicate, AResource object) {

        statement(subject, predicate,
                object.isAnonymous() ? object.getAnonymousID() : object.getURI(),
                        EMPTY_STRING, false, object.isAnonymous());
    }

    /*
     *  (non-Javadoc)
     * @see com.hp.hpl.jena.rdf.arp.StatementHandler#statement(com.hp.hpl.jena.rdf.arp.AResource, com.hp.hpl.jena.rdf.arp.AResource, com.hp.hpl.jena.rdf.arp.ALiteral)
     */
    public void statement(AResource subject, AResource predicate, ALiteral object) {

        statement(subject, predicate, object.toString(), object.getLang(), true,
                false);
    }

    /**
     *
     * @param subject
     * @param predicate
     * @param object
     * @param objectLang
     * @param litObject
     * @param anonObject
     */
    private void statement(AResource subject, AResource predicate,
            String object, String objectLang, boolean litObject, boolean anonObject) {

        try {
            if (predicate.toString().equals(RDF.type.toString())
                    && object.equals(
                    "http://rdf.geospecies.org/ont/geospecies#SpeciesConcept")) {
                if (dto != null) {
                    insert(dto);
                }

                dto = new LinkInfoDTO();
                dto.setIdentifier(subject.toString());
            }
            if (dto != null) {
                if (predicate.toString().equals(geo_ns + "hasCanonicalName")) {
                    dto.setHasCanonicalName(object);
                }
                if (predicate.toString().equals(
                        geo_ns + "hasScientificNameAuthorship")) {
                    dto.setHasScientificNameAutorship(object);
                }
                if (predicate.toString().equals(geo_ns + "hasScientificName")) {
                    dto.setHasScientificName(object);
                }
            }

        } catch (Exception e) {
            errors.add(e.getMessage());
            throw new LoadException(e.toString(), e);
        }
    }

    private void insert(LinkInfoDTO dto) throws SQLException {

        String identifier = dto.getIdentifier();
        String canName = dto.getHasCanonicalName();

        if (canName == null) {
            canName = "";
        }
        String sciName = dto.getHasScientificName();

        if (sciName == null) {
            sciName = "";
        }
        String author = dto.getHasScientificNameAutorship();

        if (canName != null && author != null) {
            String query = "SELECT ID_NATURE_OBJECT, AUTHOR, VALID_NAME FROM chm62edt_species WHERE SCIENTIFIC_NAME = '"
                + EunisUtil.replaceTagsImport(canName) + "'";

            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                ps = con.prepareStatement(query);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String natob_id = rs.getString("ID_NATURE_OBJECT");
                    String sql_author = rs.getString("AUTHOR");
                    int valid_name = rs.getInt("VALID_NAME");

                    if (author != null && sql_author != null
                            && author.equals(sql_author)) {
                        insertExternalObject(natob_id, "sameSynonym", identifier,
                                sciName);
                        if (valid_name == 1) {
                            insertExternalObject(natob_id, "sameSpecies",
                                    identifier, sciName);
                        }
                    } else {
                        insertExternalObject(natob_id, "maybeSameSynonym",
                                identifier, sciName);
                    }
                }
            } catch (Exception e) {
                logger.debug(e, e);
                errors.add(e.getMessage());
            } finally {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
        }
    }

    private void insertExternalObject(String natob_id, String type, String identifier, String name) throws SQLException {
        if (!externalObjectExists(natob_id, type)) {

            insertName(natob_id, name);

            try {
                preparedStatement.setString(1, natob_id);
                preparedStatement.setString(2, type);
                preparedStatement.setString(3, identifier);
                preparedStatement.setString(4, "reference");
                preparedStatement.addBatch();

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

            } catch (Exception e) {
                logger.debug(e, e);
                errors.add(e.getMessage());
            }
        }
    }

    private void insertName(String natob_id, String name) throws SQLException {

        try {
            preparedStatement.setString(1, natob_id);
            preparedStatement.setString(2, "_geospeciesScientificName");
            preparedStatement.setString(3, EunisUtil.replaceTagsImport(name));
            preparedStatement.setBoolean(4, true);
            preparedStatement.addBatch();

            counter++;
            if (counter % 10000 == 0) {
                preparedStatement.executeBatch();
                preparedStatement.clearParameters();
            }

        } catch (Exception e) {
            logger.debug(e, e);
            errors.add(e.getMessage());
        }
    }

    public boolean externalObjectExists(String natob_id, String type) throws SQLException {

        boolean ret = false;

        String query = "SELECT OBJECT FROM chm62edt_nature_object_attributes WHERE ID_NATURE_OBJECT="
            + natob_id
            + " AND NAME IN ('sameSynonym', 'maybeSameSynonym', 'notSameSynonym') LIMIT 1";

        if (type != null && type.equals("sameSpecies")) {
            query = "SELECT OBJECT FROM chm62edt_nature_object_attributes WHERE ID_NATURE_OBJECT="
                + natob_id
                + " AND NAME IN ('sameSpecies', 'maybeSameSpecies', 'notSameSpecies') LIMIT 1";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
            errors.add(e.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return ret;
    }

    /**
     *
     * @throws SQLException
     */
    public void endOfFile() throws SQLException {

        try {
            if (dto != null) {
                insert(dto);
            }

            if (!(counter % 10000 == 0)) {
                preparedStatement.executeBatch();
                preparedStatement.clearParameters();
            }
        } catch (Exception e) {
            logger.debug(e, e);
            errors.add(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) throws SAXException {

        if (!saxErrorSet) {
            errors.add(e.getMessage());
            logger.warn("SAX error encountered: " + e.toString(), e);
            saxErrorSet = true;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException {

        if (!saxWarningSet) {
            logger.warn("SAX warning encountered: " + e.toString(), e);
            errors.add(e.getMessage());
            saxWarningSet = true;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException {
        throw new LoadException(e.toString(), e);
    }

    /**
     * @return the saxError
     */
    public List<String> getErrors() {
        return errors;
    }
}
