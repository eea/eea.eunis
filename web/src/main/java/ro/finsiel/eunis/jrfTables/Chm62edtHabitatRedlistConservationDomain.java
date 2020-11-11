package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;


/**
 * JRF table for chm62edt_conservation_measures (red list of habitats).
 **/
public class Chm62edtHabitatRedlistConservationDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatRedlistConservationPersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        this.setTableName("chm62edt_conservation_measures");
        this.setReadOnly(true);

        this.addColumnSpec(
                new IntegerColumnSpec("ID_MEASURE", "getIdMeasure",
                        "setIdMeasure", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));

        this.addColumnSpec(
                new StringColumnSpec("MAIN_CATEGORY", "getMainCategory",
                "setMainCategory", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("SUBCATEGORY", "getSubcategory",
                "setSubcategory", DEFAULT_TO_NULL));

        JoinTable threats = new JoinTable("chm62edt_habitat_redlist_conservation", "ID_MEASURE",
                "ID_MEASURE");
        threats.addJoinColumn(new IntegerJoinColumn("ID_HABITAT", "setIdHabitat"));


        this.addJoinTable(threats);

    }
}
