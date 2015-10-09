package eionet.eunis.servlets;

import eionet.eunis.jasper.JasperReportGenerator;
import eionet.eunis.stripes.actions.ExternalDataGlobalActionBean;
import net.sf.jasperreports.engine.JRException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by miahi on 9/8/2015.
 */
public class JasperReportDownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // generate the PDF version and
        String query = request.getParameter("query");
        String format = request.getParameter("format");
        if(format == null){
            format = "PDF";
        }

        JasperReportGenerator jrg = (JasperReportGenerator) request.getSession().getAttribute(ExternalDataGlobalActionBean.SESSION_PREFIX +"." + query);
        if(jrg==null) {
            // todo error out
        } else {

            if(format.equalsIgnoreCase("PDF"))
                try {

                    byte[] pdf = jrg.generatePDF();

                    response.setContentType("application/pdf");
                    response.addHeader("Content-Disposition", "attachment; filename=" + query + ".pdf");

                    response.getOutputStream().write(pdf);

                } catch (JRException e) {
                    e.printStackTrace();
                }

        }
    }
}
