package eionet.eunis.stripes.actions;

import eionet.eunis.dao.DaoFactory;
import eionet.eunis.dao.ISpeciesFactsheetDao;
import eionet.eunis.dto.*;
import eionet.eunis.rdf.LinkedData;
import eionet.eunis.rdf.LinkedDataQuery;
import eionet.eunis.stripes.viewdto.SitesByNatureObjectViewDTO;
import eionet.eunis.util.Constants;
import eionet.eunis.util.JstlFunctions;
import eionet.sparqlClient.helpers.ResultValue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sourceforge.stripes.action.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import ro.finsiel.eunis.factsheet.species.*;
import ro.finsiel.eunis.jrfTables.*;
import ro.finsiel.eunis.jrfTables.species.factsheet.SitesByNatureObjectPersist;
import ro.finsiel.eunis.search.CountryUtil;
import ro.finsiel.eunis.search.UniqueVector;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.species.SpeciesSearchUtility;
import ro.finsiel.eunis.search.species.VernacularNameWrapper;
import ro.finsiel.eunis.search.species.factsheet.PublicationWrapper;
import ro.finsiel.eunis.utilities.EunisUtil;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * ActionBean for species factsheet. Data is loaded from {@link ro.finsiel.eunis.factsheet.species.SpeciesFactsheet} and
 * {@link ro.finsiel.eunis.jrfTables.SpeciesNatureObjectPersist}.
 *
 * @author Aleksandr Ivanov <a href="mailto:aleksandr.ivanov@tietoenator.com">contact</a>
 */
@UrlBinding("/species/{idSpecies}/{tab}")
public class SpeciesFactsheetActionBean extends AbstractStripesAction {

    // List of Habitats and Birds annexes
    private static Set<Integer> habitatsDirectiveIdDc = new HashSet<Integer>();
    private static Set<Integer> birdsDirectiveIdDc = new HashSet<Integer>();
    private Integer habitatsDirectiveIdDcII = 2325;

    static {
        habitatsDirectiveIdDc.add(2324);
        habitatsDirectiveIdDc.add(2325);
        habitatsDirectiveIdDc.add(2326);
        habitatsDirectiveIdDc.add(2327);

        birdsDirectiveIdDc.add(2441);
        birdsDirectiveIdDc.add(2456);
        birdsDirectiveIdDc.add(2457);
        birdsDirectiveIdDc.add(2440);
        birdsDirectiveIdDc.add(2482);
    }

    /** The argument given. Can be a species number or scientific name */
    private String idSpecies;
    private int idSpeciesLink;

    private SpeciesFactsheet factsheet;
    private String scientificName = "";
    private String author = "";

    /**
     * senior synonym name.
     */
    private String seniorSpecies;

    /**
     * senior synonym ID.
     */
    private int seniorIdSpecies;

    /** Variables for the "general" tab. */
    private List<PictureDTO> pics;
    private SpeciesNatureObjectPersist specie;
    private List<ClassificationDTO> classifications;
    private List<String> breadcrumbClassificationExpands;
    private String authorDate;

    private String kingdomname;
    /** IUCN Redlist number */
    private String redlistLink;
    private String scientificNameURL;
    private String speciesName;
    /** World Register of Marine Species - also has seals etc. */
    private String wormsid;
    /** Natura 2000 identifier in chm62edt_nature_object_attributes. */
    private String n2000id;
    /** Fauna Europea number */
    private String faeu;
    private PublicationWrapper speciesBook;
    /** ITIS TSN number. */
    private String itisTSN;
    /** NCBI number */
    private String ncbi;
    private ArrayList<LinkDTO> links;

    LinkDTO nobanisLink = null;
    LinkDTO nobanisFactsheetLink = null;
    LinkDTO conservationStatusPDF = null;
    LinkDTO conservationStatus = null;
    List<LinkDTO> art12expert = new ArrayList<>();
    List<LinkDTO> art12factsheet = new ArrayList<>();

    /** List of conservation statuses. */
    private List<NationalThreatWrapper> consStatus;
    /** Conservation status on World level. */
    private NationalThreatWrapper consStatusWO;
    /** Conservation status on European level. */
    private NationalThreatWrapper consStatusEU;
    /** Conservation status on EU25 level. */
    private NationalThreatWrapper consStatusE25;

    private List<SpeciesNatureObjectPersist> subSpecies;
    private List<SpeciesNatureObjectPersist> parentSpecies;
    private String domainName;
    private Hashtable<String, AttributeDto> natObjectAttributes;

    /** Common names tab variables. */
    private List<VernacularNameWrapper> vernNames;

    /** geo tab variables. */
    private Vector<GeographicalStatusWrapper> bioRegions;
    boolean showGeoDistribution = false;
    private String faoCode;
    private String gbifCode;
    private String filename;
    private UniqueVector colorURL;
    private String mapserverURL;
    private String parameters;
    private Hashtable<String, String> statusColorPair;

    /** Grid distribution tab variables. */
    private boolean gridDistSuccess;

    /** Sites distribution tab variables. */
    private List<SitesByNatureObjectPersist> speciesSites;
    private String mapIds;
    private List<SitesByNatureObjectPersist> subSpeciesSites;
    private String subMapIds;

    /** LinkedData tab variables. */
    private List<ForeignDataQueryDTO> queries;
    private String query;
    private ArrayList<Map<String, Object>> queryResultCols;
    private ArrayList<HashMap<String, ResultValue>> queryResultRows;
    private String attribution;
    /** Conservation status tab variables. */
    private List<ForeignDataQueryDTO> conservationStatusQueries;
    private String conservationStatusQuery;
    private String conservationStatusAttribution;

    private LinkedHashMap<String, ArrayList<Map<String, Object>>> conservationStatusQueryResultCols =
            new LinkedHashMap<String, ArrayList<Map<String, Object>>>();
    private LinkedHashMap<String, ArrayList<HashMap<String, ResultValue>>> conservationStatusQueryResultRows =
            new LinkedHashMap<String, ArrayList<HashMap<String, ResultValue>>>();

    private boolean rangeLayer;
    private boolean distributionLayer;

    private Map<String, List<Chm62edtNatureObjectAttributesPersist>> natureObjectAttributesMap;

    // QuickFactSheet params
    private int synonymsCount;
    private List synonyms;
    private int vernNamesCount;
    private int speciesSitesCount;

    private int habitatsCount;
    private String authorYear;
    private String pageUrl;
    private String englishName = null;
    private String speciesTitle;

    // Legals params
    private List<LegalStatusWrapper> legalStatuses;
    private String unepWcmcPageLink;

    // Sites
    private List<SitesByNatureObjectViewDTO> speciesSitesTable;
    private List<SitesByNatureObjectViewDTO> subSpeciesSitesTable;
    private String scientificNameUrlEncoded;

    private boolean habitatsDirective = false;
    private boolean birdsDirective = false;

    private int otherAgreements = 0;

    private List<LegalStatusWrapper> parentLegal;

    private Set<EcosystemsPersist> uniqueEcosystems;
    private boolean habitatsDirectiveII;

    private String art12status = null;

    /**
     * Default Stripes handler
     * @return Stripes resolution
     */
    @DefaultHandler
    public Resolution index() {
        String idSpeciesText = null;

        ISpeciesFactsheetDao dao = DaoFactory.getDaoFactory().getSpeciesFactsheetDao();

        // sanity checks
        if (StringUtils.isBlank(idSpecies) && idSpeciesLink == 0) {
            factsheet = new SpeciesFactsheet(0, 0);
        }
        int mainIdSpecies = 0;

        // get idSpecies based on the request param.
        if (StringUtils.isNumeric(idSpecies)) {
            mainIdSpecies = new Integer(idSpecies);
        } else if (!StringUtils.isBlank(idSpecies)) {
            idSpeciesText = idSpecies;
            mainIdSpecies = dao.getIdSpeciesForScientificName(this.idSpecies);
        }

        seniorIdSpecies = dao.getCanonicalIdSpecies(mainIdSpecies);

        // it is a synonym, check the senior synonym name
        if (mainIdSpecies != seniorIdSpecies) {
            seniorSpecies = dao.getScientificName(seniorIdSpecies);
        }

        factsheet = new SpeciesFactsheet(mainIdSpecies, mainIdSpecies);

        if (StringUtils.isNotBlank(idSpeciesText) && !factsheet.exists()) {
            // redirecting to more general search in case user tried text based search
            String redirectUrl =
                    "/species-names-result.jsp?pageSize=10" + "&relationOp=2&typeForm=0&showGroup=true&showOrder=true"
                            + "&showFamily=true&showScientificName=true&showVernacularNames=false"
                            + "&showValidName=true&searchSynonyms=true&sort=2&ascendency=0" + "&scientificName=" + idSpeciesText;

            return new RedirectResolution(redirectUrl);
        }

        if (factsheet.exists()) {
            // set up some vars used in the presentation layer
            setMetaDescription(factsheet.getSpeciesDescription());
            scientificName = StringEscapeUtils.escapeHtml(factsheet.getSpeciesNatureObject().getScientificName());
            author = StringEscapeUtils.escapeHtml(factsheet.getSpeciesNatureObject().getAuthor());

            specie = factsheet.getSpeciesNatureObject();

            generalTabActions();

            // Sets all actionBean values for quickfactsheet
            setQuickFactSheetValues();
            // Sets all actionBean values for legals listing.
            setLegalInstruments();
            // Sets all actionBean values for sites
            setSites();
            // Sets all reported area values
            setGeoValues();

            setPictures();

            // Sets data about international threat status
            setConservationStatusData();

            // populates the external data
            linkeddataTabActions(mainIdSpecies, specie.getIdNatureObject());

            if(isBird()){
                populateBirdEcosystems();

                art12status = getNatObjectAttribute(specie.getIdNatureObject(), "art12_status");

            }

            // it is a synonym, populate the synonym fields
            if (mainIdSpecies != seniorIdSpecies) {
                SpeciesFactsheet parent = new SpeciesFactsheet(seniorIdSpecies, seniorIdSpecies);

                List<LegalStatusWrapper> parentLegalTemp = parent.getLegalStatus();
                parentLegal = new ArrayList<LegalStatusWrapper>();

                // search parentLegal for the synonym names
                for(LegalStatusWrapper legalStatusWrapper : parentLegalTemp) {
                    List<Chm62edtReportAttributesPersist> results = new Chm62edtReportAttributesDomain().findCustom("SELECT *" +
                            " FROM chm62edt_report_attributes AS F" +
                            " WHERE F.NAME = 'NAME_IN_DOCUMENT' AND F.ID_REPORT_ATTRIBUTES='" + legalStatusWrapper.getIdReportAttributes() + "'");

                    for(Chm62edtReportAttributesPersist p : results){
                        if(p.getValue().contains(scientificName)){
                            parentLegal.add(legalStatusWrapper);
                        }
                    }
                }
            }

            // Sets country level and biogeo conservation status
            // TODO The methods executes SPARQL query. Consider caching the results or at least load the content with jQuery
            setConservationStatusDetails(mainIdSpecies, specie.getIdNatureObject());
            populateBiogeoAssessment(specie.getIdNatureObject());
        }

        String eeaHome = getContext().getInitParameter("EEA_HOME");
        String btrail = "eea#" + eeaHome + ",home#index.jsp,species#species.jsp,factsheet";

        setBtrail(btrail);

        return new ForwardResolution("/stripes/species-factsheet/species-factsheet.layout.jsp");
    }
    

    /**
     * Prepares all specific information for quickFacktSheet
     */
    private void setQuickFactSheetValues() {
        pageUrl = getContext().getInitParameter("DOMAIN_NAME") + "/species/" + idSpecies;
        if (factsheet.exists()) {
            authorYear = SpeciesFactsheet.getBookDate(factsheet.getTaxcodeObject().IdDcTaxcode());
            scientificName = StringEscapeUtils.escapeHtml(factsheet.getSpeciesNatureObject().getScientificName());
            author = StringEscapeUtils.escapeHtml(factsheet.getSpeciesNatureObject().getAuthor());
            synonyms = factsheet.getSynonymsIterator();
            synonymsCount = synonyms.size();
            vernNames = SpeciesSearchUtility.findVernacularNames(specie.getIdNatureObject());
            vernNamesCount = vernNames.size();
            speciesSites = factsheet.getSitesForSpecies();
            speciesSitesCount = speciesSites.size();
            habitatsCount = factsheet.getHabitatsForSpecies().size();
            
            for (VernacularNameWrapper vernName : vernNames){
                if (vernName.getLanguageCode().toLowerCase().equals("en")){
                    englishName = vernName.getName();
                    break;
                }
            }
            
            speciesTitle = ""; 
            if (englishName != null && englishName.trim().length() > 0){
                speciesTitle += englishName +" - "; 
            }
            speciesTitle += scientificName;
            
            if (author != null && author.trim().length() > 0){
                speciesTitle += " - "+author; 
            }

        }

        // For later refactoring. The following parameteres are used in QuickFactSheet, but initialized outside this method.
        //
        // links = DaoFactory.getDaoFactory().getExternalObjectsDao().getNatureObjectLinks(specie.getIdNatureObject());
        // specie.scientificName
        // speciesName

    }

    /**
     * Set list of picture objects for gallery.
     */
    private void setPictures() {
        String picturePath = getContext().getInitParameter("UPLOAD_DIR_PICTURES_SPECIES");
        pics = factsheet.getPictures(picturePath);

        // http://taskman.eionet.europa.eu/issues/17992
        // display abstract images if no picture available

        if(pics.size() == 0) {
            // different picture by group
            String group = factsheet.getSpeciesGroup();

            PictureDTO pic = new PictureDTO();
            pic.setFilename(EunisUtil.getDefaultPicture(group));
            pic.setDescription("No photo available for this species");
            pic.setSource("Paco Sánchez Aguado");
            pic.setPath(picturePath);

            pics.add(pic);
        }
    }

    /**
     * Load and parse species conservation status data.
     */
    private void setConservationStatusData() {

        if (factsheet != null) {

            consStatus = factsheet.getConservationStatus(factsheet.getSpeciesObject());

            // List of species national threat status.
            if (consStatus != null && consStatus.size() > 0) {
                List<NationalThreatWrapper> newConsStatusList = new ArrayList<NationalThreatWrapper>();
                for (NationalThreatWrapper threat : consStatus) {
                    if (threat.getReference() != null && threat.getReference().contains("IUCN")) {
                        scientificNameURL = scientificName.replace(' ', '+');
                    }
                    newConsStatusList.add(threat);

                    if ("WO".equals(threat.getEunisAreaCode())) {
                        if (consStatusWO == null || consStatusWO.getReferenceYear() < threat.getReferenceYear()) {
                            consStatusWO = threat;
                        }
                    } else if ("EU".equals(threat.getEunisAreaCode())) {
                        if (consStatusEU == null || consStatusEU.getReferenceYear() < threat.getReferenceYear()) {
                            consStatusEU = threat;
                        }
                    } else if ("E25".equals(threat.getEunisAreaCode()) || "E27".equals(threat.getEunisAreaCode())) {
                        if (consStatusE25 == null || consStatusE25.getReferenceYear() < threat.getReferenceYear()) {
                            consStatusE25 = threat;
                        }
                    }
                }
                consStatus = newConsStatusList;
            }
        }
    }


    /**
     * Prepares all specific information for legal instruments section
     */
    private void setLegalInstruments() {
        Vector legals = factsheet.getLegalStatus();

        legalStatuses = new ArrayList<LegalStatusWrapper>();
        for (Object legal : legals) {
            LegalStatusWrapper legalStatus = (LegalStatusWrapper) legal;
            legalStatus.setDetailedReference(Utilities.formatString(Utilities.treatURLSpecialCharacters(legalStatus
                    .getDetailedReference())));
            legalStatus.setLegalText(Utilities.formatString(Utilities.treatURLSpecialCharacters(legalStatus.getLegalText())));
            legalStatus.setComments(Utilities.treatURLSpecialCharacters(legalStatus.getComments()));

            if (null != legalStatus.getUrl()) {
                String sFormattedURL = Utilities.formatString(legalStatus.getUrl()).replaceAll("#", "");
                if (sFormattedURL.length() > 50) {
                    sFormattedURL = sFormattedURL.substring(0, 50) + "...";
                }
                legalStatus.setUrl(Utilities.formatString(Utilities.treatURLSpecialCharacters(legalStatus.getUrl())).replaceAll(
                        "#", ""));
                legalStatus.setFormattedUrl(sFormattedURL);
            }

            legalStatuses.add(legalStatus);

            if(legalStatus.getIdDc().equals(habitatsDirectiveIdDcII)){
                habitatsDirectiveII = true;
            }

            if(habitatsDirectiveIdDc.contains(legalStatus.getIdDc())){
                habitatsDirective = true;
            } else if(birdsDirectiveIdDc.contains(legalStatus.getIdDc())){
                birdsDirective = true;
            } else {
                otherAgreements++;
            }
        }

        // order the legal status
        Collections.sort(legalStatuses);

        List<LinkDTO> natureLinks = DaoFactory.getDaoFactory().getExternalObjectsDao().getNatureObjectLinks(specie.getIdNatureObject());
        links = new ArrayList<LinkDTO>();

        for (LinkDTO link : natureLinks){
            boolean addToLinks = true;     // hidden by http://taskman.eionet.europa.eu/issues/18633
            if (link.getName().toLowerCase().equals("unep-wcmc page")){
                unepWcmcPageLink = link.getUrl();
            }

            if(link.getName().toUpperCase().contains("NOBANIS FACTSHEET")){
                nobanisFactsheetLink = link;
                addToLinks = false;
            } else if(link.getName().toUpperCase().contains("NOBANIS:")){
                nobanisLink = link;
                addToLinks = false;
            }

            if(link.getName().startsWith("Art.12 ")){
                if (link.getName().toLowerCase().contains("experts web viewer")){
                    art12expert.add(link);
                } else {
                    art12factsheet.add(link);
                }
                link.setName(link.getName().replace("Art.12 ", ""));
                addToLinks = false;
            } else if(link.getName().equalsIgnoreCase("Conservation status 2012 - summary (pdf)")){
                conservationStatusPDF = link;
                addToLinks = false;
            } else if (link.getName().toLowerCase().contains("experts web viewer")){
                conservationStatus = link;
                addToLinks = false;
            }

            if(link.getName().equalsIgnoreCase("Invasive species information")){
                addToLinks = false;
            }
            if(link.getName().toLowerCase().contains("bug guide")){
                addToLinks = false;
            }

            if(addToLinks){
                transformLinkData(link);
                links.add(link);
            }
        }

        if(nobanisFactsheetLink == null && nobanisLink != null) {
            links.add(nobanisLink);
            nobanisLink.setDescription("European Network on Invasive Alien Species");
        }

        LinkDTO gbifLink = new LinkDTO();
        links.add(gbifLink);
        gbifLink.setDescription("Global Biodiversity Information Facility");
        gbifLink.setName("GBIF");

        String gbifId = getNatObjectAttribute(specie.getIdNatureObject(), Constants.SAME_SYNONYM_GBIF);
        if(Utilities.isEmptyString(gbifId)){
            try {
                gbifLink.setUrl("https://www.gbif.org/species/" + URLEncoder.encode(specie.getScientificName().replaceAll("\\.", ""), "UTF-8"));
                gbifLink.setName("GBIF search");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            gbifLink.setUrl("https://www.gbif.org/species/" + gbifId);
        }

        if(!Utilities.isEmptyString(wormsid)){
            LinkDTO wormsLink = new LinkDTO();
            wormsLink.setUrl("http://www.marinespecies.org/aphia.php?p=taxdetails&id=" + wormsid);
            wormsLink.setName("WoRMS");
            wormsLink.setDescription("World Register of Marine Species");
            links.add(wormsLink);
        }

        if(!Utilities.isEmptyString(faeu)){
            LinkDTO faunaLink = new LinkDTO();
            faunaLink.setUrl("http://www.faunaeur.org/full_results.php?id=" + faeu);
            faunaLink.setName("Fauna Europaea");
            faunaLink.setDescription("Fauna Europaea");
            links.add(faunaLink);
        } else if(getKingdomname().equalsIgnoreCase("Animals")) {
            LinkDTO faunaSearchLink = new LinkDTO();
            faunaSearchLink.setUrl("http://www.faunaeur.org/index.php?show_what=search%20results&genus=" + getSpecie().getGenus()
                    + "&species=" + getSpeciesName());
            faunaSearchLink.setName("Fauna Europaea search");
            faunaSearchLink.setDescription("Fauna Europaea");
            links.add(faunaSearchLink);
        }

        if(!Utilities.isEmptyString(ncbi)){
            LinkDTO ncbiLink = new LinkDTO();
            ncbiLink.setUrl("http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=" + ncbi);
            ncbiLink.setName("NCBI");
            ncbiLink.setDescription("National Center for Biotechnology Information");
            links.add(ncbiLink);
        } else {
            LinkDTO ncbiSearchLink = new LinkDTO();
            ncbiSearchLink.setUrl("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?" +
                    "doptcmdl=ExternalLink&cmd=Search&db=taxonomy" +
                    "&term=" + JstlFunctions.treatURLSpecialCharacters(getSpecie().getScientificName()));
            ncbiSearchLink.setName("NCBI search");
            ncbiSearchLink.setDescription("National Center for Biotechnology Information");
            links.add(ncbiSearchLink);
        }

        if(Utilities.isEmptyString(itisTSN)){
            LinkDTO itisSearchLink = new LinkDTO();
            itisSearchLink.setUrl("http://www.itis.gov/servlet/SingleRpt/SingleRpt?search_topic=Scientific_Name&" +
                    "search_kingdom=every&search_span=exactly_for&" +
                    "search_value=" + JstlFunctions.treatURLSpecialCharacters(getSpecie().getScientificName()) +
                    "&categories=All&source=html&search_credRating=All");
            itisSearchLink.setName("ITIS search");
            itisSearchLink.setDescription("Interagency Taxonomic Information System");
            links.add(itisSearchLink);
        }

        Collections.sort(links);
    }

    /**
     * Temporary transform to check the design
     *
     * todo: move data to DB
     *
     * @param link
     */
    private void transformLinkData(LinkDTO link){
        if(link.getUrl().contains("catalogueoflife")){
            link.setDescription("Catalogue of Life");
            link.setName(link.getName().replace(": "," (") + ")");
        } else if (link.getUrl().contains("sovon.nl")){
            link.setDescription("European Bird Census Council's Atlas of Breeding Birds");
        } else if (link.getUrl().contains("eol.org")){
            link.setDescription("Encyclopedia of Life");
            link.setName("EoL");
        } else if (link.getUrl().contains("blx1.bto.org")){
            link.setDescription("European bird-ringing");
        } else if (link.getUrl().contains("gbif.org")){
            link.setDescription("Global Biodiversity Information Facility");
        } else if (link.getUrl().contains("fishbase.se")){
            link.setDescription("FishBase");
            link.setName("FishBase");
        } else if (link.getUrl().contains("itis.gov")){
            link.setDescription("Interagency Taxonomic Information System");
            if(link.getName().contains("(valid)")){
                link.setName("ITIS (Valid name)");
            } else if (link.getName().contains("(accepted)")) {
                link.setName("ITIS (accepted)");
            } else if (link.getName().contains("(not accepted)")) {
                link.setName("ITIS (not accepted)");
            } else if (link.getName().contains("(invalid)")) {
                link.setName("ITIS (invalid)");
            }
        } else if (link.getUrl().contains("eu-nomen.eu")){
            link.setDescription("Pan-European Species directories Infrastructure");
            link.setName(link.getName().replaceAll(" page", ""));
        } else if (link.getUrl().contains("speciesplus.net")){
            link.setDescription("Species+ by UNEP-WCMC");
            link.setName("Species+");
        } else if (link.getUrl().contains("wikipedia")){
            link.setDescription("Wikipedia");
            link.setName("Wikipedia");
        }

    }

    public LinkDTO getConservationStatusPDF() {
        return conservationStatusPDF;
    }

    public LinkDTO getConservationStatus() {
        return conservationStatus;
    }

    public LinkDTO getNobanisLink(){
        return nobanisLink;
    }

    public LinkDTO getNobanisFactsheetLink() {
        return nobanisFactsheetLink;
    }

    /**
     * Populate the member variables used in the "sites" tab.
     */
    private void setSites() {

        speciesSitesTable = new ArrayList<SitesByNatureObjectViewDTO>();
        subSpeciesSitesTable = new ArrayList<SitesByNatureObjectViewDTO>();

        // List of sites related to species.
        speciesSites = factsheet.getSitesForSpecies();
        
        try {
            scientificNameUrlEncoded = URLEncoder.encode(scientificName, "UTF-8");
            scientificNameUrlEncoded = scientificNameUrlEncoded.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (SitesByNatureObjectPersist site : speciesSites){
            SitesByNatureObjectViewDTO speciesSite = new SitesByNatureObjectViewDTO();

            speciesSite.setIDSite(site.getIDSite());
            speciesSite.setLatitude(site.getLatitude());
            speciesSite.setLongitude(site.getLongitude());
            speciesSite.setName(site.getName());
            speciesSite.setSourceDB(site.getSourceDB());
            speciesSite.setAreaNameEn(Utilities.formatString(Utilities.treatURLSpecialCharacters(site.getAreaNameEn())));

            Chm62edtCountryPersist country = CountryUtil.findCountry(site.getAreaNameEn());
            speciesSite.setAreaUrl("countries/"+Utilities.treatURLSpecialCharacters(country.getEunisAreaCode()));
            speciesSite.setSiteNameUrl("sites/" + Utilities.formatString(Utilities.treatURLSpecialCharacters(site.getIDSite())));

            if (site.getSourceDB().equals("NATURA2000")){
                speciesSite.setNatura2000(true);
                speciesSitesTable.add(speciesSite);
            }
        }


        // List of sites related to subspecies.
        subSpeciesSites = factsheet.getSitesForSubpecies();
        
        for (SitesByNatureObjectPersist site : subSpeciesSites){
            SitesByNatureObjectViewDTO speciesSite = new SitesByNatureObjectViewDTO();

            speciesSite.setIDSite(site.getIDSite());
            speciesSite.setLatitude(site.getLatitude());
            speciesSite.setLongitude(site.getLongitude());
            speciesSite.setName(site.getName());
            speciesSite.setSourceDB(site.getSourceDB());
            speciesSite.setAreaNameEn(Utilities.formatString(Utilities.treatURLSpecialCharacters(site.getAreaNameEn())));

            Chm62edtCountryPersist country = CountryUtil.findCountry(site.getAreaNameEn());
            speciesSite.setAreaUrl("countries/"+Utilities.treatURLSpecialCharacters(country.getEunisAreaCode()));
            speciesSite.setSiteNameUrl("sites/" + Utilities.formatString(Utilities.treatURLSpecialCharacters(site.getIDSite())));

            if (site.getSourceDB().equals("NATURA2000")){
                speciesSite.setNatura2000(true);
                subSpeciesSitesTable.add(speciesSite);
            }
        }
        
        mapIds = getIds(speciesSites);
        subMapIds = getIds(subSpeciesSites);
    }

    /**
     * Populate the member variables used in the "general" tab.
     *
     */
    private void generalTabActions() {

        speciesBook = factsheet.getSpeciesBook();

        // Get all pictures for species

        try {
            authorDate = SpeciesFactsheet.getBookAuthorDate(factsheet.getTaxcodeObject().IdDcTaxcode());
            breadcrumbClassificationExpands = new ArrayList<String>();
            classifications = factsheet.getClassifications();
            // Extract kingdom name
            if (classifications != null) {
                String classificationExpand = "";
                for (ClassificationDTO classif : classifications) {
                    if (classificationExpand.length() > 0){
                        classificationExpand+=",";    
                    }
                    classificationExpand += classif.getId();
                    breadcrumbClassificationExpands.add(classificationExpand);
                    if (classif.getLevel().equalsIgnoreCase("kingdom")) {
                        kingdomname = classif.getName();
                    }
                }
            }

            if (kingdomname.equalsIgnoreCase("Animalia")) {
                kingdomname = "Animals";
            } else if (kingdomname.equalsIgnoreCase("Plantae")) {
                kingdomname = "Plants";
            } else if (kingdomname.equalsIgnoreCase("Fungi")) {
                kingdomname = "Mushrooms";
            }

            speciesName =
                    (scientificName.trim().contains(" ") ? scientificName.trim().substring(scientificName.indexOf(" ") + 1)
                            : scientificName);

            redlistLink = getNatObjectAttribute(specie.getIdNatureObject(), Constants.SAME_SPECIES_REDLIST);

            // World Register of Marine Species - also has seals etc.
            wormsid = getNatObjectAttribute(specie.getIdNatureObject(), Constants.SAME_SYNONYM_WORMS);

            n2000id = specie.getNatura2000Code();

            if (kingdomname.equalsIgnoreCase("Animals")) {
                faeu = getNatObjectAttribute(specie.getIdNatureObject(), Constants.SAME_SYNONYM_FAEU);
            }

            itisTSN = getNatObjectAttribute(specie.getIdNatureObject(), Constants.SAME_SYNONYM_ITIS);
            ncbi = getNatObjectAttribute(specie.getIdNatureObject(), Constants.SAME_SYNONYM_NCBI);

            subSpecies = factsheet.getSubspecies();
            if (!subSpecies.isEmpty()) {
                List<SpeciesNatureObjectPersist> newList = new ArrayList<SpeciesNatureObjectPersist>();

                for (SpeciesNatureObjectPersist species : subSpecies) {
                    String bad = SpeciesFactsheet.getBookAuthorDate(species.getIdDublinCore());

                    if (bad != null) {
                        species.setBookAuthorDate(bad);
                    }
                    newList.add(species);
                }
                subSpecies = newList;
            }
            
            parentSpecies = factsheet.getParentSpecies();
            if (!parentSpecies.isEmpty()) {
                List<SpeciesNatureObjectPersist> newList = new ArrayList<SpeciesNatureObjectPersist>();

                for (SpeciesNatureObjectPersist species : parentSpecies) {
                    String bad = SpeciesFactsheet.getBookAuthorDate(species.getIdDublinCore());

                    if (bad != null) {
                        species.setBookAuthorDate(bad);
                    }
                    newList.add(species);
                }
                parentSpecies = newList;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get value for given ID_NATURE_OBJECT and attribute name from chm62edt_nature_object_attributes table.
     *
     * @param id - The nature object ID.
     * @param name - attribute name.
     */
    private String getNatObjectAttribute(Integer id, String name) {
        String ret = null;
        if (id != null && name != null) {
            if (natObjectAttributes == null) {
                natObjectAttributes = DaoFactory.getDaoFactory().getExternalObjectsDao().getNatureObjectAttributes(id);
            }
            AttributeDto attr = natObjectAttributes.get(name);
            if (attr != null) {
                ret = attr.getValue();
            }
        }
        return ret;
    }

    /**
     * Populate the member variables used in the "geo" tab.
     */
    private void setGeoValues() {

        // Get FAO code if one exists
        faoCode =
                DaoFactory.getDaoFactory().getNatureObjectAttrDao()
                        .getNatObjAttribute(specie.getIdNatureObject(), Constants.SAME_SPECIES_FIFAO);

        // Get GBIF code if one exists
        gbifCode =
                DaoFactory.getDaoFactory().getNatureObjectAttrDao()
                        .getNatObjAttribute(specie.getIdNatureObject(), Constants.SAME_SYNONYM_GBIF);

        bioRegions = SpeciesFactsheet.getBioRegionIterator(specie.getIdNatureObject(), factsheet.getIdSpecies());
        if (bioRegions.size() > 0) {
            // Display map
            colorURL = new UniqueVector();
            UniqueVector statuses = new UniqueVector();

            // Get all distinct statuses
            for (GeographicalStatusWrapper aRow : bioRegions) {
                statuses.addElement(aRow.getStatus());
            }
            // Compute distinct color for each status
            statusColorPair = ThreatColor.getColorsForMap(statuses);
            Vector<String> addedCountries = new Vector<String>();

            // fix to display in map legend only visible colours
            statuses.clear();
            for (GeographicalStatusWrapper aRow : bioRegions) {
                Chm62edtCountryPersist cntry = aRow.getCountry();

                if (cntry != null && !addedCountries.contains(cntry.getAreaNameEnglish())) {
                    String color = ":H" + statusColorPair.get(aRow.getStatus().toLowerCase());
                    String countryColPair = (cntry.getIso2Wcmc() == null) ? cntry.getIso2l() : cntry.getIso2Wcmc() + color;

                    colorURL.addElement(countryColPair);
                    addedCountries.add(cntry.getAreaNameEnglish());
                    // fix to display in map legend only visible colours
                    statuses.addElement(aRow.getStatus());
                }
            }
            // fix to display in map legend only visible colours
            statusColorPair = ThreatColor.getColorsForMap(statuses);

            int COUNTRIES_PER_MAP = Utilities.checkedStringToInt(getContext().getInitParameter("COUNTRIES_PER_MAP"), 120);

            if (addedCountries.size() < COUNTRIES_PER_MAP) {
                showGeoDistribution = true;
                mapserverURL = getContext().getInitParameter("EEA_MAP_SERVER");
                parameters = "mapType=Standard_B&amp;Q=" + colorURL.getElementsSeparatedByComma() + "&amp;outline=1";
                filename = mapserverURL + "/getmap.asp?" + parameters;
            }
        }

//        setRangeLayer(isSpeciesLayer(scientificName, 1));

        // Hidden according to http://taskman.eionet.europa.eu/issues/19431#note-4
        // also removed by #20530#note-2
        setRangeLayer(false);
        setDistributionLayer(false);

//        setDistributionLayer(isSpeciesLayer(scientificName, 4));
    }

    /**
     * Checks that this species layer exist in discomap server.
     *
     * @param scientificName - species scientific name
     * @param layerNumber - discomap layer number
     * @return boolean
     */
    private boolean isSpeciesLayer(String scientificName, int layerNumber) {
        boolean results = false;
        if (scientificName != null && !scientificName.isEmpty() && layerNumber > 0) {

            String layerNumberS = Integer.toString(layerNumber);
            String rawMapServerFindOperationUrl = getContext().getApplicationProperty("DISCOMAP_SERVER_BIO");
            if (rawMapServerFindOperationUrl != null && rawMapServerFindOperationUrl.trim().length() > 0) {
                String mapServerFindOperationUrl =
                        rawMapServerFindOperationUrl.replace("#{scientific_name}", scientificName.replace(" ", "+").trim())
                                .replace("#{layer_number}", layerNumberS);
                try {

                    String jsonTxt = IOUtils.toString(new URL(mapServerFindOperationUrl));
                    JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
                    JSONArray jArray = json.getJSONArray("results");
                    if (jArray.isEmpty()) {

                        results = false;
                    } else {

                        JSONObject jArray0 = jArray.getJSONObject(0); // jArray[0]
                        JSONObject attributes = jArray0.getJSONObject("attributes");
                        if (attributes.getString("Type").trim().equalsIgnoreCase("species")) {
                            results = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    /**
     * Run the queries to be executed on "Conservation status" tab.
     *
     * @param idSpecies The species id
     * @param natObjId -ID_NATURE_OBJECT
     */
    private void setConservationStatusDetails(int idSpecies, Integer natObjId) {
        try {

            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("conservationstatus_species.xml"));
            LinkedData ld = new LinkedData(props, natObjId, "_conservationStatusQueries");
            conservationStatusQueries = ld.getQueryObjects();
            for (ForeignDataQueryDTO conservationStatusQuery1 : conservationStatusQueries) {
                conservationStatusQuery = conservationStatusQuery1.getId();
                if (!StringUtils.isBlank(conservationStatusQuery)) {

                    if(conservationStatusQuery1.getIdToUse() == null) {
                        ld.executeQuery(conservationStatusQuery, idSpecies);
                    } else if(conservationStatusQuery1.getIdToUse().equalsIgnoreCase("NATURA_2000")){
                        ld.executeQuery(conservationStatusQuery, n2000id);
                    }

                    if(ld.getRows().size() > 0) {
                        conservationStatusQueryResultCols.put(conservationStatusQuery, ld.getCols());
                        conservationStatusQueryResultRows.put(conservationStatusQuery, ld.getRows());
                    }

                    conservationStatusAttribution = ld.getAttribution();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the site ID list
     * @param sites The sites list
     * @return Comma separated Site IDs
     */
    private String getIds(List<SitesByNatureObjectPersist> sites) {

        int maxSitesPerMap = Utilities.checkedStringToInt(getContext().getInitParameter("MAX_SITES_PER_MAP"), 2000);
        String ids = null;

        if (sites.size() > 0) {
            ids = "";
            if (sites.size() < maxSitesPerMap) {
                for (int i = 0; i < sites.size(); i++) {
                    SitesByNatureObjectPersist site = sites.get(i);

                    ids += "'" + site.getIDSite() + "'";
                    if (i < sites.size() - 1) {
                        ids += ",";
                    }
                }
            }
        }

        return ids;
    }

    /**
     * Populate the member variables used in the "linkeddata" tab.
     *
     * @param idSpecies
     *            - The species ID.
     */
    private void linkeddataTabActions(int idSpecies, Integer natObjId) {
        try {
            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("externaldata_species.xml"));
            LinkedData fd = new LinkedData(props, natObjId, "_linkedDataQueries");
            queries = fd.getQueryObjects();

            // runs all the queries
            allQueries = new ArrayList<LinkedDataQuery>();

            for(ForeignDataQueryDTO queryDTO : queries){
                if(queryDTO.getIdToUse() == null) {
                    fd.executeQuery(queryDTO.getId(), idSpecies);
                } else if(queryDTO.getIdToUse().equalsIgnoreCase("NATURA_2000")){
                    fd.executeQuery(queryDTO.getId(), n2000id);
                }

                LinkedDataQuery q = new LinkedDataQuery(queryDTO, fd.getCols(), fd.getRows(), fd.getAttribution());
                if(q.getResultRows() != null && q.getResultRows().size() > 0)
                    allQueries.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<HashMap<String, ResultValue>> biogeoAssessmentRows =
            new  ArrayList<>();

    public ArrayList<HashMap<String, ResultValue>> getBiogeoAssessmentRows() {
        return biogeoAssessmentRows;
    }

    /**
     * Populates the EU conservation status by biogeographical region using Art17 data
     * @param natObjId
     */
    private void populateBiogeoAssessment(Integer natObjId) {

        List<ForeignDataQueryDTO> biogeoAssessment;

        try {

            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("art17.xml"));
            LinkedData ld = new LinkedData(props, natObjId, "force");
            biogeoAssessment = ld.getQueryObjects();

            for (ForeignDataQueryDTO aBiogeoAssessment : biogeoAssessment) {
                if(aBiogeoAssessment.getId().equals("art17species_eu")){
                    String syntaxaQuery = aBiogeoAssessment.getId();
                    if (!StringUtils.isBlank(syntaxaQuery)) {
                        ld.executeQuery(syntaxaQuery, n2000id);
                        biogeoAssessmentRows = ld.getRows();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LinkedDataQuery> allQueries;

    public List<LinkedDataQuery> getAllQueries() {
        return allQueries;
    }

    public LinkedHashMap<String, ArrayList<Map<String, Object>>> getConservationStatusQueryResultCols() {
        return conservationStatusQueryResultCols;
    }

    public LinkedHashMap<String, ArrayList<HashMap<String, ResultValue>>> getConservationStatusQueryResultRows() {
        return conservationStatusQueryResultRows;
    }

    public List<ForeignDataQueryDTO> getConservationStatusQueries() {
        return conservationStatusQueries;
    }

    public String getConservationStatusQuery() {
        return conservationStatusQuery;
    }

    public String getConservationStatusAttribution() {
        return conservationStatusAttribution;
    }

    public boolean isRangeLayer() {
        return rangeLayer;
    }

    public void setRangeLayer(boolean rangeLayer) {
        this.rangeLayer = rangeLayer;
    }

    public boolean isDistributionLayer() {
        return distributionLayer;
    }

    public void setDistributionLayer(boolean distributionLayer) {
        this.distributionLayer = distributionLayer;
    }

    /**
     * @return the factsheet
     */
    public SpeciesFactsheet getFactsheet() {
        return factsheet;
    }

    /**
     * @param factsheet the factsheet to set
     */
    public void setFactsheet(SpeciesFactsheet factsheet) {
        this.factsheet = factsheet;
    }

    /**
     * @return the idSpecies
     */
    public String getIdSpecies() {
        return idSpecies;
    }

    /**
     * @param idSpecies the idSpecies to set
     */
    public void setIdSpecies(String idSpecies) {
        this.idSpecies = idSpecies;
    }

    /**
     * @return seniorSpecies
     */
    public String getSeniorSpecies() {
        return seniorSpecies;
    }

    /**
     * @return the idSpeciesLink
     */
    public int getIdSpeciesLink() {
        return idSpeciesLink;
    }

    /**
     * @param idSpeciesLink the idSpeciesLink to set
     */
    public void setIdSpeciesLink(int idSpeciesLink) {
        this.idSpeciesLink = idSpeciesLink;
    }

    /**
     * @return the scientificName
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    public boolean isGridDistSuccess() {
        return gridDistSuccess;
    }

    public void setGridDistSuccess(boolean gridDistSuccess) {
        this.gridDistSuccess = gridDistSuccess;
    }

    public List<PictureDTO> getPics() {
        return pics;
    }

    public void setPics(List<PictureDTO> pics) {
        this.pics = pics;
    }

    public SpeciesNatureObjectPersist getSpecie() {
        return specie;
    }

    public void setSpecie(SpeciesNatureObjectPersist specie) {
        this.specie = specie;
    }

    public List<ClassificationDTO> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<ClassificationDTO> classifications) {
        this.classifications = classifications;
    }

    public String getAuthorDate() {
        return authorDate;
    }

    public void setAuthorDate(String authorDate) {
        this.authorDate = authorDate;
    }

    public String getKingdomname() {
        return kingdomname;
    }

    public void setKingdomname(String kingdomname) {
        this.kingdomname = kingdomname;
    }

    public String getRedlistLink() {
        return redlistLink;
    }

    public void setRedlistLink(String redlistLink) {
        this.redlistLink = redlistLink;
    }

    public String getScientificNameURL() {
        return scientificNameURL;
    }

    public void setScientificNameURL(String scientificNameURL) {
        this.scientificNameURL = scientificNameURL;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getN2000id() {
        return n2000id;
    }

    public String getWormsid() {
        return wormsid;
    }

    public void setWormsid(String wormsid) {
        this.wormsid = wormsid;
    }

    public String getFaeu() {
        return faeu;
    }

    public void setFaeu(String faeu) {
        this.faeu = faeu;
    }

    public String getItisTSN() {
        return itisTSN;
    }

    public void setItisTSN(String itisTSN) {
        this.itisTSN = itisTSN;
    }

    public String getNcbi() {
        return ncbi;
    }

    public void setNcbi(String ncbi) {
        this.ncbi = ncbi;
    }

    public ArrayList<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<LinkDTO> links) {
        this.links = links;
    }

    public List<NationalThreatWrapper> getConsStatus() {
        return consStatus;
    }

    public void setConsStatus(List<NationalThreatWrapper> consStatus) {
        this.consStatus = consStatus;
    }

    public List<SpeciesNatureObjectPersist> getSubSpecies() {
        return subSpecies;
    }

    public void setSubSpecies(List<SpeciesNatureObjectPersist> subSpecies) {
        this.subSpecies = subSpecies;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Vector<GeographicalStatusWrapper> getBioRegions() {
        return bioRegions;
    }

    public void setBioRegions(Vector<GeographicalStatusWrapper> bioRegions) {
        this.bioRegions = bioRegions;
    }

    public boolean isShowGeoDistribution() {
        return showGeoDistribution;
    }

    public void setShowGeoDistribution(boolean showGeoDistribution) {
        this.showGeoDistribution = showGeoDistribution;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public UniqueVector getColorURL() {
        return colorURL;
    }

    public void setColorURL(UniqueVector colorURL) {
        this.colorURL = colorURL;
    }

    public String getMapserverURL() {
        return mapserverURL;
    }

    public void setMapserverURL(String mapserverURL) {
        this.mapserverURL = mapserverURL;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Hashtable<String, String> getStatusColorPair() {
        return statusColorPair;
    }

    public void setStatusColorPair(Hashtable<String, String> statusColorPair) {
        this.statusColorPair = statusColorPair;
    }

    public List<VernacularNameWrapper> getVernNames() {
        return vernNames;
    }

    public void setVernNames(List<VernacularNameWrapper> vernNames) {
        this.vernNames = vernNames;
    }

    public List<SitesByNatureObjectPersist> getSpeciesSites() {
        return speciesSites;
    }

    public void setSpeciesSites(List<SitesByNatureObjectPersist> speciesSites) {
        this.speciesSites = speciesSites;
    }

    public String getMapIds() {
        return mapIds;
    }

    public void setMapIds(String mapIds) {
        this.mapIds = mapIds;
    }

    public List<SitesByNatureObjectPersist> getSubSpeciesSites() {
        return subSpeciesSites;
    }

    public void setSubSpeciesSites(List<SitesByNatureObjectPersist> subSpeciesSites) {
        this.subSpeciesSites = subSpeciesSites;
    }

    public String getSubMapIds() {
        return subMapIds;
    }

    public void setSubMapIds(String subMapIds) {
        this.subMapIds = subMapIds;
    }

    public int getSeniorIdSpecies() {
        return seniorIdSpecies;
    }

    public PublicationWrapper getSpeciesBook() {
        return speciesBook;
    }

    public List<ForeignDataQueryDTO> getQueries() {
        return queries;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<Map<String, Object>> getQueryResultCols() {
        return queryResultCols;
    }

    public ArrayList<HashMap<String, ResultValue>> getQueryResultRows() {
        return queryResultRows;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getFaoCode() {
        return faoCode;
    }

    public String getGbifCode() {
        return gbifCode;
    }

    /**
     *
     * @return natureObjectAttributesMap - map of natureObjectAttributes names and Chm62edtNatureObjectAttributesPersist objects.
     */
    public Map<String, List<Chm62edtNatureObjectAttributesPersist>> getNatureObjectAttributesMap() {

        if (natureObjectAttributesMap == null) {

            Integer currentNaturalObjectId = specie.getIdNatureObject();
            @SuppressWarnings("unchecked")
            List<Chm62edtNatureObjectAttributesPersist> natureObjectAttributes =
                    new Chm62edtNatureObjectAttributesDomain().findWhere("ID_NATURE_OBJECT= " + currentNaturalObjectId);

            natureObjectAttributesMap = new HashMap<String, List<Chm62edtNatureObjectAttributesPersist>>();
            for (Chm62edtNatureObjectAttributesPersist noa : natureObjectAttributes) {
                if (natureObjectAttributesMap.containsKey(noa.getName())) {
                    natureObjectAttributesMap.get(noa.getName()).add(noa);

                } else {
                    List<Chm62edtNatureObjectAttributesPersist> l = new ArrayList<Chm62edtNatureObjectAttributesPersist>();
                    l.add(noa);
                    natureObjectAttributesMap.put(noa.getName(), l);

                }

            }
        }
        return natureObjectAttributesMap;
    }

    public int getSynonymsCount() {
        return synonymsCount;
    }

    public void setSynonymsCount(int synonymsCount) {
        this.synonymsCount = synonymsCount;
    }

    public List getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List synonyms) {
        this.synonyms = synonyms;
    }

    public int getVernNamesCount() {
        return vernNamesCount;
    }

    public void setVernNamesCount(int vernNamesCount) {
        this.vernNamesCount = vernNamesCount;
    }

    public int getSpeciesSitesCount() {
        return speciesSitesCount;
    }

    public void setSpeciesSitesCount(int speciesSitesCount) {
        this.speciesSitesCount = speciesSitesCount;
    }

    public int getHabitatsCount() {
        return habitatsCount;
    }

    public void setHabitatsCount(int habitatsCount) {
        this.habitatsCount = habitatsCount;
    }

    public String getAuthorYear() {
        return authorYear;
    }

    public void setAuthorYear(String authorYear) {
        this.authorYear = authorYear;
    }

    public List<SpeciesNatureObjectPersist> getParentSpecies() {
        return parentSpecies;
    }

    public void setParentSpecies(List<SpeciesNatureObjectPersist> parentSpecies) {
        this.parentSpecies = parentSpecies;
    }

    /**
     * @return the consStatusWO
     */
    public NationalThreatWrapper getConsStatusWO() {
        return consStatusWO;
    }

    /**
     * @return the consStatusEU
     */
    public NationalThreatWrapper getConsStatusEU() {
        return consStatusEU;
    }

    /**
     * @return the consStatusE25
     */
    public NationalThreatWrapper getConsStatusE25() {
        return consStatusE25;
    }

    public List<LegalStatusWrapper> getLegalStatuses() {
        return legalStatuses;
    }

    public void setLegalStatuses(List<LegalStatusWrapper> legalStatuses) {
        this.legalStatuses = legalStatuses;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getUnepWcmcPageLink() {
        return unepWcmcPageLink;
    }

    public void setUnepWcmcPageLink(String unepWcmcPageLink) {
        this.unepWcmcPageLink = unepWcmcPageLink;
    }

    public List<SitesByNatureObjectViewDTO> getSpeciesSitesTable() {
        return speciesSitesTable;
    }

    public void setSpeciesSitesTable(List<SitesByNatureObjectViewDTO> speciesSitesTable) {
        this.speciesSitesTable = speciesSitesTable;
    }

    public String getScientificNameUrlEncoded() {
        return scientificNameUrlEncoded;
    }

    public void setScientificNameUrlEncoded(String scientificNameUrlEncoded) {
        this.scientificNameUrlEncoded = scientificNameUrlEncoded;
    }

    public List<SitesByNatureObjectViewDTO> getSubSpeciesSitesTable() {
        return subSpeciesSitesTable;
    }

    public void setSubSpeciesSitesTable(List<SitesByNatureObjectViewDTO> subSpeciesSitesTable) {
        this.subSpeciesSitesTable = subSpeciesSitesTable;
    }

    public List<String> getBreadcrumbClassificationExpands() {
        return breadcrumbClassificationExpands;
    }

    public void setBreadcrumbClassificationExpands(List<String> breadcrumbClassificationExpands) {
        this.breadcrumbClassificationExpands = breadcrumbClassificationExpands;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getSpeciesTitle() {
        return speciesTitle;
    }

    public void setSpeciesTitle(String speciesTitle) {
        this.speciesTitle = speciesTitle;
    }

    /**
     * Lists the habitats for the species
     * todo: implement http://taskman.eionet.europa.eu/issues/19215
     * @return
     */
    public List<String> getHabitats(){
        return null;
    }

    /**
     * Is protected by EU Directives (Birds/Habitats)
     * @return
     */
    public boolean isProtectedByEUDirectives(){
        return isHabitatsDirective() || isBirdsDirective();
    }

    /**
     * Checks if the species is in Habitats Directive
     * @return
     */
    public boolean isHabitatsDirective(){
        return habitatsDirective;
    }

    /**
     * Checks if the species is in Birds Directive
     * @return
     */
    public boolean isBirdsDirective(){
        return birdsDirective;
    }

    /**
     * Number of other agreements (not Birds / Habitats)
     * @return
     */
    public int getOtherAgreements() {
        return otherAgreements;
    }

    public List<LegalStatusWrapper> getParentLegal() {
        return parentLegal;
    }

    private List<EcosystemsPersist> allEcosystems = null;

    private List<EcosystemsPersist> getAllEcosystems(){
        if(allEcosystems == null){
            allEcosystems = factsheet.getEcosystems();
        }
        return allEcosystems;
    }

    private List<EcosystemsPersist> preferredEcosystems = null;
    private List<EcosystemsPersist> suitableEcosystems = null;

    /**
     * Returns preferred ecosystems (unique)
     * @return
     */
    public List<EcosystemsPersist> getPreferredEcosystems(){

        if(preferredEcosystems == null){
            Set<EcosystemsPersist>preferredEcosystemsSet = new HashSet<>();

            for(EcosystemsPersist ep : getAllEcosystems()){
                if(ep.getTypeAssoc().equalsIgnoreCase("P")){
                    preferredEcosystemsSet.add(ep);
                }
            }
            preferredEcosystems = new ArrayList<>();
            preferredEcosystems.addAll(preferredEcosystemsSet);
            Collections.sort(preferredEcosystems);
        }
        return preferredEcosystems;
    }

    /**
     * Returns suitable ecosystems (unique and not in the preferred list)
     * @return
     */
    public List<EcosystemsPersist> getSuitableEcosystems(){
        if(suitableEcosystems == null){
            Set<EcosystemsPersist>suitableEcosystemsSet = new HashSet<>();
            for(EcosystemsPersist ep : getAllEcosystems()){
                if(ep.getTypeAssoc().equalsIgnoreCase("S")){
                    suitableEcosystemsSet.add(ep);
                }
            }
            suitableEcosystemsSet.removeAll(getPreferredEcosystems());

            suitableEcosystems = new ArrayList<>();
            suitableEcosystems.addAll(suitableEcosystemsSet);
            Collections.sort(suitableEcosystems);
        }
        return suitableEcosystems;
    }

    /**
     * Checks if the species is a bird
     * @return
     */
    public boolean isBird(){
        return factsheet.getSpeciesGroup().equalsIgnoreCase("Birds");
    }

    /**
     * Breeding ecosystem for Birds
     */
    private List<EcosystemsPersist> breedingEcosystems;
    /**
     * Wintering ecosystems for Birds
     */
    private List<EcosystemsPersist> winteringEcosystems;

    /**
     * Populates the Breeding/Wintering ecosystems for Birds
     */
    private void populateBirdEcosystems(){
        winteringEcosystems = new ArrayList<>();
        breedingEcosystems = new ArrayList<>();
        for(EcosystemsPersist ep : getPreferredEcosystems()){
            if(ep.getSeason().equalsIgnoreCase("B")){
                breedingEcosystems.add(ep);
            }
            if(ep.getSeason().equalsIgnoreCase("W")){
                winteringEcosystems.add(ep);
            }
        }


        // check if the breeding and wintering ecosystems are the same
        int diff = 0;

        if(breedingEcosystems.size() == winteringEcosystems.size()) {
            for (EcosystemsPersist wep : winteringEcosystems) {
                boolean found = false;
                for(EcosystemsPersist bep : breedingEcosystems){
                    if(bep.getEcoCode().equalsIgnoreCase(wep.getEcoCode())){
                        found = true;
                    }
                }
                if(!found){
                    diff++; break;
                }
            }
        } else {
            diff = 1;
        }

        if(diff == 0) {
            // show only one list as "occurs in"
            preferredEcosystems = breedingEcosystems;
            breedingEcosystems = new ArrayList<>();
            winteringEcosystems = new ArrayList<>();
        } else {
            // don't show the preferred list
            preferredEcosystems = new ArrayList<>();
        }
        // do not display suitable
        suitableEcosystems = new ArrayList<>();
    }

    /**
     * Returns the Birds breeding ecosystems; empty if the same as wintering (preferred is used in this case)
     * @return
     */
    public List<EcosystemsPersist> getBreedingEcosystems() {
        return breedingEcosystems;
    }

    /**
     * Returns the Birds wintering ecosystems; empty if the same as breeding (preferred is used in this case)
     * @return
     */
    public List<EcosystemsPersist> getWinteringEcosystems() {
        return winteringEcosystems;
    }

    public boolean isHabitatsDirectiveII() {
        return habitatsDirectiveII;
    }

    public String getArt12status() {
        return art12status;
    }

    public String getArt12statusProper(){
        if(art12status != null) {
            if(art12status.equalsIgnoreCase("NearThreatened"))
                return "Near threatened";
        }
        return art12status;
    }

    public List<LinkDTO> getArt12expert() {
        return art12expert;
    }

    public List<LinkDTO> getArt12factsheet() {
        return art12factsheet;
    }
}

