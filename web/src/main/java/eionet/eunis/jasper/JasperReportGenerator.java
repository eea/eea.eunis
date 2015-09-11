package eionet.eunis.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.SQLUtilities;
import ro.finsiel.eunis.utilities.TheOneConnectionPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * Gerenates a page based on a Jasper Reports file
 * Created by miahi on 9/7/2015.
 */
public class JasperReportGenerator implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(JasperReportGenerator.class);

    // page numbering starts at zero
    private int currentPage;
    private int lastPage;
    private String endpoint;
    private String fileName;
    private JasperPrint jasperPrint;
    private boolean pagination = true;
    SimpleHtmlReportConfiguration shrc;

//    /**
//     * Create a SQL report with optional pagination
//     * @param jrxmlFileName
//     * @param pagination
//     */
//    public JasperReportGenerator(String jrxmlFileName, List<String> subreports, boolean pagination){
//        this.fileName = jrxmlFileName;
//        this.pagination = pagination;
//
//        try {
//            InputStream is = JasperReportGenerator.class.getResourceAsStream(jrxmlFileName);
//
//            JasperReport jr = JasperCompileManager.compileReport(is);
//            is.close();
//
//            Connection connection = TheOneConnectionPool.getConnection();
//
//            HashMap<String, Object> parameters = new HashMap<>();
//
//            // Generate the jasper print using the SQL connection
//            jasperPrint = JasperFillManager.fillReport(jr, parameters, connection);
//
//            currentPage = 0;
//            lastPage = jasperPrint.getPages().size() - 1;
//            // refresh the first page
//            generatePage();
//
//            connection.close();
//
//        } catch (Exception e) {
//            System.out.print("Exception" + e);
//            LOGGER.error(e,e);
//        }
//    }

    /**
     * Create a SPARQL report with optional pagination
     * @param endpoint
     * @param jrxmlFileName
     * @param pagination
     */
    public JasperReportGenerator(String jrxmlFileName, String endpoint, boolean pagination) {
        this.endpoint = endpoint;
        this.fileName = jrxmlFileName;
        this.pagination = pagination;
        Connection connection = null;

        try {
            InputStream is = JasperReportGenerator.class.getResourceAsStream(jrxmlFileName);

            DefaultJasperReportsContext.getInstance().setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "SPARQL", "eionet.jasperreports.cds.SPARQLQueryExecuterFactory");
            DefaultJasperReportsContext.getInstance().setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "sparql", "eionet.jasperreports.cds.SPARQLQueryExecuterFactory");

            JasperReport jr = JasperCompileManager.compileReport(is);
            is.close();

            HashMap<String, Object> parameters = new HashMap<>();
            if(!Utilities.isEmptyString(endpoint)){
                parameters.put("endpoint", endpoint);
            }

            if(!pagination) {
                parameters.put(JRParameter.IS_IGNORE_PAGINATION, true);
            }

            connection = TheOneConnectionPool.getConnection();

            // Generate the jasper print
            jasperPrint = JasperFillManager.fillReport(jr, parameters, connection);

            currentPage = 0;
            lastPage = jasperPrint.getPages().size() - 1;
            // refresh the first page
            generatePage();


        } catch (Exception e) {
            System.out.print("Exception" + e);
            LOGGER.error(e,e);
        } finally {
            SQLUtilities.closeAll(connection, null, null);
        }
    }

    /**
     * Generates the current page as HTML into the currentPage String
     * @throws JRException
     */
    private String generatePage() throws JRException {
        HtmlExporter exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
        SimpleHtmlExporterConfiguration hec = new SimpleHtmlExporterConfiguration();
        hec.setHtmlFooter("");
        hec.setHtmlHeader("");
        hec.setBetweenPagesHtml("");


        shrc = new SimpleHtmlReportConfiguration();
        if(pagination) {
            shrc.setPageIndex(currentPage);
        }
        shrc.setIgnorePageMargins(true);
        shrc.setRemoveEmptySpaceBetweenRows(true);
        shrc.setWhitePageBackground(true);
        shrc.setWrapBreakWord(false);



        exporter.setConfiguration(shrc);

        exporter.setConfiguration(hec);

        StringBuffer sb = new StringBuffer();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleHtmlExporterOutput sheo = new SimpleHtmlExporterOutput(sb) ;
        WebHtmlResourceHandler hrh = new WebHtmlResourceHandler("/jreportimg?image={0}");

        sheo.setImageHandler(hrh);

        exporter.setExporterOutput(sheo);

        exporter.exportReport();

        return sb.toString();
    }

    /**
     * Generate the report as PDF
     * @throws JRException byte array
     */
    public byte[] generatePDF() throws JRException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        JRPdfExporter pdfExporter = new JRPdfExporter();
        SimplePdfExporterConfiguration spec = new SimplePdfExporterConfiguration();
        pdfExporter.setConfiguration(spec);
        pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

        pdfExporter.exportReport();
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warn(e,e);
        }
        return result;
    }

    /**
     * The generated report page; the page will actually be generated on request
     * @return
     */
    public String getGeneratedPage() throws JRException {
        String generatedPage = generatePage();
        return generatedPage;
    }

    /**
     * The index of the last page of the report
     * @return
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * The current page index (pages start at zero)
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the current page
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        if(currentPage<lastPage) {
            if(currentPage<0){
                this.currentPage = 0;
            } else {
                this.currentPage = currentPage;
            }
        } else {
            this.currentPage = lastPage;
        }
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }
}
