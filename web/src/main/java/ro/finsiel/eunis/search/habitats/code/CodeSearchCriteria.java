package ro.finsiel.eunis.search.habitats.code;


import eionet.eunis.util.Constants;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.jrfTables.habitats.code.CodeDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtClassCodeDomain;

import java.util.Hashtable;
import java.util.List;


/**
 * Search criteria used for habitats->code.
 *
 * @author finsiel
 */
public class CodeSearchCriteria extends AbstractSearchCriteria {

    /**
     * Defines a search after Group.
     */
    public static final Integer CRITERIA_LEVEL = new Integer(1);

    /**
     * Defines a search after EUNIS.
     */
    public static final Integer CRITERIA_EUNIS_CODE = new Integer(2);

    /**
     * Defines a search after ANNEX I.
     */
    public static final Integer CRITERIA_ANNEX_CODE = new Integer(3);

    /**
     * Defines a search after Family.
     */
    public static final Integer CRITERIA_SCIENTIFIC_NAME = new Integer(4);

    /**
     * How searched string is related: OPERATOR_CONTAINS/STARTS/IS.
     */
    private Integer relationOp = null;

    /**
     * Searched string.
     */
    private String searchString = null;

    /**
     * This is the classification where to search. Can be a specified code, or: any.
     */
    private String classificationCode = null;

    /**
     * Which database to search. Can be CodeDomain.SEARCH_EUNIS/SEARCH_ANNEX/SEARCH_BOTH.
     */
    private Integer database = null;

    /**
     * Map the search criterias to SQL queries.
     */
    private static Hashtable sqlMappings = null;

    /**
     * Map the search criterias to human readable strings.
     */
    private static Hashtable humanMappings = null;

    /**
     * Default constructor.
     *
     * @param classificationCode classification.
     * @param relationOp         Relation op.
     * @param searchString       Search string.
     * @param database           Database.
     */
    public CodeSearchCriteria(String classificationCode, Integer relationOp, String searchString, Integer database) {
        _initSQLMappings();
        _initHumanMappings();
        this.criteriaType = CRITERIA_SCIENTIFIC_NAME;
        this.classificationCode = classificationCode;
        this.relationOp = relationOp;
        this.searchString = searchString;
        this.database = database;
    }

    /**
     * Alternate constructor for search in results.
     *
     * @param criteriaType       Type of search.
     * @param oper               Relation operator.
     * @param criteriaSearch     Search criteria.
     * @param classificationCode Code.
     * @param database           database.
     */
    public CodeSearchCriteria(Integer criteriaType, Integer oper, String criteriaSearch, String classificationCode, Integer database) {
        _initSQLMappings();
        _initHumanMappings();
        this.classificationCode = classificationCode;
        this.criteriaType = criteriaType;
        this.criteriaSearch = criteriaSearch;
        this.oper = oper;
        this.database = database;
    }

    /**
     * Transform this object into an SQL representation.
     *
     * @return SQL string representing this object.
     */
    public String toSQL() {
        StringBuffer sql = new StringBuffer();

        if (null != classificationCode && null != relationOp && null != searchString) {

            List codes = new Chm62edtClassCodeDomain().findWhere(
                    "ID_CLASS_CODE='" + classificationCode + "' AND CURRENT_CLASSIFICATION='1'");

            if (null == codes || codes.size() <= 0) { // if isn't current classification selected
                sql.append(" ( ");
                sql.append(Utilities.prepareSQLOperator("B.CODE", searchString, relationOp));

                // if(CodeDomain.SEARCH_EUNIS.compareTo(database) == 0
                // || 0 == CodeDomain.SEARCH_ANNEX.compareTo(database)
                // || 0 == CodeDomain.SEARCH_BOTH.compareTo(database)) {
                //
                // if(sql.length() > 0) sql.append(" OR ");
                //
                // if(0 == CodeDomain.SEARCH_EUNIS.compareTo(database)) {
                // sql.append(Utilities.prepareSQLOperator("A.EUNIS_HABITAT_CODE", searchString, relationOp));
                // }
                //
                // if(0 == CodeDomain.SEARCH_ANNEX.compareTo(database)) {
                // sql.append(Utilities.prepareSQLOperator("A.CODE_ANNEX1", searchString, relationOp));
                // }
                //
                // if(0 == CodeDomain.SEARCH_BOTH.compareTo(database)) {
                // sql.append(" ( ");
                // sql.append(Utilities.prepareSQLOperator("A.CODE_ANNEX1", searchString, relationOp));
                // sql.append(" OR ");
                // sql.append(Utilities.prepareSQLOperator("A.EUNIS_HABITAT_CODE", searchString, relationOp));
                // sql.append(" ) ");
                // }

                sql.append(" ) ");
                sql.append(" AND C.ID_CLASS_CODE='" + classificationCode + "'");
            } else {
                sql.append(Utilities.prepareSQLOperator("B.CODE", searchString, relationOp));

                // if(0 == CodeDomain.SEARCH_ANNEX.compareTo(database)) {
                sql.append(" AND C.ID_CLASS_CODE='" + classificationCode + "'");
                // }

            }

            // Put the condition for EUNIS / ANNEX habitats
            if (0 != database.compareTo(CodeDomain.SEARCH_BOTH)) {
                if (0 == database.compareTo(CodeDomain.SEARCH_EUNIS)) {
                    if (sql.length() > 0) {
                        sql.append(" AND ");
                    }
                    sql.append(" A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%' ");
                }
                if (0 == database.compareTo(CodeDomain.SEARCH_ANNEX)) {
                    if (sql.length() > 0) {
                        sql.append(" AND ");
                    }
                    sql.append(" A.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "' ");
                }
            } else {
                if (sql.length() > 0) {
                    sql.append(" AND ");
                }
                sql.append(" A.ID_HABITAT <>'-1' AND A.ID_HABITAT <>'" + Constants.HABITAT_ANNEX1_ROOT +"' ");
            }
        }
        if (null != criteriaSearch && null != oper && null != criteriaSearch && null != classificationCode) {
            sql.append(Utilities.prepareSQLOperator((String) sqlMappings.get(criteriaType), criteriaSearch, oper));
        }

        // System.out.println(sql);

        return sql.toString();
    }

    /**
     * This method must be implementing by inheriting classes and should return the representation of an object as
     * an URL, for example if implementing class has 2 params: county/region then this method should return:
     * country=XXX&region=YYY, in order to put the object on the request to forward params to next page.
     *
     * @return An URL compatible representation of this object.
     */
    public String toURLParam() {
        StringBuffer url = new StringBuffer();

        if (null != classificationCode && null != relationOp && null != searchString) {
            url.append(Utilities.writeURLParameter("classificationCode", classificationCode));
            url.append(Utilities.writeURLParameter("relationOp", relationOp.toString()));
            url.append(Utilities.writeURLParameter("searchString", searchString));
            url.append(Utilities.writeURLParameter("database", database.toString()));
        }
        if (criteriaSearch != null && criteriaType != null && oper != null) {
            url.append(Utilities.writeURLParameter("criteriaSearch", criteriaSearch));
            url.append(Utilities.writeURLParameter("criteriaType", criteriaType.toString()));
            url.append(Utilities.writeURLParameter("oper", oper.toString()));
        }
        return url.toString();
    }

    /**
     * This method implements a procedure from morphing the object into an web page FORM representation. What I meant
     * to say is that I can say about an object for example:
     * < INPUT type='hidden" name="searchCriteria" value="natrix">
     * < INPUT type='hidden" name="oper" value="1">
     * < INPUT type='hidden" name="searchType" value="1">
     *
     * @return Web page FORM representation of the object
     */
    public String toFORMParam() {
        StringBuffer form = new StringBuffer();

        if (null != classificationCode && null != relationOp && null != searchString) {
            form.append(Utilities.writeFormParameter("classificationCode", classificationCode));
            form.append(Utilities.writeFormParameter("relationOp", relationOp.toString()));
            form.append(Utilities.writeFormParameter("searchString", searchString));
            form.append(Utilities.writeFormParameter("database", database.toString()));
        }
        if (criteriaSearch != null && criteriaType != null && oper != null) {
            form.append(Utilities.writeFormParameter("criteriaSearch", criteriaSearch));
            form.append(Utilities.writeFormParameter("criteriaType", criteriaType.toString()));
            form.append(Utilities.writeFormParameter("oper", oper.toString()));
        }
        return form.toString();
    }

    /**
     * This method supplies a human readable string representation of this object. for example "Country is Romania"...
     * so an representation of this object could be displayed on the page.
     *
     * @return A human readable representation of an object.
     */
    public String toHumanString() {
        StringBuffer str = new StringBuffer();

        if (null != classificationCode && null != relationOp && null != searchString) {
            str.append(Utilities.prepareHumanString("code", searchString, relationOp));
        }
        if (null != criteriaSearch && null != oper && null != criteriaSearch && null != classificationCode) {
            str.append(Utilities.prepareHumanString((String) humanMappings.get(criteriaType), criteriaSearch, oper));
        }
        return str.toString();
    }

    /**
     * Init the mappings used to compose the SQL query.
     */
    private void _initSQLMappings() {
        if (null != sqlMappings) {
            return;
        }
        sqlMappings = new Hashtable(7);
        sqlMappings.put(CRITERIA_LEVEL, "A.LEVEL");
        sqlMappings.put(CRITERIA_EUNIS_CODE, "A.EUNIS_HABITAT_CODE");
        sqlMappings.put(CRITERIA_ANNEX_CODE, "A.CODE_2000");
        sqlMappings.put(CRITERIA_SCIENTIFIC_NAME, "A.SCIENTIFIC_NAME");
    }

    /**
     * Init the human mappings so you can represent this object in human language .
     */
    private void _initHumanMappings() {
        if (null != humanMappings) {
            return;
        }
        humanMappings = new Hashtable(7);
        humanMappings.put(CRITERIA_LEVEL, "Level");
        humanMappings.put(CRITERIA_EUNIS_CODE, "Eunis habitat code");
        humanMappings.put(CRITERIA_ANNEX_CODE, "Annex I code");
        humanMappings.put(CRITERIA_SCIENTIFIC_NAME, "Scientific name");
    }
}
