package ro.finsiel.eunis.search.habitats.habitatsByReferences;


import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.jrfTables.habitats.names.NamesDomain;


/**
 * Sort criteria for habitats->habitats books.
 * @author finsiel
 */
public class ReferencesSortCriteria extends AbstractSortCriteria {

    /**
     * Do not sort.
     */
    public static final Integer SORT_NONE = new Integer(0);

    /**
     * Sort by level.
     */
    public static final Integer SORT_LEVEL = new Integer(1);

    /**
     * Sort by eunis code.
     */
    public static final Integer SORT_EUNIS_CODE = new Integer(2);

    /**
     * Sort by annex code.
     */
    public static final Integer SORT_ANNEX_CODE = new Integer(3);

    /**
     * Sort by name.
     */
    public static final Integer SORT_SCIENTIFIC_NAME = new Integer(4);

    /**
     * Sort by english name.
     */
    public static final Integer SORT_VERNACULAR_NAME = new Integer(5);

    /**
     * Ctor.
     * @param sortCriteria Sort criteria.
     * @param ascendency Ascendency.
     */
    public ReferencesSortCriteria(Integer sortCriteria, Integer ascendency) {
        setSortCriteria(sortCriteria);
        setAscendency(ascendency);
        // Initialize the mappings
        possibleSorts.put(SORT_NONE, "none"); // If none, then DO NOT SORT
        possibleSorts.put(SORT_LEVEL, "H.LEVEL");
        possibleSorts.put(SORT_EUNIS_CODE, "H.EUNIS_HABITAT_CODE ");
        possibleSorts.put(SORT_ANNEX_CODE, "H.CODE_2000 ");
        possibleSorts.put(SORT_SCIENTIFIC_NAME, "H.SCIENTIFIC_NAME");
        possibleSorts.put(SORT_VERNACULAR_NAME, "H.DESCRIPTION");
    }
}
