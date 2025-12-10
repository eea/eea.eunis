package ro.finsiel.eunis.dataimport.parsers;

import eionet.eunis.test.DbHelper;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * CDDA Sites import parser test
 */
public class CddaSitesImportParserTest {

    private static SQLUtilities sqlUtilities;

    @BeforeClass
    public static void setUpBeforeClass(){
        sqlUtilities = DbHelper.getSqlUtilities();
    }

    /**
     * Loads a Site from a file and checks the loaded data
     */
    @Test
    public void loadSitesTest(){
        try {
            String result = sqlUtilities.ExecuteSQL("select count(1) from chm62edt_nature_object where ORIGINAL_CODE='184031'");
            Assert.assertEquals("0", result);

            CddaSitesImportParser parser = new CddaSitesImportParser(sqlUtilities);
            InputStream inputStream = DbHelper.class.getClassLoader().getResourceAsStream("cdda_site_184031.xml");
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            Map<String, String> sites = parser.execute(bis);
            bis.close();

            for(String s: sites.keySet())     {
                System.out.println("Imported Site " + s);
            }

            result = sqlUtilities.ExecuteSQL("select count(1) from chm62edt_nature_object where ORIGINAL_CODE='184031'");
            Assert.assertEquals("1", result);

            result = sqlUtilities.ExecuteSQL("select id_nature_object from chm62edt_nature_object where ORIGINAL_CODE='184031'");
            result = sqlUtilities.ExecuteSQL("select MARINE_PERCENT from chm62edt_sites where id_nature_object=" + result);
            Assert.assertEquals("20.10", result);

        } catch(Exception e){
            logger.debug(e, e);
            Assert.assertTrue(false);
        }

    }
}
