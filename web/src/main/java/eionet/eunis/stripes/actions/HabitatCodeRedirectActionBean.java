package eionet.eunis.stripes.actions;

import net.sourceforge.stripes.action.*;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@UrlBinding("/habitats_codeEUNIS/{code}")
public class HabitatCodeRedirectActionBean extends AbstractStripesAction {
    private String code;
    private String message;

    /**
     * Redirect to new advanced species search location
     */
    @DefaultHandler
    public Resolution defaultAction() {

        // search by EUNIS code
        List<Chm62edtHabitatPersist> r = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE = '" + code + "'");

        String url = "/habitats/";

        if(r != null && r.size()>0) {
            url = "/habitats/" + r.get(0).getIdHabitat();
        } else {
            message = "Sorry, no habitat type has been found in the database with Habitat EUNIS code '" + code + "'. Please ensure that you are using the correct code (the 'EUNIS habitat type code' in the factsheet's quick facts).";

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
}


