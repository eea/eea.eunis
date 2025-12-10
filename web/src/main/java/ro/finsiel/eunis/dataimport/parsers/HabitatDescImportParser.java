package ro.finsiel.eunis.dataimport.parsers;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ro.finsiel.eunis.utilities.SQLUtilities;


/**
 *
 */
public class HabitatDescImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatement;
    private PreparedStatement preparedStatementNatureObject;

    private int counter = 0;

    private String habId;
    private String lang;
    private String desc;
    private String ownerText;
    private String refcd;

    private Connection con;

    private StringBuffer buf;
    private HashMap<String, Integer> langIds;
    private HashMap<String, Integer> dcIds;

    public HabitatDescImportParser(SQLUtilities sqlUtilities) {
        this.con = sqlUtilities.getConnection();
        buf = new StringBuffer();
    }

    private void parseDocument() throws SAXException {

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            // parse the file and also register this class for call backs
            sp.parse(inputStream, this);

        } catch (SAXException se) {
            // se.printStackTrace(); // preparing for decommissioning
            throw new RuntimeException(se.getMessage(), se);
        } catch (ParserConfigurationException pce) {
            // pce.printStackTrace(); // preparing for decommissioning
            throw new RuntimeException(pce.getMessage(), pce);
        } catch (IOException ie) {
            // ie.printStackTrace(); // preparing for decommissioning
            throw new RuntimeException(ie.getMessage(), ie);
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        buf = new StringBuffer();
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        buf.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("Habsccd")) {
                habId = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Langcd")) {
                lang = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Textdescr")) {
                desc = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Refcd")) {
                refcd = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Owntext")) {
                ownerText = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("HABTEXT")) {

                Integer langId = langIds.get(lang);
                Integer dcId = dcIds.get(refcd);

                if (langId == null) {
                    langId = 25;
                }

                if (dcId == null) {
                    dcId = -2;
                }

                preparedStatement.setString(1, habId);
                preparedStatement.setInt(2, langId);
                preparedStatement.setString(3, desc);
                preparedStatement.setString(4, ownerText);
                preparedStatement.setInt(5, dcId);
                preparedStatement.addBatch();

                preparedStatementNatureObject.setInt(1, dcId);
                preparedStatementNatureObject.setString(2, habId);

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();

                    preparedStatementNatureObject.executeBatch();
                    preparedStatementNatureObject.clearParameters();
                }

                habId = null;
                lang = null;
                desc = null;
                ownerText = null;
                refcd = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public void execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            deleteOldRecords();
            langIds = getLangIds();
            dcIds = getDCIds();

            String queryHabitatDescription = "INSERT INTO chm62edt_habitat_description (ID_HABITAT, ID_LANGUAGE, DESCRIPTION, OWNER_TEXT, ID_DC) VALUES (?,?,?,?,?)";

            this.preparedStatement = con.prepareStatement(
                    queryHabitatDescription);

            String queryUpdateNatureObject = "UPDATE chm62edt_nature_object SET ID_DC = ? WHERE ORIGINAL_CODE = ?";

            this.preparedStatementNatureObject = con.prepareStatement(
                    queryUpdateNatureObject);

            // con.setAutoCommit(false);
            parseDocument();
            if (!(counter % 10000 == 0)) {
                preparedStatement.executeBatch();
                preparedStatement.clearParameters();

                preparedStatementNatureObject.executeBatch();
                preparedStatementNatureObject.clearParameters();
            }
            // con.commit();
        } catch (Exception e) {
            // con.rollback();
            // con.commit();
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (preparedStatementNatureObject != null) {
                preparedStatementNatureObject.close();
            }

            if (con != null) {
                con.close();
            }
        }

    }

    private void deleteOldRecords() throws Exception {
        PreparedStatement ps = null;

        try {

            String query = "DELETE FROM chm62edt_habitat_description";

            ps = con.prepareStatement(query);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private HashMap<String, Integer> getLangIds() throws Exception {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT ID_LANGUAGE, CODE FROM chm62edt_language";

            stmt = con.prepareStatement(query);
            rset = stmt.executeQuery();
            while (rset.next()) {
                int id = rset.getInt("ID_LANGUAGE");
                String code = rset.getString("CODE");

                ret.put(code, new Integer(id));
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rset != null) {
                rset.close();
            }
        }
        return ret;
    }

    /**
     * @deprecated The REFCD column was removed, the map now uses the ID_DC column as a key
     * @return
     * @throws Exception
     */
    private HashMap<String, Integer> getDCIds() throws Exception {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT ID_DC FROM dc_index WHERE COMMENT = 'REFERENCES'";

            stmt = con.prepareStatement(query);
            rset = stmt.executeQuery();
            while (rset.next()) {
                int idDc = rset.getInt("ID_DC");
                ret.put(idDc +"", idDc);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rset != null) {
                rset.close();
            }
        }
        return ret;
    }
}
