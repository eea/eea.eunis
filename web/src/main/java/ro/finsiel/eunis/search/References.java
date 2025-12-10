package ro.finsiel.eunis.search;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.utilities.SQLUtilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;


/**
 * Data factory for main references search popup helper (header).
 * @author finsiel
 */
public class References {
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

    private static final Logger logger = Logger.getLogger(References.class);

    private Vector results = new Vector();

    /**
     * Creates a new References object.
     * @param author Author
     * @param relationOpAuthor Relation operator for author.
     * @param date Date [min]
     * @param date1 Date max.
     * @param relationOpDate Relation operator for date.
     * @param title Title.
     * @param relationOpTitle Relation operator for title.
     * @param publisher Publisher.
     * @param relationOpPublisher Relation operator for publisher.
     * @param editor Editor.
     * @param relationOpEditor Relation operator for editor.
     */
    public References(String author,
                      Integer relationOpAuthor,
                      String date,
                      String date1,
                      Integer relationOpDate,
                      String title,
                      Integer relationOpTitle,
                      String publisher,
                      Integer relationOpPublisher,
                      String editor,
                      Integer relationOpEditor) {

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
    }

    /**
     * Setter for referenced data.
     * @param fromWhere Where we come from (author, title, year etc.).
     * @param useLimit Limit the results output.
     */
    public void setReferencesList(String fromWhere, boolean useLimit) {

        StringBuffer sql = new StringBuffer();
        // if sql where condition contains another conditions, will be put 'and' before the new condition witch will be
        // attached to the sql where condition
        Vector put_and = new Vector();

        // Normal search
        if (null != author && null != relationOpAuthor) {
            if (!author.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(
                        Utilities.prepareSQLOperator("SOURCE", author,
                                relationOpAuthor));
            }
        }

        if (
                (
                        (null != date && !date.equalsIgnoreCase("")
                                && !date.equalsIgnoreCase("null"))
                                || (null != date1 && !date1.equalsIgnoreCase("")
                                        && !date1.equalsIgnoreCase("null"))
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
                    sql.append(" CREATED <=" + date1 + " ");
                }
                if (date1 == null
                        || (date1 != null && date1.equalsIgnoreCase(""))) {
                    sql.append(" CREATED >=" + date + " ");
                }
                if (date != null && date1 != null && !date.equalsIgnoreCase("")
                        && !date1.equalsIgnoreCase("")) {
                    sql.append(
                            " CREATED >=" + date + " AND CREATED<=" + date1
                            + " ");
                }
            } else {
                sql.append(
                        Utilities.prepareSQLOperator("CREATED", date,
                                relationOpDate));
            }
        }

        if (null != title && null != relationOpTitle) {
            if (!title.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(
                        Utilities.prepareSQLOperator("TITLE", title,
                                relationOpTitle));
            }
        }

        if (null != editor && null != relationOpEditor) {
            if (!editor.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(
                        Utilities.prepareSQLOperator("EDITOR", editor,
                                relationOpEditor));
            }
        }

        if (null != publisher && null != relationOpPublisher) {
            if (!publisher.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(
                        Utilities.prepareSQLOperator("PUBLISHER", publisher,
                                relationOpPublisher));
            }
        }

        Vector resultList = returnReferences(sql.toString(), fromWhere);

        if (useLimit) {
            if (resultList.size() > 0) {
                if (Utilities.MAX_POPUP_RESULTS < resultList.size()) {
                    for (int i = 0; i < resultList.size(); i++) {
                        results.addElement(resultList.get(i));
                    }
                } else {
                    results = resultList;
                }
            }
        } else {
            results = resultList;
        }

        ro.finsiel.eunis.search.SortListString sorter = new ro.finsiel.eunis.search.SortListString();

        results = sorter.sort(results, false);
    }

    /**
     * Retrieve references list.
     * @return Results to be displayed.
     */
    public List getReferencesList() {
        return results;
    }

    private Vector returnReferences(String sql, String fromWhere) {
        Vector results = new Vector();
        String SQL_REFERENCES;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL_REFERENCES = "SELECT DISTINCT ";

            if (fromWhere.equalsIgnoreCase("author")) {
                SQL_REFERENCES += " `dc_index`.`SOURCE`";
            }
            if (fromWhere.equalsIgnoreCase("date")) {
                SQL_REFERENCES += " CONCAT(`dc_index`.`CREATED`)";
            }
            if (fromWhere.equalsIgnoreCase("title")) {
                SQL_REFERENCES += " `dc_index`.`TITLE`";
            }
            if (fromWhere.equalsIgnoreCase("editor")) {
                SQL_REFERENCES += " `dc_index`.`EDITOR`";
            }
            if (fromWhere.equalsIgnoreCase("publisher")) {
                SQL_REFERENCES += " `dc_index`.`PUBLISHER`";
            }

            SQL_REFERENCES += " FROM  `dc_index`";
            // !!added to get only references with species and habitats
            SQL_REFERENCES += " INNER JOIN `chm62edt_nature_object` ON (`dc_index`.`ID_DC` = `chm62edt_nature_object`.`ID_DC`)";
            SQL_REFERENCES += " WHERE `dc_index`.`COMMENT` = 'REFERENCES'";
            SQL_REFERENCES += " AND `dc_index`.`CREATED` > 1000";

            if (sql.length() > 0) {
                SQL_REFERENCES += " AND " + sql;
            }

            // System.out.println( "SQL_REFERENCES = " + SQL_REFERENCES );
            ps = con.prepareStatement(SQL_REFERENCES);
            rs = ps.executeQuery(SQL_REFERENCES);

            while (rs.next()) {
                if (rs.getString(1) != null) {
                    results.addElement(rs.getString(1));
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        } finally {
            SQLUtilities.closeAll(con, ps, rs);
        }

        return results;
    }
}
