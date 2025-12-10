package ro.finsiel.eunis.jrfTables.combined;

import eionet.eunis.dao.impl.MySqlBaseDao;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.advanced.AdvancedSortCriteria;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;

import java.util.List;

/**
 * Date: 09.07.2003
 * Time: 17:12:53
 */
public class SpeciesCombinedDomain extends AbstractDomain implements Paginable {
  /** Criterias applied for sorting */
  private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0]; // 0 length means unsorted

  /** Cache the results of a count to avoid overhead queries for counting */
  private Long _resultCount = new Long(-1);
  private String sid = "";

  private static final Logger logger = Logger.getLogger(SpeciesCombinedDomain.class);

  /**
   * This is the default constructor and is used only when you want to call the find* methods for this object, for
   * example.
   */
  public SpeciesCombinedDomain() {
    this.sortCriteria = null;
  }

  public SpeciesCombinedDomain(String sid) {
    this.sid = sid;
  }


  /****/
  public PersistentObject newPersistentObject() {
    return new SpeciesCombinedPersist();
  }

  /****/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("chm62edt_species");
    this.setReadOnly(true);
    this.setTableAlias("A");

    // Table declaration
    this.addColumnSpec(new IntegerColumnSpec("ID_SPECIES", "getIdSpecies", "setIdSpecies", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
    this.addColumnSpec(new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_ZERO, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName", "setScientificName", DEFAULT_TO_EMPTY_STRING, REQUIRED));
    this.addColumnSpec(new ShortColumnSpec("VALID_NAME", "getValidName", "setValidName", null, REQUIRED));
    this.addColumnSpec(new IntegerColumnSpec("ID_SPECIES_LINK", "getIdSpeciesLink", "setIdSpeciesLink", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("TYPE_RELATED_SPECIES", "getTypeRelatedSpecies", "setTypeRelatedSpecies", DEFAULT_TO_NULL));
    this.addColumnSpec(new ShortColumnSpec("TEMPORARY_SELECT", "getTemporarySelect", "setTemporarySelect", null));
    this.addColumnSpec(new StringColumnSpec("SPECIES_MAP", "getSpeciesMap", "setSpeciesMap", DEFAULT_TO_NULL));
    this.addColumnSpec(new IntegerColumnSpec("ID_GROUP_SPECIES", "getIdGroupspecies", "setIdGroupspecies", DEFAULT_TO_ZERO, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("ID_TAXONOMY", "getIdTaxcode", "setIdTaxcode", DEFAULT_TO_NULL));

    // Joined tables
    OuterJoinTable groupSpecies = null;
    groupSpecies = new OuterJoinTable("chm62edt_group_species B", "ID_GROUP_SPECIES", "ID_GROUP_SPECIES");
    groupSpecies.addJoinColumn(new StringJoinColumn("COMMON_NAME", "commonName", "setCommonName"));
    this.addJoinTable(groupSpecies);

    JoinTable taxCodeFamily = null;
    taxCodeFamily = new JoinTable("chm62edt_taxonomy C", "ID_TAXONOMY", "ID_TAXONOMY");
    taxCodeFamily.addJoinColumn(new StringJoinColumn("NAME", "taxonomicNameFamily", "setTaxonomicNameFamily"));
    taxCodeFamily.addJoinColumn(new StringJoinColumn("LEVEL", "taxonomicLevel", "setTaxonomicLevel"));
    this.addJoinTable(taxCodeFamily);

    OuterJoinTable taxCodeOrder = null;
    taxCodeOrder = new OuterJoinTable("chm62edt_taxonomy D", "ID_TAXONOMY_LINK", "ID_TAXONOMY");
    taxCodeOrder.addJoinColumn(new StringJoinColumn("NAME", "taxonomicNameOrder", "setTaxonomicNameOrder"));
    taxCodeFamily.addJoinTable(taxCodeOrder);

    JoinTable advSearchResults = new JoinTable("eunis_combined_search_results F", "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");
    this.addJoinTable(advSearchResults);
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
    // Prepare the WHERE clause
    String filterSQL = " 1=1 ";
    filterSQL += " AND F.ID_SESSION='" + sid + "'";
    // Add GROUP BY clause for unique results
    if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
      filterSQL += " GROUP BY A.ID_SPECIES ";
    }
    // Add the ORDER BY clause to do the sorting
    if (sortCriteria.length > 0) {
      filterSQL += _prepareWhereSort();
    }
    filterSQL += " LIMIT " + offsetStart + ", " + pageSize;
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
   */
  private Long _rawCount() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT COUNT(DISTINCT A.ID_SPECIES) FROM chm62edt_species A " +
            "LEFT OUTER JOIN chm62edt_group_species B ON A.ID_GROUP_SPECIES=B.ID_GROUP_SPECIES " +
            "INNER JOIN chm62edt_taxonomy C ON A.ID_TAXONOMY=C.ID_TAXONOMY " +
            "LEFT OUTER JOIN chm62edt_taxonomy D ON C.ID_TAXONOMY_LINK=D.ID_TAXONOMY " +
            "INNER JOIN eunis_combined_search_results F ON A.ID_NATURE_OBJECT=F.ID_NATURE_OBJECT " +
            "WHERE 1=1 AND F.ID_SESSION='" + sid + "'");
    Long ret = findLong(sql.toString());
    if (null == ret) return new Long(0);
    return ret;
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
      logger.debug(e, e);   //To change body of catch statement use Options | File Templates.
    } finally {
      return filterSQL;
    }
  }
}
