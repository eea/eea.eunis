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

import eionet.eunis.stripes.extensions.LoadException;
import eionet.eunis.util.Constants;


/**
 *
 * @author Risto Alt, e-mail: <a href="mailto:risto.alt@tieto.com">risto.alt@tieto.com</a>
 *
 */
public class RDFHandler implements StatementHandler, ErrorHandler {

    /** */
    private boolean saxErrorSet = false;
    private boolean saxWarningSet = false;

    private Logger logger = Logger.getLogger(RDFHandler.class);

    private static final String geo_ns = "http://rdf.geospecies.org/ont/geospecies#";

    /** */
    private static final String EMPTY_STRING = "";

    private Connection con = null;

    private List<String> errors = null;

    private boolean hasGBIF = false;
    private boolean hasBiolab = false;
    private boolean hasBbc = false;
    private boolean hasWikipedia = false;
    private boolean hasWikispecies = false;
    private boolean hasBugGuide = false;
    private boolean hasNCBI = false;
    private boolean hasITIS = false;

    private String gsidentifier = null;
    private String natob_id = null;
    private String matching;

    private PreparedStatement preparedStatement;
    private int counter = 0;

    /**
     * @throws SQLException
     *
     */
    public RDFHandler(Connection con) throws SQLException {
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
            String identifier = subject.toString();

            if (gsidentifier == null || !identifier.equals(gsidentifier)) {
                natob_id = getNatureObjectID(identifier);
                gsidentifier = identifier;
            }

            if (natob_id != null && !natob_id.equals("")) {

                if (predicate.toString().equals(geo_ns + "hasGBIF") && hasGBIF) {
                    if (matching != null && matching.equals("sameSpecies")) {
                        insertReportAttribute(Constants.SAME_SPECIES_GBIF,
                                object, natob_id, litObject);
                    } else {
                        insertReportAttribute(Constants.SAME_SYNONYM_GBIF,
                                object, natob_id, litObject);
                    }
                }

                if (predicate.toString().equals(geo_ns + "hasBioLibPage")
                        && hasBiolab) {
                    insertReportAttribute(Constants.BIOLIB_PAGE, object,
                            natob_id, litObject);
                }

                if (predicate.toString().equals(geo_ns + "hasBBCPage") && hasBbc) {
                    insertReportAttribute(Constants.BBC_PAGE, object, natob_id,
                            litObject);
                }

                if (predicate.toString().equals(geo_ns + "hasWikipediaArticle")
                        && hasWikipedia) {
                    insertReportAttribute(Constants.WIKIPEDIA_ARTICLE, object,
                            natob_id, litObject);
                }

                if (predicate.toString().equals(geo_ns + "hasWikispeciesArticle")
                        && hasWikispecies) {
                    insertReportAttribute(Constants.WIKISPECIES_ARTICLE, object,
                            natob_id, litObject);
                }

                if (predicate.toString().equals(geo_ns + "hasBugGuidePage")
                        && hasBugGuide) {
                    insertReportAttribute(Constants.BUG_GUIDE, object, natob_id,
                            litObject);
                }

                if (predicate.toString().equals(geo_ns + "hasNCBI") && hasNCBI) {
                    if (matching != null && matching.equals("sameSpecies")) {
                        insertReportAttribute(Constants.SAME_SPECIES_NCBI,
                                object, natob_id, litObject);
                    } else {
                        insertReportAttribute(Constants.SAME_SYNONYM_NCBI,
                                object, natob_id, litObject);
                    }
                }

                if (predicate.toString().equals(geo_ns + "hasITIS") && hasITIS) {
                    if (matching != null && matching.equals("sameSpecies")) {
                        insertReportAttribute(Constants.SAME_SPECIES_ITIS,
                                object, natob_id, litObject);
                    } else {
                        insertReportAttribute(Constants.SAME_SYNONYM_ITIS,
                                object, natob_id, litObject);
                    }
                }
            }
        } catch (Exception e) {
            errors.add(e.getMessage());
            throw new LoadException(e.toString(), e);
        }
    }

    private void insertReportAttribute(String name, String value, String natob_id, boolean litObject) throws SQLException {

        try {

            preparedStatement.setString(1, natob_id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, EunisUtil.replaceTagsImport(value));
            if (litObject) {
                preparedStatement.setString(4, "");
            } else {
                preparedStatement.setString(4, "reference");
            }
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

    public String getNatureObjectID(String identifier) throws SQLException {

        String ret = null;

        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_nature_object_attributes WHERE OBJECT='"
            + identifier + "' AND NAME='" + matching + "'";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret = rs.getString("ID_NATURE_OBJECT");
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

    public void deleteOldRecords() throws SQLException {

        String query = "DELETE FROM chm62edt_nature_object_attributes WHERE NAME IN ";
        StringBuffer whereClause = new StringBuffer("(");

        if (hasGBIF) {
            if (matching != null && matching.equals("sameSpecies")) {
                whereClause.append("'").append(Constants.SAME_SPECIES_GBIF).append("'").append(
                ",");
            } else {
                whereClause.append("'").append(Constants.SAME_SYNONYM_GBIF).append("'").append(
                ",");
            }
        }
        if (hasBiolab) {
            whereClause.append("'").append(Constants.BIOLIB_PAGE).append("'").append(
            ",");
        }
        if (hasBbc) {
            whereClause.append("'").append(Constants.BBC_PAGE).append("'").append(
            ",");
        }
        if (hasWikipedia) {
            whereClause.append("'").append(Constants.WIKIPEDIA_ARTICLE).append("'").append(
            ",");
        }
        if (hasWikispecies) {
            whereClause.append("'").append(Constants.WIKISPECIES_ARTICLE).append("'").append(
            ",");
        }
        if (hasBugGuide) {
            whereClause.append("'").append(Constants.BUG_GUIDE).append("'").append(
            ",");
        }
        if (hasNCBI) {
            if (matching != null && matching.equals("sameSpecies")) {
                whereClause.append("'").append(Constants.SAME_SPECIES_NCBI).append("'").append(
                ",");
            } else {
                whereClause.append("'").append(Constants.SAME_SYNONYM_NCBI).append("'").append(
                ",");
            }
        }
        if (hasITIS) {
            if (matching != null && matching.equals("sameSpecies")) {
                whereClause.append("'").append(Constants.SAME_SPECIES_ITIS).append(
                "'");
            } else {
                whereClause.append("'").append(Constants.SAME_SYNONYM_ITIS).append(
                "'");
            }
        }

        if (whereClause.toString().endsWith(",")) {
            whereClause.deleteCharAt(whereClause.length() - 1);
        }

        if (whereClause.length() > 1) {
            whereClause.append(")");
            query = query + whereClause.toString();

            PreparedStatement ps = null;

            try {
                ps = con.prepareStatement(query);
                ps.executeUpdate();

            } catch (Exception e) {
                logger.debug(e, e); 
                errors.add(e.getMessage());
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }
    }

    /**
     *
     * @throws SQLException
     */
    public void endOfFile() throws SQLException {

        try {
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

    public boolean isHasGBIF() {
        return hasGBIF;
    }

    public void setHasGBIF(boolean hasGBIF) {
        this.hasGBIF = hasGBIF;
    }

    public boolean isHasBiolab() {
        return hasBiolab;
    }

    public void setHasBiolab(boolean hasBiolab) {
        this.hasBiolab = hasBiolab;
    }

    public boolean isHasBbc() {
        return hasBbc;
    }

    public void setHasBbc(boolean hasBbc) {
        this.hasBbc = hasBbc;
    }

    public boolean isHasWikipedia() {
        return hasWikipedia;
    }

    public void setHasWikipedia(boolean hasWikipedia) {
        this.hasWikipedia = hasWikipedia;
    }

    public boolean isHasWikispecies() {
        return hasWikispecies;
    }

    public void setHasWikispecies(boolean hasWikispecies) {
        this.hasWikispecies = hasWikispecies;
    }

    public boolean isHasBugGuide() {
        return hasBugGuide;
    }

    public void setHasBugGuide(boolean hasBugGuide) {
        this.hasBugGuide = hasBugGuide;
    }

    public boolean isHasNCBI() {
        return hasNCBI;
    }

    public void setHasNCBI(boolean hasNCBI) {
        this.hasNCBI = hasNCBI;
    }

    public boolean isHasITIS() {
        return hasITIS;
    }

    public void setHasITIS(boolean hasITIS) {
        this.hasITIS = hasITIS;
    }

    public String getMatching() {
        return matching;
    }

    public void setMatching(String matching) {
        this.matching = matching;
    }
}
