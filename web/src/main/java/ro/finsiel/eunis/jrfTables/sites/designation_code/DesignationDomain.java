package ro.finsiel.eunis.jrfTables.sites.designation_code;

/**
 * Date: May 22, 2003
 * Time: 10:00:04 AM
 */

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.*;
import ro.finsiel.eunis.search.sites.designation_code.DesignationSearchCriteria;
import ro.finsiel.eunis.search.sites.designation_code.DesignationSortCriteria;

import javax.xml.transform.Source;
import java.util.List;


public class DesignationDomain extends AbstractDomain implements Paginable {
  /** Criterias applied for searching */
  private AbstractSearchCriteria[] searchCriteria = new AbstractSearchCriteria[0]; // 0 length means not criteria set
  /** Criterias applied for sorting */
  private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

  /** Cache the results of a count to avoid overhead queries for counting */
  private Long _resultCount = new Long(-1);
  /** Specifies where to search: SEARCH_EUNIS or SEARCH_ANNEX_I */

  private static final Logger logger = Logger.getLogger(DesignationDomain.class);


  private SourceDb sourceDb = SourceDb.noDatabase();


  /**
   * Normal constructor
   * @param searchCriteria The search criteria used to query the database
   * @param sortCriteria Sort criterias used for sorting the results
   */
  public DesignationDomain(AbstractSearchCriteria[] searchCriteria, AbstractSortCriteria[] sortCriteria, SourceDb sourceDb) {
    this.searchCriteria = searchCriteria;
    this.sortCriteria = sortCriteria;
    this.sourceDb = sourceDb;
  }

  public DesignationDomain(AbstractSearchCriteria[] searchCriteria, SourceDb sourceDb) {
    this.searchCriteria = searchCriteria;
      this.sourceDb = sourceDb;
  }

  public DesignationDomain() {
  }

  /****/
  public PersistentObject newPersistentObject() {
    return new DesignationPersist();
  }

  /****/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("chm62edt_sites");
    this.setReadOnly(true);
    this.setTableAlias("C");

    this.addColumnSpec(new StringColumnSpec("ID_SITE", "getIdSite", "setIdSite", DEFAULT_TO_EMPTY_STRING, NATURAL_PRIMARY_KEY));
    this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("NAME", "getName", "setName", DEFAULT_TO_EMPTY_STRING, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("SOURCE_DB", "getSourceDB", "setSourceDB", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("DESIGNATION_DATE", "getDesignationDate", "setDesignationDate", null));
    this.addColumnSpec(new StringColumnSpec("AREA", "getArea", "setArea", null));
    this.addColumnSpec(new StringColumnSpec("LENGTH", "getLength", "setLength", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("LONGITUDE", "getLongitude", "setLongitude", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("LATITUDE", "getLatitude", "setLatitude", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("ID_DESIGNATION", "getIdDesign", "setIdDesign", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("ID_GEOSCOPE", "getGeoscope", "setGeoscope", DEFAULT_TO_NULL));

    JoinTable J4 = new JoinTable("chm62edt_designations J", "ID_DESIGNATION", "ID_DESIGNATION");
    J4.addJoinColumn(new StringJoinColumn("DESCRIPTION", "setDescriptionSites"));
    J4.addJoinColumn(new StringJoinColumn("DESCRIPTION_EN", "setDescriptionSitesEn"));
    J4.addJoinColumn(new StringJoinColumn("DESCRIPTION_FR", "setDescriptionSitesFr"));
    J4.addJoinColumn(new StringJoinColumn("ORIGINAL_DATASOURCE", "setDesignSourceDb"));
    this.addJoinTable(J4);

    OuterJoinTable natureObjectGeoscope = new OuterJoinTable("chm62edt_nature_object_geoscope B ", "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");
    this.addJoinTable(natureObjectGeoscope);

    OuterJoinTable country = new OuterJoinTable("chm62edt_country G", "ID_GEOSCOPE", "ID_GEOSCOPE");
    country.addJoinColumn(new StringJoinColumn("AREA_NAME_EN", "country", "setCountry"));
    natureObjectGeoscope.addJoinTable(country);

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

    StringBuffer filterSQL = _prepareWhereSearch();
    filterSQL.append(" GROUP BY C.ID_NATURE_OBJECT ");
    // Add the ORDER BY clause to do the sorting
    if (sortCriteria.length > 0) {
      filterSQL.append(_prepareWhereSort());
    }
    // Add the LIMIT clause to return only the wanted results
    if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
      filterSQL.append(" LIMIT ").append(offsetStart).append(", ").append(pageSize);
    }

    List tempList = this.findWhere(filterSQL.toString());
    _resultCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
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

   * @return
   * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException
   */
  private Long _rawCount() throws CriteriaMissingException {
    if (searchCriteria.length < 1) throw new CriteriaMissingException("Unable to search because no search criteria was specified...");
    // Prepare the WHERE clause   put search Criteria
    StringBuffer filterSQL = _prepareWhereSearch();
    String sql = "";
    sql = " SELECT COUNT(DISTINCT C.ID_NATURE_OBJECT) " +
            " FROM chm62edt_sites C " +
            " INNER JOIN chm62edt_designations J ON (C.ID_DESIGNATION=J.ID_DESIGNATION AND C.ID_GEOSCOPE=J.ID_GEOSCOPE) " +
            " LEFT OUTER JOIN chm62edt_nature_object_geoscope B ON B.ID_NATURE_OBJECT = C.ID_NATURE_OBJECT" +
            " LEFT OUTER JOIN chm62edt_country G ON B.ID_GEOSCOPE = G.ID_GEOSCOPE " +
            " WHERE " + filterSQL.toString();
    Long tempList = this.findLong(sql);
    if (null != tempList)
      return tempList;
    else
      return new Long(0);

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
    filterSQL.append(" IF(C.ID_GEOSCOPE IS NULL,'1',C.ID_GEOSCOPE) = IF(J.ID_GEOSCOPE IS NULL,'1',J.ID_GEOSCOPE) ");
    filterSQL = Utilities.getConditionForSourceDB(filterSQL, sourceDb, "C");
    for (int i = 0; i < searchCriteria.length; i++) {
      filterSQL.append(" AND ");
      DesignationSearchCriteria aCriteria = (DesignationSearchCriteria) searchCriteria[i]; // upcast
      filterSQL.append(aCriteria.toSQL());
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
      logger.debug(e, e);   //To change body of catch statement use Options | File Templates.
    }
    return filterSQL;
  }
}