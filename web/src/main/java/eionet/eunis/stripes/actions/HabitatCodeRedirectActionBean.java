package eionet.eunis.stripes.actions;

import net.sourceforge.stripes.action.*;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@UrlBinding("/habitats_codeEUNIS/{code}/{type}")
public class HabitatCodeRedirectActionBean extends AbstractStripesAction {
    private String code;
    private String type;
    private String message;

    /**
     * Redirect to new advanced species search location
     */
    @DefaultHandler
    public Resolution defaultAction() {

        // search by EUNIS code
        List<Chm62edtHabitatPersist> r = null;
        if(type == null || type.equalsIgnoreCase("eunis2007")) {
            r = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE = '" + code + "' and habitat_type='EUNIS'");
        } else if (type.equalsIgnoreCase("eunis2017")){
            r = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE = '" + code + "' and habitat_type='EUNIS2017'");
        } else if (type.equalsIgnoreCase("redlist")){
            r = new Chm62edtHabitatDomain().findWhere("EEA_CODE = '" + code + "' and habitat_type='REDLIST'");
        } else if (type.equalsIgnoreCase("annex1")){
            r = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE = '" + code + "' and habitat_type='ANNEX1'");
        }

        String url = "/habitats/";

        if(r != null && r.size()>0) {
            url = "/habitats/" + r.get(0).getIdHabitat();
        } else {

            message = "Sorry, no habitat type has been found in the database with EUNIS habitat classification code '" + code + "'. Please ensure that you are using the correct habitat code.";

            getContext().getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ForwardResolution("/stripes/habitats-error.jsp");
        }

        return new RedirectResolution(url);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


