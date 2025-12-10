package ro.finsiel.eunis.dataimport.parsers;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
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
public class HabitatImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatementHabitat;
    private PreparedStatement preparedStatementNatureObject;
    private PreparedStatement preparedStatementTabInfo;

    private int maxNoIdInt = 0;
    private int maxDcId = 0;

    private int counter = 0;

    private String eunisCode;
    private String habId;
    private String code2000;
    private String codeAnnex;
    private String level;
    private String priority;
    private String name;
    private String sciName;
    private String classRef;
    private String codePart;

    private Connection con;

    private String classif;

    private StringBuffer buf;
    private SQLUtilities sqlUtilities;

    private HashMap<String, Integer> natureObjectIds;

    public HabitatImportParser(SQLUtilities sqlUtilities, String classif) {
        this.sqlUtilities = sqlUtilities;
        this.con = sqlUtilities.getConnection();
        this.classif = classif;
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
            if (qName.equalsIgnoreCase("EUNIS_x0020_Hab")) {
                eunisCode = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Hab_x0020_level")) {
                level = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Name")) {
                name = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Scientific_x0020_name")) {
                sciName = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Habsccd")) {
                habId = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Natura2000_x0020_A_x0020_I")) {
                code2000 = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("HBCDCOAX")) {
                codeAnnex = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("P")) {
                priority = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Classref")) {
                classRef = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Code_x0020_part2")) {
                codePart = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("HABITAT")) {

                Integer noId = null;

                noId = natureObjectIds.get(habId);

                int natureObjectId = 0;

                if (noId == null) {
                    maxNoIdInt++;
                    natureObjectId = maxNoIdInt;
                } else {
                    natureObjectId = noId.intValue();
                }

                preparedStatementNatureObject.setInt(1, natureObjectId);
                preparedStatementNatureObject.setInt(2, maxDcId);
                preparedStatementNatureObject.setString(3, habId);
                preparedStatementNatureObject.addBatch();

                if (eunisCode == null) {
                    eunisCode = "";
                }
                preparedStatementHabitat.setString(1, habId);
                preparedStatementHabitat.setInt(2, natureObjectId);
                preparedStatementHabitat.setString(3, sciName);
                preparedStatementHabitat.setString(4, name);
                preparedStatementHabitat.setString(5, code2000);
                preparedStatementHabitat.setString(6, codeAnnex);
                preparedStatementHabitat.setString(7, eunisCode);
                preparedStatementHabitat.setString(8, habId);
                preparedStatementHabitat.setString(9, classRef);
                preparedStatementHabitat.setString(10, codePart);
                preparedStatementHabitat.setString(11, priority);
                preparedStatementHabitat.setString(12, level);
                preparedStatementHabitat.addBatch();

                if (noId == null) {
                    preparedStatementTabInfo.setInt(1, maxNoIdInt);
                    preparedStatementTabInfo.addBatch();
                }

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatementNatureObject.executeBatch();
                    preparedStatementNatureObject.clearParameters();

                    preparedStatementHabitat.executeBatch();
                    preparedStatementHabitat.clearParameters();

                    preparedStatementTabInfo.executeBatch();
                    preparedStatementTabInfo.clearParameters();
                }

                eunisCode = null;
                habId = null;
                code2000 = null;
                codeAnnex = null;
                level = null;
                priority = null;
                name = null;
                sciName = null;
                classRef = null;
                codePart = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public void execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            natureObjectIds = getNatureObjectIds();
            deleteOldRecords();

            maxNoIdInt = getMaxId("SELECT MAX(ID_NATURE_OBJECT) FROM chm62edt_nature_object");
            maxDcId = getMaxId("SELECT MAX(ID_DC) FROM dc_index");
            if (classif != null && classif.length() > 0) {
                insertClassification();
            }

            String queryNatureObject = "INSERT INTO chm62edt_nature_object (ID_NATURE_OBJECT, ID_DC, ORIGINAL_CODE, TYPE) VALUES (?,?,?,'HABITAT')";

            this.preparedStatementNatureObject = con.prepareStatement(queryNatureObject);

            String queryHabitat = "INSERT INTO chm62edt_habitat (ID_HABITAT, ID_NATURE_OBJECT, SCIENTIFIC_NAME, DESCRIPTION, CODE_2000, CODE_ANNEX1, EUNIS_HABITAT_CODE, ORIGINALLY_PUBLISHED_CODE, CLASS_REF, CODE_PART_2, PRIORITY, LEVEL) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            this.preparedStatementHabitat = con.prepareStatement(queryHabitat);

            String queryUpdateSitesTabInfo = "INSERT IGNORE INTO chm62edt_tab_page_habitats(ID_NATURE_OBJECT,GENERAL_INFORMATION) VALUES(?,'Y')";

            this.preparedStatementTabInfo = con.prepareStatement(queryUpdateSitesTabInfo);

            // con.setAutoCommit(false);
            parseDocument();
            if (!(counter % 10000 == 0)) {
                preparedStatementNatureObject.executeBatch();
                preparedStatementNatureObject.clearParameters();

                preparedStatementHabitat.executeBatch();
                preparedStatementHabitat.clearParameters();

                preparedStatementTabInfo.executeBatch();
                preparedStatementTabInfo.clearParameters();
            }
            // con.commit();
        } catch (Exception e) {
            // con.rollback();
            // con.commit();
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (preparedStatementHabitat != null) {
                preparedStatementHabitat.close();
            }

            if (preparedStatementNatureObject != null) {
                preparedStatementNatureObject.close();
            }

            if (preparedStatementTabInfo != null) {
                preparedStatementTabInfo.close();
            }

            if (con != null) {
                con.close();
            }
        }

    }

    private int getMaxId(String query) throws ParseException {
        String maxId = sqlUtilities.ExecuteSQL(query);
        int maxIdInt = 0;

        if (maxId != null && maxId.length() > 0) {
            maxIdInt = new Integer(maxId);
        }

        return maxIdInt;
    }

    private void deleteOldRecords() throws Exception {
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM chm62edt_habitat";

            ps = con.prepareStatement(query);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_nature_object WHERE TYPE = 'HABITAT'";
            ps = con.prepareStatement(query);
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            SQLUtilities.closeAll(null, ps, null);
        }
    }

    private HashMap<String, Integer> getNatureObjectIds() throws Exception {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT ORIGINAL_CODE, ID_NATURE_OBJECT FROM chm62edt_nature_object WHERE TYPE = 'HABITAT'";

            stmt = con.prepareStatement(query);
            rset = stmt.executeQuery();
            while (rset.next()) {
                String originalCode = rset.getString("ORIGINAL_CODE");
                int idNatureObject = rset.getInt("ID_NATURE_OBJECT");

                ret.put(originalCode, idNatureObject);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            SQLUtilities.closeAll(null, stmt, rset);
        }
        return ret;
    }

    private void insertClassification() throws Exception {

        maxDcId++;

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "INSERT INTO dc_index (ID_DC, SOURCE) VALUES (?,?)";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, maxDcId);
            stmt.setString(2, classif);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            SQLUtilities.closeAll(null, stmt, rset);
        }
    }
}
