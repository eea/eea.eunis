package eionet.eunis.rdf;

import eionet.eunis.dto.ForeignDataQueryDTO;
import eionet.sparqlClient.helpers.ResultValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miahi on 11/23/2015.
 */
public class LinkedDataQuery {
    private ForeignDataQueryDTO query;
    private List<Map<String, Object>> resultCols;
    private List<HashMap<String, ResultValue>> resultRows;
    private String attribution;
    private String title;
    private String summary;

    public LinkedDataQuery(ForeignDataQueryDTO query, List<Map<String, Object>> resultCols, List<HashMap<String, ResultValue>> resultRows, String attribution) {
        this.query = query;
        this.resultCols = resultCols;
        this.resultRows = resultRows;
        this.attribution = attribution;
        this.title = query.getTitle();
        this.summary = query.getSummary();
    }

    public ForeignDataQueryDTO getQuery() {
        return query;
    }

    public List<Map<String, Object>> getResultCols() {
        return resultCols;
    }

    public List<HashMap<String, ResultValue>> getResultRows() {
        return resultRows;
    }

    public String getAttribution() {
        return attribution;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }
}
