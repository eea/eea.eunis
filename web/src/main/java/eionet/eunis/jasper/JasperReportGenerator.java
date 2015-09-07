package eionet.eunis.jasper;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterContext;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.export.HtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;

/**
 * Created by miahi on 9/7/2015.
 */
public class JasperReportGenerator {

    public String generate(String endpoint, String jrxmlFileName) {
        String page = "";
        try {
            InputStream is = JasperReportGenerator.class.getResourceAsStream(jrxmlFileName);

            DefaultJasperReportsContext.getInstance().setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "SPARQL", "eionet.jasperreports.cds.SPARQLQueryExecuterFactory");

            JasperReport jr = JasperCompileManager.compileReport(is);
            is.close();

            jr.setProperty("endpoint", endpoint);
            jr.setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "SPARQL", "eionet.jasperreports.cds.SPARQLQueryExecuterFactory");

            HashMap<String, Object> props = new HashMap<>();
            props.put("endpoint", endpoint);
            props.put(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "SPARQL", "eionet.jasperreports.cds.SPARQLQueryExecuterFactory");

            // Generate jasper print
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, props);

            HtmlExporter exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
            SimpleHtmlExporterConfiguration hec = new SimpleHtmlExporterConfiguration();
            hec.setHtmlFooter("");
            hec.setHtmlHeader("");

            exporter.setConfiguration(hec);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleHtmlExporterOutput(baos));

            exporter.exportReport();

            page = new String(baos.toByteArray());

            baos.close();

        } catch (Exception e) {
            System.out.print("Exception" + e);
            // todo log message + handle
        }
        return page;
    }
}
