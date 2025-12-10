package eionet.eunis.stripes.actions;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import ro.finsiel.eunis.dataimport.parsers.RDFHandler;
import ro.finsiel.eunis.utilities.EunisUtil;
import ro.finsiel.eunis.utilities.SQLUtilities;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.hp.hpl.jena.rdf.arp.ARP;

import eionet.eunis.util.Constants;


/**
 * Action bean to handle RDF export.
 *
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
@UrlBinding("/dataimport/importpagelinks")
public class LinksImporterActionBean extends AbstractStripesAction {

    private FileBean file;
    private boolean hasGBIF = false;
    private boolean hasBiolab = false;
    private boolean hasBbc = false;
    private boolean hasWikipedia = false;
    private boolean hasWikispecies = false;
    private boolean hasBugGuide = false;
    private boolean hasNCBI = false;
    private boolean hasITIS = false;

    private boolean delete = false;
    private String matching;

    @DefaultHandler
    public Resolution defaultAction() {
        String forwardPage = "/stripes/linkimporter.jsp";

        setMetaDescription("Import Links from GeoSpecies");
        return new ForwardResolution(forwardPage);
    }

    public Resolution importLinks() {

        String forwardPage = "/stripes/linkimporter.jsp";

        setMetaDescription("Import Links from GeoSpecies");
        if (getContext().getSessionManager().isAuthenticated()
                && getContext().getSessionManager().isImportExportData_RIGHT()) {
            Connection con = null;
            InputStream inputStream = null;

            try {
                SQLUtilities sqlUtil = getContext().getSqlUtilities();

                con = sqlUtil.getConnection();
                RDFHandler rdfHandler = new RDFHandler(con);

                rdfHandler.setHasGBIF(hasGBIF);
                rdfHandler.setHasBiolab(hasBiolab);
                rdfHandler.setHasBbc(hasBbc);
                rdfHandler.setHasWikipedia(hasWikipedia);
                rdfHandler.setHasWikispecies(hasWikispecies);
                rdfHandler.setHasBugGuide(hasBugGuide);
                rdfHandler.setHasNCBI(hasNCBI);
                rdfHandler.setHasITIS(hasITIS);
                if (matching == null || matching.length() == 0) {
                    matching = "sameSynonym";
                }
                rdfHandler.setMatching(matching);
                if (delete) {
                    rdfHandler.deleteOldRecords();
                }

                if (file != null) {
                    inputStream = file.getInputStream();
                }

                ARP arp = new ARP();

                arp.getHandlers().setStatementHandler(rdfHandler);
                arp.getHandlers().setErrorHandler(rdfHandler);
                arp.load(inputStream);
                rdfHandler.endOfFile();

                List<String> errors = rdfHandler.getErrors();

                if (errors != null && errors.size() > 0) {
                    for (Iterator<String> it = errors.iterator(); it.hasNext();) {
                        String error = EunisUtil.replaceTagsExport(
                                EunisUtil.replaceBrackets(it.next()));

                        handleEunisException(error, Constants.SEVERITY_WARNING);
                    }
                } else {
                    showMessage("Successfully imported!");
                }

            } catch (Exception e) {
                logger.debug(e, e); 
            } finally {
                if (file != null) {
                    try {
                        file.delete();
                    } catch (IOException ie) {
                        // ie.printStackTrace(); // preparing for decommissioning
                    }
                }
                // close input stream
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        logger.debug(e, e); 
                    }
                }
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

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public boolean isHasGBIF() {
        return hasGBIF;
    }

    public void setHasGBIF(boolean hasGBIF) {
        this.hasGBIF = hasGBIF;
    }

    public boolean isHasBiolab() {
        return hasBiolab;
    }

    public void setHasBiolab(boolean hasBiolab) {
        this.hasBiolab = hasBiolab;
    }

    public boolean isHasBbc() {
        return hasBbc;
    }

    public void setHasBbc(boolean hasBbc) {
        this.hasBbc = hasBbc;
    }

    public boolean isHasWikipedia() {
        return hasWikipedia;
    }

    public void setHasWikipedia(boolean hasWikipedia) {
        this.hasWikipedia = hasWikipedia;
    }

    public boolean isHasWikispecies() {
        return hasWikispecies;
    }

    public void setHasWikispecies(boolean hasWikispecies) {
        this.hasWikispecies = hasWikispecies;
    }

    public boolean isHasBugGuide() {
        return hasBugGuide;
    }

    public void setHasBugGuide(boolean hasBugGuide) {
        this.hasBugGuide = hasBugGuide;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isHasNCBI() {
        return hasNCBI;
    }

    public void setHasNCBI(boolean hasNCBI) {
        this.hasNCBI = hasNCBI;
    }

    public boolean isHasITIS() {
        return hasITIS;
    }

    public void setHasITIS(boolean hasITIS) {
        this.hasITIS = hasITIS;
    }

    public String getMatching() {
        return matching;
    }

    public void setMatching(String matching) {
        this.matching = matching;
    }

}
