package ro.finsiel.eunis.search.habitats.sites;


import eionet.eunis.util.Constants;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.jrfTables.habitats.sites.HabitatsSitesDomain;

import java.util.*;


/**
 * Helper class used in habitats->sites search.
 * Used to generate list of values displayed in popup.
 * @author finsiel
 */
public class SitesWrapper {
    private List results;
    private Integer count = new Integer(-1);

    /**
     * Set the criteria for searching.
     * @param dom SQL (which this class ads the WHERE criteria).
     * @param expandAll Expand all results.
     * @param database Database to search.
     */
    public void setResults(String dom, boolean expandAll, Integer database) {
        StringBuffer sql = new StringBuffer(dom);

        sql.append(" AND ");
        if (0 != database.compareTo(HabitatsSitesDomain.SEARCH_BOTH)) {
            if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_EUNIS)) {
                sql.append("  C.ID_HABITAT >=1 and C.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'  ");
            }
            if (0 == database.compareTo(HabitatsSitesDomain.SEARCH_ANNEX_I)) {
                sql.append(" C.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  ");
            }
        } else {
            sql.append("  C.ID_HABITAT <>'-1' and C.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  ");
        }

        sql.append(" GROUP BY H.NAME");
        if (!expandAll) {
            sql.append(" LIMIT 0, " + Utilities.MAX_POPUP_RESULTS);
        }
        List l1 = new HabitatsSitesDomain().findWhere(sql.toString());

        if (l1 != null && l1.size() > 0) {
            results = l1;
            count = new Integer(l1.size());
        }
    }

    /**
     * Getter for results property. (List of values).
     * @return results. List of HabitatsSitesPersist objects.
     */
    public List getResults() {
        return results;
    }

    /**
     * Getter for count property.
     * @return count. (Number of results).
     */
    public Integer getCount() {
        return count;
    }
}
