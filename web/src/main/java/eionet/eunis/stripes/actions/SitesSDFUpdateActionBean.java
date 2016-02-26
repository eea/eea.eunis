package eionet.eunis.stripes.actions;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.dataimport.parsers.CallbackSAXParser;
import ro.finsiel.eunis.dataimport.parsers.Natura2000ParserCallbackV2;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Tries to update the given site data from the SDFs at
 * http://natura2000.eea.europa.eu/Natura2000/SDFXML.aspx?site=ES0000024&release=4&form=Clean
 */
@UrlBinding("/updatesite/{idsite}")
public class SitesSDFUpdateActionBean extends AbstractStripesAction  {
    private String idsite = "";

    public Resolution defaultAction() {
        Date d1 = new Date();
        // downloads the data and runs the import
        try {
            URL url = new URL("http://natura2000.eea.europa.eu/Natura2000/SDFXML.aspx?site=" + idsite + "&release=5&form=Clean");

            System.out.println(d1 + " Trying to update SDF for site " + idsite + " from URL " + url);


            InputStream inputStream = url.openStream();

            SQLUtilities sqlUtil = getContext().getSqlUtilities();

            try{
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                Natura2000ParserCallbackV2 callback = new Natura2000ParserCallbackV2(sqlUtil);
                CallbackSAXParser parser = new CallbackSAXParser(callback);
                parser.setDebug(false);
                List<Exception> errors = parser.execute(bis);
                for(Exception e : errors) e.printStackTrace();
                bis.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long diff = (new Date().getTime() - d1.getTime())/1000;
        System.out.println(new Date() + " Update finished for " + idsite + " in " + diff + " seconds");
        // redirect to the page
        return new RedirectResolution("/sites/" + idsite);
    }

    public String getIdsite() {
        return idsite;
    }

    public void setIdsite(String idsite) {
        this.idsite = idsite;
    }
}
