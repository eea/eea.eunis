package ro.finsiel.eunis.jrfTables.habitats.references;

import java.util.ArrayList;
import java.util.List;

import eionet.eunis.dao.impl.MySqlBaseDao;
import eionet.eunis.util.Constants;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.DateJoinColumn;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;
import net.sf.jrf.join.joincolumns.ShortJoinColumn;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.habitats.references.ReferencesSearchCriteria;
import ro.finsiel.eunis.search.habitats.references.ReferencesSortCriteria;
import ro.finsiel.eunis.utilities.TableColumns;

public class HabitatsBooksDomain extends AbstractDomain implements Paginable {

    public static final Integer SEARCH_EUNIS = new Integer(0);
    public static final Integer SEARCH_ANNEX_I = new Integer(1);
    public static final Integer SEARCH_BOTH = new Integer(2);
    private static final Logger logger = Logger.getLogger(HabitatsBooksDomain.class);

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted
    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    /* Cache the count result of habitats scientific name which carry out the search criteria */
    private Long _choiceCount = new Long(-1);
    /** Specifies where to search: SEARCH_EUNIS or SEARCH_ANNEX_I */
    private Integer searchPlace = SEARCH_EUNIS;

    public HabitatsBooksDomain(AbstractSearchCriteria[] searchCriteria, Integer searchPlace) {
        this.searchCriteria = searchCriteria;
        this.searchPlace = searchPlace;
    }

    public HabitatsBooksDomain() {
    }

    public HabitatsBooksDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, Integer searchPlace) {
        this.searchCriteria = searchCriteria;
        this.searchPlace = searchPlace;
        this.sortCriteria = sortCriteria;

    }


    /****/
    public PersistentObject newPersistentObject() {
        return new HabitatsBooksPersist();
    }

    /****/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_habitat");
        this.setReadOnly(true);
        this.setTableAlias("C");

        this.addColumnSpec(new StringColumnSpec("ID_HABITAT", "getIdHabitat", "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_ZERO, REQUIRED));
        this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName", "setScientificName", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("DESCRIPTION", "getDescription", "setDescription", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_ANNEX1", "getCodeAnnex1", "setCodeAnnex1", DEFAULT_TO_NULL));
        this.addColumnSpec(new ShortColumnSpec("PRIORITY", "getPriority", "setPriority", null, REQUIRED));
        this.addColumnSpec(new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode", "setEunisHabitatCode", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CLASS_REF", "getClassRef", "setClassRef", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_PART_2", "getCodePart2", "setCodePart2", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("LEVEL", "getHabLevel", "setHabLevel", DEFAULT_TO_NULL));

        // Joined tables
        JoinTable habitatReferences = null;
        habitatReferences = new JoinTable("chm62edt_habitat_references B", "ID_HABITAT", "ID_HABITAT");
        habitatReferences.addJoinColumn(new IntegerJoinColumn("ID_DC", "idDC", "setIdDC"));
        habitatReferences.addJoinColumn(new ShortJoinColumn("HAVE_SOURCE", "haveSource", "setHaveSource"));
        habitatReferences.addJoinColumn(new ShortJoinColumn("HAVE_OTHER_REFERENCES", "haveOtherReferences", "setHaveOtherReferences"));
        this.addJoinTable(habitatReferences);

        JoinTable dc_index = null;
        dc_index = new JoinTable("dc_index J", "ID_DC", "ID_DC");
        dc_index.addJoinColumn(new StringJoinColumn("SOURCE", "source", "setSource"));
        dc_index.addJoinColumn(new StringJoinColumn("EDITOR", "editor", "setEditor"));
        dc_index.addJoinColumn(new StringJoinColumn("CREATED", "created", "setCreated"));
        dc_index.addJoinColumn(new StringJoinColumn("TITLE", "title", "setTitle"));
        dc_index.addJoinColumn(new StringJoinColumn("ALTERNATIVE", "alternative", "setAlternative"));
        dc_index.addJoinColumn(new StringJoinColumn("PUBLISHER", "publisher", "setPublisher"));
        habitatReferences.addJoinTable(dc_index);
    }

    /** This method is used to retrieve a sub-set of the main results of a query given its start index offset and end
     * index offset.
     * @param offsetStart The start offset (i.e. 0). If offsetStart = offSetEnd then return the whole list
     * @param pageSize The end offset (i.e. 1). If offsetStart = offSetEnd then return the whole list
     * @param sortCriteria The criteria used for sorting
     * @return A list of objects which match query criteria
     */
    public List getResults(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria) throws CriteriaMissingException {
        this.sortCriteria = sortCriteria;
        if (searchCriteria.length < 1) throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();
        // Add GROUP BY clause for unique results
        filterSQL.append(" GROUP BY J.SOURCE,J.EDITOR,J.CREATED,J.TITLE,J.PUBLISHER ");
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
     * Cache result of habitats scientific name which carry out the search criteria
     * @return a list with results
     * @throws CriteriaMissingException searchCriteria is empty
     */
    public List getHabitatsWithReferences(boolean useLimit) throws CriteriaMissingException {
        if (searchCriteria.length < 1) throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();
        // Add GROUP BY clause for unique results
        filterSQL.append(" GROUP BY C.SCIENTIFIC_NAME ");
        if (useLimit) filterSQL.append(" LIMIT 0," + Utilities.MAX_POPUP_RESULTS);
        List tempList = this.findWhere(filterSQL.toString());
        _choiceCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
        return tempList;
    }


    /**
     * Prepair a string for search
     * @param s string to be converted
     * @return the new string
     */
    public String convertor(Object s) {
        if (null == s)
            return " IS NULL";
        else
            return " ='" + s + "'";
    }


    /**
     * Return the list of habitats witch apear in a reference selected by id_dc
     * @param id id_dc for that reference
     * @return a list of habitats witch apear in a reference selected by id_dc
     * @throws CriteriaMissingException searchCriteria is empty
     */

    public List getHabitatsByReferences(String id, boolean useLimit) throws CriteriaMissingException {
        if (searchCriteria.length < 1) throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();

        if (filterSQL.length() > 0) filterSQL.append(" AND ");
        filterSQL.append(" J.ID_DC = "+id);
        filterSQL.append(" GROUP BY C.SCIENTIFIC_NAME ");
        if (useLimit) filterSQL.append(" LIMIT 0," + Utilities.MAX_POPUP_RESULTS);
        List tempList = new HabitatsBooksDomain().findWhere(filterSQL.toString());
        List habitats = new ArrayList();
        for (int i = 0; i < tempList.size(); i++)
        {
            HabitatsBooksPersist habitat = (HabitatsBooksPersist) tempList.get(i);
            TableColumns tableColumns = new TableColumns();
            List habitatsAttributes = new ArrayList();
            habitatsAttributes.add(habitat.getScientificName());
            habitatsAttributes.add(habitat.getIdHabitat());
            tableColumns.setColumnsValues(habitatsAttributes);
            habitats.add(tableColumns);
        }
        return habitats;
    }

    /** This method is used to count the total list of results from a query. It is used to find all for use in pagination.
     * Having the total number of results and the results displayed per page, the you could find the number of pages i.e.
     * @return Number of results found
     */
    public Long countResults() throws CriteriaMissingException {
        if (-1 == _resultCount.longValue()) {
            _resultCount = _rawCount();
        }
        return _resultCount;
    }


    /** This method does the raw counting (meaning that will do a DB query for retrieving results count). You should check
     * in your code that this method is called (in ideal way) only once and results are cached. This is what
     * countResults() method does in this class

     * @return
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {
        StringBuffer sql = new StringBuffer();
        // Set the main QUERY
        // Apply WHERE CLAUSE
        sql.append(_prepareWhereSearch().toString());
        sql.append("GROUP BY J.SOURCE,J.EDITOR,J.CREATED,J.TITLE,J.PUBLISHER");
        // Apply SORT CLAUSE - DON'T NEED IT FOR COUNT...
        List l = findWhere(sql.toString());
        if (null != l)
            return new Long(l.size());
        else
            return new Long(0);
    }

    /** This helper method is used to construct the string after WHERE...based on search criterias set. In another words
     * constructs .....WHERE...>> H.SCIENTIFIC_NAME = 'HABITATS MARINE' .....
     * @return SQL string
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
     */
    private StringBuffer _prepareWhereSearch() throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();
        if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");

        filterSQL.append(" ((J.SOURCE IS NOT NULL AND TRIM(J.SOURCE) <> '') OR ");
        filterSQL.append(" (J.EDITOR IS NOT NULL AND TRIM(J.EDITOR) <> '') OR ");
        filterSQL.append(" (J.CREATED IS NOT NULL AND TRIM(J.CREATED) <> '') OR ");
        filterSQL.append(" (J.TITLE IS NOT NULL AND TRIM(J.TITLE) <> '') OR ");
        filterSQL.append(" (J.PUBLISHER IS NOT NULL AND TRIM(J.PUBLISHER) <> '')) ");

        if (0 != SEARCH_BOTH.compareTo(searchPlace)) {
            if (0 == searchPlace.compareTo(HabitatsBooksDomain.SEARCH_EUNIS)) {
                filterSQL.append(" AND C.ID_HABITAT >=1 AND C.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS +"%' ");
            }
            if (0 == searchPlace.compareTo(HabitatsBooksDomain.SEARCH_ANNEX_I)) {
                filterSQL.append(" AND C.HABITAT_TYPE = '" + Constants.HABITAT_ANNEX1 +"' ");
            }
        } else
            filterSQL.append(" AND C.ID_HABITAT <>'-1' AND C.ID_HABITAT <> '" + Constants.HABITAT_ANNEX1_ROOT + "' ");

        for (int i = 0; i < searchCriteria.length; i++) {
            filterSQL.append(" AND ");
            ReferencesSearchCriteria aCriteria = (ReferencesSearchCriteria) searchCriteria[i]; // upcast
            filterSQL.append(aCriteria.toSQL());
        }
        filterSQL.append(" AND IF(TRIM(C.CODE_2000) <> '',RIGHT(C.CODE_2000,2),1) <> IF(TRIM(C.CODE_2000) <> '','00',2) AND IF(TRIM(C.CODE_2000) <> '',LENGTH(C.CODE_2000),1) = IF(TRIM(C.CODE_2000) <> '',4,1) ");
        return filterSQL;
    }


    /** Prepare the ORDER BY clause used to do the sorting. Basically it adds the ORDER clause with the criterias set in
     * the sortCriteria[] array.
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
            logger.debug(e, e);   //To change body of catch statement use Options | File Templates.
        } finally {
            return filterSQL;
        }
    }
}
