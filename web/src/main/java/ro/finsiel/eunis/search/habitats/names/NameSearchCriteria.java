package ro.finsiel.eunis.search.habitats.names;

import java.util.Hashtable;

import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.EunisUtil;

/**
 * Search criteria used for habitats->references.
 * 
 * @author finsiel
 */
public class NameSearchCriteria extends AbstractSearchCriteria {

    /** Used for search in results, to filter after the level. */
    public static final Integer CRITERIA_LEVEL = 0;

    /** Used for search in results, to filter after the eunis code. */
    public static final Integer CRITERIA_CODE_EUNIS = 1;

    /** Used for search in results, to filter after the scientific name. */
    public static final Integer CRITERIA_SCIENTIFIC_NAME = 2;

    /** Used for search in results, to filter after the common name. */
    public static final Integer CRITERIA_NAME = 3;

    /** Used for search in results, to filter after the annex code. */
    public static final Integer CRITERIA_CODE_ANNEX = 4;

    /** Searched string. */
    private String searchString = null;

    /** How searched string is related: OPERATOR_CONTAINS/STARTS/IS. */
    private Integer relationOp = null;

    /** Search description or not. */
    private boolean useDescription = false;

    /** Search in Scientific name or not. */
    private boolean useScientificName = false;

    /** Search in Common names or not. */
    private boolean useVernacularName = false;

    /** Where to search: EUNIS or ANNEX I. */
    private Integer database = null;

    /** SQL mappings for search in results. */
    private Hashtable sqlMappings = null;

    /** Mapping to human language. */
    private Hashtable humanMappings = null;

    private boolean isExtra = false;

    /**
     * Default constructor used for first search.
     * 
     * @param searchString
     *            String to be searched
     * @param relationOp
     *            Relation (IS/CONTAINS/STARTS)
     * @param database
     *            Type of search: eunis / annex I
     * @param useDescription
     *            Specify if search will use description or not
     * @param useScientificName
     *            Use scientific name in search
     * @param useVernacularName
     *            Use common name in search
     * @param isExtra
     *            Is another main search criteria.
     */
    public NameSearchCriteria(String searchString, Integer relationOp, Integer database, boolean useDescription,
            boolean useScientificName, boolean useVernacularName, boolean isExtra) {
        _initSQLMappings(database);
        _initHumanMappings(database);
        this.searchString = searchString;
        this.relationOp = relationOp;
        this.useDescription = useDescription;
        this.useScientificName = useScientificName;
        this.useVernacularName = useVernacularName;
        this.database = database;
        this.isExtra = isExtra;
    }

    /**
     * This constructor is used for search in results.
     * 
     * @param criteriaSearch
     *            String to be searched
     * @param criteriaType
     *            Where to search
     * @param oper
     *            Relation between criteriaSearch and criteriaType
     * @param database
     *            What database to use: EUNIS or ANNEX I
     * @param isExtra
     *            Is another main search criteria.
     */
    public NameSearchCriteria(String criteriaSearch, Integer criteriaType, Integer oper, Integer database, boolean isExtra) {
        _initSQLMappings(database);
        _initHumanMappings(database);
        this.criteriaSearch = criteriaSearch;
        this.criteriaType = criteriaType;
        this.oper = oper;
        this.database = database;
        this.isExtra = isExtra;
    }

    /**
     * Init the mappings used to compose the SQL query.
     * 
     * @param database
     *            Not used.
     */
    private void _initSQLMappings(Integer database) {
        if (null != sqlMappings) {
            return;
        }
        sqlMappings = new Hashtable(5);

        sqlMappings.put(CRITERIA_CODE_EUNIS, "A.EUNIS_HABITAT_CODE ");
        sqlMappings.put(CRITERIA_CODE_ANNEX, "A.CODE_2000 ");
        sqlMappings.put(CRITERIA_LEVEL, "A.LEVEL ");
        sqlMappings.put(CRITERIA_NAME, "A.DESCRIPTION ");
        sqlMappings.put(CRITERIA_SCIENTIFIC_NAME, "A.SCIENTIFIC_NAME ");
    }

    /**
     * Init the mappings used to compose the SQL query.
     * 
     * @param database
     *            Not used.
     */
    private void _initHumanMappings(Integer database) {
        if (null != humanMappings) {
            return;
        }
        humanMappings = new Hashtable(5);

        humanMappings.put(CRITERIA_CODE_EUNIS, "EUNIS code ");
        humanMappings.put(CRITERIA_CODE_ANNEX, "ANNEX I code ");
        humanMappings.put(CRITERIA_LEVEL, "Habitat level ");
        humanMappings.put(CRITERIA_NAME, "English name ");
        humanMappings.put(CRITERIA_SCIENTIFIC_NAME, "Scientific name ");
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

        if (false != useDescription) {
            url.append(Utilities.writeURLParameter("useDescription", "true"));
        }
        if (false != useScientificName) {
            url.append(Utilities.writeURLParameter("useScientific", "true"));
        }
        if (false != useVernacularName) {
            url.append(Utilities.writeURLParameter("useVernacular", "true"));
        }
        if (null != database) {
            url.append(Utilities.writeURLParameter("database", database.toString()));
        }

        if (!isExtra) {
            if (null != searchString) {
                url.append(Utilities.writeURLParameter("searchString", searchString));
            }
            if (null != relationOp) {
                url.append(Utilities.writeURLParameter("relationOp", relationOp.toString()));
            }
        } else {
            if (null != searchString) {
                url.append(Utilities.writeURLParameter("searchStringExtra", searchString));
            }
            if (null != relationOp) {
                url.append(Utilities.writeURLParameter("relationOpExtra", relationOp.toString()));
            }
        }
        // Search in results
        if (null != criteriaSearch) {
            url.append(Utilities.writeURLParameter("criteriaSearch", criteriaSearch));
        }
        if (null != criteriaType) {
            url.append(Utilities.writeURLParameter("criteriaType", criteriaType.toString()));
        }
        if (null != oper) {
            url.append(Utilities.writeURLParameter("oper", oper.toString()));
        }
        return url.toString();
    }

    /**
     * Transform this object into an SQL representation without fuzzy search.
     * 
     * @return SQL string representing this object.
     */
    public String toSQL() {
        return toSQL(false);
    }

    /**
     * Transform this object into an SQL representation.
     * 
     * @return SQL string representing this object.
     */
    public String toSQL(boolean fuzzySearch) {
        StringBuffer sql = new StringBuffer();

        // Normal search
        if (null != searchString && null != relationOp) {
            sql.append("(");
            boolean addOROperator = false;

            if (useDescription) {
                sql.append(Utilities.prepareSQLOperator("B.DESCRIPTION", searchString, relationOp));
                addOROperator = true;
            }
            if (useScientificName) {
                if (addOROperator) {
                    sql.append(" OR ");
                }
                sql.append("(");

                sql.append(Utilities.prepareSQLOperator("A.SCIENTIFIC_NAME", searchString, Utilities.OPERATOR_CONTAINS));

                sql.append(" OR ");
                sql.append(Utilities.prepareSQLOperator("A.CODE_2000", searchString, Utilities.OPERATOR_CONTAINS));
                sql.append(" OR ");
                sql.append(Utilities.prepareSQLOperator("A.CODE_ANNEX1", searchString, Utilities.OPERATOR_CONTAINS));
                sql.append(" OR ");
                sql.append(Utilities.prepareSQLOperator("A.EUNIS_HABITAT_CODE", searchString, Utilities.OPERATOR_CONTAINS));


                int substringLength = 3;
                if (fuzzySearch && searchString.length() >=substringLength) {
                    String subSearchString = searchString.substring(0, substringLength);
                    subSearchString = EunisUtil.mysqlEscapes(subSearchString);
                    sql.append(" OR (A.SCIENTIFIC_NAME LIKE '%").append(subSearchString).append("%' AND levenshtein('")
                            .append(subSearchString.toLowerCase()).append("', LOWER(A.SCIENTIFIC_NAME)) <= 10 ) ");
                }

                sql.append(") ");
                addOROperator = true;
            }
            if (useVernacularName) {
                if (addOROperator) {
                    sql.append(" OR ");
                }
                sql.append(Utilities.prepareSQLOperator("A.DESCRIPTION", searchString, relationOp));
            }
            sql.append(")");
        }
        // Search in results
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            sql.append(Utilities.prepareSQLOperator((String) sqlMappings.get(criteriaType), criteriaSearch, oper));
        }
        return sql.toString();
    }

    public boolean isUseScientificName() {
        return useScientificName;
    }

    /**
     * This method implements a procedure from morphing the object into an web page FORM representation. What I meant to say is that
     * I can say about an object for example: < INPUT type='hidden" name="searchCriteria" value="natrix"> < INPUT type='hidden"
     * name="oper" value="1"> < INPUT type='hidden" name="searchType" value="1">.
     * 
     * @return Web page FORM representation of the object
     */
    public String toFORMParam() {
        StringBuffer form = new StringBuffer();

        if (false != useDescription) {
            form.append(Utilities.writeFormParameter("useDescription", "true"));
        }
        if (false != useScientificName) {
            form.append(Utilities.writeFormParameter("useScientific", "true"));
        }
        if (false != useVernacularName) {
            form.append(Utilities.writeFormParameter("useVernacular", "true"));
        }
        if (null != database) {
            form.append(Utilities.writeFormParameter("database", database.toString()));
        }
        if (!isExtra) {
            if (null != searchString) {
                form.append(Utilities.writeFormParameter("searchString", searchString));
            }
            if (null != relationOp) {
                form.append(Utilities.writeFormParameter("relationOp", relationOp.toString()));
            }
        } else {
            if (null != searchString) {
                form.append(Utilities.writeFormParameter("searchStringExtra", searchString));
            }
            if (null != relationOp) {
                form.append(Utilities.writeFormParameter("relationOpExtra", relationOp.toString()));
            }
        }
        // Search in results
        if (null != criteriaSearch) {
            form.append(Utilities.writeFormParameter("criteriaSearch", criteriaSearch));
        }
        if (null != criteriaType) {
            form.append(Utilities.writeFormParameter("criteriaType", criteriaType.toString()));
        }
        if (null != oper) {
            form.append(Utilities.writeFormParameter("oper", oper.toString()));
        }
        return form.toString();
    }

    /**
     * This method supplies a human readable string representation of this object. for example "Country is Romania"... so an
     * representation of this object could be displayed on the page.
     * 
     * @return A human readable representation of an object.
     */
    public String toHumanString() {
        StringBuffer human = new StringBuffer();

        // Normal search
        if (null != searchString && null != relationOp) {
            boolean addOROperator = false;

            if (useDescription) {
                human.append("Description");
                addOROperator = true;
            }
            if (useScientificName) {
                if (addOROperator) {
                    human.append(" / ");
                }
                human.append("Scientific name");
                addOROperator = true;
            }
            if (useVernacularName) {
                if (addOROperator) {
                    human.append(" / ");
                }
                human.append("Common name");
            }
            if(addOROperator) {
                human.append(" / ");
            }
            human.append("Code");
            human = Utilities.prepareHumanString(human.toString(), searchString, relationOp);
        }
        // Search in results
        if (null != criteriaSearch && null != criteriaType && null != oper) {
            human.append(Utilities.prepareHumanString((String) humanMappings.get(criteriaType), criteriaSearch, oper));
        }
        return human.toString();
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
