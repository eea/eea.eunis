package eionet.eunis.dto;

import java.io.Serializable;

import org.simpleframework.xml.Root;

/**
 *
 * @author Risto Alt
 */
@Root
public class ForeignDataQueryDTO implements Serializable {

    /**
     * serial.
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String summary;
    private String query;
    private String queryType;
    private String endpoint;
    private String idToUse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getIdToUse() {
        return idToUse;
    }

    public void setIdToUse(String idToUse) {
        this.idToUse = idToUse;
    }
}
