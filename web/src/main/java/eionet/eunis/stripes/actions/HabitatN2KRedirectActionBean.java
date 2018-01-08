package eionet.eunis.stripes.actions;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import org.apache.commons.lang.StringUtils;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist;

import java.util.List;

@UrlBinding("/habitats_code2000/{code2000}")
public class HabitatN2KRedirectActionBean extends AbstractStripesAction {
    private String code2000;

    /**
     * Redirect to new advanced species search location
     */
    @DefaultHandler
    public Resolution defaultAction() {

        // search by code 2000
        List<Chm62edtHabitatPersist> r = new Chm62edtHabitatDomain().findWhere("CODE_2000 = '" + code2000+ "'");

        String url = "/habitats/";

        if(r != null && r.size()>0) {
            url = "/habitats/" + r.get(0).getIdHabitat();
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


