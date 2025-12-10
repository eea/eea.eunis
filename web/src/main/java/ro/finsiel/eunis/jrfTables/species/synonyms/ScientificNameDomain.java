package ro.finsiel.eunis.jrfTables.species.synonyms;


import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.species.synonyms.SynonymsSearchCriteria;
import ro.finsiel.eunis.search.species.synonyms.SynonymsSortCriteria;
import ro.finsiel.eunis.utilities.TableColumns;

import java.util.ArrayList;
import java.util.List;


public class ScientificNameDomain extends AbstractDomain implements Paginable {

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set

    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);

    private static final Logger logger = Logger.getLogger(ScientificNameDomain.class);

    /** user name*/
    private boolean showEUNISInvalidatedSpecies = false;

    /**
     * Normal constructor
     * @param searchCriteria The search criteria used to query the database
     * @param sortCriteria Sort criterias used for sorting the results
     */
    public ScientificNameDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, boolean showEUNISInvalidatedSpecies) {
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
    }

    public ScientificNameDomain(AbstractSearchCriteria[] searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /****/
    public PersistentObject newPersistentObject() {
        return new ScientificNamePersist();
    }

     /****/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_species");
        this.setReadOnly(true);
        this.setTableAlias("C");

        this.addColumnSpec(
                new IntegerColumnSpec("ID_SPECIES", "getIdSpecies",
                "setIdSpecies", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject",
                "setIdNatureObject", DEFAULT_TO_ZERO, REQUIRED));
        this.addColumnSpec(
                new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName",
                "setScientificName", DEFAULT_TO_EMPTY_STRING, REQUIRED));
        this.addColumnSpec(
                new IntegerColumnSpec("ID_SPECIES_LINK", "getIdSpeciesLink",
                "setIdSpeciesLink", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new IntegerColumnSpec("ID_GROUP_SPECIES", "getIdGroupspecies",
                "setIdGroupspecies", DEFAULT_TO_ZERO, REQUIRED));
        this.addColumnSpec(
                new StringColumnSpec("TYPE_RELATED_SPECIES",
                "getTypeRelatedSpecies", "setTypeRelatedSpecies",
                DEFAULT_TO_NULL));

        // Joined tables
        JoinTable gSpecies = null;

        gSpecies = new JoinTable("chm62edt_group_species E", "ID_GROUP_SPECIES",
                "ID_GROUP_SPECIES");
        gSpecies.addJoinColumn(
                new StringJoinColumn("COMMON_NAME", "grName", "setGrName"));
        this.addJoinTable(gSpecies);

        JoinTable Species2 = null;

        Species2 = new JoinTable("chm62edt_species D", "ID_SPECIES_LINK",
                "ID_SPECIES_LINK");
        Species2.addJoinColumn(
                new StringJoinColumn("SCIENTIFIC_NAME", "scName", "setScName"));
        Species2.addJoinColumn(
                new IntegerJoinColumn("ID_SPECIES", "IdSpec", "setIdSpec"));
        Species2.addJoinColumn(
                new IntegerJoinColumn("ID_SPECIES_LINK", "IdSpecLink",
                "setIdSpecLink"));
        Species2.addJoinColumn(
                new IntegerJoinColumn("ID_NATURE_OBJECT", "IdNatObj",
                "setIdNatObj"));
        this.addJoinTable(Species2);

        // OuterJoinTable taxCodeFamily = null;
        // taxCodeFamily = new OuterJoinTable("chm62edt_taxonomy F", "ID_TAXONOMY", "ID_TAXONOMY");
        // taxCodeFamily.addJoinColumn(new StringJoinColumn("NAME", "taxonomicNameFamily", "setTaxonomicNameFamily"));
        // taxCodeFamily.addJoinColumn(new StringJoinColumn("LEVEL", "taxonomicLevel", "setTaxonomicLevel"));
        // this.addJoinTable(taxCodeFamily);
        //
        // OuterJoinTable taxCodeOrder = null;
        // taxCodeOrder = new OuterJoinTable("chm62edt_taxonomy G", "ID_TAXONOMY_LINK", "ID_TAXONOMY");
        // taxCodeOrder.addJoinColumn(new StringJoinColumn("NAME", "taxonomicNameOrder", "setTaxonomicNameOrder"));
        // taxCodeFamily.addJoinTable(taxCodeOrder);

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
        if (searchCriteria.length < 1) {
            throw new CriteriaMissingException(
                    "I refuse to search, since you didn't say what.");
        }
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();

        // user validation condition
        // filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME", showEUNISInvalidatedSpecies));
        // filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND D.VALID_NAME", showEUNISInvalidatedSpecies));
        // filterSQL.append(" AND F.LEVEL='FAMILY' AND G.LEVEL='ORDER' ");

        filterSQL.append(" AND C.ID_SPECIES<>D.ID_SPECIES ");
        filterSQL.append(" GROUP BY D.ID_SPECIES ");
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

    public List geSpeciesListForASynonym(Integer idSpeciesSynonym) throws CriteriaMissingException {
        List species = new ArrayList();

        if (searchCriteria.length < 1) {
            throw new CriteriaMissingException(
                    "I refuse to search, since you didn't say what.");
        }
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();

        // user validation condition
        // filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME", showEUNISInvalidatedSpecies));
        // filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND D.VALID_NAME", showEUNISInvalidatedSpecies));
        // filterSQL.append(" AND F.LEVEL='FAMILY' AND G.LEVEL='ORDER' ");

        filterSQL.append(" AND D.ID_SPECIES = " + idSpeciesSynonym);
        filterSQL.append(" AND C.ID_SPECIES<>D.ID_SPECIES ");
        filterSQL.append(" GROUP BY C.ID_SPECIES ");
        filterSQL.append(" ORDER BY C.SCIENTIFIC_NAME ");

        List tempList = this.findWhere(filterSQL.toString());

        for (int i = 0; i < tempList.size(); i++) {
            ScientificNamePersist specie = (ScientificNamePersist) tempList.get(
                    i);
            TableColumns tableColumns = new TableColumns();
            List speciesAttributes = new ArrayList();

            speciesAttributes.add(specie.getScientificName());
            speciesAttributes.add(specie.getIdSpecies());
            speciesAttributes.add(specie.getIdSpeciesLink());
            tableColumns.setColumnsValues(speciesAttributes);
            species.add(tableColumns);
        }
        return species;
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

     * @return count of the total list of results from a query as Long
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {

        if (searchCriteria.length < 1) {
            throw new CriteriaMissingException(
                    "I refuse to search, since you didn't say what.");
        }
        // Prepare the WHERE clause
        StringBuffer filterSQL = _prepareWhereSearch();

        // user validation condition
        // filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME", showEUNISInvalidatedSpecies));
        // filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND D.VALID_NAME", showEUNISInvalidatedSpecies));
        // filterSQL.append(" AND F.LEVEL='FAMILY' AND G.LEVEL='ORDER' ");
        filterSQL.append(" AND C.ID_SPECIES<>D.ID_SPECIES ");
        filterSQL.append(" GROUP BY D.ID_SPECIES ");

        List tempList = this.findWhere(filterSQL.toString());

        if (null != tempList) {
            return new Long(tempList.size());
        } else {
            return new Long(0);
        }

    }

    /**
     * This helper method is used to construct the string after WHERE...based on search criterias set. In another words
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
            SynonymsSearchCriteria aCriteria = (SynonymsSearchCriteria) searchCriteria[i]; // upcast

            filterSQL.append(
                    aCriteria.toSQL());

            filterSQL.append(" AND C.TYPE_RELATED_SPECIES<>'Synonym'");
            filterSQL.append(" AND D.`TYPE_RELATED_SPECIES`='Synonym'");
            filterSQL.append(" AND D.ID_SPECIES<>D.ID_SPECIES_LINK");
        }

        return filterSQL;
    }

    /**
     * Prepare the ORDER BY clause used to do the sorting. Basically it adds the ORDER clause with the criterias set in
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
            logger.debug(e, e);
        } finally {
            return filterSQL;
        }
    }
}
