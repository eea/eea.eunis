package eionet.eunis.stripes.actions;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.dataimport.parsers.CddaSitesImportParser;
import ro.finsiel.eunis.dataimport.parsers.DesignationsImportParser;
import ro.finsiel.eunis.utilities.SQLUtilities;
import eionet.eunis.dao.DaoFactory;
import eionet.eunis.util.Constants;


/**
 * Action bean to handle RDF export.
 *
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
@UrlBinding("/dataimport/importcdda")
public class CDDAImporterActionBean extends AbstractStripesAction {

    private FileBean fileDesignations;
    private FileBean fileSites;
    private boolean updateCountrySitesFactsheet = false;

    @DefaultHandler
    public Resolution defaultAction() {
        String forwardPage = "/stripes/cddaimporter.jsp";

        setMetaDescription("Import National CDDA Sites and Designations");
        return new ForwardResolution(forwardPage);
    }

    public Resolution importCdda() {

        String forwardPage = "/stripes/cddaimporter.jsp";

        setMetaDescription("Import National CDDA Sites and Designations");
        if (getContext().getSessionManager().isAuthenticated()
                && getContext().getSessionManager().isImportExportData_RIGHT()) {
            Connection con = null;
            InputStream inputStreamDesignations = null;
            InputStream inputStreamSites = null;

            try {

                SQLUtilities sqlUtil = getContext().getSqlUtilities();

                con = sqlUtil.getConnection();

                if (fileDesignations != null) {
                    inputStreamDesignations = fileDesignations.getInputStream();

                    DesignationsImportParser parser = new DesignationsImportParser(
                            sqlUtil);

                    parser.execute(inputStreamDesignations);
                    fileDesignations.delete();
                    if (inputStreamDesignations != null) {
                        inputStreamDesignations.close();
                    }
                }

                if (fileSites != null) {
                    inputStreamSites = fileSites.getInputStream();

                    CddaSitesImportParser parser = new CddaSitesImportParser(
                            sqlUtil);
                    Map<String, String> sites = parser.execute(inputStreamSites);

                    fileSites.delete();
                    if (inputStreamSites != null) {
                        inputStreamSites.close();
                    }

                    DaoFactory.getDaoFactory().getSitesDao().deleteSitesCdda(
                            sites);
                    DaoFactory.getDaoFactory().getSitesDao().updateDesignationsTable();
                }

                if (updateCountrySitesFactsheet) {
                    DaoFactory.getDaoFactory().getSitesDao().updateCountrySitesFactsheet();
                }

                showMessage("Successfully imported!");

            } catch (Exception e) {
                logger.debug(e, e);
                handleEunisException(e.getMessage(), Constants.SEVERITY_ERROR);
            } finally {
                // close connection
                SQLUtilities.closeAll(con, null, null);
            }
        } else {
            handleEunisException(
                    "You are not logged in or you do not have enough privileges to view this page!",
                    Constants.SEVERITY_WARNING);
        }
        return new ForwardResolution(forwardPage);
    }

    public boolean isUpdateCountrySitesFactsheet() {
        return updateCountrySitesFactsheet;
    }

    public void setUpdateCountrySitesFactsheet(boolean updateCountrySitesFactsheet) {
        this.updateCountrySitesFactsheet = updateCountrySitesFactsheet;
    }

    public FileBean getFileDesignations() {
        return fileDesignations;
    }

    public void setFileDesignations(FileBean fileDesignations) {
        this.fileDesignations = fileDesignations;
    }

    public FileBean getFileSites() {
        return fileSites;
    }

    public void setFileSites(FileBean fileSites) {
        this.fileSites = fileSites;
    }

}
