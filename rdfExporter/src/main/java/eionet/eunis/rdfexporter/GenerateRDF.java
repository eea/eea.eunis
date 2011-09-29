package eionet.eunis.rdfexporter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Arrays;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * A struct to hold a complex type.
 * No need to do data encapsulation.
 */
class RDFField {
    /** Name of column. */
    String name;
    /** Datatype of column. */
    String datatype;
    /** Language code of column. */
    String langcode;

    /**
     * Constructor.
     */
    RDFField() {
        name = "";
        datatype = "";
        langcode = "";
    }
}

/**
 * RDF generator.
 */
public class GenerateRDF {
    /** Base of XML file. */
    private String baseurl;
    /** Connection to database. */
    private Connection con;
    /** Names, types and langcodes of columns. */
    private RDFField[] names;
    /** The URL of the null namespace. */
    private String nullNamespace;
    /** The namespaces to add to the rdf:RDF element. */
    private HashMap namespaces;
    /** The properties that are object properties. They point to another object. */
    private HashMap objectProperties;
    /** All the tables in the properties file. */
    private String[] tables;
    /** Hashtable of loaded properties. */
    private Properties props;
    /** The output stream to send output to. */
    private PrintStream outputStream;

    /**
     * Constructor.
     * @param writer - The output stream to send output to
     * @throws IOException - if the properties file is missing
     * @throws SQLException - if the SQL database is not available
     * @throws ClassNotFoundException - if the SQL driver is unavailable
     * @throws InstantiationException - if the SQL driver can't be instantiatied
     * @throws IllegalAccessException - unknown
     */
    public GenerateRDF(PrintStream writer) throws IOException,
                                SQLException, ClassNotFoundException,
                                InstantiationException, IllegalAccessException {
        props = new Properties();
        String dbUrl;
        String driver;
        String userName;
        String password;

        outputStream = writer;
        props.load(getClass().getClassLoader().getResourceAsStream("rdfexport.properties"));
//      props.load(new FileInputStream("rdfexport.properties"));

        driver = props.getProperty("driver");
        dbUrl = props.getProperty("database");
        userName = props.getProperty("user");
        password = props.getProperty("password");
        tables = props.getProperty("tables").split(" ");

        Class.forName(driver).newInstance();
        con = DriverManager.getConnection(dbUrl, userName, password);
        // Generate exception if there is no vocabulary property
        nullNamespace = props.getProperty("vocabulary");
        baseurl = props.getProperty("baseurl");

        namespaces = new HashMap();
        namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        objectProperties = new HashMap();
        // Get the namespaces from the properties file.
        // Get the objectproperties from the properties file.
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("xmlns.")) {
                addNamespace(key.substring(6), props.getProperty(key));
            } else if (key.startsWith("objectproperty.")) {
                String value = props.getProperty(key);
                addObjectProperty(key.substring(15), "->".concat(value));
            }
        }
    }

    /**
     * Do a nice shutdown in case the user forgets to call close().
     *
     * @throws Throwable - ignored
     */
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /**
     * Close the connection to the database.
     *
     * @throws SQLException if there is a database problem.
     */
    public void close() throws SQLException {
        if (con != null) {
            con.close();
            con = null;
        }
    }

    /**
     * Called from the other methods to do the output.
     *
     * @param v - value to print.
     */
    private void output(String v) {
        outputStream.print(v);
    }

    /**
     * Write a property. If the property.datatype is "->" then it is a resource reference.
     *
     * @param property triple consisting of name, datatype and langcode
     * @param value from database.
     */
    private void writeProperty(RDFField property, Object value) {
        String typelangAttr = "";

        if (value == null) {
            return;
        }
        output(" <");
        output(property.name);
        if (property.datatype.startsWith("->")) {
            // Handle pointers
            if (property.datatype.length() == 2) {
                output(" rdf:resource=\"");
                output(StringEscapeUtils.escapeXml(value.toString()));
                output("\"/>\n");
            } else {
                output(" rdf:resource=\"");
                output(property.datatype.substring(2));
                output("/");
                output(StringEscapeUtils.escapeXml(value.toString()));
                output("\"/>\n");
            }
            return;
        } else if (!"".equals(property.datatype)) {
            if (property.datatype.startsWith("xsd:")) {
                property.datatype = "http://www.w3.org/2001/XMLSchema#" + property.datatype.substring(4);
            }
            typelangAttr = " rdf:datatype=\"" + property.datatype + '"';
        } else if (!"".equals(property.langcode)) {
            typelangAttr = " xml:lang=\"" + property.langcode + '"';
        }
        output(typelangAttr);
        output(">");
        output(value.toString());
        output("</");
        output(property.name);
        output(">\n");
    }

    /**
     * The user can choose one record to output. This is done by inserting
     *  a HAVING ID=... into the SELECT statement. (using HAVING is slow).
     *  If the ID is numeric, then Mysql will convert the type to match
     *
     *  @param query - SQL query to patch
     *  @param identifier to insert into query
     *  @return patched SQL query
     */
    private String injectIdentifier(String query, String identifier) {
        String[] keywords = {" order ", " limit ", " procedure ", " into ", " for ", " lock " };
        String lquery = query.toLowerCase().replace("\n", " ");
        int insertBefore = lquery.length();
        for (String k : keywords) {
            int i = lquery.indexOf(k);
            if (i >= 0 && i < insertBefore) {
                insertBefore = i;
            }
        }
        int h = lquery.indexOf(" having ");
        if (h == -1) {
            query = query.substring(0, insertBefore) + " HAVING id='" + identifier.replace("'", "''") + "'"
                    + query.substring(insertBefore);
        } else {
            query = query.substring(0, h + 8) +  "id='" + identifier.replace("'", "''") + "' AND "
                    + query.substring(h + 8);
        }
        return query;
    }


    /**
     * Return all known tables in properties file.
     * @return list of strings.
     */
    public String[] getAllTables() {
        return Arrays.copyOf(tables, tables.length);
    }

    /**
     * Export a table as RDF. A table can consist of several queries.
     *
     * @param table - name of table in properties file
     */
    public void exportTable(String table) {
        exportTable(table, null);
    }

    /**
     * Export a table as RDF. A table can consist of several queries.
     *
     * @param table - name of table in properties file
     * @param identifier - primary key of the record we want or null for all records.
     */
    public void exportTable(String table, String identifier) {
        Boolean firstQuery = true;
        String rdfClass = table.substring(0, 1).toUpperCase() + table.substring(1).toLowerCase();
        rdfClass = props.getProperty(table.concat(".class"), rdfClass);
        String tableQueryKey = table.concat(".query");

        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(tableQueryKey)) {
                String query = props.getProperty(key);
                if (identifier != null) {
                    query = injectIdentifier(query, identifier);
                }
                runQuery(table, query, firstQuery ? rdfClass : "rdf:Description");
                firstQuery = false;
            }
        }
    }

    /**
     * Add namespace to table.
     *
     * @param name - namespace token.
     * @param url - namespace url.
     */
    private void addNamespace(String name, String url) {
        namespaces.put(name, url);
    }

    /**
     * Add name of property to table of object properties.
     *
     * @param name - name of column.
     * @param reference - will always start with '->'.
     */
    private void addObjectProperty(String name, String reference) {
        objectProperties.put(name, reference);
    }

    /**
     * Set the vocabulary in case it needs to be different from the properties file.
     *
     * @param url - namespace url.
     */
    public void setVocabulary(String url) {
        nullNamespace = url;
    }

    /**
     * Generate the RDF header element.
     */
    public void rdfHeader() {
        output("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        output("<rdf:RDF");
        for (Object key : namespaces.keySet()) {
            String url = namespaces.get(key).toString();
            output(" xmlns:");
            output(key.toString());
            output("=\"");
            output(url);
            output("\"\n");
        }
        output(" xmlns=\"");
        output(nullNamespace);
        output("\"");
        if (baseurl != null) {
            output(" xml:base=\"");
            output(baseurl);
            output("\"");
        }
        output(">\n");
    }

    /**
     * Generate the RDF footer element.
     */
    public void rdfFooter() {
        output("</rdf:RDF>\n");
    }

    /**
     * Run a query.
     *
     * @param segment - the namespace of the table
     * @param sql - the query to run.
     * @param rdfClass - the class to assign or rdf:Description
     */
    private void runQuery(String segment, String sql, String rdfClass) {
        try {
            Statement stmt = con.createStatement();

            if (stmt.execute(sql)) {
                // There's a ResultSet to be had
                ResultSet rs = stmt.getResultSet();

                ResultSetMetaData rsmd = rs.getMetaData();
                queryStruct(rsmd);

                int numcols = rsmd.getColumnCount();

                while (rs.next()) {
                    output("<");
                    output(rdfClass);
                    output(" rdf:about=\"");
                    output(segment);
                    output("/");
                    output(rs.getObject(1).toString());
                    output("\">\n");
                    for (int i = 2; i <= numcols; i++) {
                        writeProperty(names[i], rs.getObject(i));
                    }
                    output("</");
                    output(rdfClass);
                    output(">\n");
                }
            }
            stmt.close();
        } catch (SQLException e) {
            output("ERROR: " + e.getMessage());
        }
    }

    /**
     * Get the metadata from the columns. Check what datatype the database delivers.
     * but override if the user has specified something else in the column label.
     *
     * @param rsmd - metadata extracted from database.
     * @throws SQLException - if the SQL database is not available
     */
    private void queryStruct(ResultSetMetaData rsmd) throws SQLException {
        String dbDatatype;
        String rdfDatatype = "";
        int numcols = rsmd.getColumnCount();

        this.names = new RDFField[numcols + 1];

        for (int i = 1; i <= numcols; i++) {
            dbDatatype = rsmd.getColumnTypeName(i);
            //TODO: implement all types, perhaps even implement mapping in properties file.
            if ("VARCHAR".equals(dbDatatype) || "CHAR".equals(dbDatatype)) {
                rdfDatatype = "";
            } else if ("INT".equals(dbDatatype)) {
                rdfDatatype = "xsd:integer";
            } else if ("DATETIME".equals(dbDatatype)) {
                rdfDatatype = "xsd:dateTime";
            } else if ("DECIMAL".equals(dbDatatype)) {
                rdfDatatype = "xsd:decimal";
            }
            if (objectProperties.containsKey(rsmd.getColumnLabel(i))) {
                rdfDatatype = objectProperties.get(rsmd.getColumnLabel(i)).toString();
            }
            names[i] = parseName(rsmd.getColumnLabel(i), rdfDatatype);
        }
    }

    /**
     * Parses a column label. It can be parsed into three parts: name, datatype, language.
     *      hasRef-> becomes "hasRef","->",""
     *      hasRef->expert becomes "hasRef","->expert",""
     *      price^^xsd:decimal becomes "price","xsd:decimal",""
     *      rdfs:label@fr becomes "rdfs:label","","fr"
     *
     * @param complexname - name containing column name plus datatype or language code.
     * @param datatype - suggested datatype from database.
     * @return RDFField - struct of three strings: Name, datatype and langcode.
     */
    private RDFField parseName(String complexname, String datatype) {
        RDFField result = new RDFField();
        String name = complexname;
        String language = "";

        int foundReference = complexname.indexOf("->");
        if (foundReference >= 0) {
            name = complexname.substring(0, foundReference);
            datatype = complexname.substring(foundReference);
        } else {
            int foundDatatype = complexname.indexOf("^^");
            if (foundDatatype >= 0) {
                name = complexname.substring(0, foundDatatype);
                datatype = complexname.substring(foundDatatype + 2);
            } else {
                int foundLanguage = complexname.indexOf("@");
                if (foundLanguage >= 0) {
                    name = complexname.substring(0, foundLanguage);
                    language = complexname.substring(foundLanguage + 1);
                    datatype = "";
                }
            }
        }
        result.name = name;
        result.datatype = datatype;
        result.langcode = language;
        return result;
    }

    /*
    public static void main(String[] args) {
        ArrayList unusedArgs;
        String[] tables;
        String identifier = null;

        unusedArgs = new ArrayList(args.length);

        // Parse arguments. Just find an -i option
        // The -i takes an argument that is the record id we're interested in
        // variable "i" is in fact used.
        for (int a = 0, i = 0; a < args.length; a++) {
            if (args[a].equals("-i")) {
                identifier = args[++a];
            } else if (args[a].startsWith("-i")) {
                identifier = args[a].substring(2);
            } else {
                 unusedArgs.add(args[a]);
                 i++;
            }
        }
        try {
            GenerateRDF r = new GenerateRDF(System.out);

            if (unusedArgs.size() == 0) {
                tables = r.getAllTables();
            } else {
                tables = new String[unusedArgs.size()];
                for (int i = 0; i < unusedArgs.size(); i++) {
                    tables[i] = (String) unusedArgs.get(i).toString();
                }
            }

            r.rdfHeader();
            for (String table : tables) {
                r.exportTable(table, identifier);
            }
            r.rdfFooter();
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */
}
//vim: set expandtab sw=4 :