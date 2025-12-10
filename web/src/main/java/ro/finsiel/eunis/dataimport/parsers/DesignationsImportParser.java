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
public class DesignationsImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatement;

    private int counter = 0;

    private String iso3;
    private String desigAbbr;
    private String category;
    private String odesignate;
    private String designate;
    private String law;
    private String lawref;
    private String agency;
    private String number;
    private String totalArea;
    private String remark;

    private Connection con;

    private StringBuffer buf;
    private HashMap<String, Integer> geoscopeIds;

    public DesignationsImportParser(SQLUtilities sqlUtilities) {
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
            if (qName.equalsIgnoreCase("ISO3")) {
                iso3 = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("DESIG_ABBR")) {
                desigAbbr = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Category")) {
                category = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("ODESIGNATE")) {
                odesignate = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("DESIGNATE")) {
                designate = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Law")) {
                law = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Lawreference")) {
                lawref = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Agency")) {
                agency = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Number")) {
                number = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Total_Area")) {
                totalArea = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Remark")) {
                remark = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("CDDA_designations")) {

                Integer geoscopeId = 0;

                if (iso3 != null && iso3.length() > 0) {
                    geoscopeId = geoscopeIds.get(iso3);
                    if (geoscopeId == null) {
                        geoscopeId = 0;
                    }
                }

                String hasSites = "N";

                if (number != null && number.length() > 0) {
                    int num = new Integer(number).intValue();

                    if (num > 0) {
                        hasSites = "Y";
                    }
                }

                if (desigAbbr == null) {
                    desigAbbr = "";
                }

                if (odesignate == null) {
                    odesignate = "";
                }

                preparedStatement.setInt(1, geoscopeId);
                preparedStatement.setString(2, desigAbbr);
                preparedStatement.setString(3, category);
                preparedStatement.setString(4, odesignate);
                preparedStatement.setString(5, designate);
                preparedStatement.setString(6, law);
                preparedStatement.setString(7, lawref);
                preparedStatement.setString(8, agency);
                preparedStatement.setString(9, number);
                preparedStatement.setString(10, totalArea);
                preparedStatement.setString(11, remark);
                preparedStatement.setString(12, hasSites);
                preparedStatement.addBatch();

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearParameters();
                }

                iso3 = null;
                desigAbbr = null;
                category = null;
                odesignate = null;
                designate = null;
                law = null;
                lawref = null;
                agency = null;
                number = null;
                totalArea = null;
                remark = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public void execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            deleteOldRecords();
            geoscopeIds = getGeoscopeIds();

            String query = "INSERT INTO chm62edt_designations (ID_GEOSCOPE, ID_DESIGNATION, NATIONAL_CATEGORY, "
                    + "DESCRIPTION, DESCRIPTION_EN, NATIONAL_LAW, NATIONAL_LAW_REFERENCE, NATIONAL_LAW_AGENCY, TOTAL_NUMBER, "
                    + "TOTAL_AREA, REMARK, CDDA_SITES, SOURCE_DB, ORIGINAL_DATASOURCE, ID_DC) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'CDDA_NATIONAL','Common Database on Designated Areas (CDDA)',-1)";

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

    }

    private void deleteOldRecords() throws Exception {
        PreparedStatement ps = null;

        try {

            String query = "DELETE FROM chm62edt_designations WHERE SOURCE_DB='CDDA_NATIONAL'";

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

    private HashMap<String, Integer> getGeoscopeIds() throws Exception {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT ID_GEOSCOPE, ISO_3L FROM chm62edt_country";

            stmt = con.prepareStatement(query);
            rset = stmt.executeQuery();
            while (rset.next()) {
                int idGeoscope = rset.getInt("ID_GEOSCOPE");
                String iso = rset.getString("ISO_3L");

                ret.put(iso, new Integer(idGeoscope));
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
