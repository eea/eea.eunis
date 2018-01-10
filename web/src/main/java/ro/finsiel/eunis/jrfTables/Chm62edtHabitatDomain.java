package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.TimestampColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;


/**
 * JRF table for chm62edt_habitat.
 * @author finsiel
 **/
public class Chm62edtHabitatDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatPersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());
        this.setTableName("chm62edt_habitat");
        this.setReadOnly(true);

        this.addColumnSpec(
                new StringColumnSpec("ID_HABITAT", "getIdHabitat",
                "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new IntegerColumnSpec("ID_NATURE_OBJECT", "getIdNatureObject",
                "setIdNatureObject", DEFAULT_TO_ZERO, REQUIRED));
        this.addColumnSpec(
                new StringColumnSpec("SCIENTIFIC_NAME", "getScientificName",
                "setScientificName", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("DESCRIPTION", "getDescription",
                "setDescription", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("CODE_ANNEX1", "getCodeAnnex1",
                "setCodeAnnex1", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new ShortColumnSpec("PRIORITY", "getPriority", "setPriority",
                null, REQUIRED));
        this.addColumnSpec(
                new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode",
                "setEunisHabitatCode", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("CLASS_REF", "getClassRef", "setClassRef",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("CODE_PART_2", "getCodePart2",
                "setCodePart2", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new IntegerColumnSpec("LEVEL", "getHabLevel", "setHabLevel",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("ORIGINALLY_PUBLISHED_CODE",
                "getOriginallyPublishedCode", "setOriginallyPublishedCode",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("HABITAT_TYPE",
                "getHabitatType", "setHabitatType",
                DEFAULT_TO_NULL));
    }

    /**
     * Wrapper for SELECT COUNT(*) FROM ...
     * @param sqlWhere WHERE condition.
     * @return List of Chm62edtHabitatPersist.
     */
    public Long countWhere(String sqlWhere) {
        return this.findLong(
                "SELECT COUNT(*) FROM " + this.getTableAlias() + " WHERE "
                + sqlWhere);
    }

    /**
     * Wrapper for SELECT MAX(LEVEL) FROM chm62edt_habitat WHERE...
     * @param sqlWhere WHERE condition.
     * @return Integer.
     */
    public Integer maxlevel(String sqlWhere) {
        String isGoodHabitat = " IF(TRIM(chm62edt_habitat.CODE_2000) <> '',RIGHT(chm62edt_habitat.CODE_2000,2),1) <> IF(TRIM(chm62edt_habitat.CODE_2000) <> '','00',2) AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',LENGTH(chm62edt_habitat.CODE_2000),1) = IF(TRIM(chm62edt_habitat.CODE_2000) <> '',4,1) ";

        return this.findInteger(
                "SELECT max(LEVEL) FROM chm62edt_habitat WHERE " + isGoodHabitat
                + " AND " + sqlWhere);
    }
}
