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
public class ClassCodesImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatement;

    private int counter = 0;

    private String classId;
    private String sort;
    private String name;
    private String classRef;
    private String refcd;
    private String current;
    private String legal;

    private String classif;

    private Connection con;

    private StringBuffer buf;
    private HashMap<String, Integer> dcIds;

    public ClassCodesImportParser(SQLUtilities sqlUtilities) {
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
            if (qName.equalsIgnoreCase("Classcd")) {
                classId = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Sortcd")) {
                sort = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Classref")) {
                classRef = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Classname")) {
                name = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Refcd")) {
                refcd = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Current")) {
                current = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Legal")) {
                legal = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("CLASSCODES")) {

                Integer dcId = dcIds.get(refcd);

                if (dcId == null) {
                    dcId = -2;
                }

                preparedStatement.setString(1, classId);
                preparedStatement.setString(2, sort);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, classRef);
                preparedStatement.setString(5, legal);
                preparedStatement.setString(6, current);
                preparedStatement.setInt(7, dcId);
                preparedStatement.addBatch();

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

                classif = name;

                classId = null;
                sort = null;
                name = null;
                classRef = null;
                refcd = null;
                current = null;
                legal = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public String execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            deleteOldRecords();
            dcIds = getDCIds();

            String query = "INSERT INTO chm62edt_class_code (ID_CLASS_CODE, SORT_ORDER, NAME, UNVALIDATED_CODE, LEGAL, CURRENT_DATA, ID_DC) VALUES (?,?,?,?,?,?,?)";

            this.preparedStatement = con.prepareStatement(query);

            // con.setAutoCommit(false);
            parseDocument();
            if (!(counter % 10000 == 0)) {
                preparedStatement.executeBatch();
                preparedStatement.clearParameters();
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

            if (con != null) {
                con.close();
            }
        }

        return classif;
    }

    private void deleteOldRecords() throws Exception {
        PreparedStatement ps = null;

        try {

            String query = "DELETE FROM chm62edt_class_code";

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

                ret.put(idDc + "", idDc);
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
