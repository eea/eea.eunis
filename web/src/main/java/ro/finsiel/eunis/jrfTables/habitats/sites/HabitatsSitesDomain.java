package ro.finsiel.eunis.jrfTables.habitats.sites;

/**
 * Date: May 12, 2003
 * Time: 2:52:18 PM
 */

import eionet.eunis.dao.impl.MySqlBaseDao;
import eionet.eunis.util.Constants;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.*;
import ro.finsiel.eunis.search.sites.SitesSearchUtility;
import ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria;
import ro.finsiel.eunis.search.habitats.sites.SitesSortCriteria;
import ro.finsiel.eunis.jrfTables.GenericDomain;
import ro.finsiel.eunis.jrfTables.GenericPersist;

import java.util.List;
import java.util.Vector;


public class HabitatsSitesDomain extends AbstractDomain implements Paginable {
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
  private Integer database = SEARCH_BOTH;
  private Integer searchAttribute = null;

  private SourceDb sourceDb = SourceDb.noDatabase();

  private static final Logger logger = Logger.getLogger(HabitatsSitesDomain.class);

  /**
   * Normal constructor
   * @param searchCriteria The search criteria used to query the database
   * @param sortCriteria Sort criterias used for sorting the results
   */
    public HabitatsSitesDomain(AbstractSearchCriteria[] searchCriteria,
                               AbstractSortCriteria[] sortCriteria,
                               Integer searchAttribute,
                               Integer searchPlace,
                               SourceDb sourceDb) {
        this.searchCriteria = searchCriteria;
        this.sortCriteria = sortCriteria;
        //this.database = searchPlace;
        this.sourceDb = sourceDb;
        this.searchAttribute = searchAttribute;
    }

  public HabitatsSitesDomain(AbstractSearchCriteria[] searchCriteria, Integer searchPlace) {
    this.searchCriteria = searchCriteria;
    this.database = SEARCH_BOTH;
  }

  public HabitatsSitesDomain() {
  }

  /****/
  public PersistentObject newPersistentObject() {
    return new HabitatsSitesPersist();
  }

  /****/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("chm62edt_habitat");
    this.setReadOnly(true);
    this.setTableAlias("C");

    this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_ZERO, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("ID_HABITAT", "getIdHabitat", "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
    this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName", "setScientificName", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("DESCRIPTION", "getDescription", "setDescription", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("CODE_ANNEX1", "getCodeAnnex1", "setCodeAnnex1", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode", "setEunisHabitatCode", DEFAULT_TO_NULL));
    this.addColumnSpec(new IntegerColumnSpec("LEVEL", "getHabLevel", "setHabLevel", DEFAULT_TO_NULL));
    // FROM chm62edt_sites
    this.addColumnSpec(new StringColumnSpec("NAME", "getSiteName", "setSiteName", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("SOURCE_DB", "getSourceDb", "setSourceDb", DEFAULT_TO_NULL));
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
    String sql = prepareSQL("H.ID_NATURE_OBJECT, H.ID_HABITAT, H.SCIENTIFIC_NAME, " +
            "H.DESCRIPTION, H.CODE_2000, " +
            "H.CODE_ANNEX1, H.EUNIS_HABITAT_CODE, H.LEVEL, C.NAME, C.SOURCE_DB ",
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

   * @return
   * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
   */
  private Long _rawCount() throws CriteriaMissingException {
    Long results = new Long(-1);
    StringBuffer filterSQL = _prepareWhereSearch();
    String sql = prepareSQL("COUNT(DISTINCT H.ID_NATURE_OBJECT)", filterSQL.toString());
    try {
      results = new HabitatsSitesDomain().findLong(sql);
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
    if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
    for (int i = 0; i < searchCriteria.length; i++) {
      filterSQL.append(" AND ");
      SitesSearchCriteria aCriteria = (SitesSearchCriteria) searchCriteria[i]; // upcast
      filterSQL.append(aCriteria.toSQL());
    }
    //System.out.println("database = " + database);
    if (0 != database.compareTo(SEARCH_BOTH)) {
      if (0 == database.compareTo(SEARCH_EUNIS)) {
        if (filterSQL.length() > 0) filterSQL.append(" AND ");
        filterSQL.append(" H.ID_HABITAT>=1 and H.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'  ");
      }
      if (0 == database.compareTo(SEARCH_ANNEX_I)) {
        if (filterSQL.length() > 0) filterSQL.append(" AND ");
        filterSQL.append(" H.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  ");
      }
    } else {
      if (filterSQL.length() > 0) filterSQL.append(" AND ");
      filterSQL.append(" H.ID_HABITAT>=1 and H.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  ");
    }
    filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "C");
    filterSQL.append(" AND IF(TRIM(H.CODE_2000) <> '',RIGHT(H.CODE_2000,2),1) <> IF(TRIM(H.CODE_2000) <> '','00',2) AND IF(TRIM(H.CODE_2000) <> '',LENGTH(H.CODE_2000),1) = IF(TRIM(H.CODE_2000) <> '',4,1) ");
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
   * Finds habitats from the specified site
   * @param criteria New search criteria
   * @return A non-null list with habitats names
   */
  public List findSitesWithHabitats(SitesSearchCriteria criteria,
                                    SourceDb sourceDb,
                                    Integer searchAttribute,
                                    Integer idNatureObject,
                                    Integer database) {
    if (null == criteria) {
      System.out.println("Warning: " + HabitatsSitesDomain.class.getName() + "::findSitesWithHabitats(" + criteria + ", " + "...). One of criterias was null.");
      return new Vector();
    }
    this.searchCriteria = new AbstractSearchCriteria[1];
    this.searchCriteria[0] = criteria;
    this.searchAttribute = searchAttribute;
    this.sourceDb = sourceDb;
    this.database = SEARCH_BOTH;
    StringBuffer filterSQL = new StringBuffer("");
    List results = new Vector();
    List habitats = new Vector();
    try {
      filterSQL = _prepareWhereSearch();
    } catch (CriteriaMissingException _ex) {
      _ex.printStackTrace();
    }
    if (null != idNatureObject) {
      filterSQL.append(" AND H.ID_NATURE_OBJECT='" + idNatureObject + "'");
    }
    String sql = prepareSQL("H.ID_NATURE_OBJECT, H.ID_HABITAT, H.SCIENTIFIC_NAME, H.DESCRIPTION, " +
            "H.CODE_2000, H.CODE_ANNEX1, H.EUNIS_HABITAT_CODE, H.LEVEL, C.NAME, C.SOURCE_DB ",
            filterSQL + " GROUP BY C.ID_NATURE_OBJECT ORDER BY C.NAME");
    try {
      results = new HabitatsSitesDomain().findCustom(sql);
    } catch (Exception _ex) {
      _ex.printStackTrace();
    }
    for (int i = 0; i < results.size(); i++) {
      HabitatsSitesPersist habitat = (HabitatsSitesPersist) results.get(i);
      //habitats.add(habitat.getSiteName() + " (" + SitesSearchUtility.translateSourceDB(habitat.getSourceDb()) + ")");
      List l = new Vector();
      l.add(habitat.getSiteName());
      l.add(SitesSearchUtility.translateSourceDB(habitat.getSourceDb()));
      habitats.add(l);
    }
    return habitats;
  }


  /**
   * Finds habitats from the specified site.
   * @param criteria New search criteria
   * @return A non-null list with species scientific names
   */
  public List findPopupLOV(SitesSearchCriteria criteria,
                           SourceDb sourceDb,
                           Integer searchAttribute,
                           Integer database) {
    if (null == criteria) {
      System.out.println("Warning:" + HabitatsSitesDomain.class.getName() + "::findPopupLOV(" + criteria + ", " + "...). One of criterias was null.");
      return new Vector();
    }
    this.searchCriteria = new AbstractSearchCriteria[1];
    this.searchCriteria[0] = criteria;
    this.searchAttribute = searchAttribute;
    this.sourceDb = sourceDb;
    this.database = SEARCH_BOTH;
    StringBuffer filterSQL = new StringBuffer("");
    List results = new Vector();
    List habitats = new Vector();
    try {
      filterSQL = _prepareWhereSearch();
    } catch (CriteriaMissingException _ex) {
      _ex.printStackTrace();
    }
    String sql = "";
    // SITE NAME
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_NAME.intValue()) {
      sql = prepareSQL("H.ID_NATURE_OBJECT, H.ID_HABITAT, H.SCIENTIFIC_NAME, H.DESCRIPTION, " +
              "H.CODE_2000, H.CODE_ANNEX1, H.EUNIS_HABITAT_CODE, H.LEVEL, C.NAME, C.SOURCE_DB ",
              filterSQL + " GROUP BY C.ID_NATURE_OBJECT ORDER BY C.NAME LIMIT 0," + Utilities.MAX_POPUP_RESULTS);
      try {
        results = new HabitatsSitesDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        HabitatsSitesPersist habitat = (HabitatsSitesPersist) results.get(i);
        habitats.add(habitat.getSiteName());
      }
    }
    // COUNTRY
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_COUNTRY.intValue()) {
      sql = prepareSQL("E.AREA_NAME_EN AS COLUMN1", filterSQL.toString() + " GROUP BY E.AREA_NAME_EN");
      try {
        results = new GenericDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        GenericPersist habitat = (GenericPersist) results.get(i);
        habitats.add(habitat.getColumn1());
      }
    }
    // REGION
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_REGION.intValue()) {
      sql = prepareSQL("E.NAME AS COLUMN1", filterSQL.toString() + " GROUP BY E.NAME");
      try {
        results = new GenericDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        GenericPersist habitat = (GenericPersist) results.get(i);
        habitats.add(habitat.getColumn1());
      }
    }
    return habitats;
  }

  /**
   * Prepare the SQL for the search.
   * @param what The SELECT condition (ex. SELECT C.SCIENTIFIC_NAME,E.VALUE...)
   * @param whereCondition the WHERE condition for SQL.
   * @return The full SQL for the search.
   */
  private String prepareSQL(String what, String whereCondition) {
    String sql = "";
    // If we search on habitat scientific name as main criteria
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_NAME.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_sites AS C " +
              " INNER JOIN chm62edt_nature_object_report_type AS G ON C.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_habitat AS H ON G.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitat legal instruments as main criteria
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_SIZE.intValue() ||
            searchAttribute.intValue() == SitesSearchCriteria.SEARCH_LENGTH.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_sites AS C " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_habitat AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitats country as main criteria
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_COUNTRY.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_country AS E " +
              " INNER JOIN chm62edt_nature_object_geoscope AS D ON D.ID_GEOSCOPE = E.ID_GEOSCOPE " +
              " INNER JOIN chm62edt_sites AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_habitat AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitats country as main criteria
    if (searchAttribute.intValue() == SitesSearchCriteria.SEARCH_REGION.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_biogeoregion AS E " +
              " INNER JOIN chm62edt_nature_object_geoscope AS D ON D.ID_GEOSCOPE = E.ID_GEOSCOPE " +
              " INNER JOIN chm62edt_sites AS C ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_habitat AS H ON K.ID_NATURE_OBJECT_LINK = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    return sql;
  }
}