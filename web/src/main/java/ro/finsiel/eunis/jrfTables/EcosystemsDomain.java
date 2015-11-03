package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;

/**
 * Domain object for Ecosystems
 */
public class EcosystemsDomain extends AbstractDomain {
    @Override
    protected void setup() {
        this.setTableName("chm62edt_nature_object_ecosystem");
        this.setReadOnly(true);

        this.addColumnSpec(new IntegerColumnSpec("id_nature_object", "getIdNatureObject", "setIdNatureObject", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(new StringColumnSpec("code_eco", "getEcoCode", "setEcoCode", DEFAULT_TO_EMPTY_STRING, REQUIRED));
        this.addColumnSpec(new IntegerColumnSpec("id_biogeoregion", "getIdBioGeoRegion", "setIdBioGeoRegion", DEFAULT_TO_ZERO));
        this.addColumnSpec(new StringColumnSpec("type_assoc", "getTypeAssoc", "setTypeAssoc", DEFAULT_TO_EMPTY_STRING));

        JoinTable ecosystemsNames = new JoinTable("chm62edt_ecosystems A", "code_eco", "code");
        ecosystemsNames.addJoinColumn(new StringJoinColumn("value", "ecoName", "setEcoName"));
        ecosystemsNames.addJoinColumn(new StringJoinColumn("maes", "isMaes", "setIsMaes"));

        this.addJoinTable(ecosystemsNames);
    }

    @Override
    public PersistentObject newPersistentObject() {
        return new EcosystemsPersist();
    }
}
