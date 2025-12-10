package eionet.eunis.stripes.actions;


import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import eionet.eunis.dao.DaoFactory;
import eionet.eunis.util.Constants;


/**
 * Action bean to handle RDF export.
 *
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
@UrlBinding("/dataimport/updatecountrysites")
public class UpdateCountrySitesFactsheetActionBean extends AbstractStripesAction {

    @DefaultHandler
    public Resolution defaultAction() {
        String forwardPage = "/stripes/updatecountrysites.jsp";

        setMetaDescription("Update country sites factsheet");
        return new ForwardResolution(forwardPage);
    }

    public Resolution update() {

        String forwardPage = "/stripes/updatecountrysites.jsp";

        setMetaDescription("Update country sites factsheet");
        if (getContext().getSessionManager().isAuthenticated()
                && getContext().getSessionManager().isImportExportData_RIGHT()) {
            try {
                DaoFactory.getDaoFactory().getSitesDao().updateCountrySitesFactsheet();
                showMessage("Successfully updated!");
            } catch (Exception e) {
                logger.debug(e, e); 
                handleEunisException(e.getMessage(), Constants.SEVERITY_ERROR);
            }
        } else {
            handleEunisException(
                    "You are not logged in or you do not have enough privileges to view this page!",
                    Constants.SEVERITY_WARNING);
        }
        return new ForwardResolution(forwardPage);
    }
}
