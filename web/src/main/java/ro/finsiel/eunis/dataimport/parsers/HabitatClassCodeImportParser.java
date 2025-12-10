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
public class HabitatClassCodeImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatement;
    private PreparedStatement preparedStatementTabInfo;

    private int counter = 0;

    private String habId;
    private String classId;
    private String code;
    private String relation;
    private String title;

    private Connection con;

    private StringBuffer buf;
    private HashMap<String, Integer> noIds;
    private HashMap<String, String> classCodesLegal;

    public HabitatClassCodeImportParser(SQLUtilities sqlUtilities) {
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
            if (qName.equalsIgnoreCase("Classcd")) {
                classId = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Code")) {
                code = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Relation")) {
                relation = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Title")) {
                title = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("HABEQUIV")) {

                if (title == null) {
                    title = "";
                }

                if (relation == null) {
                    relation = "";
                }

                if (code == null) {
                    code = "";
                }

                preparedStatement.setString(1, habId);
                preparedStatement.setString(2, classId);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, relation);
                preparedStatement.setString(5, code);
                preparedStatement.addBatch();

                Integer noId = noIds.get(habId);
                String legal = classCodesLegal.get(classId);

                if (noId != null && legal != null && legal.equals("1")) {
                    preparedStatementTabInfo.setInt(1, noId);
                    preparedStatementTabInfo.addBatch();
                }

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();

                    preparedStatementTabInfo.executeBatch();
                    preparedStatementTabInfo.clearParameters();
                }

                habId = null;
                classId = null;
                code = null;
                relation = null;
                title = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public void execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            deleteOldRecords();
            noIds = getNOIds();
            classCodesLegal = getClassCodeLegal();

            String query = "INSERT INTO chm62edt_habitat_class_code (ID_HABITAT, ID_CLASS_CODE, TITLE, RELATION_TYPE, CODE) VALUES (?,?,?,?,?)";

            this.preparedStatement = con.prepareStatement(query);

            String queryUpdateSitesTabInfo = "UPDATE chm62edt_tab_page_habitats SET LEGAL_INSTRUMENTS='Y' WHERE ID_NATURE_OBJECT = ?";

            this.preparedStatementTabInfo = con.prepareStatement(
                    queryUpdateSitesTabInfo);

            // con.setAutoCommit(false);
            parseDocument();
            if (!(counter % 10000 == 0)) {
                preparedStatement.executeBatch();
                preparedStatement.clearParameters();

                preparedStatementTabInfo.executeBatch();
                preparedStatementTabInfo.clearParameters();
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

            if (preparedStatementTabInfo != null) {
                preparedStatementTabInfo.close();
            }

            if (con != null) {
                con.close();
            }
        }

    }

    private void deleteOldRecords() throws Exception {
        PreparedStatement ps = null;

        try {

            String query = "DELETE FROM chm62edt_habitat_class_code";

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

    private HashMap<String, Integer> getNOIds() throws Exception {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT ID_NATURE_OBJECT, ID_HABITAT FROM chm62edt_habitat";

            stmt = con.prepareStatement(query);
            rset = stmt.executeQuery();
            while (rset.next()) {
                int noId = rset.getInt("ID_NATURE_OBJECT");
                String habitatId = rset.getString("ID_HABITAT");

                ret.put(habitatId, new Integer(noId));
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

    private HashMap<String, String> getClassCodeLegal() throws Exception {
        HashMap<String, String> ret = new HashMap<String, String>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT LEGAL, ID_CLASS_CODE FROM chm62edt_class_code";

            stmt = con.prepareStatement(query);
            rset = stmt.executeQuery();
            while (rset.next()) {
                String legal = rset.getString("LEGAL");
                String classCode = rset.getString("ID_CLASS_CODE");

                ret.put(classCode, legal);
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
