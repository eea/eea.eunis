package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.CompoundPrimaryKeyColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;


/**
 * JRF table for chm62edt_habitat_description.
 * @author finsiel
 **/
public class Chm62edtHabitatDescriptionDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatDescriptionPersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());
        this.setTableName("chm62edt_habitat_description");
        this.setReadOnly(true);

        this.addColumnSpec(
                new CompoundPrimaryKeyColumnSpec(
                        new StringColumnSpec("ID_HABITAT", "getIdHabitat",
                        "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY),
                        new IntegerColumnSpec("ID_LANGUAGE", "getIdLanguage",
                        "setIdLanguage", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY)));
        this.addColumnSpec(
                new StringColumnSpec("DESCRIPTION", "getDescription",
                "setDescription", null));
        this.addColumnSpec(
                new StringColumnSpec("SPECIES", "getSpecies",
                "setSpecies", null));
        this.addColumnSpec(
                new StringColumnSpec("OWNER_TEXT", "getOwnerText",
                "setOwnerText", null));
        this.addColumnSpec(
                new IntegerColumnSpec("ID_DC", "getIdDc", "setIdDc",
                DEFAULT_TO_ZERO));

        JoinTable langTable = new JoinTable("chm62edt_language", "ID_LANGUAGE",
                "ID_LANGUAGE");

        langTable.addJoinColumn(
                new StringJoinColumn("NAME_EN", "setLanguageName"));
        this.addJoinTable(langTable);
    }
}
