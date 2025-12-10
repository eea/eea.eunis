package ro.finsiel.eunis.jrfTables.habitats.names;

import java.util.List;

import eionet.eunis.dao.impl.MySqlBaseDao;
import eionet.eunis.util.Constants;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.OuterJoinTable;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.jrfTables.species.names.ScientificNamePersist;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.habitats.names.NameSearchCriteria;
import ro.finsiel.eunis.search.habitats.names.NameSortCriteria;

/**
 * @author finsiel
 * @since 29.01.2003
 * @version 1.0
 */
public class NamesDomain extends AbstractDomain implements Paginable {
    public static final Integer SEARCH_EUNIS = new Integer(0);
    public static final Integer SEARCH_ANNEX_I = new Integer(1);
    public static final Integer SEARCH_BOTH = new Integer(2);

    private static final Logger logger = Logger.getLogger(NamesDomain.class);

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
    /**
     * Extra criteria applied to search (from first page). These criterias differs from that are computer as SQL **OR** NOT **AND**
     * as are searchCriteria
     */
    private AbstractSearchCriteria[] searchCriteriaExtra = new AbstractSearchCriteria[0];
    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted
    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    /** Specifies where to search: SEARCH_EUNIS or SEARCH_ANNEX_I */
    private Integer searchPlace = SEARCH_EUNIS;
    /** Specifies whether to use fuzzy search */
    private boolean fuzzySearch;

    String habitatsName = null;

    /**
     * Default constructor
     * 
     * @param searchCriteria
     *            The criteria used for searching
     * @param searchPlace
     *            Where to search. This can be: this.SEARCH_EUNIS or this.SEARCH_ANNEX_I. Default to SEARCH_EUNIS
     * @param fuzzySearch
     *            Whether to use fuzzy search / levenshtein extension. Defaults to false.
     */
    public NamesDomain(AbstractSearchCriteria[] searchCriteria, AbstractSearchCriteria[] searchCriteriaExtra,
            AbstractSortCriteria[] sortCriteria, Integer searchPlace, boolean fuzzySearch) {
        this.searchCriteria = searchCriteria;
        this.searchPlace = searchPlace;
        this.sortCriteria = sortCriteria;
        this.searchCriteriaExtra = searchCriteriaExtra;
        this.fuzzySearch = fuzzySearch;
    }

    /**
     * Default constructor
     * 
     * @param searchCriteria
     *            The criteria used for searching
     * @param searchPlace
     *            Where to search. This can be: this.SEARCH_EUNIS or this.SEARCH_ANNEX_I. Default to SEARCH_EUNIS
     */
    public NamesDomain(AbstractSearchCriteria[] searchCriteria, AbstractSearchCriteria[] searchCriteriaExtra,
            AbstractSortCriteria[] sortCriteria, Integer searchPlace) {
        this(searchCriteria, searchCriteriaExtra, sortCriteria, searchPlace, false);
    }

    /**
     * This is the default constructor and is used only when you want to call the find* methods for this object, for example.
     */
    public NamesDomain() {
        this.searchCriteria = null;
        this.searchPlace = null;
        this.sortCriteria = null;
    }

    /****/
    public PersistentObject newPersistentObject() {
        return new NamesPersist();
    }

    /****/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_habitat");

        this.addColumnSpec(new StringColumnSpec("ID_HABITAT", "getIdHabitat", "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_ZERO,
                REQUIRED));
        this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName", "setScientificName", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("DESCRIPTION", "getDescription", "setDescription", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_ANNEX1", "getCodeAnnex1", "setCodeAnnex1", DEFAULT_TO_NULL));
        this.addColumnSpec(new ShortColumnSpec("PRIORITY", "getPriority", "setPriority", null, REQUIRED));
        this.addColumnSpec(new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode", "setEunisHabitatCode",
                DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CLASS_REF", "getClassRef", "setClassRef", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_PART_2", "getCodePart2", "setCodePart2", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("LEVEL", "getHabLevel", "setHabLevel", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("HABITAT_TYPE", "getHabitatType", "setHabitatType", DEFAULT_TO_NULL));

        this.setTableAlias("A");
        this.setReadOnly(true);
        // Add the join only if the search is also done in descriptions
        OuterJoinTable habitatDescr = new OuterJoinTable("chm62edt_habitat_description B", "ID_HABITAT", "ID_HABITAT");
        this.addJoinTable(habitatDescr);
    }

    /**
     * This method is used to retrieve a sub-set of the main results of a query given its start index offset and end index offset.
     * 
     * @param offsetStart
     *            The start offset (i.e. 0). If offsetStart = offSetEnd then return the whole list
     * @param pageSize
     *            The end offset (i.e. 1). If offsetStart = offSetEnd then return the whole list
     * @param sortCriteria
     *            The criteria used for sorting
     * @return A list of objects which match query criteria
     * @throws CriteriaMissingException
     */
    public List<ScientificNamePersist> getResults(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria)
            throws CriteriaMissingException {
        this.sortCriteria = sortCriteria;
        if (searchCriteria.length < 1) {
            throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
        }
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();
        // Add GROUP BY clause for unique results
        // Add the ORDER BY clause to do the sorting
        if (sortCriteria.length > 0) {
            filterSQL.append(_prepareWhereSort());
        }
        // Add the LIMIT clause to return only the wanted results
        if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
            filterSQL.append(" LIMIT " + offsetStart + ", " + pageSize);
        }

        List tempList = this.findWhere(filterSQL.toString());
        _resultCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
        return tempList;
    }

    /**
     * This method is used to count the total list of results from a query. It is used to find all for use in pagination. Having the
     * total number of results and the results displayed per page, the you could find the number of pages i.e.
     * 
     * @return The total number of results
     */
    public Long countResults() throws CriteriaMissingException {
        if (-1 == _resultCount.longValue()) {
            _resultCount = (long) _rawCount();
        }
        return _resultCount;
    }

    /**
     * This method does the raw counting (meaning that will do a DB query for retrieving results count). You should check in your
     * code that this method is called (in ideal way) only once and results are cached. This is what countResults() method does in
     * this class
     * 
     * @see ro.finsiel.eunis.jrfTables.species.country.RegionDomain#countResults
     * @return
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {
        StringBuffer sql = new StringBuffer();
        // Set the main QUERY
        // sql.append("SELECT COUNT(*) FROM chm62edt_habitat AS A LEFT JOIN chm62edt_habitat_description AS B ON A.ID_HABITAT = B.ID_HABITAT WHERE ");
        // sql.append("SELECT COUNT(DISTINCT A.SCIENTIFIC_NAME) FROM chm62edt_habitat AS A LEFT JOIN chm62edt_habitat_description AS B ON A.ID_HABITAT = B.ID_HABITAT WHERE ");
        // Apply WHERE CLAUSE

        sql.append(_prepareWhereSearch().toString());
        // System.out.println( "sql = " + sql );
        List ret = findWhere(sql.toString());
        if (null == ret)
            return new Long(0);
        return new Long(ret.size());
    }

    /**
     * This helper method is used to construct the string after WHERE...based on search criterias set. In another words constructs
     * .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX OR B.ID_GEOSCOPE_LINK=YYY OR B.ID_GEOSCOPE_LINK=ZZZ .....
     * 
     * @return SQL string
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     *             If no search criteria search or wrong criteria is set.
     */
    private StringBuffer _prepareWhereSearch() throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();

        if (searchCriteria.length <= 0) {
            throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
        }
        // Filter habitat types (ANNEX / EUNIS / BOTH)
        if (0 != searchPlace.compareTo(NamesDomain.SEARCH_BOTH)) {
            if (0 == searchPlace.compareTo(NamesDomain.SEARCH_EUNIS)) {
                filterSQL.append(" A.ID_HABITAT>=1 AND A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'  AND ");
            }
            if (0 == searchPlace.compareTo(NamesDomain.SEARCH_ANNEX_I)) {
                filterSQL.append(" A.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  AND ");
            }
        } else {
            filterSQL.append(" A.ID_HABITAT<>'-1' AND A.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  AND ");
        }

        int i;
        for (i = 0; i < searchCriteriaExtra.length; i++) {
            if (i > 0) {
                filterSQL.append(" AND ");
            }
            NameSearchCriteria aCriteria = (NameSearchCriteria) searchCriteriaExtra[i];
            filterSQL.append(aCriteria.toSQL(fuzzySearch));
        }
        if (i > 0) {
            filterSQL.append(" AND ");
        }
        for (i = 0; i < searchCriteria.length; i++) {
            if (i > 0)
                filterSQL.append(" AND ");
            NameSearchCriteria aCriteria = (NameSearchCriteria) searchCriteria[i];
            filterSQL.append(aCriteria.toSQL(fuzzySearch));
        }
        filterSQL
                .append(" AND IF(TRIM(A.CODE_2000) <> '',RIGHT(A.CODE_2000,2),1) <> IF(TRIM(A.CODE_2000) <> '','00',2) AND IF(TRIM(A.CODE_2000) <> '',LENGTH(A.CODE_2000),1) = IF(TRIM(A.CODE_2000) <> '',4,1) ");
        // filterSQL.append(" GROUP BY A.SCIENTIFIC_NAME ");
        filterSQL.append(" GROUP BY A.ID_HABITAT ");
        return filterSQL;
    }

    /**
     * Prepare the ORDER BY clause used to do the sorting. Basically it adds the ORDER clause with the criterias set in the
     * sortCriteria[] array.
     * 
     * @return SQL representation of the sorting.
     */
    private StringBuffer _prepareWhereSort() {
        StringBuffer filterSQL = new StringBuffer();
        try {
            boolean useSort = false;

            for (AbstractSortCriteria aSortCriteria : sortCriteria) {
                if (!aSortCriteria.getCriteriaAsString().equals("none")) { // Do not add if criteria is sort to NOT SORT
                    if (!aSortCriteria.getAscendencyAsString().equals("none")) { // Don't add if ascendency is set to none, nasty hacks
                        if (useSort) {
                            filterSQL.append(", ");
                        }
                        filterSQL.append(aSortCriteria.toSQL());
                        useSort = true;
                    }
                }
            }
            if (useSort) { // If a sort criteria was indeed used, then insert ORDER BY clause at the start of the string
                filterSQL.insert(0, " ORDER BY ");
            }
        } catch (InitializationException e) {
            logger.debug(e, e);  // To change body of catch statement use Options | File Templates.
        }
        return filterSQL;
    }

}
