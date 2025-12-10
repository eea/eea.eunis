package ro.finsiel.eunis.jrfTables.species.speciesByReferences;


/**
 * Date: Aug 19, 2003
 * Time: 3:54:00 PM
 */


import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.sql.JRFResultSet;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.species.speciesByReferences.ReferencesSearchCriteria;
import ro.finsiel.eunis.search.species.speciesByReferences.ReferencesSortCriteria;
import ro.finsiel.eunis.search.species.speciesByReferences.SpeciesRefWrapper;


public class RefDomain extends AbstractDomain implements Paginable {

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set

    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    private boolean showInvalidatedSpecies = false;
    Vector ListOfIdSpecies = new Vector();

    private static final Logger logger = Logger.getLogger(RefDomain.class);

    public RefDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, boolean showInvalidatedSpecies) {
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        this.showInvalidatedSpecies = showInvalidatedSpecies;
    }
    
	@Override
    public PersistentObject newPersistentObject() {
	    // this is not actually used, see SpeciesRefWrapper instead 
        return new ro.finsiel.eunis.jrfTables.species.speciesByReferences.RefPersist();
    }    

	@Override
    public void setup() {
        // This is just a silly implementation to make JRF happy, since the entire results are handled directly 
        this.setTableName("DUMMY");
        this.setReadOnly(true);
        this.addColumnSpec(new IntegerColumnSpec("ID_SPECIES", "getIdSpecies", "setIdSpecies", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
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
            "Unable to search because no search criteria was specified...");
        }

        // add filter from results page
        StringBuffer filterSQL = _prepareWhereSearch("SpeciesPart");
        // Add the ORDER BY clause to do the sorting
        StringBuffer sortOrderAndLimit = new StringBuffer();

        if (sortCriteria.length > 0) {
            sortOrderAndLimit.append(_prepareWhereSort());
        }
        // Add the LIMIT clause to return only the wanted results
        if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
            sortOrderAndLimit.append(" LIMIT " + offsetStart + ", " + pageSize);
        }

        List tempList = getResultsByIdSpecies(filterSQL, sortOrderAndLimit, ListOfIdSpecies);

        _resultCount = new Long(-1); // After each query, reset the _resultCount, so countResults do correct numbering.
        return tempList;
    }

    private Vector getIdSpeciesList(StringBuffer SQLfilter) {
        final Vector results = new Vector();
        String SQL = "";

        String condition = (SQLfilter.length() > 0 ? " AND " + SQLfilter : "");

        SQL = "SELECT DISTINCT H.ID_SPECIES "
            + "FROM dc_index A "
            + "INNER JOIN chm62edt_nature_object B ON A.ID_DC=B.ID_DC "
            + "INNER JOIN chm62edt_species H ON B.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT "
            + "WHERE 1=1 " + condition + " UNION "
            + "SELECT DISTINCT H.ID_SPECIES " + "FROM dc_index A "
            + "INNER JOIN chm62edt_reports B ON A.ID_DC=B.ID_DC "
            + "INNER JOIN chm62edt_report_type K ON B.ID_REPORT_TYPE = K.ID_REPORT_TYPE "
            + "INNER JOIN chm62edt_species H ON B.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT "
            + "WHERE 1=1 " + condition
            + " AND K.LOOKUP_TYPE IN ('DISTRIBUTION_STATUS','LANGUAGE','CONSERVATION_STATUS','SPECIES_GEO','LEGAL_STATUS','SPECIES_STATUS','POPULATION_UNIT','TREND') "
            + "UNION "
            + "SELECT DISTINCT H.ID_SPECIES "
            + "FROM dc_index A "
            + "INNER JOIN chm62edt_taxonomy B ON A.ID_DC=B.ID_DC "
            + "INNER JOIN chm62edt_species H ON B.ID_TAXONOMY = H.ID_TAXONOMY "
            + "WHERE  1=1 " + condition;
        this.executeSQLQuery(SQL, new RowHandler() {
            public void handleRow(JRFResultSet rs) throws Exception {
                results.addElement(new Integer(rs.getint(1)));
            }
        });

        return results;
    }

    private Vector getResultsByIdSpecies(StringBuffer SQLfilter, StringBuffer sortOrderAndLimit, Vector idSpeciesList) {
        final Vector results = new Vector();
        String SQL = "";

        String condition = (SQLfilter.length() > 0 ? " AND " + SQLfilter : "");

        String conditionIn = "";

        if (idSpeciesList.size() > 0) {
            conditionIn += " AND H.ID_SPECIES IN (";
            for (int i = 0; i < idSpeciesList.size(); i++) {
                if (i < idSpeciesList.size() - 1) {
                    conditionIn += ((Integer) idSpeciesList.get(i)).toString()
                    + ",";
                } else {
                    conditionIn += ((Integer) idSpeciesList.get(i)).toString()
                    + ")";
                }
            }

            SQL = "SELECT H.ID_SPECIES,H.ID_SPECIES_LINK,H.ID_NATURE_OBJECT,H.SCIENTIFIC_NAME,A.COMMON_NAME,B.NAME,B.LEVEL,B.TAXONOMY_TREE "
                + "FROM chm62edt_species H "
                + "LEFT JOIN chm62edt_group_species A ON H.ID_GROUP_SPECIES=A.ID_GROUP_SPECIES "
                + "LEFT JOIN chm62edt_taxonomy B ON (H.ID_TAXONOMY = B.ID_TAXONOMY ) "
                + "WHERE 1=1 " + conditionIn + condition + sortOrderAndLimit;
            this.executeSQLQuery(SQL, new RowHandler() {
                public void handleRow(JRFResultSet rs) throws Exception {
                    results.addElement(
                            new SpeciesRefWrapper(new Integer(rs.getint(1)),
                                    new Integer(rs.getint(2)),
                                    new Integer(rs.getint(3)), 
                                    rs.getString(4),
                                    rs.getString(5),
                                    getTaxonomicName(rs.getString(7), rs.getString(6), rs.getString(8), "order_column"),
                                    getTaxonomicName(rs.getString(7), rs.getString(6), rs.getString(8), "family")
                                    )
                            );
                }
            });
        }
        return results;
    }

    private Long getCountResultsByIdSpecies(StringBuffer SQLfilter, Vector idSpeciesList) {
        Long results = new Long(0);
        // workaround to get the count from the inner RowHandler
        final List<Long> resultsList = new java.util.ArrayList<Long>();

        String condition = (SQLfilter.length() > 0 ? " AND " + SQLfilter : "");

        String conditionIn = "";

        if (idSpeciesList.size() > 0) {
            conditionIn += " AND H.ID_SPECIES IN (";
            for (int i = 0; i < idSpeciesList.size(); i++) {
                if (i < idSpeciesList.size() - 1) {
                    conditionIn += ((Integer) idSpeciesList.get(i)).toString()
                    + ",";
                } else {
                    conditionIn += ((Integer) idSpeciesList.get(i)).toString()
                    + ")";
                }
            }


            String SQL = "SELECT count(ID_SPECIES) " + "FROM chm62edt_species H "
            + "LEFT JOIN chm62edt_group_species A ON H.ID_GROUP_SPECIES=A.ID_GROUP_SPECIES "
            + "LEFT JOIN chm62edt_taxonomy B ON (H.ID_TAXONOMY = B.ID_TAXONOMY ) "
            + "WHERE 1=1 " + conditionIn + condition;
            this.executeSQLQuery(SQL, new RowHandler() {
                public void handleRow(JRFResultSet rs) throws Exception {
                    resultsList.add(new Long(rs.getint(1)));
                }
            });
            if ( resultsList.size() > 0 ) {
                results = resultsList.get(resultsList.size()-1);
            }
        }
        return results;
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
        Long result = new Long(0);

        if (searchCriteria.length < 1) {
            throw new CriteriaMissingException(
            "Unable to search because no search criteria was specified...");
        }

        StringBuffer filterSQL = _prepareWhereSearch("ReferencesPart");

        try {
            ListOfIdSpecies = getIdSpeciesList(filterSQL);
        } catch (Exception e) {
            logger.debug(e, e);
        }

        StringBuffer speciesFilterSQL = _prepareWhereSearch("SpeciesPart");

        try {
            result = getCountResultsByIdSpecies(speciesFilterSQL,
                    ListOfIdSpecies);
        } catch (Exception e) {
            logger.debug(e, e);
        }

        return result;
    }

    /** This helper method is used to construct the string after WHERE...based on search criterias set. In another words
     * constructs .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX OR B.ID_GEOSCOPE_LINK=YYY OR B.ID_GEOSCOPE_LINK=ZZZ .....
     * @return SQL string
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
     */
    private StringBuffer _prepareWhereSearch(String whatPart) throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();

        if (searchCriteria.length <= 0) {
            throw new CriteriaMissingException(
            "No criteria set for searching. Search interrupted.");
        }

        filterSQL.append(Utilities.showEUNISInvalidatedSpecies(" H.VALID_NAME", showInvalidatedSpecies));
        for (int i = 0; i < searchCriteria.length; i++) {
            ReferencesSearchCriteria aCriteria = (ReferencesSearchCriteria) searchCriteria[i]; // upcast

            if (aCriteria != null) {
                aCriteria.setPart(whatPart);
                if (aCriteria.toSQL().length() > 0 && filterSQL.length() > 0) {
                    filterSQL.append(" AND ");
                }
                filterSQL.append(aCriteria.toSQL());
            }
        }
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

    private String getTaxonomicName(String taxonomyLevel, String taxonomyName, String taxonomyTree, String what) {
        String level = taxonomyLevel;

        if (level != null && level.equalsIgnoreCase(what)) {
            return taxonomyName;
        } else {
            if ( taxonomyTree == null ) {
                return null;
            }
            String result = "";
            String str = taxonomyTree;

            StringTokenizer st = new StringTokenizer(str, ",");

            while (st.hasMoreTokens()) {
                StringTokenizer sts = new StringTokenizer(st.nextToken(), "*");
                String classification_id = sts.nextToken();
                String classification_level = sts.nextToken();
                String classification_name = sts.nextToken();

                if (classification_level != null
                        && classification_level.equalsIgnoreCase(what)) {
                    result = classification_name;
                    break;
                }
            }

            return result;
        }
    }
}
