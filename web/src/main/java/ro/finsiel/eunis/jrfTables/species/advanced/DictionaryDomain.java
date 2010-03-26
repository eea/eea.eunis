/**
 * User: root
 * Date: May 21, 2003
 * Time: 9:37:19 AM
 */
package ro.finsiel.eunis.jrfTables.species.advanced;

import ro.finsiel.eunis.search.Paginable;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.advanced.AdvancedSortCriteria;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;

import java.util.List;

public class DictionaryDomain extends AbstractDomain implements Paginable {
  /** Criterias applied for sorting */
  private AbstractSortCriteria[] sortCriteria = new AbstractSortCriteria[0];
  /** Cache the results of a count to avoid overhead queries for counting */
  private Long _resultCount = new Long(-1);
  /** Session ID to do proper search on the EUNIS_ADVANCED_SEARCH_RESULTS table */
  private String IdSession = "";

  private boolean showInvalidatedSpecies = false;

  /**
   * This is the default constructor and is used only when you want to call the find* methods for this object, for
   * example.
   */
  public DictionaryDomain() {
    this.sortCriteria = null;
  }

  /**
   */
  public DictionaryDomain(AbstractSortCriteria[] sortCriteria, boolean showInvalidatedSpecies) {
    this.sortCriteria = sortCriteria;

  }

  /**
   * This is the proper constructor for the class, as it uses ID session for the correct join
   * @param IdSession
   */
  public DictionaryDomain(String IdSession) {
    this.sortCriteria = null;
    this.IdSession = IdSession;
  }

  /****/
  public PersistentObject newPersistentObject() {
    return new DictionaryPersist();
  }

  /****/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("CHM62EDT_SPECIES");
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
    this.addColumnSpec(new StringColumnSpec("TAXONOMIC_SPECIES_CODE", "getTaxonomicSpeciesCode", "setTaxonomicSpeciesCode", DEFAULT_TO_EMPTY_STRING, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("SPECIES_MAP", "getSpeciesMap", "setSpeciesMap", DEFAULT_TO_NULL));
    this.addColumnSpec(new IntegerColumnSpec("ID_GROUP_SPECIES", "getIdGroupspecies", "setIdGroupspecies", DEFAULT_TO_ZERO, REQUIRED));
    this.addColumnSpec(new StringColumnSpec("ID_TAXONOMY", "getIdTaxcode", "setIdTaxcode", DEFAULT_TO_NULL));

    // Joined tables
    OuterJoinTable groupSpecies;
    groupSpecies = new OuterJoinTable("CHM62EDT_GROUP_SPECIES B", "ID_GROUP_SPECIES", "ID_GROUP_SPECIES");
    groupSpecies.addJoinColumn(new StringJoinColumn("COMMON_NAME", "commonName", "setCommonName"));
    this.addJoinTable(groupSpecies);

    JoinTable taxCodeFamily;
    taxCodeFamily = new JoinTable("CHM62EDT_TAXONOMY C", "ID_TAXONOMY", "ID_TAXONOMY");
    taxCodeFamily.addJoinColumn(new StringJoinColumn("NAME", "taxonomicNameFamily", "setTaxonomicNameFamily"));
    taxCodeFamily.addJoinColumn(new StringJoinColumn("LEVEL", "taxonomicLevel", "setTaxonomicLevel"));
    this.addJoinTable(taxCodeFamily);

    OuterJoinTable taxCodeOrder;
    taxCodeOrder = new OuterJoinTable("CHM62EDT_TAXONOMY D", "ID_TAXONOMY_LINK", "ID_TAXONOMY");
    taxCodeOrder.addJoinColumn(new StringJoinColumn("NAME", "taxonomicNameOrder", "setTaxonomicNameOrder"));
    taxCodeFamily.addJoinTable(taxCodeOrder);

    JoinTable natureObject;
    natureObject = new JoinTable("CHM62EDT_NATURE_OBJECT M", "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");
    natureObject.addJoinColumn(new IntegerJoinColumn("ID_DC", "idDc", "setIdDc"));
    this.addJoinTable(natureObject);

    JoinTable dcSource;
    dcSource = new JoinTable("DC_SOURCE N", "ID_DC", "ID_DC");
    dcSource.addJoinColumn(new StringJoinColumn("SOURCE", "source", "setSource"));
    dcSource.addJoinColumn(new StringJoinColumn("EDITOR", "editor", "setEditor"));
    dcSource.addJoinColumn(new StringJoinColumn("BOOK_TITLE", "bookTitle", "setBookTitle"));
    natureObject.addJoinTable(dcSource);

    JoinTable advSearchResults = new JoinTable("EUNIS_ADVANCED_SEARCH_RESULTS", "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");
    this.addJoinTable(advSearchResults);
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
    this.sortCriteria = sortCriteria;
    //String filterSQL = _prepareWhereSearch();
    String filterSQL = " 1=1 AND EUNIS_ADVANCED_SEARCH_RESULTS.ID_SESSION = '" + IdSession + "'";
    // Add the ORDER BY clause to do the sorting
    if (sortCriteria.length > 0) {
      filterSQL += _prepareWhereSort();
    }
    // Add the LIMIT clause to return only the proper results
    if (pageSize != 0) { // Doesn't make sense for pageSize = 0.
      filterSQL += " LIMIT " + offsetStart + ", " + pageSize;
    }
    List tempList = this.findWhere(filterSQL);
    _resultCount = new Long(-1);// After each query, reset the _resultCount, so countResults do correct numbering.
    return tempList;
  }

  /**
   * This method is used to count the total list of results from a query. It is used to find all for use in pagination.
   * Having the total number of results and the results displayed per page, the you could find the number of pages i.e.
   * @return The total number of results
   */
  public Long countResults() throws CriteriaMissingException {
    if (-1 == _resultCount.longValue()) {
      _resultCount = _rawCount();
    }
    return _resultCount;
  }

  /**
   * This method does the raw counting (meaning that will do a DB query for retrieving results count). You should check
   * in your code that this method is called (in ideal way) only once and results are cached. This is what
   * countResults() method does in this class
   * @see ro.finsiel.eunis.jrfTables.species.country.RegionDomain#countResults
   */
  private Long _rawCount() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT COUNT(*) FROM CHM62EDT_SPECIES A " +
            " LEFT OUTER JOIN CHM62EDT_GROUP_SPECIES B ON A.ID_GROUP_SPECIES=B.ID_GROUP_SPECIES " +
            " INNER JOIN CHM62EDT_TAXONOMY C ON A.ID_TAXONOMY=C.ID_TAXONOMY " +
            " LEFT OUTER JOIN CHM62EDT_TAXONOMY D ON C.ID_TAXONOMY_LINK=D.ID_TAXONOMY " +
            " INNER JOIN EUNIS_ADVANCED_SEARCH_RESULTS E ON A.ID_NATURE_OBJECT=E.ID_NATURE_OBJECT " +
            " WHERE 1=1 AND E.ID_SESSION='" + IdSession + "' ");
    Long ret = findLong(sql.toString());
    if (null == ret) return new Long(0);
    return ret;
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
      if (sortCriteria.length > 0) {
        int i = 0;
        do {
          if (i > 0) filterSQL.append(", ");
          AdvancedSortCriteria criteria = (AdvancedSortCriteria) sortCriteria[i]; // Notice the upcast here
          if (!criteria.getCriteriaAsString().equals("none")) {// Do not add if criteria is sort to NOT SORT
            if (!criteria.getAscendencyAsString().equals("none")) { // Don't add if ascendency is set to none, nasty hacks
              filterSQL.append(criteria.toSQL());
              useSort = true;
            }
          }
          i++;
        } while (i < sortCriteria.length);
      }
      if (useSort) {
        // If a sort criteria was indeed used, then insert ORDER BY clause at the start of the string
        filterSQL.insert(0, " ORDER BY ");
      }
    } catch (InitializationException e) {
      e.printStackTrace();
    }
    return filterSQL;
  }
}