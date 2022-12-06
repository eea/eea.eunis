package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.CompoundPrimaryKeyColumnSpec;
import net.sf.jrf.column.columnspecs.BooleanColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;


/**
 * JRF table for chm62edt_habitat_maps.
 * @author finsiel
 **/
public class Chm62edtHabitatMapsDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatMapsPersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());
        this.setTableName("chm62edt_habitat_maps");
        this.setReadOnly(true);

        this.addColumnSpec(
                new StringColumnSpec("habitat_code", "getHabitatCode", "setHabitatCode", DEFAULT_TO_NULL);

        this.addColumnSpec(
                new StringColumnSpec("habitat_name", "getHabitatName", "setHabitatName", DEFAULT_TO_NULL);

        this.addColumnSpec(
                new BooleanColumnSpec("distribution_map", "getDistributionMap", "setDistributionMap", DEFAULT_TO_FALSE);

        this.addColumnSpec(
                new BooleanColumnSpec("suitability_map", "getSuitabilityMap", "setSuitabilityMap", DEFAULT_TO_FALSE);
        
        this.addColumnSpec(
                new BooleanColumnSpec("probability_map", "getProbabilityMap", "setProbabilityMap", DEFAULT_TO_FALSE);
    }
}
