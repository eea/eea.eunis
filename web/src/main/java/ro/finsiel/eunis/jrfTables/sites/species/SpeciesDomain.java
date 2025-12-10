package ro.finsiel.eunis.jrfTables.sites.species;

/**
 * Date: May 19, 2003
 * Time: 3:11:52 PM
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.jrfTables.Chm62edtBiogeoregionDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtBiogeoregionPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtReportAttributesDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtReportAttributesPersist;
import ro.finsiel.eunis.jrfTables.GenericDomain;
import ro.finsiel.eunis.jrfTables.GenericPersist;
import ro.finsiel.eunis.search.*;
import ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria;
import ro.finsiel.eunis.search.species.SpeciesSearchUtility;
import ro.finsiel.eunis.utilities.TableColumns;

/**
 * Data factory for Species->Pick species, show sites type of search
 */
public class SpeciesDomain extends AbstractDomain implements Paginable {
    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    private boolean showEUNISInvalidatedSpecies = false;

    SourceDb sourceDb = SourceDb.noDatabase();

    private Integer searchAttribute = null;

    private static final Logger logger = Logger.getLogger(SpeciesDomain.class);

    /**
     * Normal constructor
     * @param searchCriteria The search criteria used to query the database
     * @param sortCriteria Sort criterias used for sorting the results
     */
    public SpeciesDomain(AbstractSearchCriteria[] searchCriteria,
            AbstractSortCriteria[] sortCriteria,
            boolean showEUNISInvalidatedSpecies,
            SourceDb source,
            Integer searchAttribute) {
        this.searchAttribute = searchAttribute;
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
        this.sourceDb = source;
    }

    public SpeciesDomain(AbstractSearchCriteria[] searchCriteria, boolean showEUNISInvalidatedSpecies, SourceDb source) {
        this.searchCriteria = searchCriteria;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
        this.sourceDb = source;
    }

    public SpeciesDomain() {
    }

    /****/
    public PersistentObject newPersistentObject() {
        return new SpeciesPersist();
    }

    /****/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_sites");
        this.setReadOnly(true);
        this.setTableAlias("A");
        this.addColumnSpec(new StringColumnSpec("ID_SITE", "getIdSite", "setIdSite", DEFAULT_TO_EMPTY_STRING, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(new StringColumnSpec("SOURCE_DB", "getSourceDB", "setSourceDB", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("ID_GEOSCOPE", "getIdGeoscope", "setIdGeoscope", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("ID_DESIGNATION", "getIdDesignation", "setIdDesignation", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("NAME", "getName", "setName", DEFAULT_TO_EMPTY_STRING, REQUIRED));
        this.addColumnSpec(new StringColumnSpec("LONGITUDE", "getLongitude", "setLongitude", DEFAULT_TO_NULL));
        this.addColumnSpec(new StringColumnSpec("LATITUDE", "getLatitude", "setLatitude", DEFAULT_TO_NULL));

        // FROM chm62edt_species
        this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getSpecieScientificName", "setSpecieScientificName", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("ID_SPECIES", "getIdSpecies", "setIdSpecies", DEFAULT_TO_NULL));
        this.addColumnSpec(new IntegerColumnSpec("ID_SPECIES_LINK", "getIdSpeciesLink", "setIdSpeciesLink", DEFAULT_TO_NULL));

    }

    /**
     * This method is used to retrieve a sub-set of the main results of a query given its start index offset and end
     * index offset.
     * @param offsetStart The start offset (i.e. 0). If offsetStart = offSetEnd then return the whole list
     * @param pageSize The end offset (i.e. 1). If offsetStart = offSetEnd then return the whole list
     * @param sortCriteria The criteria used for sorting
     * @return A list of objects which match query criteria
     */
    public List getResults(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria) throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();
        this.sortCriteria = sortCriteria;
        filterSQL = _prepareWhereSearch();
        String sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME," +
                " H.LONGITUDE, H.LATITUDE, C.SCIENTIFIC_NAME,C.ID_SPECIES,C.ID_SPECIES_LINK",
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
            //      System.out.println("sql = " + sql);
            results = findCustom(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            results = new Vector();
        } finally {
            _resultCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
            return results;
        }
    }


    /**
     * This method is used to count the total list of results from a query. It is used to find all for use in pagination.
     * Having the total number of results and the results displayed per page, the you could find the number of pages i.e.
     * @return Number of results found
     */
    public Long countResults() throws CriteriaMissingException {
        if (-1 == _resultCount.longValue()) {
            _resultCount = _rawCount();
            //      _resultCount = new Long(this.recCount());
        }
        return _resultCount;
    }


    /** This method does the raw counting (meaning that will do a DB query for retrieving results count). You should check
     * in your code that this method is called (in ideal way) only once and results are cached. This is what
     * countResults() method does in this class.
     * @return
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {
        Long results = new Long(-1);
        StringBuffer filterSQL = _prepareWhereSearch();
        String sql = prepareSQL("COUNT(DISTINCT H.ID_NATURE_OBJECT)", filterSQL.toString());
        try {
            results = findLong(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            results = new Long(-1);
        } finally {
            return results;
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
        filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME", showEUNISInvalidatedSpecies));
        if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
        for (int i = 0; i < searchCriteria.length; i++) {
            filterSQL.append(" AND ");
            SpeciesSearchCriteria aCriteria = (SpeciesSearchCriteria) searchCriteria[i]; // upcast
            filterSQL.append(aCriteria.toSQL());
        }
        filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "H");
        return filterSQL;
    }


    private StringBuffer _prepareWhereSearchForLOV() throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();
        filterSQL.append(Utilities.showEUNISInvalidatedSpecies("AND C.VALID_NAME", showEUNISInvalidatedSpecies));
        if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
        for (int i = 0; i < searchCriteria.length; i++) {
            filterSQL.append(" AND ");
            SpeciesSearchCriteria aCriteria = (SpeciesSearchCriteria) searchCriteria[i]; // upcast
            filterSQL.append(aCriteria.toSQL());
        }

        filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "H");
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
            logger.debug(e, e);   //To change body of catch statement use Options | File Templates.
        } finally {
            return filterSQL;
        }
    }


    /**
     * Finds species from the specified habitat
     * @param criteria New search criteria
     * @param showEUNISNonValidatedSpecies from SessionManager.getShowEUNISInvalidatedSpecies
     * @param idNatureObject ID_NATURE_OBJECT of the habitat to filter. null denotes all habitats will be considered.
     * @return A non-null list with species scientific names
     */
    public List findSpeciesFromSite(SpeciesSearchCriteria criteria,
            boolean showEUNISNonValidatedSpecies,
            Integer idNatureObject,
            Integer searchAttribute,
            SourceDb source) {
        if (null == criteria) {
            System.out.println("Warning:" + SpeciesDomain.class.getName() + "::findSpeciesFromSite(" + criteria + ", " + "...). One of criterias was null.");
            return new Vector();
        }
        this.showEUNISInvalidatedSpecies = showEUNISNonValidatedSpecies;
        this.searchCriteria = new AbstractSearchCriteria[1];
        this.searchCriteria[0] = criteria;
        this.searchAttribute = searchAttribute;
        this.sourceDb = source;
        StringBuffer filterSQL = new StringBuffer("");
        List results = new Vector();
        List species = new Vector();
        try {
            filterSQL = _prepareWhereSearch();
        } catch (CriteriaMissingException _ex) {
            _ex.printStackTrace();
        }

        if (null != idNatureObject) {
            filterSQL.append(" AND H.ID_NATURE_OBJECT='" + idNatureObject + "'");
        }

        String sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME, H.LONGITUDE, H.LATITUDE, " +
                "C.SCIENTIFIC_NAME,C.ID_SPECIES,C.ID_SPECIES_LINK",
                filterSQL + " GROUP BY C.ID_NATURE_OBJECT");
        try {
            results = new SpeciesDomain().findCustom(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
        for (int i = 0; i < results.size(); i++) {
            SpeciesPersist specie = (SpeciesPersist) results.get(i);
            TableColumns tableColumns = new TableColumns();
            List speciesAttributes = new ArrayList();
            speciesAttributes.add(specie.getSpecieScientificName());
            speciesAttributes.add(specie.getIdSpecies());
            speciesAttributes.add(specie.getIdSpeciesLink());
            tableColumns.setColumnsValues(speciesAttributes);
            species.add(tableColumns);
        }
        return species;
    }

    /**
     * Finds species from the specified habitat.
     * @param criteria New search criteria
     * @param showEUNISNonValidatedSpecies from SessionManager.getShowEUNISInvalidatedSpecies
     * @return A non-null list with species scientific names
     */
    public List findPopupLOV(SpeciesSearchCriteria criteria,
            boolean showEUNISNonValidatedSpecies,
            Integer searchAttribute,
            SourceDb source) {
        if (null == criteria) {
            System.out.println("Warning: " + SpeciesDomain.class.getName() + "::findPopupLOV(" + criteria + ", " + "...). One of criterias was null.");
            return new Vector();
        }
        this.showEUNISInvalidatedSpecies = showEUNISNonValidatedSpecies;
        this.searchCriteria = new AbstractSearchCriteria[1];
        this.searchCriteria[0] = criteria;
        this.searchAttribute = searchAttribute;
        this.sourceDb = source;
        StringBuffer filterSQL = new StringBuffer("");
        List results = new Vector();
        List species = new Vector();
        StringBuffer filter = new StringBuffer("");


        try {
            filterSQL = _prepareWhereSearchForLOV();
            //main criteria without species conditions and sites conditions
            for (int i = 0; i < searchCriteria.length; i++) {
                filter.append(" AND ");
                SpeciesSearchCriteria aCriteria = (SpeciesSearchCriteria) searchCriteria[i]; // upcast
                filter.append(aCriteria.toSQL());
            }

        } catch (CriteriaMissingException _ex) {
            _ex.printStackTrace();
        }
        String sql = "";
        // SCIENTIFIC NAME
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME.intValue()) {
            sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME," +
                    " H.LONGITUDE, H.LATITUDE, C.SCIENTIFIC_NAME,C.ID_SPECIES,C.ID_SPECIES_LINK",
                    filterSQL + " GROUP BY C.ID_NATURE_OBJECT");
            try {
                results = new SpeciesDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                SpeciesPersist specie = (SpeciesPersist) results.get(i);
                species.add(specie.getSpecieScientificName());
            }
        }
        // COMMON NAME
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_VERNACULAR.intValue()) {
            try {
                results = new Chm62edtReportAttributesDomain().findCustom("SELECT *" +
                        " FROM chm62edt_report_attributes AS F" +
                        " WHERE F.NAME = 'VERNACULAR_NAME' " + filter.toString() + " GROUP BY F.VALUE");
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                Chm62edtReportAttributesPersist attribute = (Chm62edtReportAttributesPersist) results.get(i);
                species.add(attribute.getValue());
            }

            /*
      sql = prepareSQL("F.VALUE AS COLUMN1", filterSQL.toString() + " GROUP BY F.VALUE");
      try
      {
        results = new GenericDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        GenericPersist specie = (GenericPersist)results.get(i);
        species.add(specie.getColumn1());
      }
             */
        }
        // GROUP
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_GROUP.intValue()) {
            //      System.out.println("finding groups...");

            species = SpeciesSearchUtility.findGroupCommonName(criteria.getSearchString(), criteria.getRelationOp());
        }
        // LEGAL INSTRUMENTS
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()) {
            sql = prepareSQL("CONCAT('Annex ', F.ANNEX,' - ',G.ALTERNATIVE) AS COLUMN1", filterSQL.toString() + " GROUP BY CONCAT('Annex ', F.ANNEX,' - ',G.ALTERNATIVE)");
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
            try {
                results = new Chm62edtCountryDomain().findCustom("SELECT *" +
                        " FROM chm62edt_country AS E" +
                        " WHERE  E.ISO_2L<>'' AND E.ISO_2L<>'null' AND E.ISO_2L IS NOT NULL AND E.SELECTION <> 0 "
                        + filter.toString() + " GROUP BY E.AREA_NAME_EN");
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                Chm62edtCountryPersist country = (Chm62edtCountryPersist) results.get(i);
                species.add(country.getAreaNameEnglish());
            }

            /*
      sql = prepareSQL("E.AREA_NAME_EN AS COLUMN1", filterSQL.toString() + " GROUP BY E.AREA_NAME_EN");
      //System.out.println("country="+sql);
      try
      {
        results = new GenericDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        GenericPersist specie = (GenericPersist)results.get(i);
        species.add(specie.getColumn1());
      }
             */
        }
        // REGION
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_REGION.intValue()) {
            try {
                results = new Chm62edtBiogeoregionDomain().findCustom("SELECT *" +
                        " FROM chm62edt_biogeoregion AS E" +
                        " WHERE  (1=1) "
                        + filter.toString() + " GROUP BY E.NAME");
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                Chm62edtBiogeoregionPersist region = (Chm62edtBiogeoregionPersist) results.get(i);
                species.add(region.getBiogeoregionName());
            }
            /*
      sql = prepareSQL("E.NAME AS COLUMN1", filterSQL.toString() + " GROUP BY E.NAME");
      try
      {
        results = new GenericDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        GenericPersist specie = (GenericPersist)results.get(i);
        species.add(specie.getColumn1());
      }
             */
        }
        return species;
    }

    /**
     * Prepare the SQL for the search.
     * @param what The SELECT condition (ex. SELECT C.SCIENTIFIC_NAME,E.VALUE...)
     * @param whereCondition the WHERE condition for SQL.
     * @return The full SQL for the search.
     */
    private String prepareSQL(String what, String whereCondition) {
        String sql = "";
        // If we search on species scientific name or species group as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME.intValue() ||
                searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_GROUP.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_species AS C " +
            " INNER JOIN chm62edt_nature_object_report_type AS G ON C.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT_LINK " +
            " INNER JOIN chm62edt_sites AS H ON G.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;
        }
        // If we search on species common name as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_VERNACULAR.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_species AS C " +
            " INNER JOIN chm62edt_reports AS D ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_report_attributes AS F ON (D.ID_REPORT_ATTRIBUTES = F.ID_REPORT_ATTRIBUTES AND F.NAME='VERNACULAR_NAME')" +
            " INNER JOIN chm62edt_nature_object_report_type AS G ON C.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT_LINK " +
            " INNER JOIN chm62edt_sites AS H ON G.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
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
            " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
            " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;

        }
        // If we search on species country as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_COUNTRY.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_country AS E " +
            " INNER JOIN chm62edt_reports AS D ON E.ID_GEOSCOPE = D.ID_GEOSCOPE" +
            " INNER JOIN chm62edt_species AS C ON D.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
            " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;
        }
        // If we search on species country as main criteria
        if (searchAttribute.intValue() == SpeciesSearchCriteria.SEARCH_REGION.intValue()) {
            sql = "SELECT " + what +
            " FROM chm62edt_biogeoregion AS E " +
            " INNER JOIN chm62edt_reports AS D ON D.ID_GEOSCOPE_LINK = E.ID_GEOSCOPE" +
            " INNER JOIN chm62edt_species AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
            " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
            " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
            " WHERE (1 = 1) " + whereCondition;
        }
        return sql;
    }
}