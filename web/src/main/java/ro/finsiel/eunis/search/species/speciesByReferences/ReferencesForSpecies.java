package ro.finsiel.eunis.search.species.speciesByReferences;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.search.SortListString;
import ro.finsiel.eunis.search.Utilities;


/**
 * Class used to retrieve data displayed in species-references-choice popup.
 * @author finsiel
 */
public class ReferencesForSpecies {
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
    private boolean showInvalidatedSpecies = false;

    private Vector results = new Vector();

    private static final Logger logger = Logger.getLogger(ReferencesForSpecies.class);

    /**
     * Constructor (takes form data as input).
     * @param author Author
     * @param relationOpAuthor Relation operator for author
     * @param date Start date
     * @param date1 End date
     * @param relationOpDate Relation operator for date
     * @param title Title
     * @param relationOpTitle Relation operator for title
     * @param publisher Publisher
     * @param relationOpPublisher Relation operator for publisher
     * @param editor Editor
     * @param relationOpEditor Relation operator for editor
     * @param showInvalidatedSpecies Display / Hide invalidated species in results
     */
    public ReferencesForSpecies(String author,
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
                                boolean showInvalidatedSpecies) {

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
        this.showInvalidatedSpecies = showInvalidatedSpecies;
    }

    /**
     * Initialize the list of references.
     * @param fromWhere What information to retrieve: author, title, publisher etc.
     * @param useLimit Limit the list of output results
     */
    public void setReferencesList(String fromWhere, boolean useLimit) {

        StringBuffer sql = new StringBuffer();
        // if sql where condition contains another conditions, will be put 'and' before the new condition witch will be
        // attached to the sql where condition
        Vector put_and = new Vector();

        // Normal search
        if (null != author && null != relationOpAuthor) {
            if (!author.trim().equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("A.SOURCE", author, relationOpAuthor));
            }
        }

        if (
                (
                        (null != date && !date.trim().equalsIgnoreCase("") && !date.equalsIgnoreCase("null"))
                        || (null != date1 && !date1.trim().equalsIgnoreCase("") && !date1.equalsIgnoreCase("null"))
                )
                && null != relationOpDate
        ) {

            if (put_and.contains("true")) {
                sql.append(" AND ");
            } else {
                put_and.addElement("true");
            }

            if (relationOpDate.compareTo(Utilities.OPERATOR_BETWEEN) == 0) {
                if (date == null || (date != null && date.trim().equalsIgnoreCase(""))) {
                    sql.append(" A.CREATED <=" + date1 + " ");
                }
                if (date1 == null || (date1 != null && date1.trim().equalsIgnoreCase(""))) {
                    sql.append(" A.CREATED >=" + date + " ");
                }
                if (date != null && date1 != null && !date.trim().equalsIgnoreCase("") && !date1.trim().equalsIgnoreCase("")) {
                    sql.append(" A.CREATED >=" + date + " AND A.CREATED<=" + date1 + " ");
                }
            } else {
                sql.append(Utilities.prepareSQLOperator("A.CREATED", date, relationOpDate));
            }
        }

        if (null != title && null != relationOpTitle) {
            if (!title.trim().equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("A.TITLE", title, relationOpTitle));
            }
        }

        if (null != editor && null != relationOpEditor) {
            if (!editor.trim().equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("A.EDITOR", editor, relationOpEditor));
            }
        }

        if (null != publisher && null != relationOpPublisher) {
            if (!publisher.equalsIgnoreCase("")) {
                if (put_and.contains("true")) {
                    sql.append(" AND ");
                } else {
                    put_and.addElement("true");
                }
                sql.append(Utilities.prepareSQLOperator("A.PUBLISHER", publisher, relationOpPublisher));
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

        SortListString sorter = new SortListString();

        results = sorter.sort(results, false);
    }

    /**
     * Retrieve the list of references.
     * @return List of results
     */
    public List getReferencesList() {
        return results;
    }

    private Vector returnReferences(String sql, String fromWhere) {
        Vector results = new Vector();
        String SQL = "";
        String SQL_REFERENCES = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL_REFERENCES = "SELECT DISTINCT A.";

            if (fromWhere.equalsIgnoreCase("author")) {
                SQL_REFERENCES += "SOURCE ";
            } else if (fromWhere.equalsIgnoreCase("date")) {
                SQL_REFERENCES += "CREATED ";
            } else if (fromWhere.equalsIgnoreCase("title")) {
                SQL_REFERENCES += "TITLE ";
            } else if (fromWhere.equalsIgnoreCase("editor")) {
                SQL_REFERENCES += "EDITOR ";
            } else if (fromWhere.equalsIgnoreCase("publisher")) {
                SQL_REFERENCES += "PUBLISHER ";
            }

            SQL_REFERENCES += "FROM dc_index A ";


            SQL = SQL_REFERENCES;

            SQL += "INNER JOIN chm62edt_nature_object B ON A.ID_DC=B.ID_DC "
                + "INNER JOIN chm62edt_species H ON B.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT ";

            SQL += "WHERE 1=1 ";

            SQL += Utilities.showEUNISInvalidatedSpecies(" AND H.VALID_NAME", showInvalidatedSpecies);

            if (sql.length() > 0) {
                SQL += " AND ";
            }

            SQL += sql;

            SQL += "UNION ";

            SQL += SQL_REFERENCES;

            SQL += "INNER JOIN chm62edt_reports B ON A.ID_DC=B.ID_DC "
                + "INNER JOIN chm62edt_report_type K ON B.ID_REPORT_TYPE = K.ID_REPORT_TYPE "
                + "INNER JOIN chm62edt_species H ON B.ID_NATURE_OBJECT = H.ID_NATURE_OBJECT ";

            SQL += "WHERE 1=1 ";

            SQL += Utilities.showEUNISInvalidatedSpecies(" AND H.VALID_NAME", showInvalidatedSpecies);

            if (sql.length() > 0) {
                SQL += " AND ";
            }

            SQL += sql;

            SQL += " AND K.LOOKUP_TYPE IN ('DISTRIBUTION_STATUS','LANGUAGE','CONSERVATION_STATUS',"
                + "'SPECIES_GEO','LEGAL_STATUS','SPECIES_STATUS','POPULATION_UNIT','TREND') ";

            SQL += " UNION ";

            SQL += SQL_REFERENCES;

            SQL += "INNER JOIN chm62edt_taxonomy B ON A.ID_DC=B.ID_DC "
                + "INNER JOIN chm62edt_species H ON B.ID_TAXONOMY = H.ID_TAXONOMY ";

            SQL += "WHERE 1=1 ";

            SQL += Utilities.showEUNISInvalidatedSpecies(" AND H.VALID_NAME", showInvalidatedSpecies);

            if (sql.length() > 0) {
                SQL += " AND ";
            }

            SQL += sql;
            ps = con.prepareStatement(SQL);
            rs = ps.executeQuery(SQL);
            while (rs.next()) {
                if (!fromWhere.equalsIgnoreCase("date")) {
                    if (rs.getString(1) != null) {
                        results.addElement(rs.getString(1));
                    }
                } else {
                    if (rs.getString(1) != null && rs.getLong(1) != 0) {
                        results.addElement(new Long(rs.getLong(1)).toString());
                    }
                }
            }
            ps.close();
            con.close();
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return results;
    }
}
