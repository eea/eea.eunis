/**
 * Date: May 5, 2003
 * Time: 9:02:40 AM
 */
package ro.finsiel.eunis.jrfTables.habitats.factsheet;

import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.TimestampColumnSpec;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;

public class OtherClassificationDomain extends AbstractDomain {


  /****/
  public PersistentObject newPersistentObject() {
    return new OtherClassificationPersist();
  }

  /****/
  public void setup() {
    // These setters could be used to override the default.
    // this.setDatabasePolicy(new null());
    // this.setJDBCHelper(JDBCHelperFactory.create());

    this.setTableName("chm62edt_habitat_class_code");
    this.setReadOnly(true);
    this.setTableAlias("A");

    this.addColumnSpec(new StringColumnSpec("ID_HABITAT", "getIdHabitat", "setIdHabitat", DEFAULT_TO_ZERO, REQUIRED));
    this.addColumnSpec(new IntegerColumnSpec("ID_CLASS_CODE", "getIdClassCode", "setIdClassCode", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
    this.addColumnSpec(new StringColumnSpec("TITLE", "getTitle", "setTitle", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("RELATION_TYPE", "getRelationType", "setRelationType", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("CODE", "getCode", "setCode", DEFAULT_TO_NULL));
    this.addColumnSpec(new StringColumnSpec("COMMENT", "getComment", "setComment", DEFAULT_TO_NULL));

    JoinTable classCode = new JoinTable("chm62edt_class_code B", "ID_CLASS_CODE", "ID_CLASS_CODE");
    classCode.addJoinColumn(new StringJoinColumn("NAME", "setName"));
    classCode.addJoinColumn(new IntegerJoinColumn("SORT_ORDER", "setSortOrder"));
    classCode.addJoinColumn(new IntegerJoinColumn("ID_DC", "setIdDc"));
    this.addJoinTable(classCode);
  }
}
