package ro.finsiel.eunis.dataimport;


import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import ro.finsiel.eunis.utilities.SQLUtilities;


public class Schemas extends HttpServlet {

    private static List<String> errors = null;

    private static final Logger logger = Logger.getLogger(Schemas.class);

    /**
     * Overrides public method doPost of javax.servlet.http.HttpServlet.
     * @param request Request object
     * @param response Response object.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pathInfo = request.getPathInfo();
        String schemaName = "";
        String table = "";

        if (pathInfo != null && !pathInfo.equals("")) {
            StringTokenizer st = new StringTokenizer(pathInfo, "/");

            if (st.hasMoreElements()) {
                schemaName = st.nextToken();
                if (schemaName != null && !schemaName.equals("")) {
                    int index = schemaName.indexOf(".xsd");

                    if (index != -1) {
                        table = schemaName.substring(0, index);
                    }
                }
            }
        }

        errors = new ArrayList<String>();

        String nl = "\n";

        try {

            SQLUtilities sql = new SQLUtilities();

            sql.Init();
            List<ColumnDTO> columns = sql.getTableInfoList(table);

            StringBuilder s = new StringBuilder();

            s.append("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">").append(
                    nl);
            s.append("<xsd:element name=\"ROWSET\">").append(nl);
            s.append("<xsd:complexType>").append(nl);
            s.append("<xsd:sequence>").append(nl);
            s.append("<xsd:element ref=\"ROW\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>").append(
                    nl);
            s.append("</xsd:sequence>").append(nl);
            s.append("</xsd:complexType>").append(nl);
            s.append("</xsd:element>").append(nl).append(nl);

            s.append("<xsd:element name=\"ROW\">").append(nl);
            s.append("<xsd:complexType>").append(nl);
            s.append("<xsd:sequence>").append(nl);

            for (Iterator<ColumnDTO> it = columns.iterator(); it.hasNext();) {
                ColumnDTO column = it.next();
                String name = column.getColumnName();
                int type = column.getColumnType();
                int size = column.getColumnSize();

                if (type == Types.VARCHAR || type == Types.CHAR
                        || type == Types.LONGVARCHAR) {
                    s.append("<xsd:element name=\"").append(name).append("\" minOccurs=\"0\" maxOccurs=\"1\">").append(
                            nl);
                    s.append("<xsd:simpleType>").append(nl);
                    s.append("<xsd:restriction base=\"xsd:string\">").append(nl);
                    s.append("<xsd:maxLength value=\"").append(size).append("\"/>").append(
                            nl);
                    s.append("</xsd:restriction>").append(nl);
                    s.append("</xsd:simpleType>").append(nl);
                    s.append("</xsd:element>").append(nl);
                } else {
                    String xsdType = "";

                    if (type == Types.INTEGER || type == Types.BIGINT
                            || type == Types.NUMERIC) {
                        xsdType = "xsd:int";
                    } else if (type == Types.FLOAT || type == Types.REAL || type == Types.DECIMAL) {
                        xsdType = "xsd:double";
                    } else if (type == Types.TINYINT || type == Types.SMALLINT) {
                        xsdType = "xsd:short";
                    } else if (type == Types.BOOLEAN) {
                        xsdType = "xsd:boolean";
                    } else if (type == Types.DATE) {
                        if (size == 4) {
                            xsdType = "xsd:gYear";
                        } else {
                            xsdType = "xsd:date";
                        }
                    } else {
                        xsdType = "xsd:string";
                    }

                    s.append("<xsd:element name=\"").append(name).append("\" minOccurs=\"0\" maxOccurs=\"1\" type=\"").append(xsdType).append("\"/>").append(
                            nl);
                }
            }

            s.append("</xsd:sequence>").append(nl);
            s.append("</xsd:complexType>").append(nl);
            s.append("</xsd:element>").append(nl);
            s.append("</xsd:schema>").append(nl);

            response.setContentType("text/plain ;charset=UTF-8");
            response.getWriter().write(s.toString());

        } catch (Exception e) {
            logger.debug(e, e); 
            errors.add(e.getMessage());
        }

    }

}
