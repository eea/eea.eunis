package ro.finsiel.eunis.jrfTables.habitats.species;

/**
 * Date: Apr 24, 2003
 * Time: 9:37:34 AM
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eionet.eunis.util.Constants;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.jrfTables.GenericDomain;
import ro.finsiel.eunis.jrfTables.GenericPersist;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria;
import ro.finsiel.eunis.search.species.SpeciesSearchUtility;
import ro.finsiel.eunis.utilities.TableColumns;

public class ScientificNameDomain extends AbstractDomain implements Paginable {
    public static final Integer SEARCH_EUNIS = new Integer(0);
    public static final Integer SEARCH_ANNEX_I = new Integer(1);
    public static final Integer SEARCH_BOTH = new Integer(2);

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted
    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    /** Specifies where to search: SEARCH_EUNIS or SEARCH_ANNEX_I */
    private Integer database = SEARCH_EUNIS;
    private Integer searchAttribute = null;
    private boolean showInvalidatedSpecies = false;

    /**
     *
     * @param searchCriteria
     * @param sortCriteria
     * @param database
     * @param showInvalidatedSpecies
     * @param searchAttribute Type of search. Values are from ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_XXX<br />
     * Depending on this value, the query is adjusted accordingly.
     */
    public ScientificNameDomain(AbstractSearchCriteria[] searchCriteria,
            AbstractSortCriteria[] sortCriteria,
            Integer database,
            boolean showInvalidatedSpecies,
            Integer searchAttribute) {
        this.searchAttribute = searchAttribute;
        this.searchCriteria = searchCriteria;
        this.database = database;
        this.sortCriteria = sortCriteria;
        this.showInvalidatedSpecies = showInvalidatedSpecies;
    }

    public ScientificNameDomain() {
    }

    /**
     **/
    public PersistentObject newPersistentObject() {
        return new ScientificNamePersist();
    }

    /****/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_habitat");
        this.setReadOnly(true);
        this.setTableAlias("A");

        this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_ZERO, REQUIRED));
        this.addColumnSpec(new StringColumnSpec("ID_HABITAT", "getIdHabitat", "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName", "setScientificName", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("DESCRIPTION", "getDescription", "setDescription", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("CODE_ANNEX1", "getCodeAnnex1", "setCodeAnnex1", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode", "setEunisHabitatCode", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("HABITAT_TYPE", "getHabitatType", "setHabitatType", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("LEVEL", "getHabLevel", "setHabLevel", DEFAULT_TO_NULL));
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
        StringBuffer filterSQL = _prepareWhereSearch();
        String sql = prepareSQL(" H.ID_NATURE_OBJECT, H.ID_HABITAT, H.SCIENTIFIC_NAME, " +
                "H.DESCRIPTION, H.CODE_2000, " +
                "H.CODE_ANNEX1, H.EUNIS_HABITAT_CODE, H.LEVEL",
                filterSQL + " GROUP BY H.ID_NATURE_OBJECT");
        // Add the ORDER BY clause to do the sorting
        if (sortCriteria.length > 0) {
            sql += _prepareWhereSort();
        }
        // Add the LIMIT clause to return only the wanted results
        if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
            sql += " LIMIT " + offsetStart + ", " + pageSize;
        }
        List results = new Vector();
        try {
            results = findCustom(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            results = new Vector();
        } finally {
            _resultCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
            return results;
        }
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
     * @return
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {
        Long results = new Long(-1);
        StringBuffer filterSQL = _prepareWhereSearch();
        String sql = prepareSQL("COUNT(DISTINCT H.ID_NATURE_OBJECT)", filterSQL.toString());
        try {
            results = new HabitatsDomain().findLong(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            results = new Long(-1);
        } finally {
            return results;
        }
    }

    /** This helper method is used to construct the string after WHERE...based on search criterias set. In another words
     * constructs .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX OR B.ID_GEOSCOPE_LINK=YYY OR B.ID_GEOSCOPE_LINK=ZZZ .....
     * @return SQL string
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
     */
    private StringBuffer _prepareWhereSearch() throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer(" AND ");
        if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
        if (0 != database.compareTo(ScientificNameDomain.SEARCH_BOTH)) {
            if (0 == database.compareTo(ScientificNameDomain.SEARCH_EUNIS)) {
                filterSQL.append(" H.ID_HABITAT >= 1 and H.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'  ");
            }
            if (0 == database.compareTo(ScientificNameDomain.SEARCH_ANNEX_I)) {
                filterSQL.append(" H.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  ");
            }
        } else {
            filterSQL.append("  H.ID_HABITAT <>'-1' AND H.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  ");
        }
        for (int i = 0; i < searchCriteria.length; i++) {
            SpeciesSearchCriteria aCriteria = (SpeciesSearchCriteria) searchCriteria[i];
            filterSQL.append(" AND ");
            filterSQL.append(aCriteria.toSQL());
        }
        filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME", showInvalidatedSpecies));
        filterSQL.append(" AND IF(TRIM(H.CODE_2000) <> '',RIGHT(H.CODE_2000,2),1) <> IF(TRIM(H.CODE_2000) <> '','00',2) AND IF(TRIM(H.CODE_2000) <> '',LENGTH(H.CODE_2000),1) = IF(TRIM(H.CODE_2000) <> '',4,1) ");
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
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } finally {
            return filterSQL;
        }
    }

    /**
     * Finds species from the specified habitat
     * @param criteria New search criteria
     * @param database database (one of SEARCH_**** declared here)
     * @param showEUNISNonValidatedSpecies from SessionManager.getShowEUNISInvalidatedSpecies
     * @param idNatureObject ID_NATURE_OBJECT of the habitat to filter. null denotes all habitats will be considered.
     * @return A non-null list with species scientific names
     */
    public List findSpeciesFromHabitat(SpeciesSearchCriteria criteria,
            Integer database,
            boolean showEUNISNonValidatedSpecies,
            Integer idNatureObject,
            Integer searchAttribute) {
        if (null == criteria || null == database) {
            System.out.println("Warning: " + ScientificNameDomain.class.getName() + "::findSpeciesFromHabitat(" + criteria + ", " + database + "...). One of criterias was null.");
            return new Vector();
        }
        this.database = database;
        this.showInvalidatedSpecies = showEUNISNonValidatedSpecies;
        this.searchCriteria = new AbstractSearchCriteria[1];
        this.searchCriteria[0] = criteria;
        this.searchAttribute = searchAttribute;
        StringBuffer filterSQL = new StringBuffer("");
        List results = new ArrayList();
        List species = new ArrayList();
        try {
            filterSQL = _prepareWhereSearch();
        } catch (CriteriaMissingException _ex) {
            _ex.printStackTrace();
        }

        if (null != idNatureObject) {
            filterSQL.append(" AND H.ID_NATURE_OBJECT='" + idNatureObject + "'");
        }

        String sql = "";
        sql = prepareSQL("C.SCIENTIFIC_NAME, C.ID_SPECIES, C.ID_SPECIES_LINK", filterSQL.toString() + " GROUP BY C.ID_NATURE_OBJECT");
        try {
            results = new SpeciesDomain().findCustom(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
        for (int i = 0; i < results.size(); i++) {
            SpeciesPersist specie = (SpeciesPersist) results.get(i);
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

    /**
     * Finds species from the specified habitat
     * @param criteria New search criteria
     * @param database database (one of SEARCH_**** declared here)
     * @param showEUNISNonValidatedSpecies from SessionManager.getShowEUNISInvalidatedSpecies
     * @return A non-null list with species scientific names
     */
    public List findPopupLOV(SpeciesSearchCriteria criteria,
            Integer database,
            boolean showEUNISNonValidatedSpecies,
            Integer searchAttribute) {
        if (null == criteria || null == database) {
            System.out.println("Warning: InternationalThreatStatusDomain::findPopupLOV(" + criteria + ", " + database + "...). One of criterias was null.");
            return new Vector();
        }
        this.database = database;
        this.showInvalidatedSpecies = showEUNISNonValidatedSpecies;
        this.searchCriteria = new AbstractSearchCriteria[1];
        this.searchCriteria[0] = criteria;
        this.searchAttribute = searchAttribute;
        StringBuffer filterSQL = new StringBuffer("");
        List results = new Vector();
        List species = new Vector();
        try {
            filterSQL = _prepareWhereSearch();
        } catch (CriteriaMissingException _ex) {
            _ex.printStackTrace();
        }
        String sql = "";
        // SCIENTIFIC NAME
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME.intValue()) {
            sql = prepareSQL("C.SCIENTIFIC_NAME, C.ID_SPECIES, C.ID_SPECIES_LINK", filterSQL.toString() + " GROUP BY C.ID_NATURE_OBJECT LIMIT 0,200");
            try {
                results = new SpeciesDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                SpeciesPersist specie = (SpeciesPersist) results.get(i);
                species.add(specie.getScientificName());
            }
        }
        // COMMON NAME
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_VERNACULAR.intValue()) {
            sql = prepareSQL("F.VALUE AS COLUMN1", filterSQL.toString() + " GROUP BY F.VALUE LIMIT 0,200");
            try {
                results = new GenericDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                GenericPersist specie = (GenericPersist) results.get(i);
                species.add(specie.getColumn1());
            }
        }
        // GROUP
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_GROUP.intValue()) {
            species = SpeciesSearchUtility.findGroupCommonName(criteria.getscientificName(), criteria.getrelationOp());
        }
        // LEGAL INSTRUMENTS
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()) {
            sql = prepareSQL("CONCAT('Annex ', F.ANNEX,' - ',G.ALTERNATIVE) AS COLUMN1", filterSQL.toString() + " GROUP BY CONCAT('Annex ', F.ANNEX,' - ',G.ALTERNATIVE) LIMIT 0,200");
            try {
                results = new GenericDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                GenericPersist specie = (GenericPersist) results.get(i);
                species.add(specie.getColumn1());
            }
        }
        // COUNTRY
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_COUNTRY.intValue()) {
            sql = prepareSQL("E.AREA_NAME_EN AS COLUMN1", filterSQL.toString() + " GROUP BY E.AREA_NAME_EN LIMIT 0,200");
            try {
                results = new GenericDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                GenericPersist specie = (GenericPersist) results.get(i);
                species.add(specie.getColumn1());
            }
        }
        // REGION
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_REGION.intValue()) {
            sql = prepareSQL("E.NAME AS COLUMN1", filterSQL.toString() + " GROUP BY E.NAME LIMIT 0,200");
            try {
                results = new GenericDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                GenericPersist specie = (GenericPersist) results.get(i);
                species.add(specie.getColumn1());
            }
        }
        return species;
    }

    private String prepareSQL(String what, String whereCondition) {
        String sql = "";
        // If we search on species scientific name or species group as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME.intValue() ||
                searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_GROUP.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_habitat AS H " +
            " INNER JOIN chm62edt_nature_object_report_type AS G ON H.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT_LINK " +
            " INNER JOIN chm62edt_species AS C ON G.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;
        }
        // If we search on species common name as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_VERNACULAR.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_species AS C " +
            " INNER JOIN chm62edt_reports AS D ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_report_attributes AS F ON (D.ID_REPORT_ATTRIBUTES = F.ID_REPORT_ATTRIBUTES AND F.NAME='VERNACULAR_NAME')" +
            " INNER JOIN chm62edt_nature_object_report_type AS G ON C.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_habitat AS H ON G.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;

        }
        // If we search on species legal instruments as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_legal_status AS F " +
            " INNER JOIN chm62edt_report_type AS E ON (F.ID_LEGAL_STATUS = E.ID_LOOKUP AND E.LOOKUP_TYPE='LEGAL_STATUS') " +
            " INNER JOIN chm62edt_reports AS D ON E.ID_REPORT_TYPE = D.ID_REPORT_TYPE " +
            " INNER JOIN chm62edt_species AS C ON D.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT " +
            " INNER JOIN dc_index AS G ON D.ID_DC = G.ID_DC " +
            " INNER JOIN chm62edt_nature_object_report_type AS I ON C.ID_NATURE_OBJECT = I.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_habitat AS H ON I.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;

        }
        // If we search on species country as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_COUNTRY.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_country AS E " +
            " INNER JOIN chm62edt_reports AS D ON E.ID_GEOSCOPE = D.ID_GEOSCOPE" +
            " INNER JOIN chm62edt_species AS C ON D.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_nature_object_report_type AS I ON C.ID_NATURE_OBJECT = I.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_habitat AS H ON I.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;
        }
        // If we search on species country as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_REGION.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_biogeoregion AS E " +
            " INNER JOIN chm62edt_reports AS D ON D.ID_GEOSCOPE_LINK = E.ID_GEOSCOPE" +
            " INNER JOIN chm62edt_species AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_nature_object_report_type AS I ON C.ID_NATURE_OBJECT = I.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_habitat AS H ON I.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;
        }
        return sql;
    }
}