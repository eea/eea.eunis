package eionet.eunis.stripes.actions;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import eionet.eunis.stripes.EunisActionBeanContext;
import eionet.eunis.test.DbHelper;
import net.sourceforge.stripes.action.Resolution;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.finsiel.eunis.utilities.SQLUtilities;

public class SpeciesFactsheetActionBeanTest {

    private static SQLUtilities sqlUtils;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        sqlUtils = DbHelper.getSqlUtilities();
        DbHelper.handleSetUpOperation("seed-four-species.xml");
    }

//  todo: this test does not work because the test DB lacks some of the data (like: kingdom)

    /**
     * Test with a species that exists
     */
//    @Test
//    public void testExistingSpecies(){
//    try{
//        SpeciesFactsheetActionBean bean = new SpeciesFactsheetActionBean();
//        bean.setIdSpecies("78776"); //Cerchysiella laeviscuta
//
//        EunisActionBeanContext context = new EunisActionBeanContext()
//        {
//            /**
//             * Mock for unitialized servlet context
//             * @param key
//             * @return
//             */
//            @Override
//            public String getInitParameter(String key) {
//                return "";
//            }
//
//            @Override
//            public SQLUtilities getSqlUtilities() {
//                return sqlUtils;
//            }
//        }
//        ;
//
//        bean.setContext(context);
//
//        Resolution r = bean.index();
//
//        assertTrue(bean.getFactsheet().exists());
//    }
//    catch (Exception e){
//        logger.debug(e, e);
//    }
//    }

    /**
     * Test with a species that does not exist; it should not throw exceptions
     */
    @Test
    public void testNotExistingSpecies(){

        SpeciesFactsheetActionBean bean = new SpeciesFactsheetActionBean();
        bean.setIdSpecies("11320"); //non-existing species

        EunisActionBeanContext context = new EunisActionBeanContext() {
            /**
             * Mock for unitialized servlet context
             *
             * @param key
             * @return
             */
            @Override
            public String getInitParameter(String key) {
                return "";
            }

            @Override
            public SQLUtilities getSqlUtilities() {
                return sqlUtils;
            }
        };

        bean.setContext(context);

        Resolution r = bean.index();

        assertTrue(!bean.getFactsheet().exists());
    }

}
