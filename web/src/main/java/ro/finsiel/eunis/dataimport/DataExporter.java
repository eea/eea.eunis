package ro.finsiel.eunis.dataimport;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import ro.finsiel.eunis.session.SessionManager;
import ro.finsiel.eunis.utilities.SQLUtilities;


public class DataExporter extends HttpServlet {

    private static List<String> errors = null;

    private static final Logger logger = Logger.getLogger(DataExporter.class);

    /**
     * Overrides public method doPost of javax.servlet.http.HttpServlet.
     * @param request Request object
     * @param response Response object.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        SessionManager sessionManager = (SessionManager) session.getAttribute(
                "SessionManager");

        if (sessionManager.isAuthenticated()
                && sessionManager.isImportExportData_RIGHT()) {
            errors = new ArrayList<String>();

            String table = request.getParameter("table");
            String addSchema = request.getParameter("schema");
            String nl = "\n";

            try {

                SQLUtilities sql = new SQLUtilities();

                sql.Init();
                String content = sql.getTableContentAsXML(table);

                StringBuilder s = new StringBuilder();

                s.append("<?xml version=\"1.0\"?>").append(nl);
                if (addSchema != null && addSchema.equals("on")) {
                    s.append("<ROWSET>").append(nl);
                } else {
                    s.append("<ROWSET xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://eunis.eea.europa.eu/schemas/").append(table).append(".xsd\">").append(
                            nl);
                }
                s.append(content);
                s.append("</ROWSET>").append(nl);

                response.setContentType("application/xml;charset=UTF-8");
                response.setHeader("Content-Disposition",
                        "attachment; filename=" + table + ".xml");
                response.getWriter().write(s.toString());

            } catch (Exception e) {
                logger.debug(e, e); 
                errors.add(e.getMessage());
            }
        } else {
            response.sendRedirect("dataimport/data-exporter.jsp");
        }
    }

}
