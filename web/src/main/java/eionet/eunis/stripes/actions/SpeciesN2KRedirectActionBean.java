package eionet.eunis.stripes.actions;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesPersist;

import java.util.List;

@UrlBinding("/species_code2000/{code2000}")
public class SpeciesN2KRedirectActionBean extends AbstractStripesAction {
    private String code2000;

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
        }

        return new RedirectResolution(url);
    }

    public String getCode2000() {
        return code2000;
    }

    public void setCode2000(String code2000) {
        this.code2000 = code2000;
    }
}


