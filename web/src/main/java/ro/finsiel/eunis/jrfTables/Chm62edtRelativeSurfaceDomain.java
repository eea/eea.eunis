package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;


/**
 * JRF table for chm62edt_isolation.
 * @author finsiel
 **/
public class Chm62edtRelativeSurfaceDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtRelativeSurfacePersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());
        this.setTableName("chm62edt_relative_surface");
        this.addColumnSpec(
                new StringColumnSpec("ID_RELATIVE_SURFACE",
                "getIdRepresentativity", "setIdRepresentativity",
                DEFAULT_TO_NULL, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new StringColumnSpec("NAME", "getName", "setName",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("DESCRIPTION", "getDescription",
                "setDescription", DEFAULT_TO_NULL));

    }
}
