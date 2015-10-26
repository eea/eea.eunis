package ro.finsiel.eunis.search.species.names;

import java.util.Hashtable;

import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.species.SpeciesSearchUtility;

/**
 * Search criteria used for species->names.
 * 
 * @author finsiel
 */
public class NameSearchCriteria extends AbstractSearchCriteria {

    /** Coming from first form (scientific name). */
    public static final Integer CRITERIA_SCIENTIFIC = 0;

    /** Coming from second form (common name). */
    public static final Integer CRITERIA_VERNACULAR = 1;

    /** Used in filters, filtering by Group. */
    public static final Integer CRITERIA_GROUP = 2;

    /** Used in filters, filtering by Order. */
    public static final Integer CRITERIA_ORDER = 3;

    /** Used in filters, filtering by Family. */
    public static final Integer CRITERIA_FAMILY = 5;

    /** Used in filters, filtering by Scientific name. */
    public static final Integer CRITERIA_SCIENTIFIC_NAME = 4;

    /** Used to search for Natura 2000 codes */
    public static final Integer CRITERIA_NATURA2000 = 5;

    /** Scientific name if coming from first form. */
    private String scientificName = null;

    /** Common name if coming from second form. */
    private String vernacularName = null;

    /** Relation between scientificName or vernacularName (starts, contains, is). */
    private Integer relationOp = null;

    /** Language for common name. */
    private String language = null;

    /** Mappings from search criteria -> SQL Statement. */
    private Hashtable sqlMappings = null;

    /** Mappings from search criteria -> Human language. */
    private Hashtable humanMappings = null;

    private boolean isMainCriteria = false;

    /**
     * Main constructor, when doing initial search for scientific name.
     * 
     * @param scientificName
     *            Scientific name or common, depending which form we came from
     * @param relationOp
     *            Relation used for search (starts, contains, is). Can be OPERATOR_IS/CONTAINS/STARTS
     */
    public NameSearchCriteria(String scientificName, Integer relationOp) {
        _initHumanMappings();
        _initSQLMappings();
        this.scientificName = scientificName;
        this.relationOp = relationOp;
        this.isMainCriteria = true;
    }

    /**
     * Main constructor, when doing initial search for common name.
     * 
     * @param vernacularName
     *            Common name searching for
     * @param language
     *            ID of the language we're searching in (could also be 'any'
     * @param relationOp
     *            Relation used for search (starts/is/contains). Can be OPERATOR_IS/CONTAINS/STARTS
     */
    public NameSearchCriteria(String vernacularName, String language, Integer relationOp) {
        _initHumanMappings();
        _initSQLMappings();
        this.vernacularName = vernacularName;
        this.relationOp = relationOp;
        this.language = language;
        this.isMainCriteria = false;
    }

    /**
     * Second constructor used to construct filter search criterias (search in results).
     * 
     * @param criteriaSearch
     *            Search string.
     * @param criteriaType
     *            What we search for. Can be CRITERIA_GROUP/ORDER/SCIENTIFIC_NAME
     * @param oper
     *            Type of relation between criteriaSearch & criteriaType. Can be OPERATOR_IS/CONTAINS/STARTS.
     */
    public NameSearchCriteria(String criteriaSearch, Integer criteriaType, Integer oper) {
        _initHumanMappings();
        _initSQLMappings();
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            this.criteriaSearch = criteriaSearch;
            this.criteriaType = criteriaType;
            this.oper = oper;
        }
    }

    /** Default constructor, do not use this for normal operations. Used only for debugging & testing. */
    public NameSearchCriteria() {
        this.criteriaSearch = null;
        this.criteriaType = null;
        this.oper = null;
        this.scientificName = null;
        this.vernacularName = null;
        this.relationOp = null;
        this.language = null;
    }

    /** Init the mappings used to compose the SQL query. */
    private void _initSQLMappings() {
        if (null != sqlMappings) {
            return;
        }
        sqlMappings = new Hashtable();
        sqlMappings.put(CRITERIA_SCIENTIFIC, "A.SCIENTIFIC_NAME ");
        sqlMappings.put(CRITERIA_VERNACULAR, "G.VALUE");
        sqlMappings.put(CRITERIA_GROUP, "A.ID_GROUP_SPECIES");
        sqlMappings.put(CRITERIA_ORDER, "D.NAME");
        sqlMappings.put(CRITERIA_FAMILY, "C.NAME");
        sqlMappings.put(CRITERIA_SCIENTIFIC_NAME, "A.SCIENTIFIC_NAME ");
        sqlMappings.put(CRITERIA_NATURA2000, "A.CODE_2000 ");
    }

    /** Init the human mappings so you can represent this object in human language. */
    private void _initHumanMappings() {
        if (null != humanMappings) {
            return;
        }
        humanMappings = new Hashtable();
        humanMappings.put(CRITERIA_SCIENTIFIC, "Scientific name ");
        humanMappings.put(CRITERIA_VERNACULAR, "Common name ");
        humanMappings.put(CRITERIA_GROUP, "Group ");
        humanMappings.put(CRITERIA_ORDER, "Order ");
        humanMappings.put(CRITERIA_FAMILY, "Family ");
        humanMappings.put(CRITERIA_SCIENTIFIC_NAME, "Scientific name ");
        humanMappings.put(CRITERIA_NATURA2000, "Natura 2000 code ");

    }

    /**
     * This method must be implementing by inheriting classes and should return the representation of an object as an URL, for
     * example if implementing class has 2 params: county/region then this method should return: country=XXX&region=YYY, in order to
     * put the object on the request to forward params to next page.
     * 
     * @return An URL compatible representation of this object.
     */
    public String toURLParam() {
        StringBuffer url = new StringBuffer();

        if (null != scientificName && null != relationOp) {
            url.append(Utilities.writeURLParameter("scientificName", scientificName));
            url.append(Utilities.writeURLParameter("relationOp", relationOp.toString()));
        }
        if (null != vernacularName && null != relationOp && null != language) {
            url.append(Utilities.writeURLParameter("vernacularName", vernacularName));
            url.append(Utilities.writeURLParameter("relationOp", relationOp.toString()));
            url.append(Utilities.writeURLParameter("language", language.toString()));
        }
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            url.append(Utilities.writeURLParameter("criteriaSearch", criteriaSearch));
            url.append(Utilities.writeURLParameter("criteriaType", criteriaType.toString()));
            url.append(Utilities.writeURLParameter("oper", oper.toString()));
        }
        return url.toString();
    }

    /**
     * Transform this object into an SQL representation.
     * 
     * @return SQL string representing this object.
     */
    public String toSQL() {
        StringBuilder sql = new StringBuilder();

        // Coming from form 1
        if (null != scientificName && null != relationOp) {
            sql.append("(");
            sql.append(Utilities.prepareSQLOperator((String) sqlMappings.get(CRITERIA_SCIENTIFIC), scientificName, relationOp));
            sql.append(" OR ");
            sql.append(Utilities.prepareSQLOperator((String) sqlMappings.get(CRITERIA_NATURA2000), scientificName, Utilities.OPERATOR_CONTAINS));
            sql.append(")");
        }
        // Coming from form 2
        if (null != vernacularName && null != relationOp && null != language) {
            String _criteria = null;

            // See if we are using query with any language or a specified language.
            _criteria =
                    (language.equalsIgnoreCase("any")) ? " AND I.NAME ='VERNACULAR_NAME' AND  I.VALUE " : " AND H.NAME_EN = '"
                            + language + "' AND I.NAME ='VERNACULAR_NAME' AND I.VALUE ";
            sql.append(Utilities.prepareSQLOperator(_criteria, vernacularName, relationOp));
        }
        // Search in results
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            String _criteria = criteriaSearch;

            // Do the mapping / transform from group name to group ID
            if (0 == criteriaType.compareTo(CRITERIA_GROUP)) {
                _criteria = SpeciesSearchUtility.findGroupID(criteriaSearch).toString();
            }
            sql.append(Utilities.prepareSQLOperator((String) sqlMappings.get(criteriaType), _criteria, oper));
        }
        return sql.toString();
    }

    /**
     * This method implements a procedure from morphing the object into an web page FORM representation. What I meant to say is that
     * I can say about an object for example: < INPUT type='hidden" name="searchCriteria" value="natrix"> < INPUT type='hidden"
     * name="oper" value="1"> < INPUT type='hidden" name="searchType" value="1">
     * 
     * @return Web page FORM representation of the object
     */
    public String toFORMParam() {
        StringBuffer url = new StringBuffer();

        if (null != scientificName && null != relationOp) {
            url.append(Utilities.writeFormParameter("scientificName", scientificName));
            url.append(Utilities.writeFormParameter("relationOp", relationOp.toString()));
        }
        if (null != vernacularName && null != relationOp) {
            url.append(Utilities.writeFormParameter("vernacularName", vernacularName));
            url.append(Utilities.writeFormParameter("relationOp", relationOp.toString()));
            url.append(Utilities.writeFormParameter("language", language.toString()));
        }
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            url.append(Utilities.writeFormParameter("criteriaSearch", criteriaSearch));
            url.append(Utilities.writeFormParameter("criteriaType", criteriaType.toString()));
            url.append(Utilities.writeFormParameter("oper", oper.toString()));
        }
        return url.toString();
    }

    /**
     * This method supplies a human readable string representation of this object. for example "Country is Romania"... so an
     * representation of this object could be displayed on the page.
     * 
     * @return A human readable representation of an object.
     */
    public String toHumanString() {
        StringBuffer sql = new StringBuffer();

        // Coming from form 1
        if (null != scientificName && null != relationOp) {
            sql.append(Utilities.prepareHumanString((String) humanMappings.get(CRITERIA_SCIENTIFIC), scientificName, relationOp));
        }
        // Coming from form 2
        if (null != vernacularName && null != relationOp) {
            sql.append(Utilities.prepareHumanString((String) humanMappings.get(CRITERIA_VERNACULAR), vernacularName, relationOp));
        }
        // Search in results
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            String _criteria = criteriaSearch;

            // Do the mapping / transform from group name to group ID
            if (0 == criteriaType.compareTo(CRITERIA_GROUP)) {
                _criteria = criteriaSearch;
            }
            sql.append(Utilities.prepareHumanString((String) humanMappings.get(criteriaType), _criteria, oper));
        }
        return sql.toString();
    }

    /**
     * Getter.
     * 
     * @return scientificName
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * Getter.
     * 
     * @return relationOp
     */
    public Integer getRelationOp() {
        return relationOp;
    }

    /**
     * Getter.
     * 
     * @return isMainCriteria
     */
    public boolean isMainCriteria() {
        return isMainCriteria;
    }
}
