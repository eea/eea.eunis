package eionet.eunis.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Access to FME maps with authentication token and keeping it hidden
 */
public class FMEAuthServlet extends HttpServlet {

    private String src="https://fme.discomap.eea.europa.eu/fmedatastreaming/ArcGisOnline/getStaticImage.fmw?webmap=0b2680c2bc544431a9a97119aa63d707&width=480&height=400&SiteCode=";
    private final static int timeout = 30;

    private static final Logger LOGGER = Logger.getLogger(FMEAuthServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idSite = req.getParameter("idsite");

        if(idSite == null || idSite.length()<3) {
            resp.setStatus(HttpStatus.SC_BAD_REQUEST);
            return;
        }

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();

        org.apache.http.client.HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config).build();

        HttpGet get = new HttpGet(src + idSite);
        get.setHeader("Authorization", "fmetoken token=" + Settings.getSetting("FME_MAP_TOKEN"));

        try {
            HttpResponse response = httpClient.execute(get);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                byte[] responseContent = IOUtils.toByteArray(response.getEntity().getContent());
                resp.setContentType(response.getEntity().getContentType().getValue());
                resp.getOutputStream().write(responseContent);
            } else {
                resp.setStatus(response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            resp.setStatus(HttpStatus.SC_BAD_REQUEST);
            LOGGER.warn(e, e);
        }

    }
}
