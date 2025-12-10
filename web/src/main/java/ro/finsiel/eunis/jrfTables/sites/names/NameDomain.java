package ro.finsiel.eunis.jrfTables.sites.names;

import java.util.List;
import java.util.Vector;

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.*;
import ro.finsiel.eunis.search.sites.names.NameSearchCriteria;

/**
 * SELECT * FROM chm62edt_sites AS A INNER JOIN chm62edt_nature_object_geoscope AS B ON A.ID_NATURE_OBJECT=B.ID_NATURE_OBJECT INNER
 * JOIN chm62edt_country AS C ON B.ID_GEOSCOPE = C.ID_GEOSCOPE WHERE A.NAME="%Moor" AND C.AREA_NAME_EN='FRANCE' AND
 * LEFT(A.DESIGNATION_DATE,4)>=1997 AND LEFT(A.DESIGNATION_DATE,4)<=2000 AND ((A.SOURCE_DB = 'Nationally Designated Areas') OR
 * (A.SOURCE_DB='International Designated Areas') OR (A.SOURCE_DB = 'EMERALD') OR (A.SOURCE_DB='CORINE Biotopes') OR
 * (A.SOURCE_DB='Monitoring Sites'))
 */
public class NameDomain extends AbstractDomain implements Paginable {
    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted
    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    private String user = null;
    private boolean fuzzySearch = false;
    private String sitesName = "";

    private static final Logger logger = Logger.getLogger(NameDomain.class);

    private SourceDb sourceDb = SourceDb.noDatabase();

    public NameDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, String user, SourceDb sourceDb) {
        this(searchCriteria, sortCriteria, user, sourceDb, false);
    }

    public NameDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, String user, SourceDb sourceDb,
            boolean fuzzySearch) {
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        this.user = user;
        this.sourceDb = sourceDb;
        this.fuzzySearch = fuzzySearch;
    }

    /**
   **/
    public PersistentObject newPersistentObject() {
        return new NamePersist();
    }

    /**
   **/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_sites");
        this.setReadOnly(true);
        this.setTableAlias("A");

        this.addColumnSpec(new StringColumnSpec("ID_SITE", "getIdSite", "setIdSite", DEFAULT_TO_EMPTY_STRING, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("ID_GEOSCOPE", "getIdGeoscope", "setIdGeoscope", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("ID_DESIGNATION", "getIdDesignation", "setIdDesignation", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("RESPONDENT", "getRespondent", "setRespondent", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("NAME", "getName", "setName", DEFAULT_TO_EMPTY_STRING, REQUIRED));
        this.addColumnSpec(new StringColumnSpec("MANAGER", "getManager", "setManager", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("COMPLEX_NAME", "getComplexName", "setComplexName", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("DISTRICT_NAME", "getDistrictName", "setDistrictName", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("OWNERSHIP", "getOwnership", "setOwnership", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("HISTORY", "getHistory", "setHistory", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CHARACTER", "getCharacter", "setCharacter", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("DESCRIPTION", "getDescription", "setDescription", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("MANAGEMENT_PLAN", "getManagementPlan", "setManagementPlan", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("IUCNAT", "getIucnat", "setIucnat", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("DESIGNATION_DATE", "getDesignationDate", "setDesignationDate", null));
        this.addColumnSpec(new StringColumnSpec("COMPILATION_DATE", "getCompilationDate", "setCompilationDate", null));
        this.addColumnSpec(new StringColumnSpec("PROPOSED_DATE", "getProposedDate", "setProposedDate", null));
        this.addColumnSpec(new StringColumnSpec("CONFIRMED_DATE", "getConfirmedDate", "setConfirmedDate", null));
        this.addColumnSpec(new StringColumnSpec("UPDATE_DATE", "getUpdateDate", "setUpdateDate", null));
        this.addColumnSpec(new StringColumnSpec("SPA_DATE", "getSpaDate", "setSpaDate", null));
        this.addColumnSpec(new StringColumnSpec("SAC_DATE", "getSacDate", "setSacDate", null));
        this.addColumnSpec(new StringColumnSpec("NATIONAL_CODE", "getNationalCode", "setNationalCode", null));
        this.addColumnSpec(new StringColumnSpec("NATURA_2000", "getNatura2000", "setNatura2000", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("NUTS", "getNuts", "setNuts", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("AREA", "getArea", "setArea", null));
        this.addColumnSpec(new StringColumnSpec("LENGTH", "getLength", "setLength", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("ALT_MEAN", "getAltMean", "setAltMean", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("ALT_MAX", "getAltMax", "setAltMax", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("ALT_MIN", "getAltMin", "setAltMin", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("LONGITUDE", "getLongitude", "setLongitude", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("LATITUDE", "getLatitude", "setLatitude", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("SOURCE_DB", "getSourceDB", "setSourceDB", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("MATCH_RELEVANCE", "getMatchRelevance", "setMatchRelevance", DEFAULT_TO_NULL));

        OuterJoinTable natureObjectGeoscope =
                new OuterJoinTable("chm62edt_nature_object_geoscope B ", "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");
        this.addJoinTable(natureObjectGeoscope);

        OuterJoinTable country = new OuterJoinTable("chm62edt_country C", "ID_GEOSCOPE", "ID_GEOSCOPE");
        country.addJoinColumn(new StringJoinColumn("AREA_NAME_EN", "setAreaNameEn"));
        country.addJoinColumn(new StringJoinColumn("ISO_2L", "setIso2L"));
        natureObjectGeoscope.addJoinTable(country);
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
     */
    public List getResults(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria) throws CriteriaMissingException {
        List tempList = null;
        this.sortCriteria = sortCriteria;
        if (searchCriteria.length < 1)
            throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
        try {
            // Prepare the WHERE clause
            StringBuffer filterSQL = _prepareWhereSearch();
            // Add GROUP BY clause for unique results
            // Add the ORDER BY clause to do the sorting
            filterSQL.append(" GROUP BY A.ID_NATURE_OBJECT ");
            if (sortCriteria.length > 0) {
                filterSQL.append(_prepareWhereSort());
            }

            // Add the LIMIT clause to return only the wanted results
            if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
                filterSQL.append(" LIMIT " + offsetStart + ", " + pageSize);
            }

            String query = this.getSelectSQLBuilder().buildSQL();
            if (fuzzySearch) {
                query = query.replace("A.MATCH_RELEVANCE, ", getRelevanceColumn().toString());
            } else {
                //The A.MATCH_RELEVANCE field was generated by JRF for query manipulations although it is not actually in DB
                query = query.replace("A.MATCH_RELEVANCE, ", "0 AS MATCH_RELEVANCE, ");
            }
            query += " WHERE " + filterSQL.toString();
            tempList = this.findCustom(query);

            _resultCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
            return tempList;
        } catch (Exception _ex) {
            _ex.printStackTrace();
            return new Vector();
        }
    }

    /**
     * This method is used to count the total list of results from a query. It is used to find all for use in pagination. Having the
     * total number of results and the results displayed per page, the you could find the number of pages i.e.
     * 
     * @return The total number of results
     */
    public Long countResults() throws CriteriaMissingException {
        if (-1 == _resultCount.longValue()) {
            _resultCount = _rawCount();
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
     */
    private Long _rawCount() {
        Long ret = new Long(0);
        try {
            StringBuffer sql = new StringBuffer();
            // Set the main QUERY
            sql.append("SELECT COUNT(DISTINCT A.ID_NATURE_OBJECT) FROM chm62edt_sites A "
                    + " LEFT JOIN chm62edt_nature_object_geoscope B ON A.ID_NATURE_OBJECT=B.ID_NATURE_OBJECT "
                    + " LEFT JOIN chm62edt_country C ON B.ID_GEOSCOPE = C.ID_GEOSCOPE WHERE");
            // Apply WHERE CLAUSE
            sql.append(_prepareWhereSearch().toString());
            ret = findLong(sql.toString());
            if (null == ret)
                return new Long(0);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
        return ret;
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

        for (int i = 0; i < searchCriteria.length; i++) {
            if (i > 0) {
                filterSQL.append(" AND ");
            }
            AbstractSearchCriteria aCriteria = searchCriteria[i]; // upcast
            filterSQL.append(aCriteria.toSQL(fuzzySearch));
        }
        filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "A");
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
        } finally {
            return filterSQL;
        }
    }
    
    
    /**
     * Generates a special column MATCH_RELEVANCE to be added to the customized original JFR query
     * in order to calculate the result rows A.NAME fields similarity to search word 
     * 
     * @return a special column 
     */
    private StringBuffer getRelevanceColumn() {
        StringBuffer column = new StringBuffer();
        int levenshteinBase = 10;
        
        // preparing exact match
        column.append("IF(LOWER(A.NAME) ");
        
        for (int i = 0; i < searchCriteria.length; i++) {
            if (i > 0) {
                column.append(" OR ");
            }
            NameSearchCriteria aCriteria = (NameSearchCriteria) searchCriteria[i];
            column.append(" LIKE '").append(aCriteria.getEnglishName().toLowerCase()).append("' ");
        }
        // Adding exact match relevance score
        column.append(",1,");
        

        // preparing starting with match
        column.append("IF(LOWER(A.NAME) ");
        for (int i = 0; i < searchCriteria.length; i++) {
            if (i > 0) {
                column.append(" OR ");
            }
            NameSearchCriteria aCriteria = (NameSearchCriteria) searchCriteria[i];
            column.append(" LIKE '").append(aCriteria.getEnglishName().toLowerCase()).append("%' ");
        }
        // Adding starting with match relevance score
        column.append(",2,");


        // preparing includes keyword match
        column.append("IF(LOWER(A.NAME) ");
        for (int i = 0; i < searchCriteria.length; i++) {
            if (i > 0) {
                column.append(" OR ");
            }
            NameSearchCriteria aCriteria = (NameSearchCriteria) searchCriteria[i];
            column.append(" LIKE '%").append(aCriteria.getEnglishName().toLowerCase()).append("%' ");
        }
        // Adding includes keyword relevance score
        column.append(",3,");
        

        // If the keyword does not match the previous conditions, relevance is the levenshtein value + basevalue
        column.append("LEAST(1000");
        for (int i = 0; i < searchCriteria.length; i++) {
            NameSearchCriteria aCriteria = (NameSearchCriteria) searchCriteria[i];
            int substringLength = 3;
            String englishName = aCriteria.getEnglishName();
            if (fuzzySearch && englishName.length() >=substringLength) {
                String subSearchString = englishName.substring(0, substringLength).toLowerCase();
                column.append(", levenshtein(LOWER(A.NAME),  '").append(subSearchString).append("') + ").append(levenshteinBase);
            }
        }
        column.append(")");
        
        // Closing "includes keyword" relevance IF clause
        column.append(" )");
        
        // Closing "starting with" relevance IF clause
        column.append(" )");
        
        // Closing "exact match" relevance IF clause
        column.append(" )");
        
        
        column.append(" AS MATCH_RELEVANCE, ");

        return column;
    }

}