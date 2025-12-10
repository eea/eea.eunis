package ro.finsiel.eunis.factsheet.species;

import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.jrfTables.species.taxonomy.Chm62edtTaxcodeDomain;
import ro.finsiel.eunis.jrfTables.species.taxonomy.Chm62edtTaxcodePersist;
import ro.finsiel.eunis.jrfTables.species.taxonomy.Chm62edtTaxcodeAllJoinsDomain;
import ro.finsiel.eunis.jrfTables.species.taxonomy.Chm62edtTaxcodeAllJoinsPersist;
import ro.finsiel.eunis.search.Utilities;

import java.util.List;
import java.util.Vector;

/**
 * Taxonomic information for species.
 * @author finsiel
 */
public class FactsheetTaxcode {
  private String id = null;
  private Vector whatCanFind = new Vector();
  private String levelMax = "";

  private static final Logger logger = Logger.getLogger(FactsheetTaxcode.class);

  /**
   * Constructs an new FactsheetTaxcode object.
   * @param id ID_TAXONOMY from chm62edt_taxonomy. Each species has a link to this table.
   */
  public FactsheetTaxcode(String id) {
    this.id = id;
  }

  /**
   * Fills the whatCanFind property, depending on what information is available for the given ID_TAXONOMY.
   * @return true if this id givent at initialization exists in chm62edt_taxonomy.
   */
  private boolean setWhatCanFind() {
    boolean idTaxonomyExist = false;
    List list = new Vector();
    try
    {
      list = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY = '" + id + "'");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (list != null && list.size() > 0)
    {
      idTaxonomyExist = true;
      Chm62edtTaxcodePersist t = (Chm62edtTaxcodePersist) list.get(0);
      String level = Utilities.formatString(t.getTaxonomicLevel(), "");
      if (level.equalsIgnoreCase("family"))
      {
        whatCanFind.addElement("family");
        whatCanFind.addElement("order_column");
        whatCanFind.addElement("subclass");
        whatCanFind.addElement("class");
        whatCanFind.addElement("phylum");
        whatCanFind.addElement("kingdom");
      }
      if (level.equalsIgnoreCase("order_column")) {
        whatCanFind.addElement("order_column");
        whatCanFind.addElement("subclass");
        whatCanFind.addElement("class");
        whatCanFind.addElement("phylum");
        whatCanFind.addElement("kingdom");
      }
      if (level.equalsIgnoreCase("subclass")) {
        whatCanFind.addElement("subclass");
        whatCanFind.addElement("class");
        whatCanFind.addElement("phylum");
        whatCanFind.addElement("kingdom");
      }
      if (level.equalsIgnoreCase("class")) {
        whatCanFind.addElement("class");
        whatCanFind.addElement("phylum");
        whatCanFind.addElement("kingdom");
      }
      if (level.equalsIgnoreCase("phylum") || level.equalsIgnoreCase("division")) {
        whatCanFind.addElement("phylum");
        whatCanFind.addElement("kingdom");
      }
      if (level.equalsIgnoreCase("kingdom")) {
        whatCanFind.addElement("kingdom");
      }
    }
    return idTaxonomyExist;
  }

  /**
   * Checks if an given level exists in whatCanFind.
   * @param level level to check.
   * @return true if this information is available.
   */
  private boolean CanFind(String level) {
    boolean exist = false;
    if (setWhatCanFind())
    {
      for (int i = 0; i < whatCanFind.size(); i++)
      {
        if (((String) whatCanFind.get(i)).equalsIgnoreCase(level))
        {
          exist = true;
        }
      }
    }
    return exist;
  }

  /**
   * Return the ID_TAXONOMY for the 'family' level of the given ID_TAXONOMY during construction.
   * @return ID_TAXONOMY.
   */
  public String returnIdFamily() {
    return CanFind("family") ? id : "-1";
  }

  /**
   * Return the ID_TAXONOMY for the 'class' level of the given ID_TAXONOMY during construction.
   * @return ID_TAXONOMY.
   */
  public String returnIdClass() {
    String idCl = "-1";
    if (CanFind("class"))
    {
      List l1 = new Vector();
      try {
        l1 = new Chm62edtTaxcodeAllJoinsDomain().findWhere("A.ID_TAXONOMY ='" + id + "' AND (A.LEVEL = 'CLASS' OR B.LEVEL='CLASS' OR C.LEVEL='CLASS' OR D.LEVEL='CLASS' OR E.LEVEL='CLASS' OR G.LEVEL='CLASS') ");
      } catch (Exception e) {
        logger.debug(e, e); 
      }
      if (l1 != null && l1.size() > 0)
      {
        Chm62edtTaxcodeAllJoinsPersist t = (Chm62edtTaxcodeAllJoinsPersist) l1.get(0);
        if (t.getTaxonomicLevel() != null && t.getTaxonomicLevel().equalsIgnoreCase("class")) idCl = t.getIdTaxcode();
        if (t.getLevel1() != null && t.getLevel1().equalsIgnoreCase("class")) idCl = t.getId1();
        if (t.getLevel2() != null && t.getLevel2().equalsIgnoreCase("class")) idCl = t.getId2();
        if (t.getLevel3() != null && t.getLevel3().equalsIgnoreCase("class")) idCl = t.getId3();
        if (t.getLevel4() != null && t.getLevel4().equalsIgnoreCase("class")) idCl = t.getId4();
        if (t.getLevel5() != null && t.getLevel5().equalsIgnoreCase("class")) idCl = t.getId5();
      }
    }
    return idCl;
  }


  /**
   * Return the ID_TAXONOMY for the 'order' level of the given ID_TAXONOMY during construction.
   * @return ID_TAXONOMY.
   */
  public String returnIdOrder() {
    String idOrder = "-1";
    if (CanFind("order_column"))
    {
      List l1 = new Vector();
      try
      {
        l1 = new Chm62edtTaxcodeAllJoinsDomain().findWhere("A.ID_TAXONOMY ='" + id + "' AND (A.LEVEL = 'order' OR B.LEVEL='order' OR C.LEVEL='order' OR D.LEVEL='order' OR E.LEVEL='order' OR G.LEVEL='order') ");
      } catch (Exception e) {
        logger.debug(e, e); 
      }
      if (l1 != null && l1.size() > 0)
      {
        Chm62edtTaxcodeAllJoinsPersist t = (Chm62edtTaxcodeAllJoinsPersist) l1.get(0);
        if (t.getTaxonomicLevel() != null && t.getTaxonomicLevel().equalsIgnoreCase("order_column")) idOrder = t.getIdTaxcode();
        if (t.getLevel1() != null && t.getLevel1().equalsIgnoreCase("order_column")) idOrder = t.getId1();
        if (t.getLevel2() != null && t.getLevel2().equalsIgnoreCase("order_column")) idOrder = t.getId2();
        if (t.getLevel3() != null && t.getLevel3().equalsIgnoreCase("order_column")) idOrder = t.getId3();
        if (t.getLevel4() != null && t.getLevel4().equalsIgnoreCase("order_column")) idOrder = t.getId4();
        if (t.getLevel5() != null && t.getLevel5().equalsIgnoreCase("order_column")) idOrder = t.getId5();
      }
    }
    return idOrder;
  }


  /**
   * Return the ID_TAXONOMY for the 'phylum' or 'division' level of the given ID_TAXONOMY during construction.
   * @return ID_TAXONOMY.
   */
  public String returnIdPhylum() {
    String idPhylum = "-1";
    if (CanFind("phylum"))
    {
      List l1 = new Vector();
      try
      {
        l1 = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY<='" + id + "' AND SUBSTRING(ID_TAXONOMY,2,2)='" + id.substring(1, 3) + "' AND (LEVEL='PHYLUM' OR LEVEL='DIVISION') ORDER BY ID_TAXONOMY DESC");
      } catch (Exception e) {
        logger.debug(e, e); 
      }
      if (l1 != null && l1.size() > 0)
      {
        Chm62edtTaxcodePersist h = (Chm62edtTaxcodePersist) l1.get(0);
        idPhylum = h.getIdTaxcode();
        levelMax = h.getTaxonomicLevel();
      }
    }
    return idPhylum;
  }

  /**
   * Return the ID_TAXONOMY for the 'kingdom' level of the given ID_TAXONOMY during construction.
   * @return ID_TAXONOMY.
   */
  public String returnIdKingdom() {
    String idKingdom = "-1";
    if (CanFind("kingdom"))
    {
      List l1 = new Vector();
      try
      {
        l1 = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY<='" + id + "' AND SUBSTRING(ID_TAXONOMY,1,1)='" + id.substring(0, 1) + "' AND LEVEL='KINGDOM' ORDER BY ID_TAXONOMY DESC");
      } catch (Exception e) {
        logger.debug(e, e); 
      }
      if (l1 != null && l1.size() > 0)
      {
        Chm62edtTaxcodePersist h = (Chm62edtTaxcodePersist) l1.get(0);
        idKingdom = h.getIdTaxcode();
      }
    }
    return idKingdom;
  }

  /**
   * Return the ID_TAXONOMY for the 'subclass' level of the given ID_TAXONOMY during construction.
   * @return ID_TAXONOMY.
   */
  public String returnIdSubclass() {
    String idSubclass = "-1";
    if (CanFind("subclass") && classHaveSubclass(returnIdClass()))
    {
      List l1 = new Vector();
      try {
        l1 = new Chm62edtTaxcodeAllJoinsDomain().findWhere("A.ID_TAXONOMY ='" + id + "' AND (A.LEVEL = 'subclass' OR B.LEVEL='subclass' OR C.LEVEL='subclass' OR D.LEVEL='subclass' OR E.LEVEL='subclass' OR G.LEVEL='subclass') ");
      } catch (Exception e) {
        logger.debug(e, e); 
      }
      if (l1 != null && l1.size() > 0)
      {
        Chm62edtTaxcodeAllJoinsPersist t = (Chm62edtTaxcodeAllJoinsPersist) l1.get(0);
        if (t.getTaxonomicLevel() != null && t.getTaxonomicLevel().equalsIgnoreCase("subclass")) idSubclass = t.getIdTaxcode();
        if (t.getLevel1() != null && t.getLevel1().equalsIgnoreCase("subclass")) idSubclass = t.getId1();
        if (t.getLevel2() != null && t.getLevel2().equalsIgnoreCase("subclass")) idSubclass = t.getId2();
        if (t.getLevel3() != null && t.getLevel3().equalsIgnoreCase("subclass")) idSubclass = t.getId3();
        if (t.getLevel4() != null && t.getLevel4().equalsIgnoreCase("subclass")) idSubclass = t.getId4();
        if (t.getLevel5() != null && t.getLevel5().equalsIgnoreCase("subclass")) idSubclass = t.getId5();
      }
    }
    return idSubclass;
  }

  /**
   * Checks if a given class have a subclass.
   * @param id ID_TAXONOMY_LINK from chm62edt_taxonomy.
   * @return true if has subclass.
   */
  private boolean classHaveSubclass(String id) {
    boolean have = false;
    List l1 = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY_LINK='" + id + "' AND LEVEL='SUBCLASS'");
    if (l1 != null && l1.size() > 0) have = true;
    return have;
  }

  /**
   * Retrieve NAME column from chm62edt_taxonomy for a given ID_TAXONOMY.
   * @param id ID_TAXONOMY.
   * @return Taxonomy name.
   */
  public String returnName(String id) {
    String ret = "";
    try {
      List list = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY = '" + id + "'");
      if (null != list && !list.isEmpty()) {
        Chm62edtTaxcodePersist taxCode = (Chm62edtTaxcodePersist) list.get(0);
        ret = taxCode.getTaxonomicName();
      }
    } catch (Exception _ex) {
      _ex.printStackTrace(System.err);
      ret = "";
    }
    if (null == ret) ret = "";
    return ret;
  }

  /**
   * Checks if a given ID_TAXONOMY exists in chm62edt_taxonomy.
   * @param id Id.
   * @return true if exists.
   */
  public boolean HaveId(String id) {
    boolean ret = false;
    try {
      List list = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY = '" + id + "'");
      if (null != list && !list.isEmpty()) ret = true;
    } catch (Exception _ex) {
      _ex.printStackTrace(System.err);
      ret = false;
    }
    return ret;
  }

  /**
   * Retrieves ID_DC for taxonomy represented by this object.
   * @return ID_DC from chm62edt_taxonomy.
   */
  public Integer IdDcTaxcode()
  {
    List l1 = new Chm62edtTaxcodeAllJoinsDomain().findWhere("A.ID_TAXONOMY ='" + id + "'");
    if (l1 != null && l1.size() > 0)
    {
      Chm62edtTaxcodeAllJoinsPersist t = (Chm62edtTaxcodeAllJoinsPersist) l1.get(0);
      return t.getIdDc();
    }
    return new Integer(-1);
  }

  /**
   * Getter for levelMax property.
   * @return levelMax.
   */
  public String getLevelMax() {
    return levelMax;
  }
}
