package ro.finsiel.eunis.dataimport.parsers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.text.ParseException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ro.finsiel.eunis.utilities.EunisUtil;
import ro.finsiel.eunis.utilities.SQLUtilities;
import eionet.eunis.util.Constants;

/**
 *
 */
public class Natura2000ImportParser extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(Natura2000ImportParser.class);

    private BufferedInputStream inputStream;

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

    private String siteNatureObjectId;

    private String siteCode;
    private String siteName;
    private String siteType;
    private String siteDesignation;
    private String dateCompilation;
    private String dateUpdate;
    private String dateSpa;
    private String respondent;
    private String description;
    private String longitude;
    private String latitude;
    private String area;
    private String altMin;
    private String altMax;
    private String altMean;
    private String lengthKm;
    private String geoscopeId;
    private String nutsCode;
    private String nutsCover;

    private int maxNoIdInt = 0;
    private int maxReportAttributeId = 0;
    private int maxReportTypeId = 0;

    private String habitatCode;
    private String habitatCover;
    private String habitatRepresentatity;
    private String habitatRelsurface;
    private String habitatConsStatus;
    private String habitatGlobalAssesment;

    private String speciesCode;
    private String speciesName;
    private String speciesResident;
    private String speciesPopulation;
    private String speciesConservation;
    private String speciesIsolation;
    private String speciesGlobal;
    private String speciesWinter;
    private String speciesStaging;
    private String speciesBreeding;

    private String otherSpeciesGroup;
    private String otherSpeciesSciName;
    private String otherSpeciesPopulation;
    private String otherSpeciesMotivation;

    private String siteDescriptionHabClassDesc;
    private String siteDescriptionHabClassCover;
    private int siteDescriptionHabClassCodeCnt;

    private String siteDescriptionOtherCharacteristics;
    private String siteDescriptionQualityImportance;
    private String siteDescriptionVulnerability;
    private String siteDescriptionSiteDesignation;
    private String siteDescriptionDocumentation;

    private String siteProtectionDesigTypeCode;
    private String siteProtectionDesigTypeCover;

    private String siteImpactsCode;
    private String siteImpactsIntensity;
    private String siteImpactsPercent;
    private String siteImpactsInfluence;

    private String responsible;

    private String ecoInfo;

    private List<String> errors;

    private Connection con;

    private StringBuffer buf;
    private SQLUtilities sqlUtilities;

    private static final Map<String, String> designationsMapping = new HashMap<String, String>();
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
    }

    public Natura2000ImportParser(SQLUtilities sqlUtilities) {
        this.sqlUtilities = sqlUtilities;
        this.con = sqlUtilities.getConnection();
        buf = new StringBuffer();
        errors = new ArrayList<String>();
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
            errors.add(se.getMessage());
            // throw new RuntimeException(se.getMessage(), se);
        } catch (ParserConfigurationException pce) {
            errors.add(pce.getMessage());
            // throw new RuntimeException(pce.getMessage(), pce);
        } catch (IOException ie) {
            errors.add(ie.getMessage());
            // throw new RuntimeException(ie.getMessage(), ie);
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
            if (qName.equalsIgnoreCase("SiteCode")) {
                siteCode = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("SiteName")) {
                siteName = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("SiteType")) {

                siteType = buf.toString().trim();

                // Resolve site designation
                if (siteType != null && siteType.length() > 0) {
                    if (designationsMapping.containsKey(siteType)) {
                        siteDesignation = designationsMapping.get(siteType);
                    }
                }

                getSiteNatObjectId();
                deleteOldRecords();
                setSitesTab(siteNatureObjectId, Constants.SITES_TAB_GENERAL);

            } else if (qName.equalsIgnoreCase("Date_Compilation")) {
                dateCompilation = buf.toString().trim();
                if (dateCompilation.contains("T")) {
                    dateCompilation = parseDate(dateCompilation);
                }
            } else if (qName.equalsIgnoreCase("Date_Update")) {
                dateUpdate = buf.toString().trim();
                if (dateUpdate.contains("T")) {
                    dateUpdate = parseDate(dateUpdate);
                }
            } else if (qName.equalsIgnoreCase("Date_Spa")) {
                dateSpa = buf.toString().trim();
                if (dateSpa.contains("T")) {
                    dateSpa = parseDate(dateSpa);
                }
            } else if (qName.equalsIgnoreCase("Respondent")) {
                respondent = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Description")) {
                description = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Longitude")) {
                longitude = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Latitude")) {
                latitude = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("AreaHA")) {
                area = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Altitude_Min")) {
                altMin = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Altitude_Max")) {
                altMax = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Altitude_Mean")) {
                altMean = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("NutsCode")) {
                nutsCode = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("NutsCover")) {
                nutsCover = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("LengthKM")) {
                lengthKm = buf.toString().trim();
                if (lengthKm != null && lengthKm.length() > 0) {
                    double length;
                    try {
                        length = Double.parseDouble(lengthKm);
                        length = length * 1000;
                        lengthKm = new Double(length).toString();
                    } catch (NumberFormatException e) {
                        System.out.println("Length " + lengthKm + " is unparseable!");
                    }
                }
            }

            if (qName.equalsIgnoreCase("OtherSiteCode")) {
                String code = buf.toString().trim();

                if (code != null && code.length() > 0) {
                    preparedStatementSiteSites.setString(1, siteCode);
                    preparedStatementSiteSites.setString(2, code);
                    preparedStatementSiteSites.setString(3, "=");
                    preparedStatementSiteSites.executeUpdate();

                    setSitesTab(siteNatureObjectId, Constants.SITES_TAB_SITES);
                }
            }

            if (qName.equalsIgnoreCase("AdministrativeRegion")) {
                if (nutsCode != null && nutsCode.length() > 0 && nutsCover != null && nutsCover.length() > 0) {
                    boolean regionExists = regionExists(nutsCode);

                    if (regionExists) {
                        maxReportTypeId++;
                        maxReportAttributeId++;

                        preparedStatementNatObjectReportType.setString(1, siteNatureObjectId);
                        preparedStatementNatObjectReportType.setInt(2, -1);
                        preparedStatementNatObjectReportType.setInt(3, -1);
                        preparedStatementNatObjectReportType.setInt(4, maxReportTypeId);
                        preparedStatementNatObjectReportType.setInt(5, maxReportAttributeId);
                        preparedStatementNatObjectReportType.setInt(6, -1);
                        preparedStatementNatObjectReportType.executeUpdate();

                        preparedStatementReportType.setInt(1, maxReportTypeId);
                        preparedStatementReportType.setString(2, nutsCode);
                        preparedStatementReportType.setString(3, "REGION_CODE");
                        preparedStatementReportType.executeUpdate();

                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, "COVER");
                        preparedStatementReportAttribute.setString(3, "NUMBER");
                        preparedStatementReportAttribute.setString(4, nutsCover);
                        preparedStatementReportAttribute.executeUpdate();
                    } else {
                        System.out.println(" Warning! AdministrativeRegion with code " + nutsCode + " doesn't exist!");
                    }
                }
            }

            if (qName.equalsIgnoreCase("BioDescription")) {
                String bioRegion = buf.toString().trim();

                if (bioRegion != null && bioregionExists(bioRegion)) {
                    preparedStatementSiteAttribute.setString(1, siteCode);
                    preparedStatementSiteAttribute.setString(2, bioRegion.toUpperCase());
                    preparedStatementSiteAttribute.setString(3, "BOOLEAN");
                    preparedStatementSiteAttribute.setString(4, "TRUE");
                    preparedStatementSiteAttribute.setString(5, "sdfxml");
                    preparedStatementSiteAttribute.executeUpdate();
                } else {
                    System.out.println(" Warning! BioRegion '" + bioRegion + "' doesn't exist!");
                }
            }

            if (qName.equalsIgnoreCase("SiteLocation")) {

                if (dateUpdate == null) {
                    dateUpdate = "";
                }

                if (dateCompilation == null) {
                    dateCompilation = "";
                }

                if (dateSpa == null) {
                    dateSpa = "";
                }

                preparedStatementNatObject.setString(1, siteNatureObjectId);
                preparedStatementNatObject.setString(2, siteCode);
                preparedStatementNatObject.executeUpdate();

                //String geoIdEu = getGeoscopeIdEu();

                preparedStatementSiteInsert.setString(1, siteCode);
                preparedStatementSiteInsert.setString(2, siteNatureObjectId);
                preparedStatementSiteInsert.setString(3, siteName);
                preparedStatementSiteInsert.setString(4, dateCompilation);
                preparedStatementSiteInsert.setString(5, dateUpdate);
                preparedStatementSiteInsert.setString(6, dateSpa);
                preparedStatementSiteInsert.setString(7, respondent);
                preparedStatementSiteInsert.setString(8, description);
                preparedStatementSiteInsert.setString(9, latitude);
                preparedStatementSiteInsert.setString(10, longitude);
                preparedStatementSiteInsert.setString(11, area);
                preparedStatementSiteInsert.setString(12, altMin);
                preparedStatementSiteInsert.setString(13, altMax);
                preparedStatementSiteInsert.setString(14, altMean);
                preparedStatementSiteInsert.setString(15, "NATURA2000");
                preparedStatementSiteInsert.setString(16, "80");
                preparedStatementSiteInsert.setString(17, siteDesignation);
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

                preparedStatementSiteAttribute.setString(1, siteCode);
                preparedStatementSiteAttribute.setString(2, "TYPE");
                preparedStatementSiteAttribute.setString(3, "TEXT");
                preparedStatementSiteAttribute.setString(4, siteType);
                preparedStatementSiteAttribute.setString(5, "sdfxml");
                preparedStatementSiteAttribute.addBatch();

                preparedStatementSiteAttribute.executeBatch();
                preparedStatementSiteAttribute.clearParameters();
            }

            if (qName.equalsIgnoreCase("HCode")) {
                habitatCode = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("HCover")) {
                habitatCover = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("HRepresentatity")) {
                habitatRepresentatity = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("HRelsurface")) {
                habitatRelsurface = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("HConsStatus")) {
                habitatConsStatus = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("HGlobalAssesment")) {
                habitatGlobalAssesment = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("Habitat")) {

                if (habitatCode != null && habitatCode.length() > 0) {
                    String habitatIdNatObject = getHabitatNatObjectId(habitatCode);

                    if (habitatIdNatObject != null && habitatIdNatObject.length() > 0) {

                        maxReportAttributeId++;

                        preparedStatementNatObjectReportType.setString(1, siteNatureObjectId);
                        preparedStatementNatObjectReportType.setString(2, habitatIdNatObject);
                        preparedStatementNatObjectReportType.setInt(3, -1);
                        preparedStatementNatObjectReportType.setInt(4, -1);
                        preparedStatementNatObjectReportType.setInt(5, maxReportAttributeId);
                        preparedStatementNatObjectReportType.setInt(6, -1);

                        preparedStatementNatObjectReportType.executeUpdate();

                        setSitesTab(siteNatureObjectId, Constants.SITES_TAB_HABITAT_TYPES);
                        setHabitatTabSites(habitatIdNatObject);

                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_HABITAT_SOURCE_TABLE);
                        preparedStatementReportAttribute.setString(3, "TEXT");
                        preparedStatementReportAttribute.setString(4, "habit2");
                        preparedStatementReportAttribute.addBatch();

                        if (habitatCover != null && habitatCover.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_HABITAT_COVER);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, habitatCover);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (habitatRepresentatity != null && habitatRepresentatity.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_HABITAT_REPRESENTATIVITY);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, habitatRepresentatity);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (habitatRelsurface != null && habitatRelsurface.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_HABITAT_RELATIVE_SURFACE);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, habitatRelsurface);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (habitatConsStatus != null && habitatConsStatus.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_HABITAT_CONSERVATION);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, habitatConsStatus);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (habitatGlobalAssesment != null && habitatGlobalAssesment.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_HABITAT_GLOBAL);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, habitatGlobalAssesment);
                            preparedStatementReportAttribute.addBatch();
                        }

                        preparedStatementReportAttribute.executeBatch();
                        preparedStatementReportAttribute.clearParameters();

                    } else {
                        preparedStatementSiteAttribute.setString(1, siteCode);
                        preparedStatementSiteAttribute.setString(2, "HABITAT_CODE_" + habitatCode);
                        preparedStatementSiteAttribute.setString(3, "TEXT");
                        preparedStatementSiteAttribute.setString(4, habitatCode);
                        preparedStatementSiteAttribute.setString(5, "habit2");
                        preparedStatementSiteAttribute.addBatch();

                        preparedStatementSiteAttribute.setString(1, siteCode);
                        preparedStatementSiteAttribute.setString(2, "HABITAT_COVER_" + habitatCode);
                        preparedStatementSiteAttribute.setString(3, "NUMBER");
                        preparedStatementSiteAttribute.setString(4, habitatCover);
                        preparedStatementSiteAttribute.setString(5, "habit2");
                        preparedStatementSiteAttribute.addBatch();

                        if (habitatRepresentatity != null && habitatRepresentatity.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "HABITAT_REPRESENTATIVITY_" + habitatCode);
                            preparedStatementSiteAttribute.setString(3, "NUMBER");
                            preparedStatementSiteAttribute.setString(4, habitatRepresentatity);
                            preparedStatementSiteAttribute.setString(5, "habit2");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (habitatRelsurface != null && habitatRelsurface.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "HABITAT_RELATIVE_SURFACE_" + habitatCode);
                            preparedStatementSiteAttribute.setString(3, "NUMBER");
                            preparedStatementSiteAttribute.setString(4, habitatRelsurface);
                            preparedStatementSiteAttribute.setString(5, "habit2");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (habitatConsStatus != null && habitatConsStatus.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "HABITAT_CONSERVATION_" + habitatCode);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, habitatConsStatus);
                            preparedStatementSiteAttribute.setString(5, "habit2");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (habitatGlobalAssesment != null && habitatGlobalAssesment.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "HABITAT_GLOBAL_" + habitatCode);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, habitatGlobalAssesment);
                            preparedStatementSiteAttribute.setString(5, "habit2");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        preparedStatementSiteAttribute.executeBatch();
                        preparedStatementSiteAttribute.clearParameters();
                    }

                    habitatCover = null;
                    habitatRepresentatity = null;
                    habitatRelsurface = null;
                    habitatConsStatus = null;
                    habitatGlobalAssesment = null;
                }
            }

            if (qName.equalsIgnoreCase("SpeciesCode")) {
                speciesCode = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("SpeciesName")) {
                speciesName = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Resident")) {
                speciesResident = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Population")) {
                speciesPopulation = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Conservation")) {
                speciesConservation = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("IsolationFactor")) {
                speciesIsolation = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("GlobalImportance")) {
                speciesGlobal = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Staging")) {
                speciesStaging = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Winter")) {
                speciesWinter = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Breeding")) {
                speciesBreeding = buf.toString().trim();
            }

            // values are one step ahead
            if (qName.equalsIgnoreCase("EcologicalInformation32A")) {
                ecoInfo = Constants.N2000_SPECIES_GROUP_BIRD;
            } else if (qName.equalsIgnoreCase("EcologicalInformation32B")) {
                ecoInfo = Constants.N2000_SPECIES_GROUP_MAMMAL;
            } else if (qName.equalsIgnoreCase("EcologicalInformation32C")) {
                ecoInfo = Constants.N2000_SPECIES_GROUP_AMPREP;
            } else if (qName.equalsIgnoreCase("EcologicalInformation32D")) {
                ecoInfo = Constants.N2000_SPECIES_GROUP_FISHES;
            } else if (qName.equalsIgnoreCase("EcologicalInformation32E")) {
                ecoInfo = Constants.N2000_SPECIES_GROUP_INVERT;
            } else if (qName.equalsIgnoreCase("EcologicalInformation32F")) {
                ecoInfo = Constants.N2000_SPECIES_GROUP_PLANT;
            }

            if (qName.equalsIgnoreCase("Species")) {
                if (speciesCode != null && speciesCode.length() > 0) {
                    String speciesIdNatObject = getSpeciesNatObjectId(speciesCode);

                    if (speciesIdNatObject != null && speciesIdNatObject.length() > 0) {

                        if (ecoInfo == null || ecoInfo.length() == 0) {
                            ecoInfo = Constants.N2000_SPECIES_GROUP_BIRD;
                        }

                        maxReportAttributeId++;

                        preparedStatementNatObjectReportType.setString(1, siteNatureObjectId);
                        preparedStatementNatObjectReportType.setString(2, speciesIdNatObject);
                        preparedStatementNatObjectReportType.setInt(3, -1);
                        preparedStatementNatObjectReportType.setInt(4, -1);
                        preparedStatementNatObjectReportType.setInt(5, maxReportAttributeId);
                        preparedStatementNatObjectReportType.setInt(6, -1);

                        preparedStatementNatObjectReportType.executeUpdate();

                        setSitesTab(siteNatureObjectId, Constants.SITES_TAB_FAUNA_FLORA);
                        setSpeciesTabSites(speciesIdNatObject);

                        if (speciesResident != null && speciesResident.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_RESIDENT);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesResident);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesPopulation != null && speciesPopulation.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_POPULATION);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesPopulation);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesConservation != null && speciesConservation.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_CONSERVATION);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesConservation);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesIsolation != null && speciesIsolation.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_ISOLATION);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesIsolation);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesGlobal != null && speciesGlobal.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_GLOBAL);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesGlobal);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesStaging != null && speciesStaging.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_STAGING);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesStaging);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesWinter != null && speciesWinter.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_WINTER);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesWinter);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (speciesBreeding != null && speciesBreeding.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_BREEDING);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, speciesBreeding);
                            preparedStatementReportAttribute.addBatch();
                        }

                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_OTHER_SPECIES);
                        preparedStatementReportAttribute.setString(3, "TEXT");
                        preparedStatementReportAttribute.setString(4, "False");
                        preparedStatementReportAttribute.addBatch();

                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_SOURCE_TABLE);
                        preparedStatementReportAttribute.setString(3, "TEXT");
                        preparedStatementReportAttribute.setString(4, ecoInfo);
                        preparedStatementReportAttribute.addBatch();

                        preparedStatementReportAttribute.executeBatch();
                        preparedStatementReportAttribute.clearParameters();
                    } else {
                        if (speciesName != null && speciesName.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesName);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesResident != null && speciesResident.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_RESIDENT_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesResident);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesPopulation != null && speciesPopulation.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_POPULATION_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesPopulation);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesBreeding != null && speciesBreeding.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_BREEDING_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesBreeding);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesWinter != null && speciesWinter.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_WINTERING_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesWinter);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesStaging != null && speciesStaging.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_STAGING_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesStaging);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesConservation != null && speciesConservation.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_CONSERVATION_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesConservation);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesIsolation != null && speciesIsolation.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_ISOLATION_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesIsolation);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        if (speciesGlobal != null && speciesGlobal.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_GLOBAL_" + speciesName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, speciesGlobal);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                    }
                    speciesCode = null;
                    speciesName = null;
                    speciesResident = null;
                    speciesPopulation = null;
                    speciesConservation = null;
                    speciesIsolation = null;
                    speciesGlobal = null;
                    speciesStaging = null;
                    speciesWinter = null;
                    speciesBreeding = null;
                }
            }

            if (qName.equalsIgnoreCase("Group")) {
                otherSpeciesGroup = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("SciName")) {
                otherSpeciesSciName = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Population")) {
                otherSpeciesPopulation = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Motivation")) {
                otherSpeciesMotivation = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("OtherSpecies")) {

                if (otherSpeciesSciName != null && otherSpeciesSciName.length() > 0) {

                    String speciesIdNatObject = getSpeciesNatObjectIdByName(otherSpeciesSciName);

                    if (speciesIdNatObject != null && speciesIdNatObject.length() > 0) {

                        maxReportAttributeId++;

                        preparedStatementNatObjectReportType.setString(1, siteNatureObjectId);
                        preparedStatementNatObjectReportType.setString(2, speciesIdNatObject);
                        preparedStatementNatObjectReportType.setInt(3, -1);
                        preparedStatementNatObjectReportType.setInt(4, -1);
                        preparedStatementNatObjectReportType.setInt(5, maxReportAttributeId);
                        preparedStatementNatObjectReportType.setInt(6, -1);

                        preparedStatementNatObjectReportType.executeUpdate();

                        setSitesTab(siteNatureObjectId, Constants.SITES_TAB_FAUNA_FLORA);
                        setSpeciesTabSites(speciesIdNatObject);

                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_SPECIES_SOURCE_TABLE);
                        preparedStatementReportAttribute.setString(3, "TEXT");
                        preparedStatementReportAttribute.setString(4, "spec");
                        preparedStatementReportAttribute.addBatch();

                        if (otherSpeciesMotivation != null && otherSpeciesMotivation.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_OTHER_SPECIES_MOTIVATION);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, otherSpeciesMotivation);
                            preparedStatementReportAttribute.addBatch();
                        }
                        if (otherSpeciesPopulation != null && otherSpeciesPopulation.length() > 0) {
                            preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                            preparedStatementReportAttribute.setString(2, Constants.REPORT_ATTRIBUTE_OTHER_SPECIES_POPULATION);
                            preparedStatementReportAttribute.setString(3, "TEXT");
                            preparedStatementReportAttribute.setString(4, otherSpeciesPopulation);
                            preparedStatementReportAttribute.addBatch();
                        }

                        preparedStatementReportAttribute.executeBatch();
                        preparedStatementReportAttribute.clearParameters();

                    } else {
                        preparedStatementSiteAttribute.setString(1, siteCode);
                        preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_" + otherSpeciesSciName);
                        preparedStatementSiteAttribute.setString(3, "TEXT");
                        preparedStatementSiteAttribute.setString(4, "spec");
                        preparedStatementSiteAttribute.setString(5, "spec");
                        preparedStatementSiteAttribute.addBatch();

                        setSitesTab(siteNatureObjectId, Constants.SITES_TAB_FAUNA_FLORA);

                        if (otherSpeciesGroup != null && otherSpeciesGroup.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_TAXGROUP_" + otherSpeciesSciName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, otherSpeciesGroup);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }
                        if (otherSpeciesPopulation != null && otherSpeciesPopulation.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_POPULATION_" + otherSpeciesSciName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, otherSpeciesPopulation);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }
                        if (otherSpeciesMotivation != null && otherSpeciesMotivation.length() > 0) {
                            preparedStatementSiteAttribute.setString(1, siteCode);
                            preparedStatementSiteAttribute.setString(2, "OTHER_SPECIES_MOTIVATION_" + otherSpeciesSciName);
                            preparedStatementSiteAttribute.setString(3, "TEXT");
                            preparedStatementSiteAttribute.setString(4, otherSpeciesMotivation);
                            preparedStatementSiteAttribute.setString(5, "spec");
                            preparedStatementSiteAttribute.addBatch();
                        }

                        preparedStatementSiteAttribute.executeBatch();
                        preparedStatementSiteAttribute.clearParameters();
                    }

                    otherSpeciesGroup = null;
                    otherSpeciesSciName = null;
                    otherSpeciesPopulation = null;
                    otherSpeciesMotivation = null;
                }
            }

            if (qName.equalsIgnoreCase("Description")) {
                siteDescriptionHabClassDesc = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Cover")) {
                siteDescriptionHabClassCover = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("HabitatClasses")) {
                if (siteDescriptionHabClassDesc != null && siteDescriptionHabClassCover != null) {
                    insertSiteDescriptionHabitatClasses(siteDescriptionHabClassDesc, siteCode, siteDescriptionHabClassCover);
                }
                siteDescriptionHabClassDesc = null;
                siteDescriptionHabClassCover = null;
            }

            if (qName.equalsIgnoreCase("OtherCharacteristics")) {
                siteDescriptionOtherCharacteristics = buf.toString().trim();
                if(siteDescriptionOtherCharacteristics.length() > 0)
                    insertSiteAttribute(siteCode, "HABITAT_CHARACTERIZATION", siteDescriptionOtherCharacteristics, "TEXT", "habit2");
            } else if (qName.equalsIgnoreCase("QualityImportance")) {
                siteDescriptionQualityImportance = buf.toString().trim();
                if(siteDescriptionQualityImportance.length() > 0)
                insertSiteAttribute(siteCode, "QUALITY", siteDescriptionQualityImportance, "TEXT", "habit2");
            } else if (qName.equalsIgnoreCase("Vulnerability")) {
                siteDescriptionVulnerability = buf.toString().trim();
                if(siteDescriptionVulnerability.length() > 0)
                    insertSiteAttribute(siteCode, "VULNERABILITY", siteDescriptionVulnerability, "TEXT", "habit2");
            } else if (qName.equalsIgnoreCase("SiteDesignation")) {
                siteDescriptionSiteDesignation = buf.toString().trim();
                if(siteDescriptionSiteDesignation.length() > 0)
                    insertSiteAttribute(siteCode, "SITE_DESIGNATION", siteDescriptionSiteDesignation, "TEXT", "habit2");
            } else if (qName.equalsIgnoreCase("Documentation")) {
                siteDescriptionDocumentation = buf.toString().trim();
                if(siteDescriptionDocumentation.length() > 0)
                    insertSiteAttribute(siteCode, "DOCUMENTATION", siteDescriptionDocumentation, "TEXT", "habit2");
            }

            if (qName.equalsIgnoreCase("Code")) {
                siteProtectionDesigTypeCode = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Cover")) {
                siteProtectionDesigTypeCover = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("DesignationTypes")) {
                if (siteProtectionDesigTypeCode != null && siteProtectionDesigTypeCover != null) {
                    preparedStatementSiteRelatedDesignations.setString(1, siteCode);
                    preparedStatementSiteRelatedDesignations.setString(2, siteProtectionDesigTypeCode);
                    preparedStatementSiteRelatedDesignations.setString(3, geoscopeId);
                    preparedStatementSiteRelatedDesignations.setString(4, siteProtectionDesigTypeCover);

                    preparedStatementSiteRelatedDesignations.executeUpdate();

                    setSitesTab(siteNatureObjectId, Constants.SITES_TAB_DESIGNATION);
                }
            }

            // Insert site MANAGER
            if (qName.equalsIgnoreCase("Responsible")) {
                responsible = buf.toString().trim();
                preparedStatementUpdateManager.setString(1, responsible);
                preparedStatementUpdateManager.setString(2, siteCode);

                preparedStatementUpdateManager.executeUpdate();
            }

            if (qName.equalsIgnoreCase("Code")) {
                siteImpactsCode = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Intensity")) {
                siteImpactsIntensity = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Influence")) {
                siteImpactsInfluence = buf.toString().trim();
            } else if (qName.equalsIgnoreCase("Percent")) {
                siteImpactsPercent = buf.toString().trim();
            }

            if (qName.equalsIgnoreCase("WithinSite") || qName.equalsIgnoreCase("AroundSite")) {
                if (siteImpactsCode != null && siteImpactsCode.length() > 0) {

                    maxReportTypeId++;
                    preparedStatementReportType.setInt(1, maxReportTypeId);
                    preparedStatementReportType.setString(2, siteImpactsCode);
                    preparedStatementReportType.setString(3, "HUMAN_ACTIVITY");
                    preparedStatementReportType.executeUpdate();

                    maxReportAttributeId++;

                    preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                    preparedStatementReportAttribute.setString(2, "IN_OUT");
                    preparedStatementReportAttribute.setString(3, "TEXT");
                    if (qName.equalsIgnoreCase("WithinSite")) {
                        preparedStatementReportAttribute.setString(4, "I");
                    } else {
                        preparedStatementReportAttribute.setString(4, "O");
                    }
                    preparedStatementReportAttribute.addBatch();

                    if (siteImpactsIntensity != null && siteImpactsIntensity.length() > 0) {
                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, "INTENSITY");
                        preparedStatementReportAttribute.setString(3, "TEXT");
                        preparedStatementReportAttribute.setString(4, siteImpactsIntensity);
                        preparedStatementReportAttribute.addBatch();
                    }
                    if (siteImpactsInfluence != null && siteImpactsInfluence.length() > 0) {
                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, "INFLUENCE");
                        preparedStatementReportAttribute.setString(3, "TEXT");
                        preparedStatementReportAttribute.setString(4, siteImpactsInfluence);
                        preparedStatementReportAttribute.addBatch();
                    }
                    if (siteImpactsPercent != null && siteImpactsPercent.length() > 0) {
                        preparedStatementReportAttribute.setInt(1, maxReportAttributeId);
                        preparedStatementReportAttribute.setString(2, "COVER");
                        preparedStatementReportAttribute.setString(3, "NUMBER");
                        preparedStatementReportAttribute.setString(4, siteImpactsPercent);
                        preparedStatementReportAttribute.addBatch();
                    }

                    preparedStatementReportAttribute.executeBatch();
                    preparedStatementReportAttribute.clearParameters();

                    preparedStatementNatObjectReportType.setString(1, siteNatureObjectId);
                    preparedStatementNatObjectReportType.setInt(2, -1);
                    preparedStatementNatObjectReportType.setInt(3, -1);
                    preparedStatementNatObjectReportType.setInt(4, maxReportTypeId);
                    preparedStatementNatObjectReportType.setInt(5, maxReportAttributeId);
                    preparedStatementNatObjectReportType.setInt(6, -1);

                    preparedStatementNatObjectReportType.executeUpdate();

                    setSitesTab(siteNatureObjectId, Constants.SITES_TAB_OTHER_INFO);

                    siteImpactsCode = null;
                    siteImpactsIntensity = null;
                    siteImpactsInfluence = null;
                    siteImpactsPercent = null;

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<String> execute(BufferedInputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        try {
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
                + "ID_GEOSCOPE, ID_DESIGNATION, LENGTH) VALUES "
                + "(?,?,?,?,'','',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            parseDocument();
            con.commit();
        } catch (Exception e) {
            con.rollback();
            con.commit();

            if (siteCode != null) {
                logger.debug(e, e);
                errors.add("Error! Site ID: " + siteCode + " Error Message: " + e.getMessage());
            } else {
                logger.debug(e, e);
                errors.add("Error Message: " + e.getMessage());
            }
            // throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
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

        return errors;

    }

    private String getHabitatNatObjectId(String habCode) {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_habitat WHERE CODE_2000='" + habCode + "'";
        String noId = sqlUtilities.ExecuteSQL(query);

        return noId;
    }

    private void getSiteNatObjectId() {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ID_SITE='" + siteCode + "'";

        siteNatureObjectId = sqlUtilities.ExecuteSQL(query);
        if (siteNatureObjectId == null || siteNatureObjectId.length() == 0) {
            maxNoIdInt++;
            siteNatureObjectId = new Integer(maxNoIdInt).toString();
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

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /*private String getGeoscopeIdEu() throws Exception {
        String ret = sqlUtilities.ExecuteSQL(
        "SELECT ID_GEOSCOPE FROM chm62edt_country WHERE EUNIS_AREA_CODE = 'EU' ORDER BY ID_GEOSCOPE");

        if (ret == null || ret.length() == 0) {
            ret = "-1";
        }
        return ret;
    }*/

    private String getGeoscopeId() {
        String query = "SELECT ID_GEOSCOPE FROM chm62edt_country WHERE ISO_2L = '"
            + siteCode.substring(0, 2) + "' ORDER BY ID_GEOSCOPE";
        String geoId = sqlUtilities.ExecuteSQL(query);

        return geoId;
    }

    private int getMaxId(String query) throws ParseException {
        String maxId = sqlUtilities.ExecuteSQL(query);
        int maxIdInt = 0;

        if (maxId != null && maxId.length() > 0) {
            maxIdInt = new Integer(maxId).intValue();
        }

        return maxIdInt;
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

    private boolean bioregionExists(String bioRegionCode) {
        boolean ret = false;
        String query = "SELECT ID_BIOGEOREGION FROM chm62edt_biogeoregion WHERE UPPER(NAME) = '"
            + EunisUtil.replaceTags(bioRegionCode.toUpperCase()) + "'";
        String brId = sqlUtilities.ExecuteSQL(query);

        if (brId != null && brId.length() > 0) {
            ret = true;
        }

        return ret;
    }

    private String getSpeciesNatObjectId(String speciesCode) {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_species WHERE CODE_2000='" +speciesCode+"'";
        String noId = sqlUtilities.ExecuteSQL(query);
        return noId;
    }

    private String getSpeciesNatObjectIdByName(String sciName) {
        String query = "SELECT ID_NATURE_OBJECT FROM chm62edt_species WHERE SCIENTIFIC_NAME='"
            + EunisUtil.replaceTags(sciName) + "'";
        String noId = sqlUtilities.ExecuteSQL(query);

        return noId;
    }

    private void insertSiteDescriptionHabitatClasses(String desc, String siteId, String cover) throws Exception {
        String code_query = "SELECT SUBSTRING(name,length(name) - instr(reverse(name),'_') + 2) AS CODE "
            + "FROM chm62edt_site_attributes WHERE VALUE = '" + desc + "' LIMIT 1";
        String code = sqlUtilities.ExecuteSQL(code_query);

        // If code doesn't exist figure out some test code. XML files doesn't contain habitat code.
        if (code == null || code.length() == 0) {
            code = "XX" + siteDescriptionHabClassCodeCnt;
            siteDescriptionHabClassCodeCnt++;
        }

        if (code != null && code.length() > 0) {
            insertSiteAttribute(siteId, "HABITAT_NAME_EN_" + code, desc, "TEXT", "habit2");
            insertSiteAttribute(siteId, "HABITAT_COVER_" + code, cover, "NUMBER", "habit2");
            insertSiteAttribute(siteId, "HABITAT_CODE_" + code, code, "TEXT", "habit2");
        }
    }

    private void insertSiteAttribute(String siteId, String name, String value, String type, String sourceTable) throws Exception {
        preparedStatementSiteAttribute.setString(1, siteId);
        preparedStatementSiteAttribute.setString(2, name);
        preparedStatementSiteAttribute.setString(3, type);
        preparedStatementSiteAttribute.setString(4, value);
        preparedStatementSiteAttribute.setString(5, sourceTable);
        preparedStatementSiteAttribute.executeUpdate();
    }

    private void setSitesTab(String idNatureObject, String tabName) throws Exception {
        PreparedStatement ps = null;

        try {
            if (tabName.equals(Constants.SITES_TAB_GENERAL)) {
                String query = "INSERT IGNORE INTO chm62edt_tab_page_sites(ID_NATURE_OBJECT,GENERAL_INFORMATION) VALUES(?,'Y')";

                ps = con.prepareStatement(query);
            } else {
                String query = "UPDATE chm62edt_tab_page_sites SET " + tabName
                + "='Y' WHERE ID_NATURE_OBJECT=?";

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

    private String parseDate(String input) {
        String ret = "";

        if (input != null && input.length() > 0) {
            if (input.contains("T00:00:00")) {
                int end = input.indexOf("T");
                String date = input.substring(0, end);

                ret = date.replace("-", "");
            }
        }
        return ret;
    }
}
