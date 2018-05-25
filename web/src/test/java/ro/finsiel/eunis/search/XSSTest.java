package ro.finsiel.eunis.search;

import org.junit.Test;
import ro.finsiel.eunis.search.species.names.NameBean;

import java.util.Vector;

import static junit.framework.Assert.assertEquals;

public class XSSTest {

    @Test
    public void toUrlTest() {
        NameBean nb = new NameBean();
        nb.setPageSize("10\"><svg/onload=prompt(123456789)>");
        Vector<String> classFields = new Vector<>();
        classFields.add("pageSize");
        String result = nb.toURLParam(classFields);
        assertEquals("&amp;pageSize=10&quot;&gt;&lt;svg/onload=prompt(123456789)&gt;", result);
    }

    @Test
    public void toFormTest() {
        NameBean nb = new NameBean();
        nb.setPageSize("10\"><svg/onload=prompt(123456789)>");
        Vector<String> classFields = new Vector<>();
        classFields.add("pageSize");
        String result = nb.toFORMParam(classFields);
        assertEquals("<input type=\"hidden\" name=\"pageSize\" value=\"10&amp;quot;&amp;gt;&amp;lt;svg/onload=prompt(123456789)&amp;gt;\" />", result);
    }

}
