/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        jaanus
 */

package eionet.eunis.stripes.actions;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryDomain;

/**
 * An action bean that lists all the countries found in chm62edt_country table.
 *
 * @author jaanus
 */
@UrlBinding("/countries")
public class CountriesActionBean extends AbstractStripesAction{

    /** */
    private static final String RESULT_JSP = "/stripes/countries.jsp";

    /** */
    @SuppressWarnings("rawtypes")
    private List resultList;

    /**
     *
     * @return
     */
    @DefaultHandler
    public Resolution list(){

        try {
            resultList = new Chm62edtCountryDomain().findWhereOrderBy(null, "AREA_NAME_EN");
        } catch (Exception e) {
            logger.debug(e, e);
        }

        return new ForwardResolution(RESULT_JSP);
    }

    /**
     * @return the resultList
     */
    @SuppressWarnings("rawtypes")
    public List getResultList() {
        return resultList;
    }

    /**
     *
     * @return
     */
    public String getFactsheetBeanClassName(){
        return CountryFactsheetActionBean.class.getName();
    }
}
