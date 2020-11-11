package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;
import net.sf.jrf.join.joincolumns.StringJoinColumn;


/**
 * JRF table for chm62edt_habitat_occurrence (red list of habitats).
 **/
public class Chm62edtHabitatRedlistThreatsDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatRedlistThreatsPersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        this.setTableName(" chm62edt_threats");
        this.setReadOnly(true);

        this.addColumnSpec(
                new IntegerColumnSpec("ID_THREAT", "getIdThreat",
                        "setIdThreat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new StringColumnSpec("SORT_ORDER", "getOrder",
                "setOrder", DEFAULT_TO_ZERO));
        this.addColumnSpec(
                new StringColumnSpec("MAIN_THREAT", "getMainThreat",
                "setMainThreat", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("DESCRIPTION", "getDescription",
                "setDescription", DEFAULT_TO_NULL));

        JoinTable threats = new JoinTable("chm62edt_habitat_redlist_threats", "ID_THREAT",
                "ID_THREAT");
        threats.addJoinColumn(new IntegerJoinColumn("ID_HABITAT", "setIdHabitat"));


        this.addJoinTable(threats);

    }
}
