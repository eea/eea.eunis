package ro.finsiel.eunis.jrfTables.species.sites;


/**
 * Date: May 12, 2003
 * Time: 10:15:47 AM
 */

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.*;
import ro.finsiel.eunis.search.sites.SitesSearchUtility;
import ro.finsiel.eunis.search.species.sites.SitesSearchCriteria;
import ro.finsiel.eunis.search.species.sites.SitesSortCriteria;
import ro.finsiel.eunis.jrfTables.*;

import java.util.List;
import java.util.Vector;


/**
 * @author finsiel
 * @version 1.3
 */
public class SpeciesSitesDomain extends AbstractDomain implements Paginable {

    private static final Logger logger = Logger.getLogger(SpeciesSitesDomain.class);

    /** Criterias applied for searching */
    private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set

    /** Criterias applied for sorting */
    private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

    /** Cache the results of a count to avoid overhead queries for counting */
    private Long _resultCount = new Long(-1);
    private boolean showEUNISInvalidatedSpecies = false;
    private Integer searchAttribute = null;
    private SourceDb sourceDb = SourceDb.noDatabase();

    /**
     * Default constructor.
     */
    public SpeciesSitesDomain() {}

    /**
     * Normal constructor.
     * @param searchCriteria Search criteria used for searching database.
     * @param sortCriteria Sort criteria used to sort the results.
     * @param showEUNISInvalidatedSpecies show/hide invalidated species.
     * @param searchAttribute What attribute of the site we are searching for (ex.: NAME/SIZE/LENGTH/COUNTRY etc.)
     * @param sourceDb What databases we are searching in.
     */
    public SpeciesSitesDomain(AbstractSearchCriteria[] searchCriteria,
            AbstractSortCriteria[] sortCriteria,
            boolean showEUNISInvalidatedSpecies,
            Integer searchAttribute,
            SourceDb sourceDb) {
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
        this.searchAttribute = searchAttribute;
        this.sourceDb = sourceDb;
    }

    /****/
    public PersistentObject newPersistentObject() {
        return new SpeciesSitesPersist();
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
        // FROM chm62edt_group_species
        this.addColumnSpec(
                new StringColumnSpec("COMMON_NAME", "getCommonName",
                "setCommonName", DEFAULT_TO_NULL));
        // FROM chm62edt_taxonomy
        this.addColumnSpec(
                new StringColumnSpec("NAME", "setTaxonomicNameFamily",
                "setTaxonomicNameFamily", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("NAME", "getTaxonomicNameOrder",
                "setTaxonomicNameOrder", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("NAME", "taxonomyName", "setTaxonomyName",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("LEVEL", "taxonomyLevel",
                "setTaxonomyLevel", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("TAXONOMY_TREE", "taxonomyTree",
                "setTaxonomyTree", DEFAULT_TO_NULL));

        // FROM chm62edt_sites
        this.addColumnSpec(
                new StringColumnSpec("NAME", "getSiteName", "setSiteName",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("SOURCE_DB", "getSourceDb", "setSourceDb",
                DEFAULT_TO_NULL));
    }

     /** This method is used to retrieve a sub-set of the main results of a query given its start index offset and end
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
        String sql = prepareSQL(
                "H.ID_SPECIES, H.ID_NATURE_OBJECT, H.SCIENTIFIC_NAME, H.ID_SPECIES_LINK, "
                        + "I.COMMON_NAME, J.NAME, J.NAME, J.NAME, J.LEVEL, J.TAXONOMY_TREE, J.NAME, C.SOURCE_DB", // To fill the persist correctly
                        filterSQL
                                + " GROUP BY H.ID_NATURE_OBJECT",
                                false);

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
            _resultCount = new Long(-1); // After each query, reset the _resultCount, so countResults do correct numbering.
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

     * @return count of the total list of results from a query as Long
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
     */
    private Long _rawCount() throws CriteriaMissingException {
        Long results = new Long(-1);
        StringBuffer filterSQL = _prepareWhereSearch();
        String sql = prepareSQL("COUNT(DISTINCT H.ID_NATURE_OBJECT)",
                filterSQL.toString(), false);

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

        if (searchCriteria.length <= 0) {
            throw new CriteriaMissingException(
                    "No criteria set for searching. Search interrupted.");
        }
        for (int i = 0; i < searchCriteria.length; i++) {
            filterSQL.append(" AND ");
            SitesSearchCriteria aCriteria = (SitesSearchCriteria) searchCriteria[i]; // upcast

            filterSQL.append(aCriteria.toSQL());
        }
        filterSQL.append(
                Utilities.showEUNISInvalidatedSpecies(" AND H.VALID_NAME",
                showEUNISInvalidatedSpecies));
        filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "C");
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
            logger.debug(e, e);  // To change body of catch statement use Options | File Templates.
        } finally {
            return filterSQL;
        }
    }

    /**
     * Finds habitats from the specified site
     * @param criteria New search criteria
     * @return A non-null list with habitats names
     */
    public List findSitesWithSpecies(SitesSearchCriteria criteria,
            SourceDb sourceDb,
            Integer searchAttribute,
            Integer idNatureObject,
            boolean showEUNISInvalidatedSpecies) {
        if (null == criteria) {
            return new Vector();
        }
        this.searchCriteria = new AbstractSearchCriteria[1];
        this.searchCriteria[0] = criteria;
        this.searchAttribute = searchAttribute;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
        this.sourceDb = sourceDb;
        StringBuffer filterSQL = new StringBuffer("");
        List results = new Vector();
        List sites = new Vector();

        try {
            filterSQL = _prepareWhereSearch();
        } catch (CriteriaMissingException _ex) {
            _ex.printStackTrace();
        }
        if (null != idNatureObject) {
            filterSQL.append(" AND H.ID_NATURE_OBJECT='" + idNatureObject + "'");
        }
        String sql = prepareSQL(
                "H.ID_SPECIES, H.ID_NATURE_OBJECT, H.SCIENTIFIC_NAME, H.ID_SPECIES_LINK, "
                        + "C.NAME, C.NAME, C.NAME, C.NAME, C.NAME, C.NAME, C.NAME,C.SOURCE_DB ",
                        filterSQL
                                + " GROUP BY C.ID_NATURE_OBJECT ORDER BY C.NAME",
                                true);

        try {
            results = new SpeciesSitesDomain().findCustom(sql);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
        for (int i = 0; i < results.size(); i++) {
            SpeciesSitesPersist site = (SpeciesSitesPersist) results.get(i);
            // sites.add(site.getSiteName() + " (" + SitesSearchUtility.translateSourceDB(site.getSourceDb()) + ")");
            List l = new Vector();

            l.add(site.getSiteName());
            l.add(SitesSearchUtility.translateSourceDB(site.getSourceDb()));
            sites.add(l);
        }
        return sites;
    }

    /**
     * Finds habitats from the specified site.
     * @param criteria New search criteria
     * @return A non-null list with species scientific names
     */
    public List findPopupLOV(SitesSearchCriteria criteria,
            SourceDb sourceDb,
            Integer searchAttribute,
            boolean showEUNISInvalidatedSpecies) {
        if (null == criteria) {
            System.out.println(
                    "Warning: " + SpeciesSitesDomain.class.getName()
                    + "::findPopupLOV(" + criteria + ", "
                    + "...). One of criterias was null.");
            return new Vector();
        }
        this.searchCriteria = new AbstractSearchCriteria[1];
        this.searchCriteria[0] = criteria;
        this.searchAttribute = searchAttribute;
        this.showEUNISInvalidatedSpecies = showEUNISInvalidatedSpecies;
        this.sourceDb = sourceDb;
        StringBuffer filterSQL = new StringBuffer("");
        List results = new Vector();
        List sites = new Vector();
        StringBuffer filter = new StringBuffer("");

        try {
            filterSQL = _prepareWhereSearch();
            // main criteria without species conditions and sites conditions
            for (int i = 0; i < searchCriteria.length; i++) {
                filter.append(" AND ");
                SitesSearchCriteria aCriteria = (SitesSearchCriteria) searchCriteria[i]; // upcast

                filter.append(
                        aCriteria.toSQL());
            }

        } catch (CriteriaMissingException _ex) {
            _ex.printStackTrace();
        }
        String sql = "";

        // SITE NAME
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_NAME.intValue()) {
            sql = prepareSQL(
                    "H.ID_SPECIES, H.ID_NATURE_OBJECT, H.SCIENTIFIC_NAME, H.ID_SPECIES_LINK, "
                            + "C.NAME, C.NAME, C.NAME, C.NAME, C.NAME, C.NAME, C.NAME,C.SOURCE_DB ",
                            filterSQL
                                    + " GROUP BY C.ID_NATURE_OBJECT ORDER BY C.NAME LIMIT 0,"
                                    + Utilities.MAX_POPUP_RESULTS,
                                    true);
            try {
                // System.out.println("SEARCH_NAME:sql = " + sql);
                results = new SpeciesSitesDomain().findCustom(sql);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                SpeciesSitesPersist site = (SpeciesSitesPersist) results.get(i);

                sites.add(site.getSiteName());
            }
        }
        // COUNTRY
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_COUNTRY.intValue()) {
            try {
                results = new Chm62edtCountryDomain().findCustom(
                        "SELECT *" + " FROM chm62edt_country AS E"
                        + " WHERE  E.ISO_2L<>'' AND E.ISO_2L<>'null' AND E.ISO_2L IS NOT NULL AND E.SELECTION <> 0 "
                        + filter.toString() + " GROUP BY E.AREA_NAME_EN");
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                Chm62edtCountryPersist country = (Chm62edtCountryPersist) results.get(
                        i);

                sites.add(country.getAreaNameEnglish());
            }

            /*
             sql = prepareSQL("E.AREA_NAME_EN AS COLUMN1", filterSQL.toString() + " GROUP BY E.AREA_NAME_EN",true);
             try
             {
             //System.out.println("SEARCH_COUNTRY:sql = " + sql);
             results = new GenericDomain().findCustom(sql);
             } catch (Exception _ex) {
             _ex.printStackTrace();
             }
             for (int i = 0; i < results.size(); i++) {
             GenericPersist habitat = (GenericPersist)results.get(i);
             sites.add(habitat.getColumn1());
             }
             */
        }
        // REGION
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_REGION.intValue()) {
            try {
                results = new Chm62edtBiogeoregionDomain().findCustom(
                        "SELECT *" + " FROM chm62edt_biogeoregion AS E"
                        + " WHERE  (1=1) " + filter.toString()
                        + " GROUP BY E.NAME");
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            for (int i = 0; i < results.size(); i++) {
                Chm62edtBiogeoregionPersist region = (Chm62edtBiogeoregionPersist) results.get(
                        i);

                sites.add(region.getBiogeoregionName());
            }

            /*
             sql = prepareSQL("E.NAME AS COLUMN1", filterSQL.toString() + " GROUP BY E.NAME",true);
             try
             {
             //System.out.println("SEARCH_REGION:sql = " + sql);
             results = new GenericDomain().findCustom(sql);
             } catch (Exception _ex) {
             _ex.printStackTrace();
             }
             for (int i = 0; i < results.size(); i++) {
             GenericPersist habitat = (GenericPersist)results.get(i);
             sites.add(habitat.getColumn1());
             }
             */
        }
        return sites;
    }

    /**
     * Prepare the SQL for the search.
     * @param what The SELECT condition (ex. SELECT C.SCIENTIFIC_NAME,E.VALUE...)
     * @param whereCondition the WHERE condition for SQL.
     * @param commingFromfindPopupLOV is true if thif function is call from findPopupLOV or findSitesWithSpecies functions (where is not need join with TAXONOMY and GROUP SPECIES)
     * @return The full SQL for the search.
     */
    private String prepareSQL(String what, String whereCondition, boolean commingFromfindPopupLOV) {
        String sql = "";
        String joinWithGroupSpeciesAndTaxonomy = "";

        if (!commingFromfindPopupLOV) {
            joinWithGroupSpeciesAndTaxonomy = " LEFT JOIN chm62edt_group_species AS I ON (H.ID_GROUP_SPECIES = I.ID_GROUP_SPECIES)"
                    + " LEFT JOIN chm62edt_taxonomy AS J ON (H.ID_TAXONOMY = J.ID_TAXONOMY )";
            // " LEFT JOIN chm62edt_taxonomy AS L ON (J.ID_TAXONOMY_LINK = L.ID_TAXONOMY )";

        }
        // If we search on habitat scientific name as main criteria
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_NAME.intValue()) {
            sql = "SELECT " + what + " FROM chm62edt_sites AS C "
                    + " INNER JOIN chm62edt_nature_object_report_type AS G ON C.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT "
                    + " INNER JOIN chm62edt_species AS H ON G.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT "
                    + joinWithGroupSpeciesAndTaxonomy + " WHERE (1 = 1) "
                    + whereCondition;
        }
        // If we search on habitat legal instruments as main criteria
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_SIZE.intValue()
                        || searchAttribute.intValue()
                                == SitesSearchCriteria.SEARCH_LENGTH.intValue()) {
            sql = "SELECT " + what + " FROM chm62edt_sites AS C "
                    + " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT "
                    + " INNER JOIN chm62edt_species AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT "
                    + joinWithGroupSpeciesAndTaxonomy + " WHERE (1 = 1) "
                    + whereCondition;
        }
        // If we search on habitats country as main criteria
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_COUNTRY.intValue()) {
            sql = "SELECT " + what + " FROM chm62edt_country AS E "
                    + " INNER JOIN chm62edt_nature_object_geoscope AS D ON D.ID_GEOSCOPE = E.ID_GEOSCOPE "
                    + " INNER JOIN chm62edt_sites AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT "
                    + " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT "
                    + " INNER JOIN chm62edt_species AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT "
                    + joinWithGroupSpeciesAndTaxonomy + " WHERE (1 = 1) "
                    + whereCondition;
        }
        // If we search on habitats country as main criteria
        if (searchAttribute.intValue()
                == SitesSearchCriteria.SEARCH_REGION.intValue()) {
            sql = "SELECT " + what + " FROM chm62edt_biogeoregion AS E "
                    + " INNER JOIN chm62edt_nature_object_geoscope AS D ON D.ID_GEOSCOPE = E.ID_GEOSCOPE "
                    + " INNER JOIN chm62edt_sites AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT "
                    + " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT "
                    + " INNER JOIN chm62edt_species AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT "
                    + joinWithGroupSpeciesAndTaxonomy + " WHERE (1 = 1) "
                    + whereCondition;
        }
        return sql;
    }
}
