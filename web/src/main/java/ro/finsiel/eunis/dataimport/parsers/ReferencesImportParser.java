package ro.finsiel.eunis.dataimport.parsers;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
public class ReferencesImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatementDcIndexInsert;
    private PreparedStatement preparedStatementDcIndexUpdate;

    private int counter = 0;

    private String refcd;
    private String author;
    private String date;
    private String title;
    private String abrevTitle;
    private String editor;
    private String journTitle;
    private String bookTitle;
    private String journIssue;
    private String publisher;
    private String isbn;
    private String url;
    private String legalInstCd;

    private Connection con;

    private StringBuffer buf;
    private SQLUtilities sqlUtilities;
    private int maxDcId;
    private HashMap<String, Integer> dcIds;

    private ArrayList<String> xmlRefcds = new ArrayList<String>();

    public ReferencesImportParser(SQLUtilities sqlUtilities) {
        this.sqlUtilities = sqlUtilities;
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
            if (qName.equalsIgnoreCase("Refcd")) {
                refcd = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Author")) {
                author = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Date")) {
                date = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Title")) {
                title = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Abrev_Title")) {
                abrevTitle = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Editor")) {
                editor = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Journtitle")) {
                journTitle = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Booktitle")) {
                bookTitle = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Journissue")) {
                journIssue = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Publisher")) {
                publisher = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("ISBN")) {
                isbn = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("URL")) {
                url = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("legal_inst_cd")) {
                legalInstCd = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("REFERENCES")) {

                Integer dcId = null;

                if (refcd != null && refcd.length() > 0) {
                    dcId = dcIds.get(refcd);
                    xmlRefcds.add(refcd);
                }

                if (dcId == null) {

                    maxDcId++;
                    String created = formatYear(date);

                    preparedStatementDcIndexInsert.setInt(1, maxDcId);
                    preparedStatementDcIndexInsert.setString(2, legalInstCd);
                    preparedStatementDcIndexInsert.setString(3, created);
                    preparedStatementDcIndexInsert.setString(4, title);
                    preparedStatementDcIndexInsert.setString(5, abrevTitle);
                    preparedStatementDcIndexInsert.setString(6, publisher);
                    preparedStatementDcIndexInsert.setString(7, author);
                    preparedStatementDcIndexInsert.setString(8, editor);
                    preparedStatementDcIndexInsert.setString(9, journTitle);
                    preparedStatementDcIndexInsert.setString(10, bookTitle);
                    preparedStatementDcIndexInsert.setString(11, journIssue);
                    preparedStatementDcIndexInsert.setString(12, isbn);
                    preparedStatementDcIndexInsert.setString(13, url);
                    preparedStatementDcIndexInsert.addBatch();

                } else {

                    String created = formatYear(date);

                    preparedStatementDcIndexUpdate.setString(1, created);
                    preparedStatementDcIndexUpdate.setString(2, title);
                    preparedStatementDcIndexUpdate.setString(3, abrevTitle);
                    preparedStatementDcIndexUpdate.setString(4, publisher);
                    preparedStatementDcIndexUpdate.setString(5, author);
                    preparedStatementDcIndexUpdate.setString(6, editor);
                    preparedStatementDcIndexUpdate.setString(7, journTitle);
                    preparedStatementDcIndexUpdate.setString(8, bookTitle);
                    preparedStatementDcIndexUpdate.setString(9, journIssue);
                    preparedStatementDcIndexUpdate.setString(10, isbn);
                    preparedStatementDcIndexUpdate.setString(11, url);
                    preparedStatementDcIndexUpdate.setInt(12, dcId.intValue());
                    preparedStatementDcIndexUpdate.addBatch();

                }

                counter++;
                if (counter % 10000 == 0) {
                    preparedStatementDcIndexInsert.executeBatch();
                    preparedStatementDcIndexInsert.clearParameters();

                    preparedStatementDcIndexUpdate.executeBatch();
                    preparedStatementDcIndexUpdate.clearParameters();
                }

                refcd = null;
                author = null;
                date = null;
                title = null;
                abrevTitle = null;
                editor = null;
                journTitle = null;
                bookTitle = null;
                journIssue = null;
                publisher = null;
                isbn = null;
                url = null;
                legalInstCd = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    public void execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            dcIds = getDCIds();
            // deleteOldRecords();
            maxDcId = getMaxId("SELECT MAX(ID_DC) FROM dc_index");

            // Insert statement
            String query = "INSERT INTO dc_index (ID_DC, REFERENCE, COMMENT, "
                + "CREATED, TITLE, ALTERNATIVE, PUBLISHER, SOURCE, EDITOR, JOURNAL_TITLE, "
                + "BOOK_TITLE, JOURNAL_ISSUE, ISBN, URL) VALUES (?,?,'REFERENCES',?,?,?,?,?,?,?,?,?,?,?)";
            this.preparedStatementDcIndexInsert = con.prepareStatement(query);

            // Update statement
            String queryDcIndexUpdate = "UPDATE dc_index SET CREATED = ?, TITLE = ?, "
                + "ALTERNATIVE = ?, PUBLISHER = ?, SOURCE = ?, EDITOR = ?, JOURNAL_TITLE = ?, BOOK_TITLE = ?, "
                + "JOURNAL_ISSUE = ?, ISBN = ?, URL = ? WHERE ID_DC = ?";
            this.preparedStatementDcIndexUpdate = con.prepareStatement(queryDcIndexUpdate);

            // con.setAutoCommit(false);
            parseDocument();
            if (!(counter % 10000 == 0)) {
                preparedStatementDcIndexInsert.executeBatch();
                preparedStatementDcIndexInsert.clearParameters();

                preparedStatementDcIndexUpdate.executeBatch();
                preparedStatementDcIndexUpdate.clearParameters();
            }
            deleteRedundantRecords();
            // con.commit();
        } catch (Exception e) {
            // con.rollback();
            // con.commit();
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (preparedStatementDcIndexInsert != null) {
                preparedStatementDcIndexInsert.close();
            }

            if (preparedStatementDcIndexUpdate != null) {
                preparedStatementDcIndexUpdate.close();
            }

            if (con != null) {
                con.close();
            }
        }

    }

    private void deleteRedundantRecords() throws Exception {
        PreparedStatement ps = null;

        try {
            for (Iterator<String> it = dcIds.keySet().iterator(); it.hasNext();) {
                String refCd = it.next();
                Integer dcId = dcIds.get(refCd);

                if (!xmlRefcds.contains(refCd)) {
                    int idDc = dcId.intValue();

                    ps = con.prepareStatement(
                    "DELETE FROM dc_index WHERE ID_DC = ?");
                    ps.setInt(1, idDc);
                    ps.executeUpdate();
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private int getMaxId(String query) throws ParseException {
        String maxId = sqlUtilities.ExecuteSQL(query);
        int maxIdInt = 0;

        if (maxId != null && maxId.length() > 0) {
            maxIdInt = new Integer(maxId).intValue();
        }

        return maxIdInt;
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
                int dcId = rset.getInt("ID_DC");
                ret.put(dcId + "", dcId);
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

    private String formatYear(String input) {
        String ret = null;

        if (input == null || input.length() != 4) {
            return null;
        }

        try {
            int x = Integer.parseInt(input);

            ret = new Integer(x).toString();
        } catch (NumberFormatException nFE) {
            return null;
        }
        return ret;
    }
}
