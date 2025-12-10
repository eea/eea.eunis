package ro.finsiel.eunis.jrfTables.sites.habitats;

/**
 * Date: May 19, 2003
 * Time: 10:51:29 AM
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
import ro.finsiel.eunis.search.sites.habitats.HabitatSearchCriteria;
import ro.finsiel.eunis.search.sites.habitats.HabitatSortCriteria;
import ro.finsiel.eunis.jrfTables.GenericDomain;
import ro.finsiel.eunis.jrfTables.GenericPersist;
import ro.finsiel.eunis.admin.FileUtils;

import java.util.List;
import java.util.Vector;

public class HabitatDomain extends AbstractDomain implements Paginable {
  public static final Integer SEARCH_EUNIS = new Integer(0);
  public static final Integer SEARCH_ANNEX_I = new Integer(1);
  public static final Integer SEARCH_BOTH = new Integer(2);

  private static final Logger logger = Logger.getLogger(HabitatDomain.class);
  /** Criterias applied for searching */
  private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
  /** Criterias applied for sorting */
  private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

  /** Cache the results of a count to avoid overhead queries for counting */
  private Long _resultCount = new Long(-1);
  /** Specifies where to search: SEARCH_EUNIS or SEARCH_ANNEX_I */
  private Integer searchPlace = SEARCH_EUNIS;

  SourceDb sourceDb = SourceDb.allDatabases().remove(SourceDb.Database.NATURENET);
  private Integer searchAttribute = null;

  /**
   * Normal constructor
   * @param searchCriteria The search criteria used to query the database
   * @param sortCriteria Sort criterias used for sorting the results
   */
  public HabitatDomain(AbstractSearchCriteria[] searchCriteria,
                       AbstractSortCriteria[] sortCriteria,
                       Integer searchPlace,
                       SourceDb sourceDb,
                       Integer searchAttribute) {
    this.searchAttribute = searchAttribute;
    this.searchCriteria = searchCriteria;
    this.sortCriteria = sortCriteria;
    this.searchPlace = searchPlace;
    this.sourceDb = sourceDb;
  }

  public HabitatDomain(AbstractSearchCriteria[] searchCriteria, Integer searchPlace, SourceDb sourceDb) {
    this.searchCriteria = searchCriteria;
    this.searchPlace = searchPlace;
    this.sourceDb = sourceDb;
  }

  public HabitatDomain() {
  }

  /****/
  public PersistentObject newPersistentObject() {
    return new HabitatPersist();
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

    // FROM chm62edt_habitat
    this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getHabitatName", "setHabitatName", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode", "setEunisHabitatCode", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000", DEFAULT_TO_NULL));
    // FROM chm62edt_habitat_class_code
    this.addColumnSpec(new StringColumnSpec("CODE", "getHabitatClassCodeCode", "setHabitatClassCodeCode", DEFAULT_TO_NULL));
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
    String sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME," +
            " H.LONGITUDE, H.LATITUDE, C.SCIENTIFIC_NAME, " +
            "C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME", // Because of extended columns from the table.
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
    try
    {
      results = findLong(sql);
    }
    catch (Exception _ex)
    {
      _ex.printStackTrace();
      results = new Long(-1);
    }
    return results;
  }

  /**
   * This helper method is used to construct the string after WHERE...based on search criterias set. In another words
   * constructs .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX OR B.ID_GEOSCOPE_LINK=YYY OR B.ID_GEOSCOPE_LINK=ZZZ .....
   * @return SQL string
   * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
   */
  private StringBuffer _prepareWhereSearch() throws CriteriaMissingException {
    StringBuffer filterSQL = new StringBuffer();
    if (0 != searchPlace.compareTo(SEARCH_BOTH)) {
      if (0 == searchPlace.compareTo(SEARCH_EUNIS)) {
        filterSQL.append(" AND C.ID_HABITAT >=1 AND C.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS+ "%' ");
      }
      if (0 == searchPlace.compareTo(SEARCH_ANNEX_I)) {
        filterSQL.append(" AND C.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "' ");
      }
    } else {
      filterSQL.append(" AND C.ID_HABITAT <>'-1' AND C.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "' ");
    }
    filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "H");
    if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
    for (int i = 0; i < searchCriteria.length; i++) {
      filterSQL.append(" AND ");
      HabitatSearchCriteria aCriteria = (HabitatSearchCriteria) searchCriteria[i]; // upcast
      filterSQL.append(aCriteria.toSQL());
    }
    filterSQL.append(" AND IF(TRIM(C.CODE_2000) <> '',RIGHT(C.CODE_2000,2),1) <> IF(TRIM(C.CODE_2000) <> '','00',2) AND IF(TRIM(C.CODE_2000) <> '',LENGTH(C.CODE_2000),1) = IF(TRIM(C.CODE_2000) <> '',4,1) ");
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
  public List findHabitatsFromSite(HabitatSearchCriteria criteria,
                                   Integer searchAttribute,
                                   SourceDb sourceDb) {
    if (null == criteria) {
      System.out.println("Warning:" + HabitatDomain.class.getName() + "::findHabitatsFromSite(" + criteria + ", " + "...). One of criterias was null.");
      return new Vector();
    }
    this.searchCriteria = new AbstractSearchCriteria[1];
    this.searchCriteria[0] = criteria;
    this.searchAttribute = searchAttribute;
    this.sourceDb = sourceDb;
    StringBuffer filterSQL = new StringBuffer("");
    List results = new Vector();
    List habitats = new Vector();
    try {
      filterSQL = _prepareWhereSearch();
    } catch (CriteriaMissingException _ex) {
      _ex.printStackTrace();
    }
    String sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME," +
            " H.LONGITUDE, H.LATITUDE, C.SCIENTIFIC_NAME, " +
            "C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME", // Because of extended columns from the table.
            filterSQL + " GROUP BY C.ID_NATURE_OBJECT");
    try {
      results = new HabitatDomain().findCustom(sql);
    } catch (Exception _ex) {
      _ex.printStackTrace();
    }
    for (int i = 0; i < results.size(); i++) {
      HabitatPersist habitat = (HabitatPersist) results.get(i);
      habitats.add(habitat.getHabitatName());
    }
    return habitats;
  }

  /**
   * Finds habitats from the specified site
   * @param criteria New search criteria
   * @return A non-null list with habitats names
   */
  public List findHabitatsFromSpecifiedSite(HabitatSearchCriteria criteria,
                                   Integer searchAttribute,
                                   SourceDb sourceDb,
                                   String siteName) {
    if (null == criteria) {
      System.out.println("Warning:" + HabitatDomain.class.getName() + "::findHabitatsFromSite(" + criteria + ", " + "...). One of criterias was null.");
      return new Vector();
    }
    this.searchCriteria = new AbstractSearchCriteria[1];
    this.searchCriteria[0] = criteria;
    this.searchAttribute = searchAttribute;
    this.sourceDb = sourceDb;
    StringBuffer filterSQL = new StringBuffer("");
    List results = new Vector();
    List habitats = new Vector();
    try {
      filterSQL = _prepareWhereSearch();
      filterSQL.append(" AND H.NAME = '"+siteName.replaceAll("'","''")+"' ");
    } catch (CriteriaMissingException _ex) {
      _ex.printStackTrace();
    }
    String sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME," +
            " H.LONGITUDE, H.LATITUDE, C.SCIENTIFIC_NAME, " +
            "C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME", // Because of extended columns from the table.
            filterSQL + " GROUP BY C.ID_NATURE_OBJECT");
    try {
      results = new HabitatDomain().findCustom(sql);
    } catch (Exception _ex) {
      _ex.printStackTrace();
    }
    for (int i = 0; i < results.size(); i++) {
      HabitatPersist habitat = (HabitatPersist) results.get(i);
      habitats.add(habitat.getHabitatName());
    }
    return habitats;
  }

  /**
   * Finds habitats from the specified site.
   * @param criteria New search criteria
   * @return A non-null list with species scientific names
   */
  public List findPopupLOV(HabitatSearchCriteria criteria,
                           Integer database,
                           Integer searchAttribute,
                           SourceDb sourceDb) {
    if (null == criteria) {
      System.out.println("Warning: " + HabitatDomain.class.getName() + "::findPopupLOV(" + criteria + ", " + "...). One of criterias was null.");
      return new Vector();
    }
    this.searchCriteria = new AbstractSearchCriteria[1];
    this.searchCriteria[0] = criteria;
    this.searchAttribute = searchAttribute;
    this.sourceDb = sourceDb;
    StringBuffer filterSQL = new StringBuffer("");
    List results = new Vector();
    List habitats = new Vector();
    try {
      filterSQL = _prepareWhereSearch();
    } catch (CriteriaMissingException _ex) {
      _ex.printStackTrace();
    }
    String sql = "";
    // SCIENTIFIC NAME
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_NAME.intValue()) {
      sql = prepareSQL("H.ID_SITE, H.SOURCE_DB, H.ID_NATURE_OBJECT, H.ID_GEOSCOPE, H.ID_DESIGNATION, H.NAME, " +
              "H.LONGITUDE, H.LATITUDE, C.SCIENTIFIC_NAME, " +
              "C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME, C.SCIENTIFIC_NAME", // Because of extended columns from the table.
              filterSQL + " GROUP BY C.ID_NATURE_OBJECT");
      try {
        results = new HabitatDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        HabitatPersist habitat = (HabitatPersist) results.get(i);
        habitats.add(habitat.getHabitatName());
      }
    }
    // CODE
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_CODE.intValue()) {
      filterSQL = new StringBuffer();
      if (0 != searchPlace.compareTo(SEARCH_BOTH)) {
        if (0 == searchPlace.compareTo(SEARCH_EUNIS)) {
          filterSQL.append(" C.ID_HABITAT >= 1 AND C.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS+ "%' ");
        }
        if (0 == searchPlace.compareTo(SEARCH_ANNEX_I)) {
          filterSQL.append(" C.HABITAT_TYPE =' " + Constants.HABITAT_ANNEX1 + "' ");
        }
      } else {
        filterSQL.append(" C.ID_HABITAT <> '-1' AND C.ID_HABITAT <> '" + Constants.HABITAT_ANNEX1_ROOT + "' ");
      }
      filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "H");

      String where = criteria.getSearchString();
      Integer relOp = criteria.getRelationOp();
      if ( where == null || where.trim().equalsIgnoreCase(""))
      {
        where = "%";
        relOp = Utilities.OPERATOR_CONTAINS;
      }
      sql = "(" +
              " SELECT DISTINCT C.EUNIS_HABITAT_CODE AS COLUMN1 " +
              " FROM chm62edt_habitat AS C " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) AND " + Utilities.prepareSQLOperator("C.EUNIS_HABITAT_CODE", where, relOp ) +
              " AND " + filterSQL +
              " )" +
              " UNION " +
              " (" +
              " SELECT DISTINCT C.CODE_2000 AS COLUMN1 " +
              " FROM chm62edt_habitat AS C " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) AND " + Utilities.prepareSQLOperator("C.CODE_2000", where, relOp ) +
              " AND " + filterSQL +
              " ) " +
              " UNION " +
              " ( " +
              " SELECT DISTINCT D.CODE AS COLUMN1 " +
              " FROM chm62edt_habitat AS C " +
              " INNER JOIN chm62edt_habitat_class_code AS D ON C.ID_HABITAT = D.ID_HABITAT " +
              " INNER JOIN chm62edt_class_code AS E ON (D.ID_CLASS_CODE = E.ID_CLASS_CODE AND E.LEGAL = '0') " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) AND " + Utilities.prepareSQLOperator("D.CODE", where, relOp ) +
              " AND " + filterSQL +
              ") ORDER BY COLUMN1 LIMIT 0," + Utilities.MAX_POPUP_RESULTS;
      try {
        results = new GenericDomain().findCustom(sql);
      } catch (Exception _ex) {
        _ex.printStackTrace();
      }
      for (int i = 0; i < results.size(); i++) {
        GenericPersist code = (GenericPersist) results.get(i);
        habitats.add(code.getColumn1());
      }
    }
    // LEGAL INSTRUMENTS
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()) {
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
    // COUNTRY
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_COUNTRY.intValue()) {
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
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_REGION.intValue()) {
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
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_NAME.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_habitat AS C " +
              " LEFT JOIN chm62edt_habitat_description AS K ON K.ID_HABITAT = C.ID_HABITAT " +
              " INNER JOIN chm62edt_nature_object_report_type AS G ON C.ID_NATURE_OBJECT = G.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON G.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitat legal instruments as main criteria
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_CODE.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_habitat AS C " +
              " LEFT JOIN chm62edt_habitat_class_code AS D ON C.ID_HABITAT = D.ID_HABITAT " +
              " LEFT JOIN chm62edt_class_code AS E ON (D.ID_CLASS_CODE = E.ID_CLASS_CODE AND E.LEGAL = '0') " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitat legal instruments as main criteria
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_habitat AS C " +
              " INNER JOIN chm62edt_habitat_class_code AS D ON C.ID_HABITAT = D.ID_HABITAT " +
              " INNER JOIN chm62edt_class_code AS E ON (D.ID_CLASS_CODE = E.ID_CLASS_CODE AND E.LEGAL = '1') " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitats country as main criteria
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_COUNTRY.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_habitat AS C " +
              " INNER JOIN chm62edt_reports AS D ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_country AS E ON D.ID_GEOSCOPE = E.ID_GEOSCOPE " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    // If we search on habitats country as main criteria
    if (searchAttribute.intValue() == HabitatSearchCriteria.SEARCH_REGION.intValue()) {
      sql = "SELECT " + what +
              " FROM chm62edt_habitat AS C " +
              " INNER JOIN chm62edt_reports AS D ON C.ID_NATURE_OBJECT = D.ID_NATURE_OBJECT " +
              " INNER JOIN chm62edt_biogeoregion AS E ON D.ID_GEOSCOPE_LINK = E.ID_GEOSCOPE " +
              " INNER JOIN chm62edt_nature_object_report_type AS K ON C.ID_NATURE_OBJECT = K.ID_NATURE_OBJECT_LINK " +
              " INNER JOIN chm62edt_sites AS H ON K.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT " +
              " WHERE (1 = 1) " + whereCondition;
    }
    return sql;
  }
}