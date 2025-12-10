package ro.finsiel.eunis.jrfTables;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.search.AbstractSearchCriteria;
import ro.finsiel.eunis.search.AbstractSortCriteria;


/**
 * A set of utility methods used among ro.finsiel.eunis.jfrTables.* classes.
 */
public final class DBUtilities {

    private static final Logger logger = Logger.getLogger(DBUtilities.class);

    /**
     * This helper method is used to construct the string after WHERE...based on search criterias set. In another words
     * constructs .....WHERE...>>B.ID_GEOSCOPE_LINK=XXX AND B.ID_GEOSCOPE_LINK=YYY AND B.ID_GEOSCOPE_LINK=ZZZ .....
     * @return SQL string
     * @throws ro.finsiel.eunis.exceptions.CriteriaMissingException If no search criteria search or wrong criteria is set.
     */
    public static StringBuffer prepareWhereSearch(AbstractSearchCriteria[] searchCriteria) throws CriteriaMissingException {
        StringBuffer filterSQL = new StringBuffer();

        if (null == searchCriteria || searchCriteria.length <= 0) {
            throw new CriteriaMissingException("No criteria set for searching.");
        }
        if (null != searchCriteria) {
            for (int i = 0; i < searchCriteria.length; i++) {
                if (i > 0) {
                    filterSQL.append(" AND ");
                }
                AbstractSearchCriteria aCriteria = searchCriteria[i];

                if (null != aCriteria) {
                    filterSQL.append(aCriteria.toSQL());
                }
            }
        }
        return filterSQL;
    }

    /**
     * Prepare the ORDER BY clause used to do the sorting. Basically it adds the ORDER clause with the criterias set in
     * the sortCriteria[] array.
     * @return SQL representation of the sorting.
     */
    public static StringBuffer prepareWhereSort(AbstractSortCriteria[] sortCriteria) {
        StringBuffer filterSQL = new StringBuffer();

        try {
            boolean useSort = false;

            if (sortCriteria.length > 0) {
                int i = 0;

                do {
                    if (i > 0) {
                        filterSQL.append(", ");
                    }
                    AbstractSortCriteria criteria = sortCriteria[i]; // Notice the upcast here

                    // Do not add if criteria is sort to NOT SORT
                    if (!criteria.getCriteriaAsString().equals("none")) {
                        // Don't add if ascendency is set to none, nasty hacks
                        if (!criteria.getAscendencyAsString().equals("none")) {
                            filterSQL.append(criteria.toSQL());
                            useSort = true;
                        }
                    }
                    i++;
                } while (i < sortCriteria.length);
            }
            // If a sort criteria was indeed used, then insert ORDER BY clause at the start of the string
            if (useSort) {
                filterSQL.insert(0, " ORDER BY ");
            }
        } catch (InitializationException e) {
            logger.debug(e, e);  // To change body of catch statement use Options | File Templates.
        }
        return filterSQL;
    }
}
