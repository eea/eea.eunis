package eionet.eunis.stripes.actions;

import eionet.eunis.jasper.JasperReportGenerator;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.jrfTables.EunisRdfDownloadsDomain;
import ro.finsiel.eunis.jrfTables.EunisRdfDownloadsPersist;

import java.io.File;
import java.util.List;

/**
 * Action bean for the About page
 */
@UrlBinding("/about")
public class AboutActionBean extends AbstractStripesAction {
    private static final String JSP = "/stripes/about.jsp";

    @DefaultHandler
    public Resolution defaultAction(){
        return new ForwardResolution(JSP);
    }

    /**
     * Gets the RDF file list and populates the last update date from the file properties
     * @return
     */
    public List getRdfFileList(){
        List<EunisRdfDownloadsPersist> files = new EunisRdfDownloadsDomain().findOrderBy("SORT_COLUMN");
        for(EunisRdfDownloadsPersist file : files){
            // get the last update date
            File f = new File(getContext().getInitParameter("APP_HOME") + file.getFileName());
            if(f.exists()){
                file.setRecordDate(f.lastModified());
            } else {
//                System.out.println("Not found " + f.getAbsolutePath());
            }
        }
        return files;
    }
}
