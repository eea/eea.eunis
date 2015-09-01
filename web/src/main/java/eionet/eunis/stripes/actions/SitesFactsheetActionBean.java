package eionet.eunis.stripes.actions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import eionet.eunis.stripes.actions.beans.HabitatsBean;
import eionet.eunis.stripes.actions.beans.SpeciesBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import ro.finsiel.eunis.factsheet.sites.SiteFactsheet;
import ro.finsiel.eunis.jrfTables.Chm62edtReportAttributesPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtSitesAttributesPersist;
import ro.finsiel.eunis.jrfTables.sites.factsheet.SiteHabitatsPersist;
import ro.finsiel.eunis.jrfTables.sites.factsheet.SiteSpeciesPersist;
import ro.finsiel.eunis.jrfTables.sites.factsheet.SitesSpeciesReportAttributesPersist;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.sites.SitesSearchUtility;
import ro.finsiel.eunis.utilities.EmptyLastComparator;

/**
 * Action bean to handle sites-factsheet functionality.
 * 
 * @author Aleksandr Ivanov <a href="mailto:aleksandr.ivanov@tietoenator.com">contact</a>
 */
@UrlBinding("/sites/{idsite}")
public class SitesFactsheetActionBean extends AbstractStripesAction {

    static final Map<String, String> biogeographicRegionTitles = new HashMap<String, String>();
    static final Map<String, String> sourceDBTitles = new HashMap<String, String>();

    static {
        biogeographicRegionTitles.put("alpine", "Alpine");
        biogeographicRegionTitles.put("anatol", "Anatolian");
        biogeographicRegionTitles.put("arctic", "Arctic");
        biogeographicRegionTitles.put("atlantic", "Atlantic");
        biogeographicRegionTitles.put("boreal", "Boreal");
        biogeographicRegionTitles.put("continent", "Continental");
        biogeographicRegionTitles.put("macarones", "Macaronesia");
        biogeographicRegionTitles.put("mediterranean", "Mediterranean");
        biogeographicRegionTitles.put("pannonic", "Pannonian");
        biogeographicRegionTitles.put("pontic", "Black Sea");
        biogeographicRegionTitles.put("steppic", "Steppic");
        biogeographicRegionTitles.put("marineAtlantic", "Marine Atlantic");
        biogeographicRegionTitles.put("marineBaltic", "Marine Baltic");
        biogeographicRegionTitles.put("marineBlackSea", "Marine Black Sea");
        biogeographicRegionTitles.put("marineMacaronesian", "Marine Macaronesian");
        biogeographicRegionTitles.put("marineMediterranean", "Marine Mediterranean");

        sourceDBTitles.put("NATURA2000", "Natura 2000 site");
        sourceDBTitles.put("CORINE", "Corine site");
        sourceDBTitles.put("CDDA_NATIONAL", "Nationally designated area");
        sourceDBTitles.put("EMERALD", "Emerald site");
        sourceDBTitles.put("DIPLOMA", "European Diploma of Protected Areas");
        sourceDBTitles.put("BIOGENETIC", "Biogenetic site");

    }

    /** The id of the site in question. */
    private String idsite = "";

    /** */
    private String pageTitle = "";

    /** The factsheet data object. */
    private SiteFactsheet factsheet;
    private String metaDescription;

    private String siteName;
    private String country;

    /** The site's designation date */
    private Date siteDesignationDateDisplayValue;

    List sitesDesigc;

    private String surfaceAreaKm2;

    private List<String> biogeographicRegion = new ArrayList<String>();
    private Map<String, Object> bioRegionsMap;

    private int protectedSpeciesCount;
    private int totalSpeciesCount;
    private int habitatsCount;

    private String regionCode;
    private String regionName;

    private List<SpeciesBean> allSiteSpecies;
    HashMap<String, Integer> speciesStatistics;

    private String pageUrl;

    private List habit1Eunis;
    private List habit1NotEunis;
    private List<SiteHabitatsPersist> habit2Eunis;
    private List habit2NotEunis;

    private List<HabitatsBean> habitats;
    private List<Chm62edtSitesAttributesPersist> regions;

    /**
     * The default event handler of this action bean. Note that this action bean only serves RDF through {@link RdfAware}.
     * 
     * @return Resolution
     */
    @DefaultHandler
    public Resolution defaultAction() {

        // Get EEA's home page URL from servlet context.
        String eeaHomePageUrl = getContext().getInitParameter("EEA_HOME");
        pageUrl = getContext().getInitParameter("DOMAIN_NAME") + "/sites/" + idsite;

        // Construct the factsheet data object for the given site.
        factsheet = new SiteFactsheet(idsite);

        // Set the site's meta-description and page title.
        if (factsheet.getIDNatureObject() != null && factsheet.exists()) {
            metaDescription = factsheet.getDescription();
            pageTitle =
                    getContext().getInitParameter("PAGE_TITLE") + getContentManagement().cmsPhrase("Site factsheet for") + " "
                            + factsheet.getSiteObject().getName();
        } else {
            pageTitle =
                    getContext().getInitParameter("PAGE_TITLE")
                            + getContentManagement().cmsPhrase("No data found in the database for the site with ID = ") + "'"
                            + factsheet.getIDSite() + "'";
            try {
                getContext().getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If the given site exists in the database.
        if (factsheet.exists()) {

            siteName = factsheet.getSiteObject().getName();
            country = factsheet.getCountry();
            setSiteDesignationDateDisplayValue();
            if(!Utilities.isEmptyString(factsheet.getSiteObject().getArea())) {
                surfaceAreaKm2 = Math.round(Double.parseDouble(factsheet.getSiteObject().getArea()) / 100) + "";
            }
            populateHabitatLists();
            calculateHabitatsCount();
            prepareBiogeographicRegion();
            populateSpeciesLists();

            populateDesignationArea();
            prepareRegion();

            // Forward to the factsheet layout page.
            if(isTypeDiploma()) {
                return new ForwardResolution("/stripes/site-factsheet/site-factsheet-diploma.layout.jsp");
            }
        }

        return new ForwardResolution("/stripes/site-factsheet/site-factsheet.layout.jsp");
    }

    /**
     * Populates the sitesDesigc list
     */
    private void populateDesignationArea() {
        sitesDesigc = new ArrayList();
        if( isTypeNatura2000() || factsheet.getType() == SiteFactsheet.TYPE_EMERALD ) {
            sitesDesigc = factsheet.findSiteRelationsNatura2000Desigc();
        } else if(isTypeCorine()){
            sitesDesigc = factsheet.findSiteRelationsCorine();
        } else if (isTypeCDDA()){
            sitesDesigc = SitesSearchUtility.findDesignationsForSitesFactsheet(factsheet.getSiteObject().getIdSite());
        }
    }

    /**
     * Populates the species list for the Site factsheet
     */
    private void populateSpeciesLists() {
        if (factsheet == null) return;
        allSiteSpecies = new ArrayList<SpeciesBean>();

        /* 1. everything but Natura 2000 */
        if (SiteFactsheet.TYPE_NATURA2000 != factsheet.getType()) {
            for (Object s : factsheet.findSitesSpeciesByIDNatureObject())
                allSiteSpecies.add(speciesBeanFromSiteSpecies((SiteSpeciesPersist) s, SpeciesBean.SpeciesType.SITE));

            for (Object s : factsheet.findSitesSpecificSpecies())
                allSiteSpecies.add(speciesBeanFromSitesAttributes((Chm62edtSitesAttributesPersist) s, SpeciesBean.SpeciesType.SITE_SPECIFIC));

        } else {
            for (Object s : factsheet.findEunisSpeciesListedAnnexesDirectivesForSitesNatura2000()) {
//              the query can return double resuls because of the
                SpeciesBean sb = speciesBeanFromSitesSpeciesReportAttributes((SitesSpeciesReportAttributesPersist) s, SpeciesBean.SpeciesType.EUNIS_LISTED);
                if(!allSiteSpecies.contains(sb)) {
                    allSiteSpecies.add(sb);
                }
            }
            protectedSpeciesCount = allSiteSpecies.size();

// other species - not to be displayed
//            for (Object s : factsheet.findEunisSpeciesOtherMentionedForSitesNatura2000())
//                allSiteSpecies.add(speciesBeanFromSitesSpeciesReportAttributes((SitesSpeciesReportAttributesPersist) s, SpeciesBean.SpeciesType.EUNIS_OTHER_MENTIONED));
//            for (Object s : factsheet.findNotEunisSpeciesListedAnnexesDirectives())
//                allSiteSpecies.add(speciesBeanFromSitesAttributes((Chm62edtSitesAttributesPersist) s, SpeciesBean.SpeciesType.NOT_EUNIS_LISTED));
//            for (Object s : factsheet.findNotEunisSpeciesOtherMentioned())
//                allSiteSpecies.add(speciesBeanFromSitesAttributes((Chm62edtSitesAttributesPersist) s, SpeciesBean.SpeciesType.NOT_EUNIS_OTHER));
        }
        totalSpeciesCount = allSiteSpecies.size();

        calculateSpeciesStatistics();

        Collections.sort(allSiteSpecies);
    }

    /**
     * Calculate statistics for the species
     */
    private void calculateSpeciesStatistics() {

        speciesStatistics = new HashMap<String, Integer>();

        for (SpeciesBean species : allSiteSpecies) {
            if(species.getSpeciesType() ==  SpeciesBean.SpeciesType.EUNIS_LISTED)
                addToSpeciesStatistics(species.getGroup());
        }

    }

    private int addToSpeciesStatistics(String key){
        int count = speciesStatistics.containsKey(key) ? speciesStatistics.get(key) : 0;
        count++;
        speciesStatistics.put(key, count);
        return count;
    }

    /**
     * Accessor for species statistics list
     * @return Sorted list of species statistics object; the nulls or empty are <i>last</i>
     */
    public List<SpeciesStatistics> getSpeciesStatisticsSorted(){
        ArrayList<String> sortedKeys = new ArrayList<String>(speciesStatistics.keySet());
        java.util.Collections.sort(sortedKeys, EmptyLastComparator.getComparator());
        ArrayList<SpeciesStatistics> result = new ArrayList<SpeciesStatistics>();
        for(String key:sortedKeys)
            result.add(new SpeciesStatistics(key, speciesStatistics.get(key)));
        return result;
    }

    /**
     * Returns the date format to display, depending on the factsheet type (month year for Natura, only year for others)
     * @return
     */
    public String getDateFormat(){
        if(isTypeNatura2000()){
            return "MMMM yyyy";
        } else {
            return "yyyy";
        }
    }

    /**
     * Parses a date from the DB format (yyyyMMdd or yyyy)
     * @param rawDate
     * @return The parsed date or null if it cannot be parsed
     */
    private Date parseDate(String rawDate){
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            return formatter.parse(rawDate);
        } catch (Exception e) {
            formatter = new SimpleDateFormat("yyyy");
            try {
                return formatter.parse(rawDate);
            } catch (Exception e1) {
                return null;
            }
        }
    }

    /**
     * Finds the least of two dates, ignoring the nulls
     * @param a
     * @param b
     * @return The least of the dates
     */
    public static Date least(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.before(b) ? a : b));
    }

    /**
     * Sets the site's designation date as displayed on the general tab.
     * Date Earliest date of SPA_DATE, PROPOSED_DATE or SAC_DATE
     */
    private void setSiteDesignationDateDisplayValue() {

        // Set it only when it's applicable to this site.
        if (isTypeNatura2000()) {
            Date spaDate = parseDate(factsheet.getSiteObject().getSpaDate());
            Date sacDate = parseDate(factsheet.getSiteObject().getSacDate());
            Date proposedDate = parseDate(factsheet.getSiteObject().getProposedDate());
            Date confirmedDate = parseDate(factsheet.getSiteObject().getConfirmedDate());

            this.siteDesignationDateDisplayValue = least(least(spaDate, sacDate), proposedDate);
        } else if (isTypeCDDA()){
            this.siteDesignationDateDisplayValue = parseDate(factsheet.getSiteObject().getDesignationDate());
        } else if (isTypeDiploma()){
            this.siteDesignationDateDisplayValue = parseDate(factsheet.getSiteObject().getDesignationDate());
        }
    }

    private void populateHabitatLists() {
        habitats =  new ArrayList<HabitatsBean>();
        if(isTypeNatura2000()) {

            habit1Eunis = factsheet.findHabit1Eunis();
            habit1NotEunis = factsheet.findHabit1NotEunis();
            habit2Eunis = factsheet.findHabit2Eunis();
            habit2NotEunis = factsheet.findHabit2NotEunis();

            for(SiteHabitatsPersist shp : habit2Eunis){
                Chm62edtReportAttributesPersist attributesPersist = factsheet.findSiteAttributes("COVER", shp.getIdReportAttributes());
                String cover = "";
                if(attributesPersist!= null){
                    cover = attributesPersist.getValue();
                }
                HabitatsBean h = new HabitatsBean(shp, cover);
                habitats.add(h);
            }
        }
//        else {
//            factsheet.findSitesHabitatsByIDNatureObject();
//            factsheet.findSitesSpecificHabitats();
//        }
    }

    /**
     * Calculates the percentage of one area from another
     * @param cover
     * @param area
     * @return
     */
    private String calculatePercent(String cover, String area){
        try {
            float fArea = Float.parseFloat(area);
            float fCover = Float.parseFloat(cover);
            if(fArea == 0) {
                return cover;
            }

            float fPercent = fCover*100/fArea;
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(fPercent);
        } catch (Exception e) {
            return cover;
        }
    }

    private void calculateHabitatsCount() {
        habitatsCount = habitats.size();
    }

    private void prepareBiogeographicRegion() {
        getBioRegionsMap();
    }

    /**
     * @return the bioRegionsMap
     */
    public Map<String, Object> getBioRegionsMap() {

        if (bioRegionsMap == null) {

            bioRegionsMap = new HashMap<String, Object>();
            if (factsheet.getType() == SiteFactsheet.TYPE_NATURA2000 || factsheet.getType() == SiteFactsheet.TYPE_EMERALD) {

                boolean alpine = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("ALPINE"), false);
                boolean anatol1 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("ANATOL"), false);
                boolean anatol2 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("ANATOLIAN"), false);

                boolean arctic = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("ARCTIC"), false);
                boolean atlantic = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("ATLANTIC"), false);
                boolean boreal = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("BOREAL"), false);
                boolean continent1 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("CONTINENT"), false);
                boolean continent2 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("CONTINENTAL"), false);

                boolean macarones1 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MACARONES"), false);
                boolean macarones2 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MACARONESIAN"), false);

                boolean mediterranean1 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MEDITERRANIAN"), false);
                boolean mediterranean2 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MEDITERRANEAN"), false);

                boolean pannonic1 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("PANNONIC"), false);
                boolean pannonic2 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("PANNONIAN"), false);

                boolean pontic1 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("PONTIC"), false);
                boolean pontic2 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("BLACK SEA"), false);
                boolean pontic3 = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("BLACKSEA"), false);

                boolean steppic = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("STEPPIC"), false);

                boolean marineAtlantic = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MARINEATLANTIC"), false);
                boolean marineBaltic = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MARINEBALTIC"), false);
                boolean marineBlackSea = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MARINEBLACKSEA"), false);
                boolean marineMacaronesian = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MARINEMACARONESIAN"), false);
                boolean marineMediterranean = Utilities.checkedStringToBoolean(factsheet.findSiteAttribute("MARINEMEDITERRANEAN"), false);

                if (alpine) {
                    bioRegionsMap.put("alpine", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("alpine"));
                }
                if (anatol1 || anatol2) {
                    bioRegionsMap.put("anatol", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("anatol"));
                }
                if (arctic) {
                    bioRegionsMap.put("arctic", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("arctic"));
                }
                if (atlantic) {
                    bioRegionsMap.put("atlantic", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("atlantic"));
                }
                if (boreal) {
                    bioRegionsMap.put("boreal", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("boreal"));
                }
                if (continent1 || continent2) {
                    bioRegionsMap.put("continent", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("continent"));
                }
                if (macarones1 || macarones2) {
                    bioRegionsMap.put("macarones", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("macarones"));
                }
                if (mediterranean1 || mediterranean2) {
                    bioRegionsMap.put("mediterranean", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("mediterranean"));
                }
                if (pannonic1 || pannonic2) {
                    bioRegionsMap.put("pannonic", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("pannonic"));
                }
                if (pontic1 || pontic2 || pontic3) {
                    bioRegionsMap.put("pontic", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("pontic"));
                }
                if (steppic) {
                    bioRegionsMap.put("steppic", "");
                    biogeographicRegion.add(biogeographicRegionTitles.get("steppic"));
                }
                if(marineAtlantic) {
                    bioRegionsMap.put("marineAtlantic","");
                    biogeographicRegion.add(biogeographicRegionTitles.get("marineAtlantic"));
                }
                if(marineBaltic) {
                    bioRegionsMap.put("marineBaltic","");
                    biogeographicRegion.add(biogeographicRegionTitles.get("marineBaltic"));
                }
                if(marineBlackSea) {
                    bioRegionsMap.put("marineBlackSea","");
                    biogeographicRegion.add(biogeographicRegionTitles.get("marineBlackSea"));
                }
                if(marineMacaronesian) {
                    bioRegionsMap.put("marineMacaronesian","");
                    biogeographicRegion.add(biogeographicRegionTitles.get("marineMacaronesian"));
                }
                if(marineMediterranean) {
                    bioRegionsMap.put("marineMediterranean","");
                    biogeographicRegion.add(biogeographicRegionTitles.get("marineMediterranean"));
                }
            }
        }
        return bioRegionsMap;
    }

    public boolean isTypeCorine() {
        return factsheet.getType() == SiteFactsheet.TYPE_CORINE;
    }

    public boolean isTypeDiploma() {
        return factsheet.getType() == SiteFactsheet.TYPE_DIPLOMA;
    }

    public boolean isTypeNatura2000(){
        return factsheet.getType() == SiteFactsheet.TYPE_NATURA2000;
    }

    public boolean isTypeCDDA() {
        return factsheet.getType() == SiteFactsheet.TYPE_CDDA_INTERNATIONAL
                || factsheet.getType() == SiteFactsheet.TYPE_CDDA_NATIONAL;
    }

    public String getTypeName(){
        return factsheet.getSiteObject().getSourceDB();
    }

    /**
     * The title of the site type / source_db, using the sourceDBTitles list
     * @return The site type title
     */
    public String getTypeTitle(){
        return sourceDBTitles.get(getTypeName());
    }

    public String getIdsite() {
        return idsite;
    }

    public void setIdsite(String idsite) {
        this.idsite = idsite;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public SiteFactsheet getFactsheet() {
        return factsheet;
    }

    /**
     * List of designations overlapping the site
     * @return
     */
    public List getSitesDesigc() {
        return sitesDesigc;
    }

    public Date getSiteDesignationDateDisplayValue() {
        return siteDesignationDateDisplayValue;
    }

    public String getSurfaceAreaKm2() {
        return surfaceAreaKm2;
    }

    public List<String> getBiogeographicRegion() {
        return biogeographicRegion;
    }

    public int getBiogeographicRegionsCount() {
        if(biogeographicRegion != null)
            return biogeographicRegion.size();
        else
            return 0;
    }

    public String getBiogeographicRegionList() {
        String s = "";
        for(int i=0; i<biogeographicRegion.size(); i++){
            s = s + biogeographicRegion.get(i);
            if(i == biogeographicRegion.size() - 2)  {
                s += " and ";
            } else if(i<biogeographicRegion.size() - 1) {
                s += ", ";
            }
        }
        return s;
    }

    public int getProtectedSpeciesCount() {
        return protectedSpeciesCount;
    }

    public int getTotalSpeciesCount() {
        return totalSpeciesCount;
    }

    public int getHabitatsCount() {
        return habitatsCount;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    /**
     * The percentage of marine area in the site
     * @return null if there is no data
     */
    public String getMarineAreaPercentage(){
        String val = factsheet.getSiteObject().getMarineAreaPercentage();
        if(val == null)
            return null;
        return (new DecimalFormat("#")).format(Double.parseDouble(val));
    }

    /**
     * The IUCN category (IUCNAT DB field)
     * @return The IUCN category
     */
    public String getIucnCategory(){
        return factsheet.getSiteObject().getIucnat();
    }

    /**
     * Read the region fields (NUTS code, Region name) from the factsheet
     */
    private void prepareRegion(){
        this.regionCode = factsheet.getSiteObject().getNuts();
        this.regionName = factsheet.getRegionName();

        // extract the list from the sites attributes
        regions = factsheet.findSiteRegions();
        for(Chm62edtSitesAttributesPersist region : regions){
            region.setName(region.getName().replace("NUTS_",""));
            // todo identify the region by NUTS code in the chm62edt_region_codes and display that region name, not the one in SDF
        }
    }

    /**
     * NUTS code
     * @return The code of the region
     */
    public String getRegionCode(){
        return regionCode;
    }

    /**
     *
     * @return The name of the region
     */
    public String getRegionName(){
        return regionName;
    }

    public List<SpeciesBean> getAllSiteSpecies(){
        return allSiteSpecies;
    }

    public class SpeciesStatistics{
        private String key;
        private int value;

        public SpeciesStatistics(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }
    }


    /**
     * Factory for SpeciesBean from SiteSpeciesPersist
     * @param species The species object
     * @return new SpeciesBean
     */
    public SpeciesBean speciesBeanFromSiteSpecies(SiteSpeciesPersist species, SpeciesBean.SpeciesType speciesType) {
        // there are no such species in the database
        String url = "species/" + species.getIdSpecies();
        return new SpeciesBean(speciesType, species.getSpeciesScientificName(), species.getSpeciesCommonName(), species.getSpeciesCommonName(), species, species.getNatura2000Code(), url, species.getIdNatureObjectLink());
    }

    /**
     * Factory for SpeciesBean from SitesSpeciesReportAttributesPersist
     * @param species The species object
     * @return new SpeciesBean
     */
    public SpeciesBean speciesBeanFromSitesSpeciesReportAttributes(SitesSpeciesReportAttributesPersist species, SpeciesBean.SpeciesType speciesType) {
        String url = "species/" + species.getIdSpecies();
        return new SpeciesBean(speciesType, species.getSpeciesScientificName(), null, species.getSpeciesCommonName(), species, species.getNatura2000Code(), url, species.getIdNatureObjectLink());
    }

    public SpeciesBean speciesBeanFromSitesAttributes(Chm62edtSitesAttributesPersist species, SpeciesBean.SpeciesType type) {
        String scientificName = null;
        String group = null;

        if (type == SpeciesBean.SpeciesType.SITE_SPECIFIC) {
            scientificName = species.getValue();
            group = "";
        } else if (type == SpeciesBean.SpeciesType.NOT_EUNIS_LISTED) {
            scientificName = (species.getName() == null ? "" : species.getName().substring(species.getName().lastIndexOf("_") + 1));
            String groupName = species.getSourceTable();
            groupName = (groupName == null ? "" : (groupName.equalsIgnoreCase("amprep") ? "Amphibians"
                    : (groupName.equalsIgnoreCase("bird") ? "Birds"
                    : (groupName.equalsIgnoreCase("fishes") ? "Fishes"
                    : (groupName.equalsIgnoreCase("invert") ? "Invertebrates"
                    : (groupName.equalsIgnoreCase("mammal") ? "Mammals"
                    : (groupName.equalsIgnoreCase("plant") ? "Flowering Plants" : "")))))));
            group = groupName;
        } else if (type == SpeciesBean.SpeciesType.NOT_EUNIS_OTHER) {
            scientificName = (species.getName() == null ? "" : species.getName().substring(species.getName().lastIndexOf("_") + 1));
            Chm62edtSitesAttributesPersist attribute2 = factsheet.findNotEunisSpeciesOtherMentionedAttributes("TAXGROUP_" + scientificName);
            String groupName = (null != attribute2) ? ((null != attribute2.getValue()) ? attribute2.getValue() : "") : "";
            groupName = (groupName == null ? "" : (groupName.equalsIgnoreCase("P") ? "Plants"
                    : (groupName.equalsIgnoreCase("A") ? "Amphibians"
                    : (groupName.equalsIgnoreCase("F") ? "Fishes"
                    : (groupName.equalsIgnoreCase("I") ? "Invertebrates"
                    : (groupName.equalsIgnoreCase("M") ? "Mammals"
                    : (groupName.equalsIgnoreCase("B") ? "Birds"
                    : (groupName.equalsIgnoreCase("F") ? "Flowering"
                    : (groupName.equalsIgnoreCase("R") ? "Reptiles" : "")))))))));
            group = groupName;
        }

        String url = "http://www.google.com/search?q=" + scientificName;
        return new SpeciesBean(type, scientificName, null, group, species, null, url, null);
    }


    public Date getSpaDate(){
        return parseDate(factsheet.getSiteObject().getSpaDate());
    }

    public Date getSacDate(){
        return parseDate(factsheet.getSiteObject().getSacDate());
    }

    public Date getSciProposedDate(){
        return parseDate(factsheet.getSiteObject().getProposedDate());
    }

    public Date getDesignationDate(){
        return parseDate(factsheet.getSiteObject().getDesignationDate());
    }

    public Date getUpdateDate(){
        return parseDate(factsheet.getSiteObject().getUpdateDate());
    }

    public String getSiteType(){
        return factsheet.getSiteObject().getSiteType();
    }

    public List getHabit2Eunis() {
        return habit2Eunis;
    }

    public List<HabitatsBean> getHabitats() {
        return habitats;
    }

    public List<Chm62edtSitesAttributesPersist> getRegions() {
        return regions;
    }

    public Date getSciConfirmedDate(){
        return parseDate(factsheet.getSiteObject().getConfirmedDate());
    }
}
