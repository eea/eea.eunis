package ro.finsiel.eunis.jrfTables.habitats.country;

import eionet.eunis.util.Constants;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.habitats.country.CountrySearchCriteria;
import ro.finsiel.eunis.search.habitats.country.CountrySortCriteria;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;

import java.util.List;
import java.util.Vector;

/**
 SELECT
 chm62edt_habitat.ID_HABITAT,
 chm62edt_habitat.SCIENTIFIC_NAME,
 chm62edt_country.AREA_NAME,
 chm62edt_biogeoregion.NAME,
 chm62edt_reports.ID_GEOSCOPE, ID_GEOSCOPE_LINK

 FROM chm62edt_habitat

 INNER JOIN chm62edt_reports ON  chm62edt_habitat.ID_NATURE_OBJECT = chm62edt_reports.ID_NATURE_OBJECT
 INNER JOIN chm62edt_country ON chm62edt_reports.ID_GEOSCOPE = chm62edt_country.ID_GEOSCOPE
 INNER JOIN chm62edt_biogeoregion ON chm62edt_reports.ID_GEOSCOPE_LINK = chm62edt_biogeoregion.ID_GEOSCOPE
 WHERE chm62edt_reports.ID_GEOSCOPE = 10
 AND chm62edt_reports.ID_GEOSCOPE_LINK = 257
 */
public class CountryDomain extends AbstractDomain implements Paginable {
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

  /**
   * Default constructor
   * @param searchCriteria The criteria used for searching
   * @param database Where to search. This can be: this.SEARCH_EUNIS or this.SEARCH_ANNEX_I. Default to SEARCH_EUNIS
   */
  public CountryDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, Integer database) {
    this.searchCriteria = searchCriteria;
    this.database = database;
    this.sortCriteria = sortCriteria;
  }


  /****/
  public PersistentObject newPersistentObject() {
    return new CountryPersist();
  }

  /****/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("chm62edt_habitat");
    this.setTableAlias("A");
    this.setReadOnly(true);

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
    this.addColumnSpec(new IntegerColumnSpec("LEVEL", "getLevel", "setLevel", DEFAULT_TO_NULL));

    JoinTable reports = new JoinTable("chm62edt_reports B", "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");
    this.addJoinTable(reports);
    OuterJoinTable country = new OuterJoinTable("chm62edt_country C", "ID_GEOSCOPE", "ID_GEOSCOPE");
    country.addJoinColumn(new StringJoinColumn("AREA_NAME_EN", "country", "setCountry"));
    reports.addJoinTable(country);
    OuterJoinTable biogeoregion = new OuterJoinTable("chm62edt_biogeoregion D", "ID_GEOSCOPE_LINK", "ID_GEOSCOPE");
    biogeoregion.addJoinColumn(new StringJoinColumn("NAME", "region", "setRegion"));
    reports.addJoinTable(biogeoregion);
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
    // Add the ORDER BY clause to do the sorting

    filterSQL.append(" GROUP BY B.ID_NATURE_OBJECT,B.ID_GEOSCOPE,B.ID_GEOSCOPE_LINK ");
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


   public List getCountriesList(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria) throws CriteriaMissingException {
    this.sortCriteria = sortCriteria;
    if (searchCriteria.length < 1) throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
    // Prepare the WHERE clause
    StringBuffer filterSQL = _prepareWhereSearch();
    // Add GROUP BY clause for unique results
    // Add the ORDER BY clause to do the sorting

    filterSQL.append(" GROUP BY B.ID_NATURE_OBJECT,B.ID_GEOSCOPE,B.ID_GEOSCOPE_LINK ");
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

  /** This method is used to count the total list of results from a query. It is used to find all for use in pagination.
   * Having the total number of results and the results displayed per page, the you could find the number of pages i.e.
   * @return The total number of results
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
    StringBuffer sql = new StringBuffer();
    // Set the main QUERY
    sql.append("SELECT COUNT(DISTINCT B.ID_NATURE_OBJECT,B.ID_GEOSCOPE,B.ID_GEOSCOPE_LINK) FROM chm62edt_habitat AS A " +
            "INNER JOIN chm62edt_reports AS B ON  A.ID_NATURE_OBJECT = B.ID_NATURE_OBJECT " +
            "LEFT JOIN chm62edt_country AS C ON B.ID_GEOSCOPE = C.ID_GEOSCOPE " +
            "LEFT JOIN chm62edt_biogeoregion AS D ON B.ID_GEOSCOPE_LINK = D.ID_GEOSCOPE " +
            "WHERE ");
    // Apply WHERE CLAUSE
    sql.append(_prepareWhereSearch().toString());
    // Apply SORT CLAUSE - DON'T NEED IT FOR COUNT...
    Long ret = findLong(sql.toString());
    if (null == ret) return new Long(0);
    return ret;
  }


  /** This helper method is used to construct the string after WHERE...based on search criterias set. In another words
   * constructs .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX OR B.ID_GEOSCOPE_LINK=YYY OR B.ID_GEOSCOPE_LINK=ZZZ .....
   * @return SQL string
   * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
   */
  private StringBuffer _prepareWhereSearch() throws CriteriaMissingException {
    StringBuffer filterSQL = new StringBuffer();
    if (searchCriteria.length <= 0) throw new CriteriaMissingException("No criteria set for searching. Search interrupted.");
    filterSQL.append(" (1 = 1) ");
    filterSQL.append(" AND IF(TRIM(A.CODE_2000) <> '',RIGHT(A.CODE_2000,2),1) <> IF(TRIM(A.CODE_2000) <> '','00',2) AND IF(TRIM(A.CODE_2000) <> '',LENGTH(A.CODE_2000),1) = IF(TRIM(A.CODE_2000) <> '',4,1) ");
    if (0 != database.compareTo(SEARCH_BOTH)) {
      if (0 == database.compareTo(SEARCH_EUNIS)) {
        filterSQL.append(" AND A.ID_HABITAT >=1 AND A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%' ");
      }
      if (0 == database.compareTo(SEARCH_ANNEX_I)) {
        filterSQL.append(" AND A.ID_HABITAT A.HABITAT_TYPE = '" + Constants.HABITAT_ANNEX1 + "' ");
      }
    } else
      filterSQL.append(" AND A.ID_HABITAT <>'-1' AND A.ID_HABITAT <> '" + Constants.HABITAT_ANNEX1_ROOT + "' ");
    int i = 0;
    Vector mainSearches = new Vector();
    Vector otherSearches = new Vector();
    for (i = 0; i < searchCriteria.length; i++) {
      CountrySearchCriteria criteria = (CountrySearchCriteria) searchCriteria[i]; // upcast
      if (criteria.isMainSearch()) {
        if (criteria.toSQL().length() > 0) {
          mainSearches.addElement(criteria);
        }
      }
      if (!criteria.isMainSearch()) otherSearches.addElement(criteria);
    }

    for (i = 0; i < mainSearches.size(); i++)
    {
      String sql = ((CountrySearchCriteria) mainSearches.get(i)).toSQL();
      //System.out.println("sql = " + sql);
      if (sql.length() > 0 && i == 0) filterSQL.append(" AND ( ");
      if (sql.length() > 0 && i > 0) filterSQL.append(" OR ");
      if (sql.length() > 0) filterSQL.append(sql);
      if (sql.length() > 0 && i == mainSearches.size()-1) filterSQL.append(" ) ");
    }
    for (i = 0; i < otherSearches.size(); i++) {
      filterSQL.append(" AND ");
      filterSQL.append(((CountrySearchCriteria) otherSearches.get(i)).toSQL());
    }
    //System.out.println("filterSQL = " + filterSQL);
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

}
