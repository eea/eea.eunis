package eionet.eunis.util;


public interface Constants {

    /** */
    // Warning messages
    public static final int SEVERITY_INFO = 1;
    public static final int SEVERITY_CAUTION = 2;
    public static final int SEVERITY_WARNING = 3;
    public static final int SEVERITY_ERROR = 4;

    // Link info constants
    public static final String GEOSPECIES_IDENTIFIER = "GEOSPECIES_IDENTIFIER";
    public static final String GBIF_PAGE = "hasGBIF";
    public static final String BIOLIB_PAGE = "hasBioLibPage";
    public static final String BBC_PAGE = "hasBBCPage";
    public static final String EOL_PAGE = "hasEOLPage";
    public static final String WIKIPEDIA_ARTICLE = "hasWikipediaArticle";
    public static final String WIKISPECIES_ARTICLE = "hasWikispeciesArticle";
    public static final String BUG_GUIDE = "hasBugGuidePage";
    public static final String ART17_SUMMARY = "art17SummaryPage";

    // Same Species
    public static final String SAME_SPECIES_EOL = "sameSpeciesEOL"; // www.eol.org
    public static final String SAME_SPECIES_GBIF = "sameSpeciesGBIF"; // www.gbif.org
    public static final String SAME_SPECIES_ITIS = "sameSpeciesITIS"; // www.itis.gov
    public static final String SAME_SPECIES_NCBI = "sameSpeciesNCBI"; // www.ncbi.nlm.nih.gov
    public static final String SAME_SPECIES_REDLIST = "sameSpeciesRedlist"; // IUCN Red list
    public static final String SAME_SPECIES_REDLIST_EUROPE = "sameSpeciesRedlistEurope"; // IUCN Red list for Europe
    public static final String SAME_SPECIES_FIFAO = "sameSpeciesFIFAO"; // FAO aquatic species distribution

    // Same Synonym
    public static final String SAME_SYNONYM_EOL = "sameSynonymEOL";
    public static final String SAME_SYNONYM_GBIF = "sameSynonymGBIF";
    public static final String SAME_SYNONYM_ITIS = "sameSynonymITIS";
    public static final String SAME_SYNONYM_NCBI = "sameSynonymNCBI";
    public static final String SAME_SYNONYM_FAEU = "sameSynonymFaEu"; // Fauna Europaea
    public static final String SAME_SYNONYM_WORMS = "sameSynonymWorMS"; // World Register of Marine Species

    // BioRegions
    public static final String ALPINE = "ALPINE";
    public static final String ANATOL = "ANATOL";
    public static final String ARCTIC = "ARCTIC";
    public static final String ATLANTIC = "ATLANTIC";
    public static final String BOREAL = "BOREAL";
    public static final String CONTINENT = "CONTINENT";
    public static final String MACARONES = "MACARONES";
    public static final String MEDITERRANIAN = "MEDITERRANIAN";
    public static final String PANNONIC = "PANNONIC";
    public static final String PONTIC = "PONTIC";
    public static final String STEPPIC = "STEPPIC";

    // ReportAttributes - habitats
    public static final String REPORT_ATTRIBUTE_HABITAT_COVER = "COVER";
    public static final String REPORT_ATTRIBUTE_HABITAT_REPRESENTATIVITY = "REPRESENTATIVITY";
    public static final String REPORT_ATTRIBUTE_HABITAT_RELATIVE_SURFACE = "RELATIVE_SURFACE";
    public static final String REPORT_ATTRIBUTE_HABITAT_CONSERVATION = "CONSERVATION";
    public static final String REPORT_ATTRIBUTE_HABITAT_GLOBAL = "GLOBAL";
    public static final String REPORT_ATTRIBUTE_HABITAT_SOURCE_TABLE = "SOURCE_TABLE";

    // ReportAttributes - species
    public static final String REPORT_ATTRIBUTE_SPECIES_RESIDENT = "RESIDENT";
    public static final String REPORT_ATTRIBUTE_SPECIES_POPULATION = "POPULATION";
    public static final String REPORT_ATTRIBUTE_SPECIES_CONSERVATION = "CONSERVATION";
    public static final String REPORT_ATTRIBUTE_SPECIES_ISOLATION = "ISOLATION";
    public static final String REPORT_ATTRIBUTE_SPECIES_GLOBAL = "GLOBAL";
    public static final String REPORT_ATTRIBUTE_SPECIES_STAGING = "STAGING";
    public static final String REPORT_ATTRIBUTE_SPECIES_WINTER = "WINTERING";
    public static final String REPORT_ATTRIBUTE_SPECIES_BREEDING = "BREEDING";
    public static final String REPORT_ATTRIBUTE_SPECIES_OTHER_SPECIES = "OTHER_SPECIES";
    public static final String REPORT_ATTRIBUTE_SPECIES_SOURCE_TABLE = "SOURCE_TABLE";

    public static final String REPORT_ATTRIBUTE_OTHER_SPECIES_POPULATION = "OTHER_POPULATION";
    public static final String REPORT_ATTRIBUTE_OTHER_SPECIES_MOTIVATION = "OTHER_MOTIVATION";

    // Site Factsheet tab names
    public static final String SITES_TAB_GENERAL = "GENERAL_INFORMATION";
    public static final String SITES_TAB_FAUNA_FLORA = "FAUNA_FLORA";
    public static final String SITES_TAB_DESIGNATION = "DESIGNATION";
    public static final String SITES_TAB_HABITAT_TYPES = "HABITATS";
    public static final String SITES_TAB_SITES = "SITES";
    public static final String SITES_TAB_OTHER_INFO = "OTHER";

    // SOURCE_TABLE values
    public static final String N2000_SPECIES_GROUP_BIRD = "Bird";
    public static final String N2000_SPECIES_GROUP_MAMMAL = "Mammal";
    public static final String N2000_SPECIES_GROUP_AMPREP = "Amprep";
    public static final String N2000_SPECIES_GROUP_FISHES = "Fishes";
    public static final String N2000_SPECIES_GROUP_INVERT = "invert";
    public static final String N2000_SPECIES_GROUP_PLANT = "Plant";
    public static final String N2000_SPECIES_GROUP_AMPHIBIANS = "Amphibians";
    public static final String N2000_SPECIES_GROUP_FUNGI = "Fungi";
    public static final String N2000_SPECIES_GROUP_LICHENS = "Lichens";
    public static final String N2000_SPECIES_GROUP_REPTILES = "Reptiles";

    // the list of above values
    // please update the list if new groups are added!
    public static final String[] N2000_ALL_SPECIES_GROUPS =
            {N2000_SPECIES_GROUP_BIRD,
                    N2000_SPECIES_GROUP_MAMMAL,
                    N2000_SPECIES_GROUP_AMPREP,
                    N2000_SPECIES_GROUP_FISHES,
                    N2000_SPECIES_GROUP_INVERT,
                    N2000_SPECIES_GROUP_PLANT,
                    N2000_SPECIES_GROUP_AMPHIBIANS,
                    N2000_SPECIES_GROUP_FUNGI,
                    N2000_SPECIES_GROUP_LICHENS,
                    N2000_SPECIES_GROUP_REPTILES};

    // RDF datatypes
    public static final String XSD_DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
    public static final String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

    // RDF header
    public static final String ACCEPT_RDF_HEADER = "application/rdf+xml";

    // RDF footer
    public static final String RDF_FOOTER = "\n</rdf:RDF>\n";

    /** The name of the webapp init-parameter that stands for application home. */
    public static final String APP_HOME_INIT_PARAM = "APP_HOME";

    public static final Integer RESOLUTION4 = 2467;
    public static final Integer ANNEX1 = 2324;

    public static final int IAS_ANNEX = 2485;
    public static final int IAS_REGULATION = 2484;

    public static final String HABITAT_EUNIS = "EUNIS";
    public static final String HABITAT_EUNIS_2017 = "EUNIS2017";
    public static final String HABITAT_ANNEX1 = "ANNEX1";
    public static final String HABITAT_REDLIST = "REDLIST";
    public static final int HABITAT_ANNEX1_ROOT = 10000;
}

