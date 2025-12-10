package ro.finsiel.eunis.jrfTables.sites.neighborhood;

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.jrfTables.Chm62edtSitesPersist;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;

import java.util.Vector;
import java.util.List;

/**
 * Date: Sep 23, 2003
 * Time: 10:11:01 AM
 */
public class NeighborhoodDomain extends AbstractDomain implements Paginable {
  private float radius = 0.0F;
  private float originX = 0.0F;
  private float originY = 0.0F;
  private Long _resultCount = new Long(-1);
  private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0];
  private String idSite;

  private static final Logger logger = Logger.getLogger(NeighborhoodDomain.class);

  /** **/
  public PersistentObject newPersistentObject() {
    return new Chm62edtSitesPersist();
  }

  /**
   * @param idSite ID of the site to find neighbors.
   * @param radius Radius of search.
   * @param originX Longitude of the site
   * @param originY Latitude of the site.
   */
  public NeighborhoodDomain(String idSite, float radius, float originX, float originY, AbstractSortCriteria[] sortCriteria) {
    this.idSite = idSite;
    this.radius = radius;
    this.originX = originX;
    this.originY = originY;
    this.radius /= 115; // ~= 40000km / 360deg ~= (radius of Earth / 360)
    this.sortCriteria = sortCriteria;
  }

  /**
   **/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("chm62edt_sites");
    this.setReadOnly(true);


    this.addColumnSpec(new StringColumnSpec("ID_SITE", "getIdSite", "setIdSite", DEFAULT_TO_EMPTY_STRING, NATURAL_PRIMARY_KEY));
    this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("RESPONDENT", "getRespondent", "setRespondent", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("NAME", "getName", "setName", DEFAULT_TO_EMPTY_STRING, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("MANAGER", "getManager", "setManager", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("COMPLEX_NAME", "getComplexName", "setComplexName", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("DISTRICT_NAME", "getDistrictName", "setDistrictName", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("OWNERSHIP", "getOwnership", "setOwnership", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("HISTORY", "getHistory", "setHistory", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("CHARACTER", "getCharacter", "setCharacter", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("DESCRIPTION", "getDescription", "setDescription", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("MANAGEMENT_PLAN", "getManagementPlan", "setManagementPlan", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("IUCNAT", "getIucnat", "setIucnat", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("DESIGNATION_DATE", "getDesignationDate", "setDesignationDate", null));
    this.addColumnSpec(new StringColumnSpec("COMPILATION_DATE", "getCompilationDate", "setCompilationDate", null));
    this.addColumnSpec(new StringColumnSpec("PROPOSED_DATE", "getProposedDate", "setProposedDate", null));
    this.addColumnSpec(new StringColumnSpec("CONFIRMED_DATE", "getConfirmedDate", "setConfirmedDate", null));
    this.addColumnSpec(new StringColumnSpec("UPDATE_DATE", "getUpdateDate", "setUpdateDate", null));
    this.addColumnSpec(new StringColumnSpec("SPA_DATE", "getSpaDate", "setSpaDate", null));
    this.addColumnSpec(new StringColumnSpec("SAC_DATE", "getSacDate", "setSacDate", null));
    this.addColumnSpec(new StringColumnSpec("NATIONAL_CODE", "getNationalCode", "setNationalCode", null));
    this.addColumnSpec(new StringColumnSpec("NATURA_2000", "getNatura2000", "setNatura2000", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("NUTS", "getNuts", "setNuts", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("AREA", "getArea", "setArea", null));
    this.addColumnSpec(new StringColumnSpec("LENGTH", "getLength", "setLength", DEFAULT_TO_NULL));

    this.addColumnSpec(new StringColumnSpec("ALT_MEAN", "getAltMean", "setAltMean", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("ALT_MAX", "getAltMax", "setAltMax", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("ALT_MIN", "getAltMin", "setAltMin", DEFAULT_TO_NULL));

    this.addColumnSpec(new StringColumnSpec("LONGITUDE", "getLongitude", "setLongitude", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("LATITUDE", "getLatitude", "setLatitude", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("SOURCE_DB", "getSourceDB", "setSourceDB", DEFAULT_TO_NULL));
  }

  /** This method is used to retrieve a sub-set of the main results of a query given its start index offset and end
   * index offset.
   * @param offsetStart The start offset (i.e. 0). If offsetStart = offSetEnd then return the whole list
   * @param pageSize The end offset (i.e. 1). If offsetStart = offSetEnd then return the whole list
   * @param sortCriteria The criteria used for sorting
   * @return A list of objects which match query criteria
   */
  public List getResults(int offsetStart, int pageSize, AbstractSortCriteria[] sortCriteria) throws CriteriaMissingException {
    List tempList = null;
    try {
      StringBuffer filterSQL = new StringBuffer();
      filterSQL.append(" ( LONGITUDE >= " + (originX - radius) + " AND LONGITUDE <= " + (originX + radius) + " ) ");
      filterSQL.append(" AND ");
      filterSQL.append(" ( LATITUDE  >= " + (originY - radius) + " AND LATITUDE  <= " + (originY + radius) + " ) ");
      filterSQL.append(" AND LATITUDE IS NOT NULL AND LONGITUDE IS NOT NULL ");
      filterSQL.append(" AND ID_SITE <> '" + idSite + "'");
      filterSQL.append(_prepareWhereSort());
      // Add the LIMIT clause to return only the wanted results
      if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
        filterSQL.append(" LIMIT " + offsetStart + ", " + pageSize);
      }
//      System.out.println("filterSQL = " + filterSQL);
      tempList = this.findWhere(filterSQL.toString());
      _resultCount = new Long(-1);
    } catch (Exception _ex) {
      _ex.printStackTrace();
      return new Vector();
    }
    return tempList;
  }

  /** This method is used to count the total list of results from a query. It is used to find all for use in pagination.
   * Having the total number of results and the results displayed per page, the you could find the number of pages i.e.
   * @return The total number of results
   */
  public Long countResults() throws CriteriaMissingException {
    if (-1 == _resultCount.longValue()) {
      StringBuffer filterSQL = new StringBuffer();
      filterSQL.append(" SELECT COUNT(*) FROM chm62edt_sites WHERE ");
      filterSQL.append(" ( LONGITUDE >= " + (originX - radius) + " AND LONGITUDE <= " + (originX + radius) + " ) ");
      filterSQL.append(" AND ");
      filterSQL.append(" ( LATITUDE  >= " + (originY - radius) + " AND LATITUDE  <= " + (originY + radius) + " ) ");
      filterSQL.append(" AND LATITUDE IS NOT NULL AND LONGITUDE IS NOT NULL");
      filterSQL.append(" AND ID_SITE <> '" + idSite + "'");
      filterSQL.append(" AND LONGITUDE <> 0");
      filterSQL.append(" AND LATITUDE <> 0");
      //System.out.println("filterSQL = " + filterSQL);
      _resultCount = findLong(filterSQL.toString());
    }
    return _resultCount;
  }

  /** Prepare the ORDER BY clause used to do the sorting. Basically it adds the ORDER clause with the criterias set in
   * the sortCriteria[] array. Used by Sites->Neighborhoods
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
}
