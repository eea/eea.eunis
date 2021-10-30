package ro.finsiel.eunis.reports.habitats;

import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.exceptions.InitializationException;
import ro.finsiel.eunis.formBeans.AbstractFormBean;
import ro.finsiel.eunis.formBeans.CombinedSearchBean;
import ro.finsiel.eunis.jrfTables.habitats.advanced.DictionaryDomain;
import ro.finsiel.eunis.jrfTables.habitats.advanced.DictionaryPersist;
import ro.finsiel.eunis.reports.AbstractTSVReport;
import ro.finsiel.eunis.reports.XMLReport;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.habitats.advanced.DictionaryPaginator;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * TSV and XML report generation.
 */
public class TSVHabitatsAdvanced extends AbstractTSVReport
{
  /**
   * Use the bean in order to see which columns should I display on the report.
   */
  private CombinedSearchBean formBean = null;

  /**
   * Constructor.
   *
   * @param sessionID Session ID got from page
   * @param formBean  Form bean queried for output formatting (DB query, sort criterias etc)
   */
  public TSVHabitatsAdvanced(String sessionID, AbstractFormBean formBean) {
    super("HabitatsAdvancedReport_" + sessionID + ".tsv");
    this.formBean = (CombinedSearchBean) formBean;
    this.filename = "HabitatsAdvancedReport_" + sessionID + ".tsv";
    xmlreport = new XMLReport("HabitatsAdvancedReport_" + sessionID + ".xml");
    this.dataFactory = new DictionaryPaginator(new DictionaryDomain(sessionID));
    this.dataFactory.setSortCriteria(formBean.toSortCriteria());
  }

  /**
   * Create the table headers.
   *
   * @return An array with the columns headers of the table
   */
  public List<String> createHeader() {
    if (null == formBean) {
      return new Vector<String>();
    }
    Vector<String> headers = new Vector<String>();
    headers.addElement("Level");
    headers.addElement("EUNIS code");
    headers.addElement("ANNEX I code");
    headers.addElement("Habitat type name");
    headers.addElement("Priority");
    return headers;
  }

  /**
   * Use this method to write specific data into the file. Implemented in inherited classes
   */
  public void writeData() {
    if (null == dataFactory) {
      return;
    }
    dataFactory.setPageSize(RESULTS_PER_PAGE);
    try {
      int _pagesCount = dataFactory.countPages();
      if (_pagesCount == 0) {
        closeFile();
        return;
      }
      writeRow(createHeader());
      xmlreport.writeRow(createHeader());
      for (int _currPage = 0; _currPage < _pagesCount; _currPage++) {
        List resultSet = dataFactory.getPage(_currPage);
        for (int i = 0; i < resultSet.size(); i++) {
          DictionaryPersist habitat = (DictionaryPersist) resultSet.get(i);
          String level = "";
          int idHabitat = Utilities.checkedStringToInt(habitat.getIdHabitat(), -1);
          if (habitat.getHabLevel() != null) level = habitat.getHabLevel().intValue() + "";
          boolean isEUNIS = Utilities.isHabitatEunis(habitat.getHabitatType());

          Vector<String> row = new Vector<String>();
          // Level
          row.addElement(level);
          // EUNIS code
          row.addElement(isEUNIS ? habitat.getEunisHabitatCode() : "");
          // ANNEX I code
          row.addElement((isEUNIS) ? "" : habitat.getCodeAnnex1());
          // Name
          row.addElement(habitat.getScientificName());
          // Priority
          row.addElement(habitat.getPriority() != null && 1 == habitat.getPriority().shortValue() ? "Yes" : "No");
          writeRow(row);
          xmlreport.writeRow(row);
        }
      }
    } catch (CriteriaMissingException ex) {
      ex.printStackTrace();
    } catch (InitializationException iex) {
      iex.printStackTrace();
    } catch (IOException ioex) {
      ioex.printStackTrace();
    } catch (Exception ex2) {
      ex2.printStackTrace();
    } finally {
      closeFile();
    }
  }
}
