package eionet.eunis.stripes.actions;

import java.util.*;

import javax.servlet.http.HttpServletResponse;

import eionet.eunis.dao.IReferencesDao;
import eionet.eunis.dto.*;
import eionet.eunis.rdf.LinkedDataQuery;
import eionet.eunis.stripes.actions.beans.OrderedSpeciesBean;
import eionet.eunis.util.Discodata;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.lang3.math.NumberUtils;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.factsheet.habitats.DescriptionWrapper;
import ro.finsiel.eunis.factsheet.habitats.HabitatsFactsheet;
import ro.finsiel.eunis.factsheet.habitats.LegalStatusWrapper;
import ro.finsiel.eunis.jrfTables.*;
import ro.finsiel.eunis.jrfTables.habitats.factsheet.HabitatLegalPersist;
import ro.finsiel.eunis.jrfTables.habitats.factsheet.OtherClassificationPersist;
import ro.finsiel.eunis.jrfTables.species.factsheet.SitesByNatureObjectDomain;
import ro.finsiel.eunis.jrfTables.species.factsheet.SitesByNatureObjectPersist;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.CountryUtil;
import ro.finsiel.eunis.search.Utilities;
import eionet.eunis.dao.DaoFactory;
import eionet.eunis.rdf.LinkedData;
import eionet.eunis.util.Constants;
import eionet.sparqlClient.helpers.ResultValue;
import ro.finsiel.eunis.search.species.references.ReferencesSearchCriteria;
import ro.finsiel.eunis.utilities.EunisUtil;

/**
 * Action bean to handle habitats-factsheet functionality. Data is loaded from
 * {@link ro.finsiel.eunis.factsheet.habitats.HabitatsFactsheet}.
 * 
 * @author Risto Alt
 */
@UrlBinding("/habitats/{idHabitat}")
public class HabitatsFactsheetActionBean extends AbstractStripesAction {

    /**
     * The length of the Description field after which the description is moved to full page width
     */
    private static final int DESCRIPTION_THRESHOLD = 1500;
    private String idHabitat = "";

    private static final Integer[] dictionary = {HabitatsFactsheet.OTHER_INFO_ALTITUDE, HabitatsFactsheet.OTHER_INFO_DEPTH,
        HabitatsFactsheet.OTHER_INFO_CLIMATE, HabitatsFactsheet.OTHER_INFO_GEOMORPH, HabitatsFactsheet.OTHER_INFO_SUBSTRATE,
        HabitatsFactsheet.OTHER_INFO_LIFEFORM, HabitatsFactsheet.OTHER_INFO_COVER, HabitatsFactsheet.OTHER_INFO_HUMIDITY,
        HabitatsFactsheet.OTHER_INFO_WATER, HabitatsFactsheet.OTHER_INFO_SALINITY, HabitatsFactsheet.OTHER_INFO_EXPOSURE,
        HabitatsFactsheet.OTHER_INFO_CHEMISTRY, HabitatsFactsheet.OTHER_INFO_TEMPERATURE, HabitatsFactsheet.OTHER_INFO_LIGHT,
        HabitatsFactsheet.OTHER_INFO_SPATIAL, HabitatsFactsheet.OTHER_INFO_TEMPORAL, HabitatsFactsheet.OTHER_INFO_IMPACT,
        HabitatsFactsheet.OTHER_INFO_USAGE};
    private int dictionaryLength;

    private String pageUrl;

    private String btrail;
    private String pageTitle = "";
    private String metaDescription = "";

    private HabitatsFactsheet factsheet;
    private List<HabitatFactsheetOtherDTO> otherInfo = new ArrayList<HabitatFactsheetOtherDTO>();

    private boolean isMini;

    // Sites tab variables
    private List<SitesByNatureObjectPersist> sites;
    private List<SitesByNatureObjectPersist> sitesForSubtypes;
    private String mapIds;

    // Variable for RDF generation
    private String domainName;

    /** LinkedData tab variables. */
    private List<ForeignDataQueryDTO> queries;
    private String query;
    private ArrayList<Map<String, Object>> queryResultCols;
    private ArrayList<HashMap<String, ResultValue>> queryResultRows;
    private String attribution;

    // General tab variables
    private PictureDTO pic;
    private Vector<DescriptionWrapper> descriptions;
    private String art17link;

    /** The site's external links. */
    private ArrayList<LinkDTO> links;
    private LinkDTO conservationStatusPDF;
    private LinkDTO conservationStatus;

    /** Conservation status tab specifics. */
    private List<ForeignDataQueryDTO> conservationStatusQueries;
    private List<ForeignDataQueryDTO> syntaxaQueries;

    private String conservationStatusQuery;
    private String conservationStatusAttribution;


    private LinkedHashMap<String, ArrayList<Map<String, Object>>> conservationStatusQueryResultCols =
            new LinkedHashMap<String, ArrayList<Map<String, Object>>>();
    private LinkedHashMap<String, ArrayList<HashMap<String, ResultValue>>> conservationStatusQueryResultRows =
            new LinkedHashMap<String, ArrayList<HashMap<String, ResultValue>>>();



    private List history = new ArrayList();
    private List<OtherClassificationPersist> otherClassifications = new ArrayList<>();

    private List<LegalStatusWrapper> legalInfo = null;
    private Set<String> protectedBy = null;

    private String englishDescription = null;

    private List speciesForHabitats = null;
    // cache for legal mentioned in
    private List<MentionedIn> mentionedInList = null;

    private List<EcosystemsPersist> uniqueEcosystems = null;

    private List<OrderedSpeciesBean> diagnosticSpecies;
    private List<OrderedSpeciesBean> constantSpecies;
    private List<OrderedSpeciesBean> dominantSpecies;

    private boolean resolution4Relation = false;

    private Chm62edtHabitatRedlistPersist redlist = null;
    private List<Chm62edtHabitatOccurrencePersist> redlistOccurrencesEU;
    private List<Chm62edtHabitatOccurrencePersist> redlistOccurrencesEurope;
    private List<Chm62edtHabitatOccurrencePersist> redlistOccurrencesSea;
    private List<Chm62edtHabitatRedlistConservationPersist> redlistConservation;
    private List<Chm62edtHabitatRedlistThreatsPersist> redlistThreats;
    private List<PictureDTO> pics;
    private String descriptionSpecies;


    /**
     * RDF output is served from elsewhere.
     */
    @DefaultHandler
    public Resolution defaultAction() {

        pageUrl = getContext().getInitParameter("DOMAIN_NAME") + "/habitats/" + idHabitat;

        String eeaHome = getContext().getInitParameter("EEA_HOME");

        btrail = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,factsheet";
        factsheet = new HabitatsFactsheet(idHabitat);
        // check if the habitat exists.
        if (factsheet.getHabitat() == null) {
            pageTitle =
                    getContext().getInitParameter("PAGE_TITLE")
                    + getContentManagement().cmsPhrase(
                            "Sorry, no habitat type has been found in the database with Habitat type ID = ") + "'"
                            + idHabitat + "'";

            getContext().getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ForwardResolution("/stripes/habitats-factsheet/habitats-factsheet.layout.jsp");
        }

        // set metadescription and page title
        metaDescription = factsheet.getMetaHabitatDescription();
        pageTitle =
                getContext().getInitParameter("PAGE_TITLE") + getContentManagement().cmsPhrase("Factsheet for") + " "
                        + factsheet.getHabitat().getScientificName();


        generalTabActions();

        if (factsheet.isAnnexI()) {
            sitesTabActions();
//            linkeddataTabActions(NumberUtils.toInt(idHabitat), factsheet.idNatureObject);
            conservationStatusTabActions(NumberUtils.toInt(idHabitat), factsheet.idNatureObject);
        }

        try {
            history = factsheet.getHistory();
            otherClassifications = factsheet.getOtherClassifications();
        } catch (InitializationException e) {
            e.printStackTrace();
        }

        if(factsheet.isEunis()){
            populateSyntaxa(NumberUtils.toInt(idHabitat), factsheet.idNatureObject);
        }

        if(factsheet.isEunis2017()) {
            diagnosticSpecies = factsheet.getEunis2017Species("Habitat diagnostic species");
            constantSpecies = factsheet.getEunis2017Species("Habitat constant species");
            dominantSpecies = factsheet.getEunis2017Species("Habitat dominant species");
        }

        if(factsheet.isRedList()) {
            redlist = factsheet.getRedlist();
            redlistOccurrencesEU = factsheet.getRedListOccurrencesEU();
            redlistOccurrencesEurope = factsheet.getRedListOccurrencesEurope();
            redlistOccurrencesSea = factsheet.getRedListOccurrencesSea();
            redlistConservation = factsheet.getRedListConservation();
            redlistThreats = factsheet.getRedListThreats();
            setPictures();
            if(getEnglishDescription().contains("Characteristic species")) {
                descriptionSpecies = getEnglishDescription().substring(englishDescription.indexOf("Characteristic species"));
                englishDescription = getEnglishDescription().substring(0, englishDescription.indexOf("Characteristic species"));
            } else if (getEnglishDescription().contains("Vascular plants")) {
                descriptionSpecies = getEnglishDescription().substring(englishDescription.indexOf("Vascular plants"));
                englishDescription = getEnglishDescription().substring(0, englishDescription.indexOf("Vascular plants"));
            } else if (getEnglishDescription().contains("Charactersitic species")) {
                descriptionSpecies = getEnglishDescription().substring(englishDescription.indexOf("Charactersitic species"));
                englishDescription = getEnglishDescription().substring(0, englishDescription.indexOf("Charactersitic species"));
            }

//                and description not like '%Characteristic species%' and description not like '%Vascular plants%' and description not like '%Charactersitic species%';
        }

        if(factsheet.isAnnexI()) {
            return new ForwardResolution("/stripes/habitats-factsheet/annex1/habitats-factsheet-annex1.layout.jsp");
        } else if(factsheet.isEunis2017()) {
            return new ForwardResolution("/stripes/habitats-factsheet/eunis2017/habitats-factsheet-eunis2017.layout.jsp");
        } else if (factsheet.isRedList()) {
            return new ForwardResolution("/stripes/habitats-factsheet/redlist/habitats-factsheet-redlist.layout.jsp");
        } else {
            return new ForwardResolution("/stripes/habitats-factsheet/habitats-factsheet.layout.jsp");
        }
    }

    /**
     * Populate the member variables used in the "sites" tab.
     */
    private void sitesTabActions() {
        String isGoodHabitat =
                " IF(TRIM(A.CODE_2000) <> '',RIGHT(A.CODE_2000,2),1) <> IF(TRIM(A.CODE_2000) <> '','00',2) AND IF(TRIM(A.CODE_2000) <> '',LENGTH(A.CODE_2000),1) = IF(TRIM(A.CODE_2000) <> '',4,1) ";

        // Sites for which this habitat is recorded.
        sites =
                new SitesByNatureObjectDomain()
        .findCustom("SELECT DISTINCT C.ID_SITE, C.NAME, C.SOURCE_DB, C.LATITUDE, C.LONGITUDE, E.AREA_NAME_EN "
                + " FROM chm62edt_habitat AS A "
                + " INNER JOIN chm62edt_nature_object_report_type AS B ON A.ID_NATURE_OBJECT = B.ID_NATURE_OBJECT_LINK "
                + " INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT "
                + " LEFT JOIN chm62edt_nature_object_geoscope AS D ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT "
                + " LEFT JOIN chm62edt_country AS E ON D.ID_GEOSCOPE = E.ID_GEOSCOPE " + " WHERE   "
                + isGoodHabitat + " AND A.ID_NATURE_OBJECT =" + factsheet.getHabitat().getIdNatureObject()
                + " AND C.SOURCE_DB <> 'EMERALD'" + " ORDER BY C.ID_SITE");

        // populate the area code for the country link
        for(SitesByNatureObjectPersist site : sites) {
            site.setEunisAreaCode(CountryUtil.findCountry(site.getAreaNameEn()).getEunisAreaCode());
        }

        // Sites for habitat subtypes.
        sitesForSubtypes =
                new SitesByNatureObjectDomain()
        .findCustom("SELECT DISTINCT C.ID_SITE, C.NAME, C.SOURCE_DB, C.LATITUDE, C.LONGITUDE, E.AREA_NAME_EN "
                + " FROM chm62edt_habitat AS A "
                + " INNER JOIN chm62edt_nature_object_report_type AS B ON A.ID_NATURE_OBJECT = B.ID_NATURE_OBJECT_LINK "
                + " INNER JOIN chm62edt_sites AS C ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT "
                + " LEFT JOIN chm62edt_nature_object_geoscope AS D ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT "
                + " LEFT JOIN chm62edt_country AS E ON D.ID_GEOSCOPE = E.ID_GEOSCOPE "
                + " WHERE A.ID_NATURE_OBJECT ="
                + factsheet.getHabitat().getIdNatureObject()
                + (factsheet.isAnnexI() ? " and right(A.code_2000,2) <> '00' and length(A.code_2000) = 4 AND if(right(A.code_2000,1) = '0',left(A.code_2000,3),A.code_2000) like '"
                        + factsheet.getCode2000() + "%' and A.code_2000 <> '" + factsheet.getCode2000() + "'"
                        : " AND A.EUNIS_HABITAT_CODE like '" + factsheet.getEunisHabitatCode()
                        + "%' and A.EUNIS_HABITAT_CODE<> '" + factsheet.getEunisHabitatCode() + "'")
                        + " AND C.SOURCE_DB <> 'EMERALD'" + " ORDER BY C.ID_SITE");

        if ((null != sites && !sites.isEmpty()) || (null != sitesForSubtypes && !sitesForSubtypes.isEmpty())) {
            mapIds = "";
            int maxSitesPerMap = Utilities.checkedStringToInt(getContext().getInitParameter("MAX_SITES_PER_MAP"), 2000);

            if (sites.size() < maxSitesPerMap) {
                for (int i = 0; i < sites.size(); i++) {
                    SitesByNatureObjectPersist site = sites.get(i);

                    mapIds += "'" + site.getIDSite() + "'";
                    if (i < sites.size() - 1) {
                        mapIds += ",";
                    }
                }
            }
        }
    }

    /**
     * Populate the member variables used in the "general" tab.
     */
    private void generalTabActions() {

        try {
            // Get main picture
            String picturePath = getContext().getInitParameter("UPLOAD_DIR_PICTURES_HABITATS");
            pic = factsheet.getMainPicture(picturePath, domainName);

            descriptions = factsheet.getDescrOwner();

            Hashtable<String, AttributeDto> natObjectAttributes =
                    DaoFactory.getDaoFactory().getExternalObjectsDao().getNatureObjectAttributes(factsheet.idNatureObject);
            if (natObjectAttributes != null && natObjectAttributes.size() > 0) {
                AttributeDto attr = natObjectAttributes.get(Constants.ART17_SUMMARY);
                if (attr != null) {
                    art17link = attr.getValue();
                }
            }

            // Set the site's external links.
            List<LinkDTO> natureLinks = DaoFactory.getDaoFactory().getExternalObjectsDao().getNatureObjectLinks(factsheet.idNatureObject);

            // filters the links
            links = new ArrayList<LinkDTO>();
            for(LinkDTO link : natureLinks){
                boolean addToLinks = true;
                if(link.getName().equalsIgnoreCase("Conservation Status 2007-2012 – summary")){
                    conservationStatusPDF = link;
                    addToLinks = false;
                } else if (link.getName().toLowerCase().startsWith("conservation status")){
//                    conservationStatus = link;
                    addToLinks = false;
                }
                if(addToLinks){
                    links.add(link);
                }
            }

            if (!Utilities.isEmptyString(factsheet.getCode2000())) {
                conservationStatus = new LinkDTO();
                conservationStatus.setUrl("https://nature-art17.eionet.europa.eu/article17/habitat/summary/?period=5&subject=" + factsheet.getCode2000());
                conservationStatus.setName("Conservation status 2013-2018 - experts web viewer");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Populate the member variables used in the "linkeddata" tab.
     *
     * @param idHabitat
     *            - The habitat ID.
     */
    private void linkeddataTabActions(int idHabitat, Integer natObjId) {
        try {
            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("externaldata_habitats.xml"));
            LinkedData fd = new LinkedData(props, natObjId, "_linkedDataQueries");
            queries = fd.getQueryObjects();

            // runs all the queries
            allQueries = new ArrayList<>();

            for(ForeignDataQueryDTO queryDTO : queries){
                if(queryDTO.getIdToUse() == null) {
                    fd.executeQuery(queryDTO.getId(), idHabitat);
                } else if(queryDTO.getIdToUse().equalsIgnoreCase("NATURA_2000")){
                    fd.executeQuery(queryDTO.getId(), factsheet.getCode2000());
                }

                LinkedDataQuery q = new LinkedDataQuery(queryDTO, fd.getCols(), fd.getRows(), fd.getAttribution());
                if(q.getResultRows() != null && q.getResultRows().size() > 0)
                    allQueries.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void conservationStatusTabActions(int habitatId, Integer natureObjectId) {
        try {

            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("conservationstatus_habitats.xml"));
            LinkedData ld = new LinkedData(props, natureObjectId, "_conservationStatusQueries");
            conservationStatusQueries = ld.getQueryObjects();
            for (ForeignDataQueryDTO conservationStatusQuery1 : conservationStatusQueries) {
                conservationStatusQuery = conservationStatusQuery1.getId();
                if (!StringUtils.isBlank(conservationStatusQuery)) {

                    if (conservationStatusQuery1.getIdToUse() == null) {
                        ld.executeQuery(conservationStatusQuery, habitatId);
                    } else if (conservationStatusQuery1.getIdToUse().equalsIgnoreCase("NATURA_2000")) {
                        ld.executeQuery(conservationStatusQuery, factsheet.getCode2000());
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

    private String syntaxaAttribution;
    private ArrayList<Map<String, Object>> syntaxaQueryResultCols =
            new ArrayList<Map<String, Object>>();
    private ArrayList<HashMap<String, ResultValue>> syntaxaQueryResultRows =
            new  ArrayList<HashMap<String, ResultValue>>();

    public List<Map<String, Object>> getSyntaxaQueryResultCols() {
        return syntaxaQueryResultCols;
    }

    public List<HashMap<String, ResultValue>> getSyntaxaQueryResultRows() {
        return syntaxaQueryResultRows;
    }

    public String getSyntaxaAttribution() {
        return syntaxaAttribution;
    }

    /**
     * Populate the Syntaxa/Vegetation section from SPARQL query
     * https://taskman.eionet.europa.eu/issues/21160
     * @param habitatId
     * @param natureObjectId
     */
    private void populateSyntaxa(int habitatId, Integer natureObjectId){

        try {

            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("habitats_syntaxa.xml"));
            LinkedData ld = new LinkedData(props, natureObjectId, "force");
            syntaxaQueries = ld.getQueryObjects();
            for (int i = 0; i < syntaxaQueries.size(); i++) {
                String syntaxaQuery = syntaxaQueries.get(i).getId();
                if (!StringUtils.isBlank(syntaxaQuery)) {
                    ld.executeQuery(syntaxaQuery, habitatId);
                    syntaxaQueryResultCols = ld.getCols();
                    syntaxaQueryResultRows = ld.getRows();

                    syntaxaAttribution = ld.getAttribution();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public String getIdHabitat() {
        return idHabitat;
    }

    /**
     * @param idHabitat
     */
    public void setIdHabitat(String idHabitat) {
        this.idHabitat = idHabitat;
    }

    /**
     * @return
     */
    public HabitatsFactsheet getFactsheet() {
        return factsheet;
    }

    /**
     * @return the btrail
     */
    @Override
    public String getBtrail() {
        return btrail;
    }

    /**
     * @return the pageTitle
     */
    public String getPageTitle() {
        return pageTitle;
    }

    /**
     * @return the metaDescription
     */
    @Override
    public String getMetaDescription() {
        return metaDescription;
    }

    public boolean isMini() {
        return isMini;
    }

    public void setMini(boolean isMini) {
        this.isMini = isMini;
    }

    public List<HabitatFactsheetOtherDTO> getOtherInfo() {
        return otherInfo;
    }

    public Integer[] getDictionary() {
        return dictionary;
    }

    public int getDictionaryLength() {
        return dictionaryLength;
    }

    public void setDictionaryLength(int dictionaryLength) {
        this.dictionaryLength = dictionaryLength;
    }

    public List<SitesByNatureObjectPersist> getSites() {
        return sites;
    }

    public void setSites(List<SitesByNatureObjectPersist> sites) {
        this.sites = sites;
    }

    public List<SitesByNatureObjectPersist> getSitesForSubtypes() {
        return sitesForSubtypes;
    }

    public void setSitesForSubtypes(List<SitesByNatureObjectPersist> sitesForSubtypes) {
        this.sitesForSubtypes = sitesForSubtypes;
    }

    public String getMapIds() {
        return mapIds;
    }

    public void setMapIds(String mapIds) {
        this.mapIds = mapIds;
    }

    public PictureDTO getPic() {
        return pic;
    }

    public Vector<DescriptionWrapper> getDescriptions() {
        return descriptions;
    }

    public String getArt17link() {
        return art17link;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<ForeignDataQueryDTO> getQueries() {
        return queries;
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

    public ArrayList<LinkDTO> getLinks() {
        return links;
    }

    /**
     * @return the conservationStatusQueries
     */
    public List<ForeignDataQueryDTO> getConservationStatusQueries() {
        return conservationStatusQueries;
    }

    /**
     * @return the conservationStatusQuery
     */
    public String getConservationStatusQuery() {
        return conservationStatusQuery;
    }

    /**
     * @return the conservationStatusAttribution
     */
    public String getConservationStatusAttribution() {
        return conservationStatusAttribution;
    }

    /**
     * @return the conservationStatusQueryResultCols
     */
    public LinkedHashMap<String, ArrayList<Map<String, Object>>> getConservationStatusQueryResultCols() {
        return conservationStatusQueryResultCols;
    }

    /**
     * @return the conservationStatusQueryResultRows
     */
    public LinkedHashMap<String, ArrayList<HashMap<String, ResultValue>>> getConservationStatusQueryResultRows() {
        return conservationStatusQueryResultRows;
    }

    /**
     * Returns the list of historical classifications
     * @return
     */
    public List getHistory() {
        return history;
    }

    /**
     * Returns the list of current other classifications
     * @return
     */
    public List getOtherClassifications() {
        return otherClassifications;
    }

    /**
     * Returns the current page URL
     * @return
     */
    public String getPageUrl() {
        return pageUrl;
    }

    /**
     * Returns the Protected by list; obtained from the legal info, but with unique document names.
     * @return A set of unique document names from legalInfo.
     */
    public Set<String> getProtectedBy() {
        if(protectedBy == null){
            Set<String> s = new HashSet<String>();
            for(LegalStatusWrapper h : getLegalInfo()) {
                s.add(h.getLegalPersist().getLegalName());
            }
            protectedBy = s;
        }
        return protectedBy;
    }

    public boolean isHabitatsDirective() {
        for(String s : getProtectedBy()) {
            if(s.contains("EU Habitats Directive")) return true;
        }
        return false;
    }

    public boolean isRelResolution4() {
        for(String s : getProtectedBy()) {
            if(s.contains("Resolution 4")) return true;
        }
        return false;
    }

    public String getRelEunis2007() {
        for(OtherClassificationPersist ocp:otherClassifications) {
            if(ocp.getIdClassCode() == 37) {
                return ocp.getCode();
            }
        }
        return null;
    }

    /**
     * Checks if the Habitat is mentioned in Resolution 4
     * @return
     */
    public boolean isResolution4() {
        for(MentionedIn mi : getLegalMentionedIn()) {
            if(mi.getAnnex()!=null){
                if( mi.getAnnex().getIdDc().equals(Constants.RESOLUTION4.toString()) ){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * todo: to be changed to reflect the changes in legal status
     * @deprecated
     * @return
     */
    public String getEquivalentEUHabitats() {
        String result = "";
        for(LegalStatusWrapper h : getLegalInfo()) {
            if(h.getLegalPersist().getLegalName().contains("EU Habitats Directive"))
                result = h.getLegalPersist().getTitle();
        }

        return result;
    }

    /**
     * Returns the "Mentioned in..." list for the Legal Section
     * Only lists Resolution 4 and Annex I, as the others are not legal instruments
     * @return
     */
    public List<MentionedIn> getLegalMentionedIn(){

        if(mentionedInList == null) {
            List<MentionedIn> result = new ArrayList<MentionedIn>();

            ReferencesDomain refDomain = new ReferencesDomain(new ReferencesSearchCriteria[0], new AbstractSortCriteria[0]);
            List<Integer> references = refDomain.getReferencesForHabitat(idHabitat);

            for(Integer idDc : references){
                MentionedIn m = new MentionedIn();
                // only show R4 and A1
                if(idDc.equals(Constants.RESOLUTION4) || idDc.equals(Constants.ANNEX1)) {

                    IReferencesDao dao = DaoFactory.getDaoFactory().getReferncesDao();
                    DcIndexDTO annex = dao.getDcIndex(idDc.toString());
                    m.setAnnex(annex);
                    m.setMoreInfo(dao.getLinks(annex.getIdDc()));

        //          Populate the parent and link
                    if(annex.getReference() != null){
                        DcIndexDTO dto = dao.getDcIndex(annex.getReference());
                        m.setParent(dto);

                        List<AttributeDto> attributes = dao.getDcAttributes(annex.getIdDc());
                        for(AttributeDto attribute : attributes){
                            if(attribute.getName().equalsIgnoreCase("replaces")) {
                                m.setReplaces(dao.getDcIndex(attribute.getValue()));
                            }
                        }
                    }

                    result.add(m);
                }
            }
            mentionedInList = result;
        }

        return mentionedInList;
    }

    /**
     * MentionedIn bean
     */
    public class MentionedIn {
        private DcIndexDTO annex;
        private DcIndexDTO parent;
        private DcIndexDTO replaces;
        private List<DcLinkDTO> moreInfo = new ArrayList<>();

        public DcIndexDTO getAnnex() {
            if(parent != null) {
                return annex;
            } else {
                return null;
            }
        }

        public void setAnnex(DcIndexDTO annex) {
            this.annex = annex;
        }

        public DcIndexDTO getParent() {
            if(parent == null) {
                return annex;
            } else {
                return parent;
            }
        }

        public void setParent(DcIndexDTO parent) {
            this.parent = parent;
        }

        public DcIndexDTO getReplaces() {
            return replaces;
        }

        public void setReplaces(DcIndexDTO replaces) {
            this.replaces = replaces;
        }

        public List<DcLinkDTO> getMoreInfo() {
            return moreInfo;
        }

        public void setMoreInfo(List<DcLinkDTO> moreInfo) {
            this.moreInfo = moreInfo;
        }
    }

    public List<LegalStatusWrapper> getLegalRelationTo(){
        return getLegalInfo();
    }

    /**
     * Returns the legal info list
     * @return List of HabitatLegalPersist objects
     */
    public List<LegalStatusWrapper> getLegalInfo() {
        if(legalInfo == null){
            try {
                legalInfo = new ArrayList<LegalStatusWrapper>();
                List li = factsheet.getHabitatLegalInfo();
                for(Object hlp : li) {
                    LegalStatusWrapper legalStatusWrapper = new LegalStatusWrapper((HabitatLegalPersist)hlp);
                    legalInfo.add(legalStatusWrapper);

                    IReferencesDao dao = DaoFactory.getDaoFactory().getReferncesDao();
                    DcIndexDTO annex = dao.getDcIndex(legalStatusWrapper.getLegalPersist().getIdDc().toString());
                    legalStatusWrapper.setAnnexTitle(annex.getTitle());
                    legalStatusWrapper.setAnnexLink(annex.getUrl());

//                    Populate the parent
                    if(annex.getReference() != null){
                        DcIndexDTO dto = dao.getDcIndex(annex.getReference());
                        legalStatusWrapper.setParentTitle(dto.getTitle());
                        legalStatusWrapper.setParentLink(dto.getUrl());
                        legalStatusWrapper.setParentAlternative(dto.getAlternative());
                    }

                    List<DcLinkDTO> dcLinks = dao.getLinks(legalStatusWrapper.getLegalPersist().getIdDc().toString());
                    // populate the more info section from the annex links
                    for(DcLinkDTO link : dcLinks){
                        legalStatusWrapper.addMoreInfo(link);
                        if(link.getIdDc().equals("2442")) {
                            resolution4Relation = true;
                        }
                    }
                }
                // Order the results
                Collections.sort(legalInfo);

            } catch (InitializationException e) {
                legalInfo = new ArrayList<LegalStatusWrapper>();
            }
        }
        return legalInfo;
    }

    /**
     * Cache the species factsheet query
     * @return List of species for this habitat
     */
    public List getSpecies() {
        if(speciesForHabitats == null) {
            speciesForHabitats = factsheet.getSpeciesForHabitats();
        }
        return speciesForHabitats;
    }

    /**
     * The english description
     * @return
     */
    public String getEnglishDescription(){
        if(englishDescription == null) {
            for(DescriptionWrapper d: getDescriptions()){
                if(d.getLanguage().equalsIgnoreCase("english")){
                    englishDescription = d.getDescription();
                }
            }
            if(englishDescription == null) englishDescription = "";
        }
        return englishDescription;
    }

    /**
     * The length of the Description field after which the description is moved to full page width
     */
    public int getDescriptionThreshold(){
        return DESCRIPTION_THRESHOLD;
    }

    /**
     * Returns the conservation status PDF link
     * @return Object containing the link
     */
    public LinkDTO getConservationStatusPDF() {
        return conservationStatusPDF;
    }

    /**
     * Returns the list of ecosystems of the habitat, filtered to be unique
     * @return
     */
    public List<EcosystemsPersist> getUniqueEcosystems(){
        if(uniqueEcosystems == null){
            Set<EcosystemsPersist> uniqueEcosystemsSet = new HashSet<>();
            uniqueEcosystemsSet.addAll(factsheet.getEcosystems());

            uniqueEcosystems = new ArrayList<>();
            uniqueEcosystems.addAll(uniqueEcosystemsSet);
            Collections.sort(uniqueEcosystems);
        }
        return uniqueEcosystems;
    }

    /**
     * Returns the conservation status link
     * @return Object containing the link
     */
    public LinkDTO getConservationStatus() {
        return conservationStatus;
    }

    public Chm62edtHabitatPersist getResolution4Parent(){
        return factsheet.getResolution4Parent();
    }


    private List<LinkedDataQuery> allQueries;

    public List<LinkedDataQuery> getAllQueries() {
        return allQueries;
    }

    public List<OrderedSpeciesBean> getDiagnosticSpecies() {
        return diagnosticSpecies;
    }

    public List<OrderedSpeciesBean> getConstantSpecies() {
        return constantSpecies;
    }

    public List<OrderedSpeciesBean> getDominantSpecies() {
        return dominantSpecies;
    }

    public boolean isResolution4Relation() {
        return resolution4Relation;
    }

    public Chm62edtHabitatRedlistPersist getRedlist() {
        return redlist;
    }

    public List<Chm62edtHabitatOccurrencePersist> getRedlistOccurrencesEU() {
        return redlistOccurrencesEU;
    }

    public List<Chm62edtHabitatOccurrencePersist> getRedlistOccurrencesEurope() {
        return redlistOccurrencesEurope;
    }
    public List<Chm62edtHabitatOccurrencePersist> getRedlistOccurrencesSea() {
        return redlistOccurrencesSea;
    }

    public List<Chm62edtHabitatRedlistThreatsPersist> getRedlistThreats() {
        return redlistThreats;
    }

    public List<PictureDTO> getPics() {
        return pics;
    }

    private void setPictures() {
        String picturePath = getContext().getInitParameter("UPLOAD_DIR_PICTURES_HABITATS");
        pics = factsheet.getPictures(picturePath);

        // http://taskman.eionet.europa.eu/issues/17992
        // display abstract images if no picture available

        if (pics.size() == 0) {
            // different picture by group
            PictureDTO pic = new PictureDTO();
            pic.setFilename(EunisUtil.getDefaultPicture("x"));
            pic.setDescription("No photo available for this habitat");
            pic.setSource("Paco Sánchez Aguado");
            picturePath = getContext().getInitParameter("UPLOAD_DIR_PICTURES_SPECIES");

            pic.setPath(picturePath);

            pics.add(pic);
        }
    }

    public List<Chm62edtHabitatRedlistConservationPersist> getRedlistConservation() {
        return redlistConservation;
    }

    public String getDescriptionSpecies(){
        return descriptionSpecies;
    }


    private JSONArray conservationCache = null;

    public JSONArray getConservationByCountry() {
        if (conservationCache == null) {
            JSONObject json = Discodata.readJson(Discodata.encodeQuery("https://discodata.eea.europa.eu/sql?query=", "select * from [EUNIS].[v1r1].[BISE_Habitat_Conservation_Status_by_MS] where code_2000='" + factsheet.getCode2000() + "' order by area_name_en"));

            if (json.containsKey("results")) {
                conservationCache = (JSONArray) json.get("results");
            } else {
                conservationCache = new JSONArray();
            }
        }

        return conservationCache;
    }

    private JSONArray conservationEU = null;

    public JSONArray getConservationEU() {
        if (conservationEU == null) {
            JSONObject json = Discodata.readJson(Discodata.encodeQuery("https://discodata.eea.europa.eu/sql?query=", "select * from [EUNIS].[v1r1].[BISE_Habitat_Conservation_Status] where code_2000='" + factsheet.getCode2000() + "' order by region_name"));

            if (json.containsKey("results")) {
                conservationEU = (JSONArray) json.get("results");
            } else {
                conservationEU = new JSONArray();
            }
        }

        return conservationEU;
    }

}
