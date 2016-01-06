/**
 *
 */
package eionet.eunis.rdf;

import java.util.*;

import net.sourceforge.stripes.action.ActionBeanContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ro.finsiel.eunis.utilities.EunisUtil;
import ro.finsiel.eunis.utilities.SQLUtilities;
import eionet.eunis.dao.DaoFactory;
import eionet.eunis.dao.INatureObjectAttrDao;
import eionet.eunis.dto.ForeignDataQueryDTO;
import eionet.eunis.util.sql.SPARQLQueryResultSQLReader;
import eionet.sparqlClient.helpers.QueryExecutor;
import eionet.sparqlClient.helpers.QueryResult;
import eionet.sparqlClient.helpers.ResultValue;

/**
 * A helper class for executing linked data queries.
 *
 * @author Risto Alt
 */
public class LinkedData {

    /** Placeholder for object identifier in the queries to be executed. */
    private static final String IDENTIFIER_PLACEHOLDER = "[IDENTIFIER]";

    /** Placeholder for the currently running EUNIS webapp context path in the queries to be executed. */
    private static final String CONTEXT_PATH_PLACEHOLDER = "[CONTEXT_PATH]";

    private static final Logger logger = Logger.getLogger(LinkedData.class);

    /** All the queries in the properties file. */
    private String[] queries;
    /** Hashtable of loaded properties. */
    private Properties props;
    /** Arraylist of all query objects in properties file. */
    private ArrayList<ForeignDataQueryDTO> queryObjects;
    /** Label link pairs. */
    private HashMap<String, String> links;
    /** Column labels. */
    private HashMap<String, String> columnLabels;

    /** Query result rows. */
    private ArrayList<HashMap<String, ResultValue>> rows;
    /** Query result cols. */
    private ArrayList<Map<String, Object>> cols;
    /** Source of the data. */
    private String attribution;
    /** The {@link ActionBeanContext} under which this particular {@link LinkedData} instance is exploited. */
    private ActionBeanContext actionBeanContext;

    /**
     * Create helper on the basis of given properties, and for the given queries of the given nature object.
     *
     * @param props
     * @param natureObjId
     * @param queriesName The query name in the nature_object_attributes table; the query is only executed when
     * a row is found in that table for the queriesName. To bypass the check send "force".
     * @throws Exception
     */
    public LinkedData(Properties props, Integer natureObjId, String queriesName) throws Exception {

        if (props == null) {
            throw new IllegalArgumentException("Given properties map must not be null!");
        }

        this.props = props;
        queries = props.getProperty("queries").trim().split("\\s+");

        if (queries != null) {

            queryObjects = new ArrayList<ForeignDataQueryDTO>();
            for (String queryId : queries) {

                boolean resultExists = false;
                if (natureObjId != null) {
                    INatureObjectAttrDao dao = DaoFactory.getDaoFactory().getNatureObjectAttrDao();
                    if(!queriesName.equalsIgnoreCase("force")) {
                        resultExists = dao.queryResultExists(natureObjId, queryId, queriesName);
                    } else {
                        resultExists = true; // optimistical
                    }
                }

                if (natureObjId == null || (natureObjId != null && resultExists)) {
                    ForeignDataQueryDTO dto = new ForeignDataQueryDTO();
                    dto.setId(queryId);
                    dto.setTitle(props.getProperty(queryId + ".title"));
                    dto.setSummary(props.getProperty(queryId + ".summary"));
                    dto.setQuery(props.getProperty(queryId + ".query"));
                    dto.setQueryType(props.getProperty(queryId + ".querytype"));
                    dto.setEndpoint(props.getProperty(queryId + ".endpoint"));
                    dto.setIdToUse(props.getProperty(queryId + ".idtouse"));
                    queryObjects.add(dto);
                }
            }

            Collections.sort(queryObjects);
        }
    }

    /**
     * Calls {@link #executeQuery(String, int)} with the integer argument set to -1.
     *
     * @param queryId Query to execute.
     * @throws Exception Covers all exceptions.
     */
    public void executeQuery(String queryId) throws Exception {
        executeQuery(queryId, -1);
    }

    public void executeQuery(String queryId, int id) throws Exception {
        executeQuery(queryId, id + "");
    }

    /**
     * Executes the given external-data query for the given object id. All occurrences of {@link #IDENTIFIER_PLACEHOLDER} in the
     * query will be replaced by the supplied object id.
     *
     * @param queryId The id of the query to execute.
     * @param id The id of the object for which the query is executed.
     * @throws Exception Covers all exceptions that might be thrown.
     */
    public void executeQuery(String queryId, String id) throws Exception {

        if (StringUtils.isBlank(queryId)) {
            throw new IllegalArgumentException("External data query called with blank queryid and " + String.valueOf(id) + " identifier");
        }
        if (props != null && queryId != null) {

            String query = props.getProperty(queryId + ".query");
            String endpoint = props.getProperty(queryId + ".endpoint");
            attribution = props.getProperty(queryId + ".attribution");

            if (!StringUtils.isBlank(query) && !StringUtils.isBlank(endpoint)) {

                // Replace object identifier placeholder in query.
                if (query.contains(IDENTIFIER_PLACEHOLDER)) {
                    query = query.replace(IDENTIFIER_PLACEHOLDER, String.valueOf(id));
                }

                // Replace webapp context path placeholder in the query.
                if (query.contains(CONTEXT_PATH_PLACEHOLDER)) {
                    query = query.replace(CONTEXT_PATH_PLACEHOLDER, getContextPath());
                }

                try {
                    QueryExecutor executor = new QueryExecutor();
                    executor.executeQuery(endpoint, query);
                    QueryResult result = executor.getResults();

                    generateRows(queryId, result);
                    generateCols(queryId, result);
                } catch (Exception e){
                    logger.error(e, e);
                    throw new Exception("Could not execute query");
                }
            } else {
                logger.error("Query or endpoint is not defined in linkeddata properties file for: " + queryId);
            }
        }
    }

    /**
     * Executes the assumed SQL query by the given identifier, using the given {@link SQLUtilities} as helper.
     *
     * @param queryId Given query identifier. The query must be an SQL query.
     * @throws Exception Covers all exceptions.
     */
    public void executeSQLQuery(String queryId, SQLUtilities sqlUtilities) throws Exception {

        if (StringUtils.isBlank(queryId)) {
            throw new IllegalArgumentException("Query identifier must not be blank!");
        }

        if (sqlUtilities == null) {
            throw new IllegalArgumentException("SQL utilities helper must not be null!");
        }

        String query = props.getProperty(queryId + ".query");
        if (StringUtils.isBlank(query)) {
            throw new IllegalArgumentException("No query behind the given query identifier!");
        }

        // Replace webapp context path placeholder in the query.
        if (query.contains(CONTEXT_PATH_PLACEHOLDER)) {
            query = query.replace(CONTEXT_PATH_PLACEHOLDER, getContextPath());
        }

        SPARQLQueryResultSQLReader sqlReader = new SPARQLQueryResultSQLReader();
        sqlUtilities.executeQuery(query, null, sqlReader);

        SPARQLQueryResultMock queryResultMock = new SPARQLQueryResultMock(sqlReader);
        generateRows(queryId, queryResultMock);
        generateCols(queryId, queryResultMock);
    }

    /**
     * Copy existing row values to new rows HashMap. Generate links if defined in properties file.
     *
     * @param queryId - query identifier.
     * @param result - query result object.
     */
    private void generateRows(String queryId, QueryResult result) throws Exception {

        rows = new ArrayList<HashMap<String, ResultValue>>();

        // Search for label->link relations from properties file
        links = new HashMap<String, String>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(queryId + ".link.")) {
                String linkCol = props.getProperty(key);
                links.put(key.substring(key.lastIndexOf(".") + 1), linkCol);
            }
        }

        if (result != null && result.getRows() != null) {
            for (HashMap<String, ResultValue> row : result.getRows()) {
                List<String> usedCols = new ArrayList<String>();
                HashMap<String, ResultValue> newrow = new HashMap<String, ResultValue>();
                for (String key : row.keySet()) {
                    ResultValue value = row.get(key);
                    if (!usedCols.contains(key)) {
                        if (links != null && links.containsKey(key)) {
                            String val = EunisUtil.replaceTags(value.getValue(), true, true);
                            String linkCol = links.get(key);
                            ResultValue linkValue = row.get(linkCol);
                            String link = null;
                            if(linkValue != null) {
                                 link = row.get(linkCol).getValue();
                            }
                            if (!StringUtils.isBlank(link)) {
                                link = EunisUtil.replaceTags(link, true, true);
                                val = "<a href=\"" + link + "\">" + val + "</a>";
                                value = new ResultValue(val, true);
                            } else {
                                value = new ResultValue(val, value.isLiteral());
                            }
                            usedCols.add(linkCol);
                            newrow.remove(linkCol);
                        }
                        newrow.put(key, value);
                        usedCols.add(key);
                    }
                }
                rows.add(newrow);
            }
        }
    }

    /**
     * Copy existing column values to new cols ArrayList.
     * If labels are deifned in properties file then replace title with defined label.
     *
     * @param queryId - query identifier.
     * @param result - query result object.
     */
    private void generateCols(String queryId, QueryResult result) throws Exception {

        cols = new ArrayList<Map<String, Object>>();

        // Search for column labels from properties file
        columnLabels = new HashMap<String, String>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(queryId + ".column.")) {
                String label = props.getProperty(key);
                columnLabels.put(key.substring(key.lastIndexOf(".") + 1), label);
            }
        }

        if (result != null && result.getCols() != null) {
            for (Map<String, Object> col : result.getCols()) {
                Map<String, Object> newcol = new HashMap<String, Object>();
                String prop = (String) col.get("property");
                newcol.put("property", prop);
                if (columnLabels != null && columnLabels.containsKey(prop)) {
                    newcol.put("title", columnLabels.get(prop));
                } else {
                    String title = prop.replaceAll("_", " ");
                    if (title != null && title.length() > 1) {
                        title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
                    }
                    newcol.put("title", title);
                }
                newcol.put("sortable", col.get("sortable"));

                if (rows != null && rows.size() > 0 && rows.get(0).containsKey(prop)) {
                    cols.add(newcol);
                }
            }
        }
    }

    public ArrayList<ForeignDataQueryDTO> getQueryObjects() {
        return queryObjects;
    }

    public ArrayList<Map<String, Object>> getCols() {
        return cols;
    }

    public ArrayList<HashMap<String, ResultValue>> getRows() {
        return rows;
    }

    public String getAttribution() {
        return attribution;
    }

    /**
     *
     * @param filterThese
     * @return
     */
    public ArrayList<ForeignDataQueryDTO> getQueryObjects(String... filterThese) {

        if (queryObjects == null || queryObjects.isEmpty() || filterThese == null || filterThese.length == 0) {
            return queryObjects;
        }

        ArrayList<ForeignDataQueryDTO> resultList = new ArrayList<ForeignDataQueryDTO>();
        List<String> theseOnly = Arrays.asList(filterThese);
        for (ForeignDataQueryDTO queryObject : queryObjects) {
            if (theseOnly.contains(queryObject.getId())) {
                resultList.add(queryObject);
            }
        }

        return resultList;
    }

    /**
     * Returns the given attribute of the given query.
     *
     * @param queryId The query's identifier.
     * @param attrName The attribute's name.
     * @return The value of the attribute.
     */
    public String getQueryAttribute(String queryId, String attrName) {

        if (props == null) {
            return null;
        } else {
            return props.getProperty(queryId + "." + attrName);
        }
    }

    public List<String> getQueryAttributeKeys(String queryId){
        Set<String> all = props.stringPropertyNames();
        List<String> result = new ArrayList<>();

        for(String s : all){
            if(s.startsWith(queryId)){
                result.add(s);
            }
        }

        return result;
    }

    /**
     * @return the actionBeanContext
     */
    public ActionBeanContext getActionBeanContext() {
        return actionBeanContext;
    }

    /**
     * @param actionBeanContext the actionBeanContext to set
     */
    public void setActionBeanContext(ActionBeanContext actionBeanContext) {
        this.actionBeanContext = actionBeanContext;
    }

    /**
     * A null-safe getter for the webapp context path extracted from {@link #actionBeanContext}.
     * If the latter is null then an empty string is returned.
     *
     * @return The context path.
     */
    private String getContextPath() {
        return actionBeanContext == null ? StringUtils.EMPTY : actionBeanContext.getRequest().getContextPath();
    }

    /**
     * An extension of {@link QueryResult} that can be initialized with results from {@link SPARQLQueryResultSQLReader}.
     * Overrides {@link QueryResult#getRows()} and {@link QueryResult#getCols()} by returning the values of corresponding
     * methods in {@link SPARQLQueryResultSQLReader}.
     *
     * @author jaanus
     */
    public static class SPARQLQueryResultMock extends QueryResult {
    
        /** Rows as the came from {@link SPARQLQueryResultSQLReader} at construction. */
        private ArrayList<HashMap<String, ResultValue>> rows;
    
        /** Columns as the came from {@link SPARQLQueryResultSQLReader} at construction. */
        private ArrayList<Map<String, Object>> cols;
    
        /**
         * First calls {@link QueryResult#QueryResult(org.openrdf.query.TupleQueryResult)} and then creates an instance
         * with rows and columns from the given {@link SPARQLQueryResultSQLReader}.
         *
         * @param sqlReader The given {@link SPARQLQueryResultSQLReader}.
         * @throws Exception Thrown by super-class constructor.
         */
        private SPARQLQueryResultMock(SPARQLQueryResultSQLReader sqlReader) throws Exception {
            super(null);
            this.rows = sqlReader.getRows();
            this.cols = sqlReader.getCols();
        }
    
        /*
         * (non-Javadoc)
         *
         * @see eionet.sparqlClient.helpers.QueryResult#getRows()
         */
        @Override
        public ArrayList<HashMap<String, ResultValue>> getRows() {
            return rows;
        }
    
        /*
         * (non-Javadoc)
         *
         * @see eionet.sparqlClient.helpers.QueryResult#getCols()
         */
        @Override
        public ArrayList<Map<String, Object>> getCols() {
            return cols;
        }
    }
}
