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
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ro.finsiel.eunis.utilities.SQLUtilities;

/**
 *
 */
public class RedListsImportParser extends DefaultHandler {

    private InputStream inputStream;

    private PreparedStatement preparedStatementReportType;
    private PreparedStatement preparedStatementReport;
    private PreparedStatement preparedStatementReportAttributes;

    private int counter = 0;
    private int maxReportTypeId = 0;
    private int maxReportAttributesId = 0;

    private int imported = 0;
    private List<String> notImported = new ArrayList<String>();
    private List<String> importedSpecies = new ArrayList<String>();
    private List<String> duplicateSpecies = new ArrayList<String>();

    private String scientificName;
    private String euCat;
    private String eu25Cat;
    private String eu27Cat;
    private String worldCat;
    private String notes;
    private String rationale;
    private String range;
    private String population;
    private String populationTrend;
    private String habitat;
    private String threats;
    private String conservationMeasures;
    private String assessors;
    private String iucnNumber;

    private String coverage;

    private boolean delete = false;

    private Connection con;

    private StringBuffer buf;
    private SQLUtilities sqlUtilities;
    private HashMap<String, String> conservationStatuses;
    private int euGeoscopeId = 0;
    private int eu25GeoscopeId = 0;
    private int eu27GeoscopeId = 0;
    private int worldGeoscopeId = 0;
    private Integer idDC;
    private Integer redlistCatIdDc;

    public RedListsImportParser(SQLUtilities sqlUtilities, Integer id_dc, boolean delete, Integer redlistCatIdDc) {
        this.sqlUtilities = sqlUtilities;
        this.idDC = id_dc;
        this.delete = delete;
        this.con = sqlUtilities.getConnection();
        this.redlistCatIdDc = redlistCatIdDc;
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

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        buf = new StringBuffer();
        coverage = attributes.getValue("coverage");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buf.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("Scientific_name")) {
                scientificName = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Notes")) {
                notes = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Category")) {
                if (coverage != null) {
                    if (coverage.equals("EU25")) {
                        eu25Cat = buf.toString().trim();
                    } else if (coverage.equals("EU27")) {
                        eu27Cat = buf.toString().trim();
                    } else if (coverage.equals("Europe")) {
                        euCat = buf.toString().trim();
                    } else if (coverage.equals("World")) {
                        worldCat = buf.toString().trim();
                    }
                }
                coverage = null;
            }
            if (qName.equalsIgnoreCase("Rationale")) {
                rationale = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Range")) {
                range = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Population")) {
                population = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Population_trend")) {
                populationTrend = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Habitat")) {
                habitat = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Threats")) {
                threats = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Conservation_measures")) {
                conservationMeasures = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("Assessors")) {
                assessors = buf.toString().trim();
            }
            if (qName.equalsIgnoreCase("IUCNNumber")) {
                iucnNumber = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("Row")) {

                List<String> natObIds = getNatureObjectId(scientificName, iucnNumber);
                boolean  newThreats = false;
                // The import record could match several species. In this case we add records for all of the matchings.
                // It is easier to detect duplicates than it is to discover a wrong association.
                for (String natObId : natObIds) {

                    boolean newThreat = false;
                    newThreat = prepareNewThreatReportStatements(natObId, euCat, euGeoscopeId, newThreat);
                    newThreat = prepareNewThreatReportStatements(natObId, eu25Cat, eu25GeoscopeId, newThreat);
                    newThreat = prepareNewThreatReportStatements(natObId, eu27Cat, eu27GeoscopeId, newThreat);
                    newThreat = prepareNewThreatReportStatements(natObId, worldCat, worldGeoscopeId, newThreat);

                    if (newThreat) {

                        imported++;
                        newThreats = true;

                        if (delete) {
                            deleteCurrentThreats(natObId);
                        } else {
                            deleteThreatsForThisSource(natObId);
                        }
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_NOTES", notes);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_RATIONALE", rationale);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_RANGE", range);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_POPULATION", population);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_POPULATION_TREND", populationTrend);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_HABITAT", habitat);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_THREATS", threats);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_CONSERVATION_MEASURES", conservationMeasures);
                        prepareReportAttributeStatements("EUROPEAN_RED_LIST_ASSESSORS", assessors);

                        if (!importedSpecies.contains(scientificName)){
                            importedSpecies.add(scientificName);
                        }else{
                            duplicateSpecies.add(scientificName);
                        }
                    }
                }
                if (!newThreats) {
                    notImported.add(scientificName);
                }
                if (counter != 0 && counter % 3000 == 0) {
                    preparedStatementReportType.executeBatch();
                    preparedStatementReportType.clearParameters();

                    preparedStatementReport.executeBatch();
                    preparedStatementReport.clearParameters();

                    preparedStatementReportAttributes.executeBatch();
                    preparedStatementReportAttributes.clearParameters();
                }

                scientificName = null;
                notes = null;
                euCat = null;
                eu25Cat = null;
                eu27Cat = null;
                worldCat = null;
                rationale = null;
                range = null;
                population = null;
                populationTrend = null;
                habitat = null;
                threats = null;
                conservationMeasures = null;
                assessors = null;
                iucnNumber = null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    /**
     * Prepare statements for new threat reports if coverage category and coverage Id exists.
     * @param natObId Nature Object Id.
     * @param coverageCat Coverage Category.
     * @param coverageId Coverage Id in database.
     * @param newThreat True, if any threat is already added.
     * @return True, if threat statements were added.
     * @throws Exception
     */
    private boolean prepareNewThreatReportStatements(String natObId, String coverageCat, int coverageId, boolean newThreatAdded)
    throws Exception {

        boolean newThreat = newThreatAdded;
        String csId = conservationStatuses.get(coverageCat);
        String geoId = new Integer(coverageId).toString();
        if (natObId != null && coverageCat != null && csId != null && idDC != null && geoId != null && !geoId.equals("0")) {
            maxReportTypeId++;

            if (!newThreat) {
                maxReportAttributesId++;
            }
            newThreat = true;

            preparedStatementReportType.setInt(1, maxReportTypeId);
            preparedStatementReportType.setString(2, csId);
            preparedStatementReportType.addBatch();

            preparedStatementReport.setString(1, natObId);
            preparedStatementReport.setInt(2, idDC.intValue());
            preparedStatementReport.setString(3, geoId);
            preparedStatementReport.setInt(4, maxReportTypeId);
            preparedStatementReport.setInt(5, maxReportAttributesId);
            preparedStatementReport.addBatch();

            counter++;
        }

        return newThreat;
    }
    /**
     * Prepare statements for report attributes.
     * @param attrName Report attribute name.
     * @param attrValue Report attribute value.
     * @throws SQLException Database error occurred.
     */
    private void prepareReportAttributeStatements(String attrName, String attrValue) throws SQLException{

        if (StringUtils.isNotBlank(attrName) && StringUtils.isNotBlank(attrValue)) {
            preparedStatementReportAttributes.setInt(1, maxReportAttributesId);
            preparedStatementReportAttributes.setString(2, attrName);
            preparedStatementReportAttributes.setString(3, attrValue);
            preparedStatementReportAttributes.addBatch();
        }
    }

    public void execute(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {

            conservationStatuses = getConservationStatuses();
            maxReportTypeId = getId("SELECT MAX(ID_REPORT_TYPE) FROM chm62edt_report_type");
            maxReportAttributesId = getId("SELECT MAX(ID_REPORT_ATTRIBUTES) FROM chm62edt_report_attributes");
            euGeoscopeId = getId("SELECT ID_GEOSCOPE FROM chm62edt_country WHERE EUNIS_AREA_CODE = 'EU'");
            eu25GeoscopeId = getId("SELECT ID_GEOSCOPE FROM chm62edt_country WHERE EUNIS_AREA_CODE = 'E25'");
            eu27GeoscopeId = getId("SELECT ID_GEOSCOPE FROM chm62edt_country WHERE EUNIS_AREA_CODE = 'E27'");
            worldGeoscopeId = getId("SELECT ID_GEOSCOPE FROM chm62edt_country WHERE EUNIS_AREA_CODE = 'WO'");

            String queryReportType =
                "INSERT INTO chm62edt_report_type (ID_REPORT_TYPE, ID_LOOKUP, LOOKUP_TYPE) VALUES (?,?,'CONSERVATION_STATUS')";

            this.preparedStatementReportType = con.prepareStatement(queryReportType);

            String queryReport =
                "INSERT INTO chm62edt_reports (ID_NATURE_OBJECT, ID_DC, ID_GEOSCOPE, ID_GEOSCOPE_LINK, ID_REPORT_TYPE, ID_REPORT_ATTRIBUTES) VALUES (?,?,?,-1,?,?)";

            this.preparedStatementReport = con.prepareStatement(queryReport);

            String queryReportAttributes =
                "INSERT INTO chm62edt_report_attributes (ID_REPORT_ATTRIBUTES, NAME, TYPE, VALUE) VALUES (?,?,'TEXT',?)";

            this.preparedStatementReportAttributes = con.prepareStatement(queryReportAttributes);

            con.setAutoCommit(false);
            parseDocument();
            if (!(counter % 3000 == 0)) {
                preparedStatementReportType.executeBatch();
                preparedStatementReportType.clearParameters();

                preparedStatementReport.executeBatch();
                preparedStatementReport.clearParameters();

                preparedStatementReportAttributes.executeBatch();
                preparedStatementReportAttributes.clearParameters();
            }
            con.commit();
        } catch (Exception e) {
            con.rollback();
            con.commit();
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (preparedStatementReportType != null) {
                preparedStatementReportType.close();
            }

            if (preparedStatementReport != null) {
                preparedStatementReport.close();
            }

            if (preparedStatementReportAttributes != null) {
                preparedStatementReportAttributes.close();
            }

            if (con != null) {
                con.close();
            }
        }

    }

    private int getId(String query) throws ParseException {
        String maxId = sqlUtilities.ExecuteSQL(query);
        int maxIdInt = 0;

        if (maxId != null && maxId.length() > 0) {
            maxIdInt = new Integer(maxId).intValue();
        }

        return maxIdInt;
    }

    private HashMap<String, String> getConservationStatuses() throws Exception {
        HashMap<String, String> ret = new HashMap<String, String>();

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            String query = "SELECT ID_CONSERVATION_STATUS, CODE FROM chm62edt_conservation_status WHERE ID_DC= ? ";

            stmt = con.prepareStatement(query);
            stmt.setInt(1, redlistCatIdDc.intValue());
            rset = stmt.executeQuery();
            while (rset.next()) {
                String code = rset.getString("CODE");
                String idCs = rset.getString("ID_CONSERVATION_STATUS");

                ret.put(code, idCs);
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
     * The method detects the species from database matching import record. The species is detected by IUCN number
     * and scientific name. The following logic is implemented:
     *  1. If there is an IUCN number in the import record, and there is a matching "sameSpeciesRedlist" attribute,
     *      then add the record to that species. If no species found, then continue with case 3.
     *  2. If there is no IUCN number in the import record, but there is a species that matches on scientific name,
     *      and it has a "sameSpeciesRedlist" attribute then this is the correct species
     *  3. If there is no IUCN number in the import record, and none of the species names that match in the database
     *      have a "sameSpeciesRedlist" attribute, then all the matching species get the redlist record.
     * It is easier to detect duplicates than it is to discover a wrong association.
     *
     * @param name Species scientific name
     * @param iucn IUCN number (null if not present in XML).
     * @return List of species Nature Object Ids.
     * @throws Exception Database exception occurs.
     */
    protected List<String> getNatureObjectId(String name, String iucn) throws Exception {

        List<String> result = new ArrayList<String>();
        String query = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            // 1. search by IUCN number and sameSpeciesRedlist attribute mapping
            if (StringUtils.isNotBlank(iucn)) {
                query =
                    "SELECT s.ID_NATURE_OBJECT FROM chm62edt_nature_object_attributes sa "
                    + "JOIN chm62edt_species s USING (ID_NATURE_OBJECT) "
                    + "WHERE sa.NAME='sameSpeciesRedlist' and sa.OBJECT= ?";
                stmt = con.prepareStatement(query);
                stmt.setString(1, iucn);
                rset = stmt.executeQuery();
                while (rset.next()) {
                    result.add(rset.getString("ID_NATURE_OBJECT"));
                }
            } else {
                // 2. if IUCN number is not available in import, search by scientific name where sameSpeciesRedlist attribute exists
                query =
                    "SELECT s.ID_NATURE_OBJECT FROM chm62edt_species s WHERE s.SCIENTIFIC_NAME = ? "
                    + "AND EXISTS (SELECT * FROM chm62edt_nature_object_attributes sa "
                    + "WHERE sa.NAME='sameSpeciesRedlist' and sa.ID_NATURE_OBJECT=s.ID_NATURE_OBJECT)";
                stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                rset = stmt.executeQuery();
                while (rset.next()) {
                    result.add(rset.getString("ID_NATURE_OBJECT"));
                }
            }
            // 3. get all species by scientific name
            if (result.size() == 0) {
                query =
                    "SELECT s.ID_NATURE_OBJECT FROM chm62edt_species s WHERE s.SCIENTIFIC_NAME = ? "
                    + "AND NOT EXISTS (SELECT * FROM chm62edt_nature_object_attributes sa "
                    + "WHERE sa.NAME='sameSpeciesRedlist' and sa.ID_NATURE_OBJECT=s.ID_NATURE_OBJECT)";
                stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                rset = stmt.executeQuery();
                while (rset.next()) {
                    result.add(rset.getString("ID_NATURE_OBJECT"));
                }
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
        return result;
    }

    private void deleteCurrentThreats(String natObjectId) throws Exception {
        PreparedStatement stmt = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;

        String deleteReportType = "DELETE FROM chm62edt_report_type WHERE ID_REPORT_TYPE = ?";
        String deleteReportAttributes = "DELETE FROM chm62edt_report_attributes WHERE ID_REPORT_ATTRIBUTES = ?";
        String deleteReports = "DELETE FROM chm62edt_reports WHERE ID_REPORT_TYPE = ? AND ID_NATURE_OBJECT = ?";

        ResultSet rset = null;

        try {
            ps1 = con.prepareStatement(deleteReportType);
            ps2 = con.prepareStatement(deleteReportAttributes);
            ps3 = con.prepareStatement(deleteReports);

            String query =
                "SELECT R.ID_REPORT_TYPE AS TYPE, R.ID_REPORT_ATTRIBUTES AS ATTRIBUTES "
                + "FROM chm62edt_reports AS R, chm62edt_report_type AS T "
                + "WHERE R.ID_NATURE_OBJECT = ? AND R.ID_REPORT_TYPE = T.ID_REPORT_TYPE AND T.LOOKUP_TYPE = ?";

            stmt = con.prepareStatement(query);
            stmt.setString(1, natObjectId);
            stmt.setString(2, "CONSERVATION_STATUS");
            rset = stmt.executeQuery();
            while (rset.next()) {
                String idReportType = rset.getString("TYPE");
                String idReportAttributes = rset.getString("ATTRIBUTES");

                if (idReportType != null) {
                    ps1.setString(1, idReportType);
                    ps1.addBatch();

                    ps3.setString(1, idReportType);
                    ps3.setString(2, natObjectId);
                    ps3.addBatch();
                }

                if (idReportAttributes != null && !idReportAttributes.equals("-1")) {
                    ps2.setString(1, idReportAttributes);
                    ps2.addBatch();
                }
            }
            ps1.executeBatch();
            ps1.clearParameters();

            ps2.executeBatch();
            ps2.clearParameters();

            ps3.executeBatch();
            ps3.clearParameters();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (ps1 != null) {
                ps1.close();
            }
            if (ps2 != null) {
                ps2.close();
            }
            if (ps3 != null) {
                ps3.close();
            }
            if (rset != null) {
                rset.close();
            }
        }
    }

    private void deleteThreatsForThisSource(String natObjectId) throws Exception {
        PreparedStatement stmt = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;

        String deleteReportType = "DELETE FROM chm62edt_report_type WHERE ID_REPORT_TYPE = ?";
        String deleteReportAttributes = "DELETE FROM chm62edt_report_attributes WHERE ID_REPORT_ATTRIBUTES = ?";
        String deleteReports = "DELETE FROM chm62edt_reports WHERE ID_DC = ? AND ID_REPORT_TYPE = ? AND ID_NATURE_OBJECT = ?";

        ResultSet rset = null;

        try {
            ps1 = con.prepareStatement(deleteReportType);
            ps2 = con.prepareStatement(deleteReportAttributes);
            ps3 = con.prepareStatement(deleteReports);

            String query =
                "SELECT R.ID_REPORT_TYPE AS TYPE, R.ID_REPORT_ATTRIBUTES AS ATTRIBUTES "
                + "FROM chm62edt_reports AS R, chm62edt_report_type AS T "
                + "WHERE R.ID_NATURE_OBJECT = ? AND R.ID_DC = ? AND R.ID_REPORT_TYPE = T.ID_REPORT_TYPE AND T.LOOKUP_TYPE = ?";

            stmt = con.prepareStatement(query);
            stmt.setString(1, natObjectId);
            stmt.setInt(2, idDC);
            stmt.setString(3, "CONSERVATION_STATUS");
            rset = stmt.executeQuery();
            while (rset.next()) {
                String idReportType = rset.getString("TYPE");
                String idReportAttributes = rset.getString("ATTRIBUTES");

                if (idReportType != null) {
                    ps1.setString(1, idReportType);
                    ps1.addBatch();

                    ps3.setInt(1, idDC);
                    ps3.setString(2, idReportType);
                    ps3.setString(3, natObjectId);
                    ps3.addBatch();
                }

                if (idReportAttributes != null && !idReportAttributes.equals("-1")) {
                    ps2.setString(1, idReportAttributes);
                    ps2.addBatch();
                }
            }
            ps1.executeBatch();
            ps1.clearParameters();

            ps2.executeBatch();
            ps2.clearParameters();

            ps3.executeBatch();
            ps3.clearParameters();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (ps1 != null) {
                ps1.close();
            }
            if (ps2 != null) {
                ps2.close();
            }
            if (ps3 != null) {
                ps3.close();
            }
            if (rset != null) {
                rset.close();
            }
        }
    }

    public int getImported() {
        return imported;
    }

    public List<String> getNotImported() {
        return notImported;
    }
    public List<String> getImportedSpecies() {
        return importedSpecies;
    }
    public List<String> getDuplicateSpecies() {
        return duplicateSpecies;
    }
}
