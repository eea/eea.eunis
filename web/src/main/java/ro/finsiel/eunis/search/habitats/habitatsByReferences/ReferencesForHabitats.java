package ro.finsiel.eunis.search.habitats.habitatsByReferences;


import java.util.List;
import java.util.Vector;

import eionet.eunis.util.Constants;
import ro.finsiel.eunis.jrfTables.habitats.habitatsByReferences.RefDomain;
import ro.finsiel.eunis.jrfTables.habitats.references.HabitatsBooksDomain;
import ro.finsiel.eunis.search.Utilities;


/**
 * This class is used to find the references for list of values displayed whithin popup.
 * @author finsiel
 */
public class ReferencesForHabitats {
    private Integer relationOpAuthor = null;
    private String author = null;
    private Integer relationOpDate = null;
    private String date = null;
    private String date1 = null;
    private Integer relationOpEditor = null;
    private String editor = null;
    private Integer relationOpTitle = null;
    private String title = null;
    private Integer relationOpPublisher = null;
    private String publisher = null;
    private Integer database = null;
    private Integer source = null;
    private List results = null;

    /**
     * Ctor.
     * @param author Author criteria.
     * @param relationOpAuthor Relation operator (IS/CONTAINS/STARTS).
     * @param date Starting date.
     * @param date1 Ending date.
     * @param relationOpDate Relation operator (BETWEEN etc.).
     * @param title Title.
     * @param relationOpTitle Relation operator (IS/CONTAINS/STARTS).
     * @param publisher Publisher.
     * @param relationOpPublisher Relation operator (IS/CONTAINS/STARTS).
     * @param editor Editor.
     * @param relationOpEditor Relation operator (IS/CONTAINS/STARTS).
     * @param database Database.
     * @param source Source.
     */
    public ReferencesForHabitats(String author,
            Integer relationOpAuthor,
            String date,
            String date1,
            Integer relationOpDate,
            String title,
            Integer relationOpTitle,
            String publisher,
            Integer relationOpPublisher,
            String editor,
            Integer relationOpEditor,
            Integer database,
            Integer source) {

        this.relationOpAuthor = relationOpAuthor;
        this.author = author;
        this.relationOpDate = relationOpDate;
        this.date = date;
        this.date1 = date1;
        this.relationOpEditor = relationOpEditor;
        this.editor = editor;
        this.relationOpTitle = relationOpTitle;
        this.title = title;
        this.relationOpPublisher = relationOpPublisher;
        this.publisher = publisher;
        this.database = database;
        this.source = source;
    }

    /**
     * Setter for results property.
     * @param fromWhere What information to retrieve.
     * @param useLimit Limit the output.
     */
    public void setReferencesList(String fromWhere, boolean useLimit) {

        StringBuffer sql = new StringBuffer();
        // if sql where condition contains another conditions, will be put 'and' before the new condition witch will be
        // attached to the sql where condition
        Vector put_and = new Vector();
        // if sql have where condition
        boolean haveWhere = false;

        // Normal search
        if (null != author && null != relationOpAuthor) {
            if (!author.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("J.SOURCE", author, relationOpAuthor));
            }
        }

        if (
                (
                        (null != date && !date.equalsIgnoreCase("") && !date.equalsIgnoreCase("null"))
                        || (null != date1 && !date1.equalsIgnoreCase("") && !date1.equalsIgnoreCase("null"))
                )
                && null != relationOpDate
        ) {

            if (put_and.contains("true")) {
                sql.append(" AND ");
            } else {
                put_and.addElement("true");
            }

            if (relationOpDate.compareTo(Utilities.OPERATOR_BETWEEN) == 0) {
                if (date == null || (date != null && date.equalsIgnoreCase(""))) {
                    sql.append(" J.CREATED <=" + date1 + " ");
                }
                if (date1 == null || (date1 != null && date1.equalsIgnoreCase(""))) {
                    sql.append(" J.CREATED >=" + date + " ");
                }
                if (date != null && date1 != null && !date.equalsIgnoreCase("") && !date1.equalsIgnoreCase("")) {
                    sql.append(" J.CREATED >=" + date + " AND J.CREATED<=" + date1 + " ");
                }
            } else {
                sql.append(Utilities.prepareSQLOperator("J.CREATED", date, relationOpDate));
            }
        }

        if (null != title && null != relationOpTitle) {
            if (!title.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("J.TITLE", title, relationOpTitle));
            }
        }

        if (null != editor && null != relationOpEditor) {
            if (!editor.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("J.EDITOR", editor, relationOpEditor));
            }
        }

        if (null != publisher && null != relationOpPublisher) {
            if (!publisher.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("J.PUBLISHER", publisher, relationOpPublisher));
            }
        }

        if (sql.length() > 0) {
            haveWhere = true;
        }

        if (0 != RefDomain.SEARCH_BOTH.compareTo(database)) {
            if (0 == database.compareTo(RefDomain.SEARCH_EUNIS)) {
                sql.append(" AND C.ID_HABITAT>=1 and C.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'  ");
            }
            if (0 == database.compareTo(RefDomain.SEARCH_ANNEX_I)) {
                sql.append(" AND C.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  ");
            }
        } else {
            sql.append(" AND C.ID_HABITAT<>'-1' and C.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  ");
        }

        if (0 == source.compareTo(RefDomain.SOURCE)) {
            sql.append(" AND B.HAVE_SOURCE = '1' ");
        }
        if (0 == source.compareTo(RefDomain.OTHER_INFO)) {
            sql.append(" AND B.HAVE_OTHER_REFERENCES = '1' ");
        }

        if (fromWhere.equalsIgnoreCase("author")) {
            sql.append(" GROUP BY J.SOURCE ");
        }
        if (fromWhere.equalsIgnoreCase("date")) {
            sql.append(" GROUP BY J.CREATED ");
        }
        if (fromWhere.equalsIgnoreCase("title")) {
            sql.append(" GROUP BY J.TITLE ");
        }
        if (fromWhere.equalsIgnoreCase("editor")) {
            sql.append(" GROUP BY J.EDITOR ");
        }
        if (fromWhere.equalsIgnoreCase("publisher")) {
            sql.append(" GROUP BY J.PUBLISHER ");
        }

        if (useLimit) {
            sql.append(" LIMIT 0," + Utilities.MAX_POPUP_RESULTS);
        }

        List result_list = null;

        if (haveWhere) {
            result_list = new HabitatsBooksDomain().findWhere(sql.toString());
        } else {

            if (fromWhere.equalsIgnoreCase("author")) {
                result_list = new DcSourceHabitatDomain().findWhere("1=1 " + sql.toString().replaceAll("J\\.", "D."));
            }
            if (fromWhere.equalsIgnoreCase("date")) {
                result_list = new DcDateHabitatDomain().findWhere("1=1 " + sql.toString().replaceAll("J\\.","E."));
            }
            if (fromWhere.equalsIgnoreCase("title")) {
                result_list = new DcTitleHabitatDomain().findWhere("1=1 " + sql.toString().replaceAll("J\\.","F."));
            }
            if (fromWhere.equalsIgnoreCase("editor")) {
                result_list = new DcSourceHabitatDomain().findWhere("1=1 " + sql.toString().replaceAll("J\\.", "D."));
            }
            if (fromWhere.equalsIgnoreCase("publisher")) {
                result_list = new DcPublisherHabitatDomain().findWhere("1=1 " + sql.toString().replaceAll("J\\.","G."));
            }

        }
        if (result_list != null && result_list.size() > 0) {
            results = result_list;
        }

    }

    /**
     * Getter for results property.
     * @return results.
     */
    public List getReferencesList() {
        return results;
    }

}
