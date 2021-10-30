package ro.finsiel.eunis.jrfTables.species.habitats;


import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;
import org.openrdf.query.algebra.Join;


/**
 * Date: Sep 23, 2003
 * Time: 12:38:24 PM
 */
public class HabitatsNatureObjectReportTypeSpeciesDomain extends AbstractDomain {

    /**
     **/
    public PersistentObject newPersistentObject() {
        return new HabitatsNatureObjectReportTypeSpeciesPersist();
    }

    /**
     **/
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());

        this.setTableName("chm62edt_habitat");
        this.setReadOnly(true);
        this.setTableAlias("H");

        this.addColumnSpec(
                new StringColumnSpec("ID_HABITAT", "getIdHabitat",
                "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new IntegerColumnSpec("ID_NATURE_OBJECT",
                "getIdNatureObjectHabitat", "setIdNatureObjectHabitat",
                DEFAULT_TO_ZERO, REQUIRED));
        this.addColumnSpec(
                new StringColumnSpec("EUNIS_HABITAT_CODE", "getEunisHabitatCode",
                "setEunisHabitatCode", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("CODE_2000", "getCode2000", "setCode2000",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("SCIENTIFIC_NAME",
                "getHabitatScientificName", "setHabitatScientificName",
                DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("HABITAT_TYPE",
                "getHabitatType", "setHabitatType",
                DEFAULT_TO_NULL));

        JoinTable natureObjectReportType = new JoinTable(
                "chm62edt_nature_object_report_type A", "ID_NATURE_OBJECT",
                "ID_NATURE_OBJECT_LINK");

        natureObjectReportType.addJoinColumn(
                new IntegerJoinColumn("ID_GEOSCOPE", "setIdGeoscope"));
        natureObjectReportType.addJoinColumn(
                new IntegerJoinColumn("ID_REPORT_ATTRIBUTES",
                "setIdReportAttributes"));
        natureObjectReportType.addJoinColumn(
                new IntegerJoinColumn("ID_REPORT_TYPE", "setIdReportType"));

        JoinTable reportAttributes = new JoinTable("chm62edt_report_attributes AT",
                "ID_REPORT_ATTRIBUTES", "ID_REPORT_ATTRIBUTES");
        reportAttributes.addJoinColumn(new StringJoinColumn("VALUE",  "setValue"));
        reportAttributes.addJoinColumn(new StringJoinColumn("NAME", "setAttrName"));

        natureObjectReportType.addJoinTable(reportAttributes);

        this.addJoinTable(natureObjectReportType);



        JoinTable species = new JoinTable("chm62edt_species C",
                "ID_NATURE_OBJECT", "ID_NATURE_OBJECT");

        species.addJoinColumn(
                new IntegerJoinColumn("ID_SPECIES", "setIdSpecies"));
        species.addJoinColumn(
                new IntegerJoinColumn("ID_SPECIES_LINK", "setIdSpeciesLink"));
        species.addJoinColumn(
                new StringJoinColumn("SCIENTIFIC_NAME",
                "setSpeciesScientificName"));
        species.addJoinColumn(
                new IntegerJoinColumn("ID_NATURE_OBJECT",
                "setIdNatureObjectSpecies"));
        natureObjectReportType.addJoinTable(species);

        JoinTable group = new JoinTable("chm62edt_group_species", "ID_GROUP_SPECIES", "ID_GROUP_SPECIES");
        group.addJoinColumn(new StringJoinColumn("COMMON_NAME", "setGroupName"));
        species.addJoinTable(group);
    }
}
