package ro.finsiel.eunis.jrfTables.species.country;


import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.*;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.species.country.CountrySearchCriteria;
import ro.finsiel.eunis.search.species.country.CountrySortCriteria;

import java.util.List;


/**
 * @author finsiel
 * @since 20.01.2003
 * @version 1.0
 **/
public class CountryRegionDomain extends AbstractDomain implements Paginable {

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set

    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);

    boolean showEUNISInvalidatedSpecies = false;

    private static final Logger logger = Logger.getLogger(CountryRegionDomain.class);

    /** Custom constructor, to make life easier
     * @param searchCriteria Search criteria used to do the search in this Domain.
     */
    public CountryRegionDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, boolean showEUNISInvalidatedSpecies) {
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
    }

    /****/
    public PersistentObject newPersistentObject() {
        return new CountryRegionPersist();
    }

     /****/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_reports");
        this.setTableAlias("B");

        // Faster performance
        setReadOnly(true);

        this.addColumnSpec(
                new CompoundPrimaryKeyColumnSpec(
                        new IntegerColumnSpec("ID_NATURE_OBJECT",
                        "getIdNatureObject", "setIdNatureObject",
                        DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY),
                        new IntegerColumnSpec("ID_DC", "getIdDc", "setIdDc",
                        DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY),
                        new IntegerColumnSpec("ID_GEOSCOPE", "getIdGeoscope",
                        "setIdGeoscope", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY),
                        new IntegerColumnSpec("ID_GEOSCOPE_LINK",
                        "getIdGeoscopeLink", "setIdGeoscopeLink",
                        DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY),
                        new IntegerColumnSpec("ID_REPORT_TYPE",
                        "getIdReportType", "setIdReportType", DEFAULT_TO_ZERO,
                        NATURAL_PRIMARY_KEY),
                        new IntegerColumnSpec("ID_REPORT_ATTRIBUTES",
                        "getIdReportAttributes", "setIdReportAttributes",
                        DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY)));

        JoinTable Species = null;
        OuterJoinTable GroupSpecies = null;
        OuterJoinTable TaxcodeOrder = null;
        OuterJoinTable TaxcodeFamily = null;

        Species = new JoinTable("chm62edt_species C", "ID_NATURE_OBJECT",
                "ID_NATURE_OBJECT");
        Species.addJoinColumn(
                new StringJoinColumn("SCIENTIFIC_NAME", "ScientificName",
                "setScientificName"));
        Species.addJoinColumn(
                new IntegerJoinColumn("ID_SPECIES", "IdSpecies", "setIdSpecies"));
        Species.addJoinColumn(
                new IntegerJoinColumn("ID_SPECIES_LINK", "IdSpeciesLink",
                "setIdSpeciesLink"));
        this.addJoinTable(Species);

        GroupSpecies = new OuterJoinTable("chm62edt_group_species D",
                "ID_GROUP_SPECIES", "ID_GROUP_SPECIES");
        GroupSpecies.addJoinColumn(
                new StringJoinColumn("COMMON_NAME", "CommonName",
                "setCommonName"));
        Species.addJoinTable(GroupSpecies);

        TaxcodeFamily = new OuterJoinTable("chm62edt_taxonomy E", "ID_TAXONOMY",
                "ID_TAXONOMY");
        TaxcodeFamily.addJoinColumn(
                new StringJoinColumn("NAME", "taxonomyName", "setTaxonomyName"));
        TaxcodeFamily.addJoinColumn(
                new StringJoinColumn("LEVEL", "taxonomyLevel",
                "setTaxonomyLevel"));
        TaxcodeFamily.addJoinColumn(
                new StringJoinColumn("TAXONOMY_TREE", "taxonomyTree",
                "setTaxonomyTree"));
        TaxcodeFamily.addJoinColumn(
                new StringJoinColumn("NAME", "taxonomicNameFamily",
                "setTaxonomicNameFamily"));
        TaxcodeFamily.addJoinColumn(
                new StringJoinColumn("NAME", "taxonomicNameOrder",
                "setTaxonomicNameOrder"));
        Species.addJoinTable(TaxcodeFamily);

        // TaxcodeOrder = new OuterJoinTable("chm62edt_taxonomy F", "ID_TAXONOMY_LINK", "ID_TAXONOMY");
        // TaxcodeOrder.addJoinColumn(new StringJoinColumn("NAME", "TaxonomicNameOrder", "setTaxonomicNameOrder"));
        // TaxcodeFamily.addJoinTable(TaxcodeOrder);
    }

     /** This method is used to retrieve a sub-set of the main results of a query given its start index offset and end
     * index offset. You shouldn't use this method until you set up a search criteria with setSearchCriteria(...)
     * NOTE: MySQL LIMIT takes to parameters: LIMIT @arg1, @arg2.<br />
     * arg1 represents the offset<br />
     * arg2 represents the number of rows returned.<br />
     * @param offsetStart The start offset (i.e. 0). If offsetStart = offSetEnd then return the whole list
     * @param pageSize The end offset (i.e. 1)  If offsetStart = offSetEnd then return the whole list
     * @return A list of objects which match query criteria. If the search criteria was null, return an empty list.
     */
    public List getResults(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria) throws CriteriaMissingException {
        this.sortCriteria = sortCriteria;
        if (searchCriteria.length < 1) {
            throw new CriteriaMissingException(
                    "I refuse to search, since you didn't say what.");
        }
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();

        // Add GROUP BY clause for unique results
        filterSQL.append(" GROUP BY C.ID_NATURE_OBJECT");
        // Add the ORDER BY clause to do the sorting
        if (sortCriteria.length > 0) {
            filterSQL.append(_prepareWhereSort());
        }
        // Add the LIMIT clause to return only the wanted results
        if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
            filterSQL.append(" LIMIT " + offsetStart + ", " + pageSize);
        }
        List tempList = this.findWhere(filterSQL.toString());

        _resultCount = new Long(-1); // After each query, reset the _resultCount, so countResults do correct numbering.
        return tempList;
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
     * @see ro.finsiel.eunis.jrfTables.species.country.RegionDomain#countResults
     * @return count of the total list of results from a query as Long
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {
        StringBuffer sql = new StringBuffer();

        // Set the main QUERY
        sql.append(
                "SELECT COUNT(DISTINCT C.ID_NATURE_OBJECT) FROM chm62edt_reports B "
                        + "INNER JOIN chm62edt_species C ON B.ID_NATURE_OBJECT=C.ID_NATURE_OBJECT "
                        + "LEFT JOIN chm62edt_group_species D ON C.ID_GROUP_SPECIES=D.ID_GROUP_SPECIES "
                        + "LEFT JOIN chm62edt_taxonomy E ON C.ID_TAXONOMY=E.ID_TAXONOMY "
                        + "LEFT JOIN chm62edt_taxonomy F ON E.ID_TAXONOMY_LINK=F.ID_TAXONOMY WHERE ");

        // Apply WHERE CLAUSE
        sql.append(_prepareWhereSearch().toString());
        // Apply SORT CLAUSE - DON'T NEED IT FOR COUNT...
        Long ret = findLong(sql.toString());

        if (null == ret) {
            return new Long(0);
        }
        return ret;
    }

    /** This helper method is used to construct the string after WHERE...based on search criterias set. In another words
     * constructs .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX OR B.ID_GEOSCOPE_LINK=YYY OR B.ID_GEOSCOPE_LINK=ZZZ .....
     * @return SQL string
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
     */
    private StringBuffer _prepareWhereSearch() throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();

        if (searchCriteria.length <= 0) {
            throw new CriteriaMissingException(
                    "No criteria set for searching. Search interrupted.");
        }
        for (int i = 0; i < searchCriteria.length; i++) {
            if (i > 0) {
                filterSQL.append(" AND ");
            }
            CountrySearchCriteria aCriteria = (CountrySearchCriteria) searchCriteria[i]; // upcast

            filterSQL.append(
                    aCriteria.toSQL());
        }
        filterSQL.append(
                Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME",
                showEUNISInvalidatedSpecies));
        // filterSQL.append(" AND E.LEVEL='FAMILY' AND F.LEVEL='ORDER' ");
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
            logger.debug(e, e);  // To change body of catch statement use Options | File Templates.
        } finally {
            return filterSQL;
        }
    }

    /** Method used for testing purposes.
     * @param args Command line arguments
     */
    public static final void main(String[] args) {}
}
