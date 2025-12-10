package ro.finsiel.eunis.urls;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.utilities.SQLUtilities;


public class Urls extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Urls.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            SQLUtilities sql = new SQLUtilities();

            StringBuilder s = new StringBuilder();

            s.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<rdf:RDF").append(" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"").append(" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"").append(" xmlns:seis=\"http://www.eionet.europa.eu/rdf/seis/\"").append(
                    ">");

            List<String> urls = sql.getUrls();

            for (String url : urls) {
                s.append("<seis:Resource rdf:about=\"").append(url).append(
                        "\"/>");
            }

            s.append("</rdf:RDF>");

            response.setContentType("application/rdf+xml;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=urls.rdf");

            response.getWriter().write(s.toString());

        } catch (Exception e) {
            logger.debug(e, e);
        }

    }
}
