package eionet.eunis.stripes.actions;

import net.sourceforge.stripes.action.*;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesPersist;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@UrlBinding("/species_code2000/{code2000}")
public class SpeciesN2KRedirectActionBean extends AbstractStripesAction {
    private String code2000;
    private String message;

    /**
     * Redirect to new advanced species search location
     */
    @DefaultHandler
    public Resolution defaultAction() {

        // search by code 2000
        List<Chm62edtSpeciesPersist> r = new Chm62edtSpeciesDomain().findWhere("CODE_2000 = '" + code2000+ "'");

        String url = "/species/";

        if(r != null && r.size()>0) {
            url = "/species/" + r.get(0).getIdSpecies();
        } else {

            message = "Sorry, no species has been found in the database. Please ensure that you are using the correct species code.";

            getContext().getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ForwardResolution("/stripes/habitats-error.jsp");
        }

        return new RedirectResolution(url);
    }

    public String getCode2000() {
        return code2000;
    }

    public void setCode2000(String code2000) {
        this.code2000 = code2000;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


