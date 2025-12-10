package ro.finsiel.eunis.dataimport.parsers;

import eionet.eunis.util.Constants;
import ro.finsiel.eunis.utilities.EunisUtil;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ro.finsiel.eunis.dataimport.parsers.CallbackSAXParser.SaxCallback;
import ro.finsiel.eunis.dataimport.parsers.CallbackSAXParser.Values;

/**
 * Implementation of Natura 2000 import using Callback SAX Parser
 */
public class Natura2000ParserCallbackV2 {

    private static final Map<String, String> designationsMapping = new HashMap<String, String>();
    private static final Map<String, String> speciesSourceCode = new HashMap<String, String>();

    static {
        designationsMapping.put("A", "IN08");
        designationsMapping.put("B", "IN09");
        designationsMapping.put("C", "IN12");
        designationsMapping.put("D", "IN08");
        designationsMapping.put("E", "IN09");
        designationsMapping.put("F", "IN08");
        designationsMapping.put("G", "IN09");
        designationsMapping.put("H", "IN08");
        designationsMapping.put("I", "IN09");
        designationsMapping.put("J", "IN08");
        designationsMapping.put("K", "IN09");

        // A = Amphibians, B = Birds, F = Fish, Fu = Fungi, I = Invertebrates,L = Lichens, M = Mammals, P = Plants, R = Reptiles
        // http://bd.eionet.europa.eu/activities/Natura_2000/reference_portal

        speciesSourceCode.put("A", Constants.N2000_SPECIES_GROUP_AMPHIBIANS);
        speciesSourceCode.put("B", Constants.N2000_SPECIES_GROUP_BIRD);
        speciesSourceCode.put("F", Constants.N2000_SPECIES_GROUP_FISHES);
        speciesSourceCode.put("Fu", Constants.N2000_SPECIES_GROUP_FUNGI );
        speciesSourceCode.put("I", Constants.N2000_SPECIES_GROUP_INVERT);
        speciesSourceCode.put("L", Constants.N2000_SPECIES_GROUP_LICHENS);
        speciesSourceCode.put("M", Constants.N2000_SPECIES_GROUP_MAMMAL);
        speciesSourceCode.put("P",  Constants.N2000_SPECIES_GROUP_PLANT);
        speciesSourceCode.put("R", Constants.N2000_SPECIES_GROUP_REPTILES);
    }

    private final SQLUtilities sqlUtilities;
    private Connection con;

    /**
     * Constructor
     * @param sqlUtilities DB access object
     */
    public Natura2000ParserCallbackV2(SQLUtilities sqlUtilities) {
        this.sqlUtilities = sqlUtilities;
        this.con = sqlUtilities.getConnection();
        this.errors = new ArrayList<String>();
    }

    private PreparedStatement preparedStatementNatObject;
    private PreparedStatement preparedStatementSiteInsert;
    private PreparedStatement preparedStatementNatObjectReportType;
    private PreparedStatement preparedStatementNatObjectGeoscope;
    private PreparedStatement preparedStatementReportAttribute;
    private PreparedStatement preparedStatementSiteSites;
    private PreparedStatement preparedStatementSiteAttribute;
    private PreparedStatement preparedStatementSiteRelatedDesignations;
    private PreparedStatement preparedStatementReportType;
    private PreparedStatement preparedStatementUpdateManager;

    private int maxNoIdInt = 0;
    private int maxReportAttributeId = 0;
    private int maxReportTypeId = 0;
    private int siteDescriptionHabClassCodeCnt;

    private String siteNatureObjectId;
    private String siteCode;
    private String geoscopeId;

    private List<String> errors;

    /**
     * Debug, prints the call and value; mapped with "*" will print all the XML document's data
     * @param path
     */
//    @SaxCallback("*")
    public void callDebugPrint(String path, Values values){
        System.out.println("  CALL(" + path +") = " + values.get(path));
    }

    /**
     * Init the site code and DB cleanup
     */
    @SaxCallback("sdfs.sdf.siteIdentification.siteCode")
    public void callSiteCode(String path, Values values) throws Exception {
        siteCode = values.get(path);
        getSiteNatObjectId();
        deleteOldRecords();
        setSitesTab(siteNatureObjectId, Constants.SITES_TAB_GENERAL);
    }

    /**
     * Callback for BioDescription
     * @param path The full path
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.siteLocation.biogeoRegions.code")
    public void callBioDescription(String path, Values values) throws Exception {
        String bioRegion = values.get(path);
        // todo: there is also a percentage
        if (bioRegion != null && bioregionExists(bioRegion)) {
            insertSiteAttribute(siteCode, bioRegion.toUpperCase(), "BOOLEAN", "TRUE", "sdfxml");
        } else {
            System.out.println(" Warning! BioRegion '" + bioRegion + "' doesn't exist!");
        }
    }

    /**
     * Callback for document end; commit and close the connection
     * @param exceptions List of exceptions caought by the parser
     * @throws Exception
     */
    @SaxCallback({">end"})
    public void callFinal(List<Exception> exceptions) throws Exception {
        System.out.println("Finished site " + siteCode);
        // exceptions during load
        // add the site id at the top of the exceptions list
        if(exceptions.size() > 0){
            try {
                if (siteCode != null) {
                    exceptions.add(0, new Exception("Error! Site ID: " + siteCode));
                }
                con.rollback();
            } catch(SQLException e){
                exceptions.add(e);
            }
        }  else {
            try{
                con.commit();
            } catch(SQLException e){
                exceptions.add(e);
            }
        }

        if (preparedStatementNatObject != null) {
            preparedStatementNatObject.close();
        }
        if (preparedStatementSiteInsert != null) {
            preparedStatementSiteInsert.close();
        }
        if (preparedStatementNatObjectReportType != null) {
            preparedStatementNatObjectReportType.close();
        }
        if (preparedStatementNatObjectGeoscope != null) {
            preparedStatementNatObjectGeoscope.close();
        }
        if (preparedStatementReportAttribute != null) {
            preparedStatementReportAttribute.close();
        }
        if (preparedStatementSiteSites != null) {
            preparedStatementSiteSites.close();
        }
        if (preparedStatementSiteAttribute != null) {
            preparedStatementSiteAttribute.close();
        }
        if (preparedStatementSiteRelatedDesignations != null) {
            preparedStatementSiteRelatedDesignations.close();
        }
        if (preparedStatementReportType != null) {
            preparedStatementReportType.close();
        }
        if (preparedStatementUpdateManager != null) {
            preparedStatementUpdateManager.close();
        }

        if (con != null) {
            con.close();
        }

    }

    /**
     * Writes the Site table
     * @param path
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.siteLocation")
    public void callsiteLocation(String path, Values values) throws Exception {

        String siteType = values.get("sdfs.sdf.siteIdentification.siteType");
        String siteDesignation = null;
        // Resolve site designation
        if (siteType != null && siteType.length() > 0) {
            siteDesignation = designationsMapping.get(siteType);
        }

        preparedStatementNatObject.setString(1, siteNatureObjectId);
        preparedStatementNatObject.setString(2, siteCode);
        preparedStatementNatObject.executeUpdate();

        preparedStatementSiteInsert.setString(1, siteCode);
        preparedStatementSiteInsert.setString(2, siteNatureObjectId);
        preparedStatementSiteInsert.setString(3, values.get("sdfs.sdf.siteIdentification.siteName"));
        preparedStatementSiteInsert.setString(4, parseDate(values.get("sdfs.sdf.siteIdentification.compilationDate")));
        // workaround to match the bug in the original parser :)
        String updateDate = parseDate(values.get("sdfs.sdf.siteIdentification.updateDate"));
        if(updateDate == null) updateDate = "";
        preparedStatementSiteInsert.setString(5, updateDate);
        preparedStatementSiteInsert.setString(6, parseDate(values.get("sdfs.sdf.siteIdentification.spaClassificationDate")));
        preparedStatementSiteInsert.setString(7, values.get("sdfs.sdf.siteIdentification.respondent.addressUnstructured"));
        preparedStatementSiteInsert.setString(8, values.get("sdfs.sdf.siteIdentification.Description"));   // missing
        preparedStatementSiteInsert.setString(9, values.get("sdfs.sdf.siteLocation.latitude"));
        preparedStatementSiteInsert.setString(10, values.get("sdfs.sdf.siteLocation.longitude"));
        preparedStatementSiteInsert.setString(11, values.get("sdfs.sdf.siteLocation.area"));
        preparedStatementSiteInsert.setString(12, values.get("sdfs.sdf.siteLocation.Altitude_Min"));    // missing
        preparedStatementSiteInsert.setString(13, values.get("sdfs.sdf.siteLocation.Altitude_Max"));    // missing
        preparedStatementSiteInsert.setString(14, values.get("sdfs.sdf.siteLocation.Altitude_Mean"));   // missing
        preparedStatementSiteInsert.setString(15, "NATURA2000");
        preparedStatementSiteInsert.setString(16, "80");
        preparedStatementSiteInsert.setString(17, siteDesignation);
        preparedStatementSiteInsert.setString(19, values.get("sdfs.sdf.siteLocation.marineAreaPercentage"));
        preparedStatementSiteInsert.setString(20, siteType);
        //        PROPOSED_DATE, CONFIRMED_DATE, SAC_DATE
        preparedStatementSiteInsert.setString(21, parseDate(values.get("sdfs.sdf.siteIdentification.sciProposalDate")));
        preparedStatementSiteInsert.setString(22, parseDate(values.get("sdfs.sdf.siteIdentification.sciConfirmationDate")));
        preparedStatementSiteInsert.setString(23, parseDate(values.get("sdfs.sdf.siteIdentification.sacDesignationDate")));
        // NUTS code
        // there is actually a list of region codes, the NUTS field only keeps the last one - the others are found in the attributes
        preparedStatementSiteInsert.setString(24, values.get("sdfs.sdf.siteLocation.adminRegions.region.code"));

        String lengthKm = values.get("sdfs.sdf.siteLocation.siteLength");

        try {
            if(lengthKm!= null){
                double length = Double.parseDouble(lengthKm);
                length = length * 1000;
                lengthKm = Double.toString(length);
            }
        } catch (NumberFormatException e){
            errors.add("Length " + lengthKm + " is unparseable!");
        }

        if (lengthKm != null && lengthKm.length() > 0) {
            preparedStatementSiteInsert.setString(18, lengthKm);
        } else {
            preparedStatementSiteInsert.setNull(18, Types.DECIMAL);
        }
        preparedStatementSiteInsert.executeUpdate();

        geoscopeId = getGeoscopeId();
        preparedStatementNatObjectGeoscope.setString(1, siteNatureObjectId);
        preparedStatementNatObjectGeoscope.setString(2, geoscopeId);
        preparedStatementNatObjectGeoscope.executeUpdate();

        insertSiteAttribute(siteCode, "TYPE", "TEXT", siteType, "sdfxml");
    }

    /**
     * Add all administrative regions as Site attribute "NUTS"
     * @param path
     * @param values
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.siteLocation.adminRegions.region")
    public void callAdminRegion(String path, Values values) throws Exception {
        insertSiteAttribute(siteCode, "NUTS_" + values.getFromCurrent("code"), "TEXT", values.getFromCurrent("name") , "sdfxml");
        // todo check that the region exists in chm62edt_region_codes; if not, insert it
    }

    /**
     * Inserts a habitat
     * @param path
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.ecologicalInformation.habitatTypes.habitatType")
    public void callHabitat(String path, Values values) throws Exception {
        String habitatCode = values.getFromCurrent("code");
        String habitatCover = values.getFromCurrent("coveredArea");
        String habitatRepresentatity = values.getFromCurrent("representativity");
        String habitatRelsurface = values.getFromCurrent("relativeSurface");
        String habitatConsStatus = values.getFromCurrent("conservation");
        String habitatGlobalAssesment = values.getFromCurrent("global");

        if (habitatCode != null) {
            String habitatIdNatObject = getHabitatNatObjectId(habitatCode);

            if (habitatIdNatObject != null && habitatIdNatObject.length() > 0) {

                maxReportAttributeId++;

                insertNatObjectReportType(siteNatureObjectId, habitatIdNatObject, -1, -1, maxReportAttributeId, -1);


                setSitesTab(siteNatureObjectId, Constants.SITES_TAB_HABITAT_TYPES);
                setHabitatTabSites(habitatIdNatObject);

                batchReportAttribute(Constants.REPORT_ATTRIBUTE_HABITAT_SOURCE_TABLE, "TEXT", "habit2");
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_HABITAT_COVER, "TEXT", habitatCover);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_HABITAT_REPRESENTATIVITY, "TEXT", habitatRepresentatity);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_HABITAT_RELATIVE_SURFACE, "TEXT", habitatRelsurface);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_HABITAT_CONSERVATION, "TEXT", habitatConsStatus);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_HABITAT_GLOBAL, "TEXT", habitatGlobalAssesment);

                preparedStatementReportAttribute.executeBatch();
                preparedStatementReportAttribute.clearParameters();

            } else {
                batchSiteAttribute(siteCode, "HABITAT_CODE_" + habitatCode, "TEXT", habitatCode, "habit2");
                batchSiteAttribute(siteCode, "HABITAT_COVER_" + habitatCode, "NUMBER", habitatCover, "habit2");
                batchSiteAttribute(siteCode, "HABITAT_REPRESENTATIVITY_" + habitatCode, "NUMBER", habitatRepresentatity, "habit2");
                batchSiteAttribute(siteCode, "HABITAT_RELATIVE_SURFACE_" + habitatCode, "NUMBER", habitatRelsurface, "habit2");
                batchSiteAttribute(siteCode, "HABITAT_CONSERVATION_" + habitatCode, "TEXT", habitatConsStatus, "habit2");
                batchSiteAttribute(siteCode, "HABITAT_GLOBAL_" + habitatCode, "TEXT", habitatGlobalAssesment, "habit2");

                preparedStatementSiteAttribute.executeBatch();
                preparedStatementSiteAttribute.clearParameters();
            }
        }
    }

    /**
     * Commit the species
     * @param path
     * @param values
     * @throws Exception
     */
    @SaxCallback({"sdfs.sdf.ecologicalInformation.species"})
    public void callEndSpecies(String path, Values values) throws Exception {
        preparedStatementSiteAttribute.executeBatch();
        preparedStatementSiteAttribute.clearParameters();
        preparedStatementReportAttribute.executeBatch();
        preparedStatementReportAttribute.clearParameters();
    }

    /**
     * Writes species
     * @param path
     * @throws Exception
     */
    @SaxCallback({"sdfs.sdf.ecologicalInformation.species.speciesPopulation"})
    public void callSpecies(String path, Values values) throws Exception {
        String speciesCode = values.getFromCurrent("speciesCode");

//        <!-- IF motivation element EXISTS then -->
//        <!-- 3.3. Other important species of flora and fauna (optional) -->
//        <!-- ELSE -->
//        <!-- 3.2. Species referred to in Artcle 4 of Directive 2009/147/EC and listed in Annex II of Directive 92/43/EEC and site evaluation form them -->

        if(values.existsInCurrent("motivations.motivation")){
            // Other species
            callOtherSpecies(path, values);
            return;
        }

        String speciesName = values.getFromCurrent("scientificName");
        String speciesWinter = values.getFromCurrent("Winter");
        String speciesPopulation = values.getFromCurrent("population");
        String speciesIsolation = values.getFromCurrent("isolation");
        String speciesConservation = values.getFromCurrent("conservation");
        String speciesGlobal = values.getFromCurrent("global");
        String speciesResident = values.getFromCurrent("Resident");
        String speciesStaging = values.getFromCurrent("Staging");
        String speciesBreeding = getPopulation(values);
        String sensitiveInfo = values.getFromCurrent("sensitiveInfo");

        String ecoInfo = speciesSourceCode.get(values.getFromCurrent("speciesGroup"));

        if (speciesCode != null && (sensitiveInfo == null || sensitiveInfo.equalsIgnoreCase("false"))) {
            String speciesIdNatObject = getSpeciesNatObjectId(speciesCode);

            if (speciesIdNatObject != null && speciesIdNatObject.length() > 0) {

                if (ecoInfo == null || ecoInfo.length() == 0) {
                    ecoInfo = Constants.N2000_SPECIES_GROUP_BIRD;      // default?
                }

                maxReportAttributeId++;
                insertNatObjectReportType(siteNatureObjectId, speciesIdNatObject, -1, -1, maxReportAttributeId, -1);

                setSitesTab(siteNatureObjectId, Constants.SITES_TAB_FAUNA_FLORA);
                setSpeciesTabSites(speciesIdNatObject);

                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_RESIDENT, "TEXT", speciesResident);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_POPULATION, "TEXT", speciesPopulation);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_CONSERVATION, "TEXT", speciesConservation);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_ISOLATION, "TEXT", speciesIsolation);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_GLOBAL, "TEXT", speciesGlobal);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_STAGING, "TEXT", speciesStaging);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_WINTER, "TEXT", speciesWinter);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_BREEDING, "TEXT", speciesBreeding);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_OTHER_SPECIES, "TEXT", speciesBreeding);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_BREEDING, "TEXT", "False");
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_SOURCE_TABLE, "TEXT", ecoInfo);

            } else {
                batchSiteAttribute(siteCode, "OTHER_SPECIES_" + speciesName, "TEXT", speciesName, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_RESIDENT_" + speciesName, "TEXT", speciesResident, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_POPULATION_" + speciesName, "TEXT", speciesPopulation, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_BREEDING_" + speciesName, "TEXT", speciesBreeding, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_WINTERING_" + speciesName, "TEXT", speciesWinter, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_STAGING_" + speciesName, "TEXT", speciesStaging, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_CONSERVATION_" + speciesName, "TEXT", speciesConservation, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_ISOLATION_" + speciesName, "TEXT", speciesIsolation, "spec");
                batchSiteAttribute(siteCode, "OTHER_SPECIES_GLOBAL_" + speciesName, "TEXT", speciesGlobal, "spec");
            }
        }
    }

    /**
     * Parses the population size and formats it in the older format (lower-upper[unit])
     * @param values
     * @return
     */
    private String getPopulation(Values values){
        String speciesPopulationUpper = values.getFromCurrent("populationSize.upperBound");
        String speciesPopulationLower = values.getFromCurrent("populationSize.lowerBound");
        String speciesPopulationUnit = values.getFromCurrent("populationSize.countingUnit");

        String speciesPopulation = speciesPopulationUpper;

        if(speciesPopulationUpper != null && speciesPopulationLower != null){
            if(speciesPopulationLower.equalsIgnoreCase(speciesPopulationUpper)){
                speciesPopulation = speciesPopulationUpper;
            } else {
                speciesPopulation =  speciesPopulationLower + "-" + speciesPopulationUpper;
            }
        }
        if(speciesPopulation != null && speciesPopulationUnit != null) {
            speciesPopulation = speciesPopulation + speciesPopulationUnit;
        }

        return speciesPopulation;
    }

    /**
     * Writes other species; this is not a callback because the other species are in the same list as the normal ones, but are
     * missing the code
     * @param path
     * @throws Exception
     */
//    @SaxCallback("sdfs.sdf.ecologicalInformation.EcologicalInformation33.OtherSpeciesList.OtherSpecies")
    public void callOtherSpecies(String path, Values values) throws Exception {
        String otherSpeciesSciName = values.getFromCurrent("scientificName");
        String otherSpeciesPopulation = values.getFromCurrent("abundanceCategory");
        String otherSpeciesMotivation = values.getFromCurrent("motivations.motivation");
        String otherSpeciesGroup = values.getFromCurrent("speciesGroup");

        if (otherSpeciesSciName != null && otherSpeciesSciName.length() > 0) {

            String speciesIdNatObject = getSpeciesNatObjectIdByName(otherSpeciesSciName);

            if (speciesIdNatObject != null && speciesIdNatObject.length() > 0) {

                maxReportAttributeId++;
                insertNatObjectReportType(siteNatureObjectId, speciesIdNatObject, -1, -1, maxReportAttributeId, -1);

                setSitesTab(siteNatureObjectId, Constants.SITES_TAB_FAUNA_FLORA);
                setSpeciesTabSites(speciesIdNatObject);

                batchReportAttribute(Constants.REPORT_ATTRIBUTE_SPECIES_SOURCE_TABLE, "TEXT", "spec");
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_OTHER_SPECIES_MOTIVATION, "TEXT", otherSpeciesMotivation);
                batchReportAttribute(Constants.REPORT_ATTRIBUTE_OTHER_SPECIES_POPULATION, "TEXT", otherSpeciesPopulation);
            } else {
                batchSiteAttribute(siteCode, "OTHER_SPECIES_" + otherSpeciesSciName, "TEXT", "spec", "spec" );
                setSitesTab(siteNatureObjectId, Constants.SITES_TAB_FAUNA_FLORA);

                batchSiteAttribute(siteCode, "OTHER_SPECIES_TAXGROUP_" + otherSpeciesSciName, "TEXT", otherSpeciesGroup, "spec" );
                batchSiteAttribute(siteCode, "OTHER_SPECIES_POPULATION_" + otherSpeciesSciName, "TEXT", otherSpeciesPopulation, "spec" );
                batchSiteAttribute(siteCode, "OTHER_SPECIES_MOTIVATION_" + otherSpeciesSciName, "TEXT", otherSpeciesMotivation, "spec" );
            }
        }
    }

    /**
     * Writes habitat classes
     * @param path
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.siteDescription.habitatClass")
    public void callHabitatClasses(String path, Values values) throws Exception {
        String siteDescriptionHabClassCode = values.getFromCurrent("code");
        String siteDescriptionHabClassCover = values.getFromCurrent("coveragePercentage");

        if (siteDescriptionHabClassCode != null && siteDescriptionHabClassCover != null) {
            insertSiteDescriptionHabitatClasses(siteDescriptionHabClassCode, siteCode, siteDescriptionHabClassCover);
        }
    }

    /**
     * Writes site attributes
     * Change from the previus version: doesn't write if null
     * @param path
     * @throws Exception
     */
    @SaxCallback({"sdfs.sdf.siteDescription.otherSiteCharacteristics",
            "sdfs.sdf.siteDescription.qualityAndImportance",
            "sdfs.sdf.siteDescription.Vulnerability",
            "sdfs.sdf.siteDescription.SiteDesignation",
            "sdfs.sdf.siteDescription.documentation.description"})
    public void callSiteAttribute(String path, Values values) throws Exception {
        String content = values.get(path);
        if (path.endsWith("otherSiteCharacteristics")) {
            insertSiteAttribute(siteCode, "HABITAT_CHARACTERIZATION", "TEXT", content, "habit2", true);
        } else if (path.endsWith("qualityAndImportance")) {
            insertSiteAttribute(siteCode, "QUALITY", "TEXT", content, "habit2", true);
        } else if (path.endsWith("Vulnerability")) {
            insertSiteAttribute(siteCode, "VULNERABILITY", "TEXT", content, "habit2", true);
        } else if (path.endsWith("SiteDesignation")) {
            insertSiteAttribute(siteCode, "SITE_DESIGNATION", "TEXT", content, "habit2", true);
        } else if (path.endsWith("documentation.description")) {
            insertSiteAttribute(siteCode, "DOCUMENTATION", "TEXT", content, "habit2", true);
        }
    }

    /**
     * Writes designation types
     * @param path
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.siteProtection.nationalDesignations.nationalDesignation")
    public void callDesignationTypes(String path, Values values) throws Exception {
        // todo: there are also relations:  sdfs.sdf.siteProtection.nationalRelationships.nationalRelationship
        String siteProtectionDesigTypeCode = values.getFromCurrent("designationCode");
        String siteProtectionDesigTypeCover = values.getFromCurrent("cover");

        if (siteProtectionDesigTypeCode != null && siteProtectionDesigTypeCover != null) {
            preparedStatementSiteRelatedDesignations.setString(1, siteCode);
            preparedStatementSiteRelatedDesignations.setString(2, siteProtectionDesigTypeCode);
            preparedStatementSiteRelatedDesignations.setString(3, geoscopeId);
            preparedStatementSiteRelatedDesignations.setString(4, siteProtectionDesigTypeCover);

            preparedStatementSiteRelatedDesignations.executeUpdate();

            setSitesTab(siteNatureObjectId, Constants.SITES_TAB_DESIGNATION);
        }
    }

    /**
     * Writes the site manager
     * @param path
     * @throws Exception
     */
    @SaxCallback("sdfs.sdf.siteManagement.managementBodies.managementBody.organisation")
    public void callResponsible(String path, Values values) throws Exception {
        //todo: there is more data now
        preparedStatementUpdateManager.setString(1, values.get(path));
        preparedStatementUpdateManager.setString(2, siteCode);
        preparedStatementUpdateManager.executeUpdate();
    }

    /**
     * Within / around site
     * @param path
     * @throws Exception
     */
    @SaxCallback({"sdfs.sdf.siteDescription.impacts.impact"})
    public void callImpact(String path, Values values) throws Exception {
        //todo: this changed, waiting for more info
        String siteImpactsCode = values.getFromCurrent("code");
        String siteImpactsIntensity = values.getFromCurrent("rank");
        String siteImpactsInfluence = values.getFromCurrent("natureOfImpact");
        String siteImpactsPercent = values.getFromCurrent("Percent");
        String occurence = values.getFromCurrent("occurrence");

        if(occurence != null) occurence = occurence.toUpperCase();
        if(siteImpactsInfluence != null) siteImpactsInfluence = (siteImpactsInfluence.equalsIgnoreCase("negative")?"-":"+");

        if (siteImpactsCode != null && siteImpactsCode.length() > 0) {

            maxReportTypeId++;
            preparedStatementReportType.setInt(1, maxReportTypeId);
            preparedStatementReportType.setString(2, siteImpactsCode);
            preparedStatementReportType.setString(3, "HUMAN_ACTIVITY");
            preparedStatementReportType.executeUpdate();

            maxReportAttributeId++;

            batchReportAttribute("IN_OUT", "TEXT", occurence);
            batchReportAttribute("INTENSITY", "TEXT", siteImpactsIntensity);
            batchReportAttribute("INFLUENCE", "TEXT", siteImpactsInfluence);
            batchReportAttribute("COVER", "NUMBER", siteImpactsPercent);

            preparedStatementReportAttribute.executeBatch();
            preparedStatementReportAttribute.clearParameters();

            insertNatObjectReportType(siteNatureObjectId,"-1", -1, maxReportTypeId, maxReportAttributeId, -1);

            setSitesTab(siteNatureObjectId, Constants.SITES_TAB_OTHER_INFO);
        }
    }

    /**
     * Writes other site code
     * Needs to be called after siteCode is initialized
     * @param path
     * @throws Exception
     * // todo : not found
     */
    @SaxCallback("sdfs.sdf.siteIdentification.OtherSiteCode")
    public void callOtherSiteCode(String path, Values values) throws  Exception {
        String code = values.get(path);

        if (code != null && code.length() > 0) {
            preparedStatementSiteSites.setString(1, siteCode);
            preparedStatementSiteSites.setString(2, code);
            preparedStatementSiteSites.setString(3, "=");
            preparedStatementSiteSites.executeUpdate();

            setSitesTab(siteNatureObjectId, Constants.SITES_TAB_SITES);
        }
    }



    private boolean regionExists(String regionCode) {
        boolean ret = false;
        String query = "SELECT ID_REGION_CODE FROM chm62edt_region_codes WHERE ID_REGION_CODE = '" + regionCode + "'";
        String rcId = sqlUtilities.ExecuteSQL(query);

        if (rcId != null && rcId.length() > 0) {
            ret = true;
        }

        return ret;
    }

    private void getSiteNatObjectId() {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ID_SITE='" + siteCode + "'";

        siteNatureObjectId = sqlUtilities.ExecuteSQL(query);
        if (siteNatureObjectId == null || siteNatureObjectId.length() == 0) {
            maxNoIdInt++;
            siteNatureObjectId = Integer.toString(maxNoIdInt);
        }
    }

    private void deleteOldRecords() throws Exception {

        PreparedStatement ps = null;

        try {

            String query = "DELETE FROM chm62edt_site_attributes WHERE ID_SITE = ?";

            ps = con.prepareStatement(query);
            ps.setString(1, siteCode);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_sites_sites WHERE ID_SITE = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteCode);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_sites_related_designations WHERE ID_SITE = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteCode);
            ps.executeUpdate();
            ps.close();

            query = "DELETE ST FROM chm62edt_report_attributes AS ST, chm62edt_nature_object_report_type AS TT " +
                    "WHERE ST.ID_REPORT_ATTRIBUTES = TT.ID_REPORT_ATTRIBUTES AND TT.ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_nature_object_report_type WHERE ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE ST FROM chm62edt_report_attributes AS ST, chm62edt_nature_object_geoscope AS TT " +
                    "WHERE ST.ID_REPORT_ATTRIBUTES = TT.ID_REPORT_ATTRIBUTES AND TT.ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_nature_object_geoscope WHERE ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE ST FROM chm62edt_report_attributes AS ST, chm62edt_reports AS TT " +
                    "WHERE ST.ID_REPORT_ATTRIBUTES = TT.ID_REPORT_ATTRIBUTES AND TT.ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_reports WHERE ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_sites WHERE ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

            query = "DELETE FROM chm62edt_nature_object WHERE ID_NATURE_OBJECT = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, siteNatureObjectId);
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private void setSitesTab(String idNatureObject, String tabName) throws Exception {
        PreparedStatement ps = null;

        try {
            if (tabName.equals(Constants.SITES_TAB_GENERAL)) {
                String query = "INSERT IGNORE INTO chm62edt_tab_page_sites(ID_NATURE_OBJECT,GENERAL_INFORMATION) VALUES(?,'Y')";

                ps = con.prepareStatement(query);
            } else {
                String query = "UPDATE chm62edt_tab_page_sites SET " + tabName + "='Y' WHERE ID_NATURE_OBJECT=?";

                ps = con.prepareStatement(query);
            }
            ps.setString(1, idNatureObject);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

    }

    @SaxCallback(">start")
    public void init() throws Exception{

        maxNoIdInt = getMaxId("SELECT MAX(ID_NATURE_OBJECT) FROM chm62edt_nature_object");
        maxReportAttributeId = getMaxId("SELECT MAX(ID_REPORT_ATTRIBUTES) FROM chm62edt_report_attributes");
        maxReportTypeId = getMaxId("SELECT MAX(ID_REPORT_TYPE) FROM chm62edt_report_type");

        String queryNatObject =
                "INSERT INTO chm62edt_nature_object (ID_NATURE_OBJECT, ORIGINAL_CODE, ID_DC, TYPE) VALUES (?,?, -1, 'NATURA2000_SITES')";
        this.preparedStatementNatObject = con.prepareStatement(queryNatObject);

        String querySiteInsert =
                "INSERT INTO chm62edt_sites (ID_SITE, ID_NATURE_OBJECT, NAME, COMPILATION_DATE, "
                        + "COMPLEX_NAME, DISTRICT_NAME, "
                        + "UPDATE_DATE, SPA_DATE, RESPONDENT, DESCRIPTION, LATITUDE, "
                        + "LONGITUDE, AREA, ALT_MIN, ALT_MAX, ALT_MEAN, SOURCE_DB, "
                        + "ID_GEOSCOPE, ID_DESIGNATION, LENGTH, MARINE_PERCENT, SITE_TYPE," +
                        "PROPOSED_DATE, CONFIRMED_DATE, SAC_DATE, NUTS) VALUES " //21,22,23
                        + "(?,?,?,?,'','',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        this.preparedStatementSiteInsert = con.prepareStatement(querySiteInsert);

        String queryUpdateManager = "UPDATE chm62edt_sites SET MANAGER = ? WHERE ID_SITE = ?";
        this.preparedStatementUpdateManager = con.prepareStatement(queryUpdateManager);

        String insertNatObjectReportType = "INSERT IGNORE INTO chm62edt_nature_object_report_type "
                + "(ID_NATURE_OBJECT, ID_NATURE_OBJECT_LINK, ID_GEOSCOPE, ID_REPORT_TYPE, ID_REPORT_ATTRIBUTES, ID_DC) "
                + "VALUES (?,?,?,?,?,?)";
        this.preparedStatementNatObjectReportType = con.prepareStatement(insertNatObjectReportType);

        String insertNatObjectGeoscope = "INSERT IGNORE INTO chm62edt_nature_object_geoscope "
                + "(ID_NATURE_OBJECT, ID_NATURE_OBJECT_LINK, ID_DC, ID_GEOSCOPE, ID_REPORT_ATTRIBUTES) "
                + "VALUES (?,-1,-1,?,-1)";
        this.preparedStatementNatObjectGeoscope = con.prepareStatement(insertNatObjectGeoscope);

        String insertReportAttribute = "INSERT IGNORE INTO chm62edt_report_attributes "
                + "(ID_REPORT_ATTRIBUTES, NAME, TYPE, VALUE) VALUES (?,?,?,?)";
        this.preparedStatementReportAttribute = con.prepareStatement(insertReportAttribute);

        String insertSiteSites = "INSERT IGNORE INTO chm62edt_sites_sites "
                + "(ID_SITE, ID_SITE_LINK, SEQUENCE, RELATION_TYPE, WITHIN_PROJECT, SOURCE_TABLE) "
                + "VALUES (?,?,-1,?,1,'sitrel')";
        this.preparedStatementSiteSites = con.prepareStatement(insertSiteSites);

        String insertSiteAttribute = "INSERT IGNORE INTO chm62edt_site_attributes "
                + "(ID_SITE, NAME, TYPE, VALUE, SOURCE_DB, SOURCE_TABLE) VALUES (?,?,?,?,'NATURA2000',?)";
        this.preparedStatementSiteAttribute = con.prepareStatement(insertSiteAttribute);

        String insertSiteRelatedDesignations = "INSERT IGNORE INTO chm62edt_sites_related_designations "
                + "(ID_SITE, ID_DESIGNATION, ID_GEOSCOPE, SEQUENCE, OVERLAP_TYPE, OVERLAP, SOURCE_DB, SOURCE_TABLE) "
                + "VALUES (?,?,?,-1,-1,?,'NATURA2000','desigc')";
        this.preparedStatementSiteRelatedDesignations = con.prepareStatement(insertSiteRelatedDesignations);

        String insertReportType = "INSERT IGNORE INTO chm62edt_report_type (ID_REPORT_TYPE, ID_LOOKUP, LOOKUP_TYPE) VALUES (?,?,?)";
        this.preparedStatementReportType = con.prepareStatement(insertReportType);

        con.setAutoCommit(false);

    }

    private int getMaxId(String query) throws ParseException {
        String maxId = sqlUtilities.ExecuteSQL(query);
        int maxIdInt = 0;

        if (maxId != null && maxId.length() > 0) {
            maxIdInt = new Integer(maxId);
        }

        return maxIdInt;
    }

    private String parseDate(String input) {
        String ret = input;

        if (input != null && input.length() > 0) {
            if (input.contains("T00:00:00")) {
                int end = input.indexOf("T");
                String date = input.substring(0, end);

                ret = date.replace("-", "");
            } else if (input.matches("[\\d]{4}-[\\d]{2}")){
                ret = ret.replace("-","") + "01";
            }
        }
        return ret;
    }

    private boolean bioregionExists(String bioRegionCode) {
        boolean ret = false;
        String query = "SELECT ID_BIOGEOREGION FROM chm62edt_biogeoregion WHERE CODE_SDF = '" + bioRegionCode + "'";
        String brId = sqlUtilities.ExecuteSQL(query);

        if (brId != null && brId.length() > 0) {
            ret = true;
        }

        return ret;
    }

    private String getGeoscopeId() {
        String query = "SELECT ID_GEOSCOPE FROM chm62edt_country WHERE ISO_2L = '"
                + siteCode.substring(0, 2) + "' ORDER BY ID_GEOSCOPE";

        return sqlUtilities.ExecuteSQL(query);
    }

    private String getHabitatNatObjectId(String habCode) {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_habitat WHERE CODE_2000='" + habCode + "'";
        return sqlUtilities.ExecuteSQL(query);
    }

    private void setHabitatTabSites(String habitatIdNatureObject) throws Exception {
        PreparedStatement ps = null;

        try {
            String query = "UPDATE chm62edt_tab_page_habitats SET SITES='Y' WHERE ID_NATURE_OBJECT = ?";

            ps = con.prepareStatement(query);
            ps.setString(1, habitatIdNatureObject);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private String getSpeciesNatObjectId(String speciesCode) {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_species WHERE CODE_2000='" +speciesCode+"'";
        return sqlUtilities.ExecuteSQL(query);
    }

    private void setSpeciesTabSites(String speciesIdNatureObject) throws Exception {
        PreparedStatement ps = null;

        try {
            String query = "UPDATE chm62edt_tab_page_species SET SITES='Y' WHERE ID_NATURE_OBJECT = ?";

            ps = con.prepareStatement(query);
            ps.setString(1, speciesIdNatureObject);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private String getSpeciesNatObjectIdByName(String sciName) {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_species WHERE SCIENTIFIC_NAME='"
                + EunisUtil.replaceTags(sciName) + "'";

        return sqlUtilities.ExecuteSQL(query);
    }

    private void insertSiteDescriptionHabitatClasses(String code, String siteId, String cover) throws Exception {
        if (code != null && code.length() > 0) {
            insertSiteAttribute(siteId, "HABITAT_COVER_" + code, "NUMBER", cover, "habit2");
            insertSiteAttribute(siteId, "HABITAT_CODE_" + code, "TEXT", code, "habit2");
        }
    }

    private void insertSiteAttribute(String siteId, String name, String type, String value, String sourceTable) throws Exception {
        batchSiteAttribute(siteId, name, type, value, sourceTable);
        preparedStatementSiteAttribute.executeBatch();
        preparedStatementSiteAttribute.clearParameters();
    }

    /**
     * Forced insert for SiteAttribute - workaround to match bug in the original import parser
     */
    private void insertSiteAttribute(String siteId, String name, String type, String value, String sourceTable, boolean force) throws Exception {
        preparedStatementSiteAttribute.setString(1, siteId);
        preparedStatementSiteAttribute.setString(2, name);
        preparedStatementSiteAttribute.setString(3, type);
        preparedStatementSiteAttribute.setString(4, value);
        preparedStatementSiteAttribute.setString(5, sourceTable);
        preparedStatementSiteAttribute.executeUpdate();
    }

    private void batchSiteAttribute(String siteId, String name, String type, String value, String sourceTable) throws Exception {
        if(value!=null) {
            preparedStatementSiteAttribute.setString(1, siteId);
            preparedStatementSiteAttribute.setString(2, name);
            preparedStatementSiteAttribute.setString(3, type);
            preparedStatementSiteAttribute.setString(4, value);
            preparedStatementSiteAttribute.setString(5, sourceTable);
            preparedStatementSiteAttribute.addBatch();
        }
    }

    private void batchReportAttribute(String name, String type, String value) throws Exception {
        if(value != null) {
            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
            preparedStatementReportAttribute.setString(2, name);
            preparedStatementReportAttribute.setString(3, type);
            preparedStatementReportAttribute.setString(4, value);
            preparedStatementReportAttribute.addBatch();
        }
    }

    private void insertReportAttribute(String name, String type, String value) throws Exception {
        batchReportAttribute(name, type, value);
        preparedStatementReportAttribute.executeBatch();
        preparedStatementReportAttribute.clearParameters();
    }

    private void insertNatObjectReportType(String natureObjectId, String natureObjectLink, int idGeoscope, int reportType, int reportAttributes, int idDc )  throws Exception {
        preparedStatementNatObjectReportType.setString(1, natureObjectId);
        preparedStatementNatObjectReportType.setString(2, natureObjectLink);
        preparedStatementNatObjectReportType.setInt(3, idGeoscope);
        preparedStatementNatObjectReportType.setInt(4, reportType);
        preparedStatementNatObjectReportType.setInt(5, reportAttributes);
        preparedStatementNatObjectReportType.setInt(6, idDc);
        preparedStatementNatObjectReportType.executeUpdate();
    }

    public String getSiteCode() {
        return siteCode;
    }
}
