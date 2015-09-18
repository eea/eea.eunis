package ro.finsiel.eunis.dataimport.legal;

import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.SQLUtilities;
import ro.finsiel.eunis.utilities.TableColumns;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Main class for importer
 */
public class SpeciesReportsImporter {

    private boolean debug = false;

    private ExcelReader excelReader;
    private SQLUtilities sqlUtilities;

    private static final String ID_GEOSCOPE_EU = "80";
    private static final String ID_GEOSCOPE_WORLD = "263";
    private static final String ID_GEOSCOPE_EU27 = "287";
    private static final String ID_GEOSCOPE_EU25 = "67";

    private static Map<String, String> habitatsMap = new HashMap<String, String>();
    private static Map<String, String> birdsMap = new HashMap<String, String>();
    private static Map<String, String> bernMap = new HashMap<String, String>();
    private static Map<String, String> bonnMap = new HashMap<String, String>();
    private static Map<String, String> citesMap = new HashMap<String, String>();
    private static Map<String, String> euTradeMap = new HashMap<String, String>();
    private static Map<String, String> spaMap = new HashMap<String, String>();

    private static final String EMERALD_R6 = "2443";
    private static final String AEWA = "2447";
    private static final String EUROBATS = "1804";
    private static final String ACCOBAMS = "1802";
    private static final String ASCOBANS = "1803";
    private static final String WADDEN = "2451";
    private static final String OSPAR = "1832"; // this is generic OSPAR annex V, there are more annexes but none Annex I
    private static final String HELCOM = "2455";
    private static final String RED_LIST = "2408";   // for reports table
    private static final String RED_LIST_CATEGORIES = "2407"; // from conservation_status table

    static {
        habitatsMap.put("I", "2324");
        habitatsMap.put("II", "2325");
        habitatsMap.put("IV", "2326");
        habitatsMap.put("V", "2327");

        birdsMap.put("I", "2441");
        birdsMap.put("II", "2456");    // annex II
        birdsMap.put("II A", "2456");
        birdsMap.put("II B", "2456");
        birdsMap.put("III", "2457");   // annex III
        birdsMap.put("III A", "2457");
        birdsMap.put("III B", "2457");

        // the Birds Directive
        birdsMap.put("Other", "2440");

        bernMap.put("I", "1565");
        bernMap.put("II", "1566");
        bernMap.put("III", "1567");

        bonnMap.put("I", "1799");
        bonnMap.put("II", "1800");

        citesMap.put("I", "1791");
        citesMap.put("II", "1792");
        citesMap.put("III", "1793");

        euTradeMap.put("A", "2445");
        euTradeMap.put("B", "2446");
        euTradeMap.put("C", "2458");
        euTradeMap.put("D", "2459");

        spaMap.put("II", "1818");
        spaMap.put("III", "1819");
    }

    private PreparedStatement deleteReportTypePs;
    private PreparedStatement deleteReportAttributesPs;
    private PreparedStatement deleteReportPs;
    private PreparedStatement selectToDeletePs;
    private PreparedStatement insertReportAttributePs;
    private PreparedStatement insertReportTypePs;
    private PreparedStatement insertReportPs;
    private PreparedStatement insertLegalStatusPs;
    private PreparedStatement selectToDeleteRedListPs;


    Map<String, Integer> conservationStatusCode;

    Connection connection;

    int lastReportTypeId;
    int lastLegalStatusId;
    int lastIdReportAttributesId;


    private int notFoundCount;
    private int importedCount;
    private int foundBySynonyms;

    /**
     * Runs the import
     * @param args Main arguments: list of Excel files
     */
    public static void main(String[] args){

        if(args.length == 0){
            System.out.println("Please provide the Excel files containing the data.");
            return;
        }

        boolean allFilesFound = true;
        for(String fileName : args){
            File file = new File(fileName);
            if(!file.exists()){
                System.out.println("The file " + fileName + " could not be found!");
                allFilesFound = false;
            }
        }

        if(!allFilesFound) {
            return;
        }

        ResourceBundle props = ResourceBundle.getBundle("jrf");
        String dbDriver = props.getString("mysql.driver");
        String dbUrl = props.getString("mysql.url");
        String dbUser = props.getString("mysql.user");
        String dbPass = props.getString("mysql.password");

        SQLUtilities sqlUtilities = new SQLUtilities();

        sqlUtilities.Init();

        SpeciesReportsImporter vi = new SpeciesReportsImporter(sqlUtilities);

        vi.importFiles(args);

        vi.close();
    }

    /**
     * Constructor for the importer
     * @param sqlUtilities SQL utilities object
     */
    public SpeciesReportsImporter(SQLUtilities sqlUtilities){

        this.sqlUtilities = sqlUtilities;

        try{
            connection = sqlUtilities.getConnection();
            connection.setAutoCommit(false);

            String deleteReportType = "DELETE FROM chm62edt_report_type WHERE id_report_type=? AND id_lookup=? AND lookup_type=?";
            String deleteReportAttributes = "DELETE FROM chm62edt_report_attributes WHERE id_report_attributes=?";
            String deleteReport = "DELETE FROM chm62edt_reports WHERE id_nature_object=? AND id_report_type=? AND id_report_attributes=?";
            String selectToDelete = "SELECT DISTINCT r.id_report_type, id_lookup, id_report_attributes " +
                    "FROM chm62edt_reports r, chm62edt_report_type rt " +
                    "WHERE r.ID_REPORT_TYPE=rt.id_report_type AND lookup_type='LEGAL_STATUS' AND id_nature_object=?";

            String selectToDeleteRedList = "SELECT DISTINCT r.id_report_type, id_lookup, id_report_attributes " +
                    "FROM chm62edt_reports r, chm62edt_report_type rt " +
                    "WHERE r.ID_REPORT_TYPE=rt.id_report_type AND lookup_type='CONSERVATION_STATUS' AND id_nature_object=? AND id_geoscope=?";

            deleteReportTypePs = connection.prepareStatement(deleteReportType);
            deleteReportAttributesPs = connection.prepareStatement(deleteReportAttributes);
            deleteReportPs = connection.prepareStatement(deleteReport);

            insertReportAttributePs = connection.prepareStatement("INSERT INTO chm62edt_report_attributes VALUES(?,?,?,?)");
            insertReportTypePs = connection.prepareStatement("INSERT INTO chm62edt_report_type VALUES(?,?,?)");
            insertReportPs = connection.prepareStatement("INSERT INTO chm62edt_reports VALUES(?,?,?,?,?,?)");
            insertLegalStatusPs = connection.prepareStatement("INSERT INTO chm62edt_legal_status VALUES(?,?,?,?,?)");

            // prepared statement using another connection!
            selectToDeletePs = sqlUtilities.getConnection().prepareStatement(selectToDelete);
            selectToDeleteRedListPs = sqlUtilities.getConnection().prepareStatement(selectToDeleteRedList);

            // get the max ids so we can insert over, as the tables don't have autoincrement
            String maxLegal = sqlUtilities.ExecuteSQL("select max(id_legal_status) from chm62edt_legal_status");
            if(maxLegal == null) maxLegal = "0";
            lastLegalStatusId = new Integer(maxLegal) + 1;
            lastReportTypeId = new Integer(sqlUtilities.ExecuteSQL("select max(id_report_type) from chm62edt_report_type")) + 1;
            String maxReport = sqlUtilities.ExecuteSQL("select max(id_report_attributes) from chm62edt_report_attributes");
            if(maxReport == null) maxReport = "0";
            lastIdReportAttributesId = new Integer(maxReport) + 1;

            if(debug) System.out.println("Last IDs: id_legal_status=" + lastLegalStatusId + "  id_report_type=" + lastReportTypeId + "  id_report_attributes="  + lastIdReportAttributesId);

            // populate the red list codes
            populateRedListCodes();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Import an Excel file
     * @param excelFiles Array of files
     */
    public void importFiles(String[] excelFiles){
        try {

            int totalImportedCount=0, totalFoundBySynonyms=0, totalNotFound=0;
            for(String excelFile : excelFiles){
                excelReader = new ExcelReader(excelFile);

                System.out.println("File " + excelFile + " read, found " + excelReader.getSpeciesRows().size() + " species and " + excelReader.getRestrictionsRows().size() + " restrictions");
                notFoundCount = 0; importedCount = 0; foundBySynonyms = 0;

                List<SpeciesRow> rows = importAllSpecies();
                // write the result
                ExcelWriter excelWriter = new ExcelWriter(excelFile, ExcelReader.FileType.VERTEBRATES, rows);
                excelWriter.writeToFile();

                totalImportedCount += importedCount;
                totalFoundBySynonyms += foundBySynonyms;
                totalNotFound += notFoundCount;
                System.out.println("Import for " + excelFile + " finished, " + importedCount + " species imported, " + foundBySynonyms + " identified as synonyms, "+ notFoundCount + " species not found.");
            }

            System.out.println("Full import finished, " + totalImportedCount + " species imported, " + totalFoundBySynonyms + " identified as synonyms, "+ totalNotFound + " species not found.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Import all the species in the list
     */
    private List<SpeciesRow> importAllSpecies(){
        List<SpeciesRow> rows = excelReader.getSpeciesRows();
        // identify doubles
        for(SpeciesRow sr : rows) {
            int count=0;
            for(SpeciesRow sr2 : rows) {
                if(sr.getSpeciesName().equalsIgnoreCase(sr2.getSpeciesName()) && sr2.getExcelRow()>sr.getExcelRow()){
                    count++;
                    if(count>0){
                        System.out.println("WARNING: Species " + sr.getSpeciesName() + " (row " + sr.getExcelRow() + ") doubled by row " + sr2.getExcelRow());
                        sr.appendResult( "Doubled by row " + sr2.getExcelRow());
                    }
                }
            }
        }

        for(SpeciesRow sr : rows){
            importSpecies(sr);
        }

        return rows;
    }

    /**
     * Import a species
     * @param speciesRow
     */
    private void importSpecies(SpeciesRow speciesRow){
        identifySpecies(speciesRow);

        if(speciesRow.getIdSpecies() == null) {
            notFoundCount++;
            System.out.println("WARNING: Species '" + speciesRow.getSpeciesName() + "' (Excel row " + speciesRow.getExcelRow() + ") not found!");
            speciesRow.appendResult("WARNING: species not found");
        } else {
            try {
                System.out.println("Species '" + speciesRow.getSpeciesName() + "' " +
                        "(id_species=" + speciesRow.getIdSpecies() + ", " +
                        "id_nature_object=" + speciesRow.getIdNatureObject() + ", " +
                        "Excel row=" + speciesRow.getExcelRow() +")" +
                        (speciesRow.getSpeciesName().equals(speciesRow.getDatabaseName())?"":" synonym of " + speciesRow.getDatabaseName())
                        );

                cleanExistingData(speciesRow);
                importNewData(speciesRow);

                connection.commit();
                importedCount++;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Find the IDs for the species
     * @param speciesRow
     */
    private void identifySpecies(SpeciesRow speciesRow){
        String idLink = null;

        List speciesList = selectSpeciesByName(speciesRow.getSpeciesName());

        idLink = populateSpeciesIds(speciesRow, speciesList);

        if(speciesRow.getIdSpecies() == null && idLink != null) {
            // extract the parent species
            List speciesFullList = selectSpeciesById(idLink);
            populateSpeciesIds(speciesRow, speciesFullList);
            foundBySynonyms++;

            speciesRow.appendResult("Synonym of " + speciesRow.getDatabaseName());
        }
    }

    private String speciesColumns = "id_species, id_nature_object, valid_name, id_species_link, type_related_species, scientific_name, id_group_species";
    private int speciesColumnsNumber = 7;

    private List selectSpeciesByName(String scientificName) {
        List speciesList = sqlUtilities.ExecuteSQLReturnList("select " + speciesColumns + " from chm62edt_species where scientific_name='"+ scientificName + "'", speciesColumnsNumber);
        return speciesList;
    }

    private List selectSpeciesById(String idSpecies) {
        List speciesList = sqlUtilities.ExecuteSQLReturnList("select " + speciesColumns + " from chm62edt_species where id_species='"+ idSpecies + "'", speciesColumnsNumber);
        return speciesList;
    }

    /**
     * Populate the species IDs
     * @param speciesRow
     * @param queryResults
     * @return A link (species) ID if the identified species is a synonym
     */
    private String populateSpeciesIds(SpeciesRow speciesRow, List queryResults){
        String idLink = null;

        for(Object o : queryResults){
            TableColumns l2 = (TableColumns)o;

            if(l2.getColumnsValues().get(2).toString().equals("1")){
                // is valid name
                speciesRow.setIdSpecies(l2.getColumnsValues().get(0).toString());
                speciesRow.setIdNatureObject(l2.getColumnsValues().get(1).toString());
                speciesRow.setDatabaseName(l2.getColumnsValues().get(5).toString());
                speciesRow.setIdGroup(l2.getColumnsValues().get(6).toString());
            } else {
                // extract the valid species link from the synonym
                if( l2.getColumnsValues().get(4).toString().startsWith("Synonym")) {
                    idLink = l2.getColumnsValues().get(3).toString();
                }
            }
        }
        return idLink;
    }

    /**
     * Delete the reports associated with the species
     * @param speciesRow
     * @throws SQLException
     */
    private void cleanExistingData(SpeciesRow speciesRow) throws SQLException {

        selectToDeletePs.setString(1, speciesRow.getIdNatureObject());

        ResultSet rs = selectToDeletePs.executeQuery();
        int del = 0;
        while (rs.next()){
            deleteReportAttributesPs.setString(1, rs.getString(3));
            del += deleteReportAttributesPs.executeUpdate();

            deleteReportTypePs.setString(1, rs.getString(1));
            deleteReportTypePs.setString(2, rs.getString(2));
            deleteReportTypePs.setString(3, "LEGAL_STATUS");
            del += deleteReportTypePs.executeUpdate();

            deleteReportPs.setString(1, speciesRow.getIdNatureObject());
            deleteReportPs.setString(2, rs.getString(1));
            deleteReportPs.setString(3, rs.getString(3));
            del += deleteReportPs.executeUpdate();
        }
        if(debug) System.out.println(" deleted " + del + " records");
        rs.close();
    }

    /**
     * Populate the reports
     * @param speciesRow
     */
    private void importNewData(SpeciesRow speciesRow){

        multipleInsertReport("Habitats D", speciesRow.getHabitatsDAnnex(), habitatsMap, ID_GEOSCOPE_EU, "HD", speciesRow.getHabitatsName(), speciesRow);
        if(speciesRow.getIdGroup().equals("5") && speciesRow.getBirdsDAnnex().length == 0) {
            // bird species not mentioned in Birds Directive annex, has to be associated with the Birds Directive directly
            speciesRow.setBirdsD("Other");
            System.out.println("Bird species added in Birds Directive");
        }
        multipleInsertReport("Birds D", speciesRow.getBirdsDAnnex(), birdsMap, ID_GEOSCOPE_EU, null, speciesRow.getBirdsName(), speciesRow);
        multipleInsertReport("Bern Convention", speciesRow.getBernConventionAnnex(), bernMap, ID_GEOSCOPE_EU, "Bern", speciesRow.getBernName(), speciesRow);
        singleInsertReport("Emerald Network R6", speciesRow.getEmeraldR6(), "I", EMERALD_R6, ID_GEOSCOPE_EU, "Emerald R. 6", speciesRow.getEmeraldName(), speciesRow);
        multipleInsertReport("Bonn Convention", speciesRow.getBonnConventionAnnex(), bonnMap, ID_GEOSCOPE_WORLD, "Bonn", speciesRow.getBonnName(), speciesRow);
        multipleInsertReport("CITES", speciesRow.getCitesAnnex(), citesMap, ID_GEOSCOPE_WORLD, null, speciesRow.getCitesName(), speciesRow);
        multipleInsertReport("EU Trade", speciesRow.getEuTradeAnnex(), euTradeMap, ID_GEOSCOPE_EU, null, speciesRow.getEuTradeName(), speciesRow);
        singleInsertReport("AEWA", speciesRow.getAewa(), "II", AEWA, ID_GEOSCOPE_WORLD, null, speciesRow.getAewaName(), speciesRow);
        singleInsertReport("EuroBats", speciesRow.getEurobats(), "I", EUROBATS, ID_GEOSCOPE_EU, null, speciesRow);
        singleInsertReport("ACCOBAMS", speciesRow.getAccobams(), "I", ACCOBAMS, ID_GEOSCOPE_WORLD, null, speciesRow);
        singleInsertReport("ASCOBANS", speciesRow.getAscobans(), "Yes", ASCOBANS, ID_GEOSCOPE_WORLD, null, speciesRow);
        singleInsertReport("Wadden Sea Seals", speciesRow.getWadden(), "Yes", WADDEN, ID_GEOSCOPE_EU, null, speciesRow);
        multipleInsertReport("Barcelona SPA", speciesRow.getSpaAnnex(), spaMap, ID_GEOSCOPE_EU, null, speciesRow.getSpaName(), speciesRow);
        singleInsertReport("OSPAR", speciesRow.getOspar(), "I", OSPAR, ID_GEOSCOPE_EU, null, speciesRow.getOsparName(), speciesRow);
        singleInsertReport("HELCOM", speciesRow.getHelcom(), "A", HELCOM, ID_GEOSCOPE_EU, null, speciesRow);

        // EU report
        insertRedListReport(ID_GEOSCOPE_EU, speciesRow.getIdNatureObject(), speciesRow.getRedList(), speciesRow.getRedListName(), speciesRow);
        if(!Utilities.isEmptyString(speciesRow.getRedListEU27())) {
            insertRedListReport(ID_GEOSCOPE_EU27, speciesRow.getIdNatureObject(), speciesRow.getRedListEU27(), speciesRow.getRedListEU27Name(), speciesRow);
        }
    }

    /**
     * Insert a red list record for the given species in the given geoscope
     * @param idGeoscope
     * @param speciesRow
     */
    private void insertRedListReport(String idGeoscope, String idNatureObject, String redList, String redListName, SpeciesRow speciesRow){

        // clean the EU Red List first
        try {
            selectToDeleteRedListPs.setString(1, idNatureObject);
            selectToDeleteRedListPs.setString(2, idGeoscope);

            ResultSet rs = selectToDeleteRedListPs.executeQuery();
            int del = 0;
            while (rs.next()){
                deleteReportAttributesPs.setString(1, rs.getString(3));
                del += deleteReportAttributesPs.executeUpdate();

                deleteReportTypePs.setString(1, rs.getString(1));
                deleteReportTypePs.setString(2, rs.getString(2));
                deleteReportTypePs.setString(3, "CONSERVATION_STATUS");
                del += deleteReportTypePs.executeUpdate();

                deleteReportPs.setString(1, idNatureObject);
                deleteReportPs.setString(2, rs.getString(1));
                deleteReportPs.setString(3, rs.getString(3));
                del += deleteReportPs.executeUpdate();
            }
            if(debug) System.out.println(" deleted " + del + " EU Red List records");
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        if(!redList.isEmpty()) {
            try {
                Integer idConservationStatus = conservationStatusCode.get(redList);
                if(idConservationStatus != null) {

                    int idReportType = insertReportType(idConservationStatus, "CONSERVATION_STATUS");
                    int idReportAttributes = insertRedListReportAttribute(redListName);

                    insertReport(idNatureObject, RED_LIST, idGeoscope, idReportAttributes, idReportType);

                    if(debug) System.out.println(" Inserted conservation status code " + redList);
                } else {
                    System.out.println("WARNING: Red List code " + redList + " not identified");
                    speciesRow.appendResult("Red list code not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Wrapper for the singleInsertReport without name in document
     */
    private void singleInsertReport(String name, String annex, String expectedValue, String idDc, String geoscope, String restrictionPrefix, SpeciesRow speciesRow){
        singleInsertReport(name, annex, expectedValue, idDc, geoscope, restrictionPrefix, "", speciesRow);
    }

    /**
     * Insert the report data for a single-value (annex) report
     * @param name
     * @param annex
     * @param expectedValue
     * @param idDc
     * @param geoscope
     * @param restrictionPrefix
     * @param speciesRow
     */
    private void singleInsertReport(String name, String annex, String expectedValue, String idDc, String geoscope, String restrictionPrefix, String nameInDocument, SpeciesRow speciesRow){
        if(annex.equals(expectedValue)){

            RestrictionsRow restriction = null;
            if(restrictionPrefix!= null) {
                restriction = speciesRow.getRestrictionsMap().get(restrictionPrefix);
            }

            String restrictionText = null;
            int priority = 0;

            if(restriction != null){
                restrictionText = restriction.getRestriction();
                priority = restriction.getPriority();
            }

            insertLegalStatusReport(speciesRow.getIdSpecies(), speciesRow.getIdNatureObject(), idDc, geoscope, restrictionText, priority, annex, nameInDocument);
        } else {
            if(!annex.isEmpty()) {
                System.out.println("WARNING: for species '" + speciesRow.getSpeciesName() + "' the " + name + " Annex " + annex + " was not found!");
                speciesRow.appendResult(name + " annex " + annex + " not found");
            }
        }
    }

    /**
     * Wrapper for multipleInsertReport without nameInDocument
     */
    private void multipleInsertReport(String name, String[] annexes, Map<String, String> values, String geoscope, String restrictionPrefix, SpeciesRow speciesRow){
        multipleInsertReport(name, annexes,values, geoscope, restrictionPrefix, "", speciesRow);
    }

    /**
     * Insert the report data for a multiple-annex report
     * @param name
     * @param annexes
     * @param values
     * @param geoscope
     * @param restrictionPrefix
     * @param speciesRow
     */
    private void multipleInsertReport(String name, String[] annexes, Map<String, String> values, String geoscope, String restrictionPrefix, String nameInDocument, SpeciesRow speciesRow){

        for(String annex : annexes){
            String idDc = values.get(annex);
            RestrictionsRow restriction = null;
            if(restrictionPrefix!= null) {
                restriction = speciesRow.getRestrictionsMap().get(restrictionPrefix + " " + annex);
            }

            String restrictionText = null;
            int priority = 0;

            if(restriction != null){
                restrictionText = restriction.getRestriction();
                priority = restriction.getPriority();
            }

            if(idDc != null) {
                insertLegalStatusReport(speciesRow.getIdSpecies(), speciesRow.getIdNatureObject(), idDc, geoscope, restrictionText, priority, annex, nameInDocument);
            } else {
                System.out.println("WARNING: for species '" + speciesRow.getSpeciesName() + "' the " + name + " Annex " + annex + " was not found!");
                speciesRow.appendResult( name + " annex " + annex + " not found");

            }
        }
    }


    /**
     * Insert legal status data
     * @param annex
     * @param priority
     * @param comment
     * @return
     */
    private int insertLegalStatus(String annex, int priority, String comment) {
        int idLegalStatus = lastLegalStatusId++;
        try{
            insertLegalStatusPs.setInt(1, idLegalStatus);
            insertLegalStatusPs.setString(2, annex);
            insertLegalStatusPs.setInt(3, priority);
            insertLegalStatusPs.setString(4, comment);
            insertLegalStatusPs.setString(5, null);
            insertLegalStatusPs.executeUpdate();
            insertLegalStatusPs.clearParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return idLegalStatus;
    }

    /**
     * Insert report data including report attributes
     * @param idSpecies
     * @param idNatureObject
     * @param idDc
     * @param idGeoscope
     * @param restrictionText
     * @param priority
     * @param annex
     */
    private void insertLegalStatusReport(String idSpecies, String idNatureObject, String idDc, String idGeoscope, String restrictionText, int priority, String annex, String nameInDocument) {
        try {
            int idReportAttributes = insertLegalReportAttribute(idDc, idSpecies, nameInDocument);

            int idLegalStatus = -1;
            if(restrictionText != null || priority == 1){
                idLegalStatus = insertLegalStatus(annex, priority, restrictionText);
            }

            int idReportType = insertReportType(idLegalStatus, "LEGAL_STATUS");

            insertReport(idNatureObject, idDc, idGeoscope, idReportAttributes, idReportType);

            // check that nameInDocument is synonym
            if(isSynonymName(nameInDocument)){
                System.out.print(" Searching for synonyms: " + nameInDocument + " - ");

                // find in DB
                String[] names = nameInDocument.split(";");
                for(String name : names) {
                    if(!name.trim().isEmpty()) {
                        List l = selectSpeciesByName(name.trim());
                        if(debug) System.out.println(name.trim());
                        // check it's a synonym
                        if (l.size() == 0) {
                            System.out.println(" WARNING: Not found " + name);

                            // todo add it ?
                        } else if (l.size() == 1) {
                            System.out.print("  Found ");
                            // check it's synonym or original species

                            String idSpeciesSynonym = ((TableColumns)l.get(0)).getColumnsValues().get(0).toString();
                            String validName = ((TableColumns)l.get(0)).getColumnsValues().get(2).toString();
                            String link = ((TableColumns)l.get(0)).getColumnsValues().get(3).toString();
                            String related = ((TableColumns)l.get(0)).getColumnsValues().get(4).toString();

                            System.out.println(((TableColumns)l.get(0)).getColumnsValues().get(5).toString() + " (" + related + ")");

                            if(validName.equals("0") && (related.equalsIgnoreCase("synonym") || related.equalsIgnoreCase("Subspecies")) && link.equalsIgnoreCase(idSpecies)) {
                                if(debug) System.out.println(" Synonym ok");
                            } else if(related.equalsIgnoreCase("misspelling")){
                                if(debug) System.out.println(" Misspelling ok");
                            } else if(idSpeciesSynonym.trim().equalsIgnoreCase(idSpecies.trim())) {
                                if(debug) System.out.println(" Same species! " + idSpeciesSynonym.trim());
                            } else {
                                System.out.println("WARNING: Incorrect synonym: " + name.trim() + " idSpecies=" + idSpeciesSynonym +" valid name=" + validName + " link=" + link + " related=" + related);
                            }

                        } else if (l.size() > 1) {
                           System.out.println(" Found too many (" + l.size() + ")");
                            // todo still check
                        }
                    }
                }
            }

            if(debug) System.out.println(" Inserted legal report with idDc=" + idDc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert report data
     * @param idNatureObject
     * @param idDc
     * @param idGeoscope
     * @param idReportAttributes
     * @param idReportType
     */
    private void insertReport(String idNatureObject, String idDc, String idGeoscope, int idReportAttributes, int idReportType) throws SQLException {
        insertReportPs.setString(1, idNatureObject);
        insertReportPs.setString(2, idDc);
        insertReportPs.setString(3, idGeoscope);
        insertReportPs.setString(4, "-1");
        insertReportPs.setInt(5, idReportType);
        insertReportPs.setInt(6, idReportAttributes);
        insertReportPs.executeUpdate();
        insertReportPs.clearParameters();
    }

    /**
     * Insert report type data
     * @param idLookup
     * @return
     * @throws SQLException
     */
    private int insertReportType(int idLookup, String lookupType) throws SQLException {
        int idReportType = lastReportTypeId++;

        insertReportTypePs.setInt(1, idReportType);
        insertReportTypePs.setInt(2, idLookup);
        insertReportTypePs.setString(3, lookupType);
        insertReportTypePs.executeUpdate();
        insertReportTypePs.clearParameters();

        return idReportType;
    }

    /**
     * Insert report attributes data
     * @param idDc
     * @param speciesCode
     * @param nameInDocument The name in the legal document; can be eplty if none specified
     * @return
     * @throws SQLException
     */
    private int insertLegalReportAttribute(String idDc, String speciesCode, String nameInDocument) throws SQLException {
        int idReportAttributes = lastIdReportAttributesId++;

        insertReportAttribute(idReportAttributes, "ID_DC", "NUMBER", idDc);
        insertReportAttribute(idReportAttributes, "SPECIES_CODE", "TEXT", speciesCode);
        if(!nameInDocument.isEmpty()){
            insertReportAttribute(idReportAttributes, "NAME_IN_DOCUMENT", "TEXT", nameInDocument);
        }

        return idReportAttributes;
    }

    /**
     * Insert red list attribute for NAME_IN_DOUCMENT; only written if the name is not empty
     * @param nameInDocument The name of the species in the Red List document
     * @return The ID of the inserted record; if the nameInDocument parameter is null returns -1
     * @throws SQLException
     */
    private int insertRedListReportAttribute(String nameInDocument) throws SQLException {
        int idReportAttributes = -1;
        if(!nameInDocument.isEmpty()){
            idReportAttributes = lastIdReportAttributesId++;
            insertReportAttribute(idReportAttributes, "NAME_IN_DOCUMENT", "TEXT", nameInDocument);
        }
        return idReportAttributes;
    }

    /**
     * Inserts a generic report_attributes record
     * @param idReportAttributes The record ID
     * @param name The record name
     * @param type The record type
     * @param value The record value
     * @throws SQLException
     */
    private void insertReportAttribute(int idReportAttributes, String name, String type, String value) throws SQLException {
        insertReportAttributePs.setInt(1, idReportAttributes);
        insertReportAttributePs.setString(2, name);
        insertReportAttributePs.setString(3, type);
        insertReportAttributePs.setString(4, value);
        insertReportAttributePs.executeUpdate();
        insertReportAttributePs.clearParameters();
    }

    /**
     * Reads the red list codes and populates the conservation ID map (conservationStatusCode)
     */
    private void populateRedListCodes(){
        conservationStatusCode = new HashMap<String, Integer>();

        List conservationList = sqlUtilities.ExecuteSQLReturnList("select id_conservation_status, code from chm62edt_conservation_status where id_dc='"+ RED_LIST_CATEGORIES + "'", 2);

        for(Object o : conservationList){
            TableColumns l2 = (TableColumns)o;
            conservationStatusCode.put(l2.getColumnsValues().get(1).toString(), new Integer(l2.getColumnsValues().get(0).toString()));
        }

        // fix for CR/PE (Critically Endangered, Possibly Extinct) - should be treated as Critically Endangered (CR)
        conservationStatusCode.put("CR/PE", conservationStatusCode.get("CR"));
        conservationStatusCode.put("CR (PE)", conservationStatusCode.get("CR"));
    }

    /**
     * Synonym as defined in #19506
     * @param name
     * @return
     */
    private boolean isSynonymName(String name){
        if(name == null) return false;
        return !(name.isEmpty() || name.contains("species") || name.contains("spp.") || name.contains("sp. plur."));
    }


    /**
     * Closes all resources
     */
    private void close() {
        try {
            deleteReportTypePs.close();
            deleteReportAttributesPs.close();
            deleteReportPs.close();
            selectToDeletePs.close();
            insertReportAttributePs.close();
            insertReportTypePs.close();
            insertReportPs.close();
            insertLegalStatusPs.close();
            selectToDeleteRedListPs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
