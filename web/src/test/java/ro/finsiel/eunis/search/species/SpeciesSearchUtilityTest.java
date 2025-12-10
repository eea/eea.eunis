package ro.finsiel.eunis.search.species;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import eionet.eunis.test.DbHelper;
import ro.finsiel.eunis.jrfTables.species.names.ScientificNameDomain;
import ro.finsiel.eunis.jrfTables.species.taxonomy.SpeciesGroupSpeciesPersist;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.species.names.NameBean;
import ro.finsiel.eunis.search.species.names.NamePaginator;

public class SpeciesSearchUtilityTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DbHelper.handleSetUpOperation("seed-four-species.xml");
    }

    @Test
    public void countSpeciesInGroup() {
        Long result = SpeciesSearchUtility.countUniqueSpecies(1, true);
        Long expected = new Long(1);
        assertEquals(expected, result);
    }

    @Test
    public void checkTaxanomyChildrenForOrder() {
        boolean result = SpeciesSearchUtility.IdTaxonomyHasChildren("15");
        assertEquals(true, result);
    }

    /**
     * This Family doesn't have children.
     */
    @Test
    public void checkTaxanomyChildrenForFamily() {
        boolean result = SpeciesSearchUtility.IdTaxonomyHasChildren("16");
        assertEquals(false, result);
    }

    @Test
    public void findSpeciesforFamily() {
        List result = SpeciesSearchUtility.FindSpeciesforIdTaxonomy("16", true);
        assertEquals(2, result.size());
        Object[] resultAsArray = result.toArray();
        SpeciesGroupSpeciesPersist s;

        s = (SpeciesGroupSpeciesPersist)resultAsArray[0];
        assertEquals(new Integer(9970), s.getIdSpecies());
        s = (SpeciesGroupSpeciesPersist)resultAsArray[1];
        assertEquals(new Integer(127496), s.getIdSpecies());
    }

    /**
     * Test the search engine with URLs.
     * Right now it only tests the generated SQL
     */
    @Test
    public void testScientificNameSearch() {
        String[] links = {
                "typeForm=0&showScientificName=true&showGroup=true&showOrder=true&showFamily=true&showVernacularNames=true&showValidName=true&relationOp=4&scientificName=test&searchSynonyms=true&newName=true&searchVernacular=true",
                "pageSize=10&scientificName=o&relationOp=3&showGroup=true&showOrder=true&showFamily=true&showScientificName=true&showVernacularNames=true&showValidName=true&searchSynonyms=true&searchVernacular=true&comeFromQuickSearch=true&sort=1&ascendency=0",
                "comeFromQuickSearch=true&showGroup=true&showOrder=true&showFamily=true&showScientificName=true&showVernacularNames=true&showValidName=true&showOtherInfo=true&relationOp=3&searchVernacular=true&searchSynonyms=true&sort=3&ascendency=1&scientificName=canis+lupus&Submit=Search",
                "noSoundex=true&pageSize=10&sort=3&ascendency=1&scientificName=ab&relationOp=3&typeForm=0&showGroup=true&showOrder=true&showFamily=true&showScientificName=true&showVernacularNames=true&showValidName=true&searchSynonyms=true&criteriaType=4&oper=4&typeForm=0&criteriaSearch=Abacarus&Submit=Search",
                "pageSize=10&scientificName=ab&relationOp=3&criteriaSearch=Abacarus&criteriaType=4&oper=4&typeForm=0&showGroup=true&showOrder=true&showFamily=true&showScientificName=true&showVernacularNames=true&showValidName=true&searchSynonyms=true&sort=3&ascendency=2",
                "pageSize=10&scientificName=ab&relationOp=3&criteriaSearch=Abacarus&criteriaType=4&oper=4&typeForm=0&showGroup=true&showOrder=true&showFamily=true&showScientificName=true&showVernacularNames=true&showValidName=true&searchSynonyms=true&sort=5&ascendency=1",
                "typeForm=0&showScientificName=true&searchVernacular=false&sort=3&ascendency=1&showGroup=true&showOrder=true&showFamily=true&showValidName=true&showVernacularNames=true&relationOp=3&scientificName=ab&searchSynonyms=true&submit=Search"
        };

        for(String url : links) {
            try {
                runSearch(url);
            } catch (Exception e) {
                System.out.println(" Search link: " + url);
                logger.debug(e, e); 
                assertTrue(false);
            }
        }

    }

    /**
     * Runs a ScientificNameDomain search by using the URL
     * @param url The URL of the page
     * @return The first page of the results
     * @throws Exception
     */
    private List runSearch(String url) throws Exception {
        NameBean formBean = new NameBean();
        BeanUtils.populate(formBean, populateMap(url));

        boolean searchSynonyms = Utilities.checkedStringToBoolean(formBean.getSearchSynonyms(), false);

        NamePaginator paginator = new NamePaginator(new ScientificNameDomain(formBean.toSearchCriteria(),
                formBean.toSortCriteria(),
                searchSynonyms,
                false,
                formBean.getSearchVernacular()));

        paginator.setSortCriteria(formBean.toSortCriteria());
        paginator.setPageSize(Utilities.checkedStringToInt(formBean.getPageSize(), 10));

        return paginator.getPage(0);
    }

    /**
     * Populate a map from the URL parameters
     * @param url
     * @return
     */
    private Map<String, String> populateMap(String url) {
        Map<String, String> m = new HashMap<String, String>();
        if(url.contains("?")) {
            url = url.substring(url.indexOf("?") + 1);
        }

        String[] params = url.split("&");
        for(String param : params) {
            if(param.contains("=")){
                String[] p = param.split("=");
                m.put(p[0], p[1]);
            } else {
                System.out.println("Link parse error, \"" + param + "\" cannot be parsed");
            }
        }

        return m;
    }

    /* getThreatNational is defective - not used in production? */
    /*
    @Test
    public void getSalmonThreatsForEurope() {
        List result = SpeciesSearchUtility.getThreatNational(9970, "Europe");
        assertEquals(1, result.size());
        for (Object o : result) {
            System.out.println(o.getClass().getName());
        }
    }
    */

}
