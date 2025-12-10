package ro.finsiel.eunis.dataimport.parsers;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 */
public class ImportParser extends DefaultHandler {

    HashMap<String, Integer> positions;
    private String filePath;

    private PreparedStatement preparedStatement;
    private Connection con;

    private StringBuffer buf;
    private int counter = 0;
    private int numberOfColumns = 0;

    public ImportParser() {
        buf = new StringBuffer();
        positions = new HashMap<String, Integer>();
    }

    private void parseDocument() throws SAXException {

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            File xmlFile = new File(filePath);
            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            // parse the file and also register this class for call backs
            sp.parse(xmlFile, this);
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
        if (qName.equalsIgnoreCase("ROW")) {
            try {
                for (int i = 1; i <= numberOfColumns; i++) {
                    preparedStatement.setString(i, "NULL");
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        buf.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("ROW")) {
                endRow();
            } else if (!qName.equalsIgnoreCase("ROWSET")) {
                endColumn(qName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private void endRow() throws SQLException {
        counter++;
        preparedStatement.addBatch();
        if (counter % 10000 == 0) {
            preparedStatement.executeBatch();
            preparedStatement.clearParameters();
        }
    }

    private void endColumn(String colName) throws SQLException {
        Integer position = positions.get(colName);

        if (position != null) {
            preparedStatement.setString(position.intValue(),
                    buf.toString().trim());
        }
    }

    public void execute(String filePath, String table) throws Exception {

        this.filePath = filePath;

        Statement st = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table + " limit 1");
            ResultSetMetaData rsMeta = rs.getMetaData();

            StringBuffer namesList = new StringBuffer();
            StringBuffer qList = new StringBuffer();
            ArrayList<String> mysqlColumnNames = new ArrayList<String>();

            this.numberOfColumns = rsMeta.getColumnCount();
            for (int x = 1; x <= numberOfColumns; x++) {
                String columnName = rsMeta.getColumnName(x);

                mysqlColumnNames.add(columnName);
                namesList.append("`").append(columnName).append("`");
                qList.append("?");
                positions.put(columnName, x);
                if (x < numberOfColumns) {
                    namesList.append(",");
                    qList.append(",");
                }
            }

            rs.close();
            st.close();

            StringBuffer query = new StringBuffer();

            query.append("INSERT INTO ").append(table).append(" ( ").append(namesList).append(" ) values ( ").append(qList).append(
                    " ) ");

            this.preparedStatement = con.prepareStatement(query.toString());

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
}
