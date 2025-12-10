package eionet.eunis.stripes.actions;


import java.io.InputStream;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.dataimport.parsers.ClassCodesImportParser;
import ro.finsiel.eunis.dataimport.parsers.HabitatClassCodeImportParser;
import ro.finsiel.eunis.dataimport.parsers.HabitatDescImportParser;
import ro.finsiel.eunis.dataimport.parsers.HabitatImportParser;
import ro.finsiel.eunis.dataimport.parsers.ReferencesImportParser;
import ro.finsiel.eunis.utilities.SQLUtilities;
import eionet.eunis.util.Constants;


/**
 * Action bean to handle RDF export.
 *
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
@UrlBinding("/dataimport/importhabitats")
public class HabitatsImporterActionBean extends AbstractStripesAction {

    private FileBean fileHabitats;
    private FileBean fileHabitatsDesc;
    private FileBean fileClassCodes;
    private FileBean fileHabitatClassCodes;
    private FileBean fileReferences;

    @DefaultHandler
    public Resolution defaultAction() {
        String forwardPage = "/stripes/habitatsimporter.jsp";

        setMetaDescription("Import Habitats");
        return new ForwardResolution(forwardPage);
    }

    public Resolution importHabitats() {

        String forwardPage = "/stripes/habitatsimporter.jsp";

        setMetaDescription("Import Habitats");
        if (getContext().getSessionManager().isAuthenticated() && getContext().getSessionManager().isImportExportData_RIGHT()) {
            InputStream inputStreamHabitats = null;
            InputStream inputStreamHabitatsDesc = null;
            InputStream inputStreamClassCodes = null;
            InputStream inputStreamHabitatClassCodes = null;
            InputStream inputStreamReferences = null;

            try {

                SQLUtilities sqlUtil = getContext().getSqlUtilities();

                if (fileReferences != null) {
                    inputStreamReferences = fileReferences.getInputStream();

                    ReferencesImportParser parser = new ReferencesImportParser(sqlUtil);

                    parser.execute(inputStreamReferences);
                    fileReferences.delete();
                    if (inputStreamReferences != null) {
                        inputStreamReferences.close();
                    }
                }

                String classif = null;

                if (fileClassCodes != null) {
                    inputStreamClassCodes = fileClassCodes.getInputStream();

                    ClassCodesImportParser parser = new ClassCodesImportParser(sqlUtil);

                    classif = parser.execute(inputStreamClassCodes);
                    fileClassCodes.delete();
                    if (inputStreamClassCodes != null) {
                        inputStreamClassCodes.close();
                    }
                }

                if (fileHabitats != null) {
                    inputStreamHabitats = fileHabitats.getInputStream();

                    HabitatImportParser parser = new HabitatImportParser(sqlUtil, classif);

                    parser.execute(inputStreamHabitats);
                    fileHabitats.delete();
                    if (inputStreamHabitats != null) {
                        inputStreamHabitats.close();
                    }
                }

                if (fileHabitatsDesc != null) {
                    inputStreamHabitatsDesc = fileHabitatsDesc.getInputStream();

                    HabitatDescImportParser parser = new HabitatDescImportParser(sqlUtil);

                    parser.execute(inputStreamHabitatsDesc);
                    fileHabitatsDesc.delete();
                    if (inputStreamHabitatsDesc != null) {
                        inputStreamHabitatsDesc.close();
                    }
                }

                if (fileHabitatClassCodes != null) {
                    inputStreamHabitatClassCodes = fileHabitatClassCodes.getInputStream();

                    HabitatClassCodeImportParser parser = new HabitatClassCodeImportParser(sqlUtil);

                    parser.execute(inputStreamHabitatClassCodes);
                    fileHabitatClassCodes.delete();
                    if (inputStreamHabitatClassCodes != null) {
                        inputStreamHabitatClassCodes.close();
                    }
                }

                showMessage("Successfully imported!");

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

    public FileBean getFileHabitats() {
        return fileHabitats;
    }

    public void setFileHabitats(FileBean fileHabitats) {
        this.fileHabitats = fileHabitats;
    }

    public FileBean getFileHabitatsDesc() {
        return fileHabitatsDesc;
    }

    public void setFileHabitatsDesc(FileBean fileHabitatsDesc) {
        this.fileHabitatsDesc = fileHabitatsDesc;
    }

    public FileBean getFileClassCodes() {
        return fileClassCodes;
    }

    public void setFileClassCodes(FileBean fileClassCodes) {
        this.fileClassCodes = fileClassCodes;
    }

    public FileBean getFileHabitatClassCodes() {
        return fileHabitatClassCodes;
    }

    public void setFileHabitatClassCodes(FileBean fileHabitatClassCodes) {
        this.fileHabitatClassCodes = fileHabitatClassCodes;
    }

    public FileBean getFileReferences() {
        return fileReferences;
    }

    public void setFileReferences(FileBean fileReferences) {
        this.fileReferences = fileReferences;
    }

}
