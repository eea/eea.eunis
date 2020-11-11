package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.join.JoinTable;
import net.sf.jrf.join.OuterJoinTable;
import net.sf.jrf.join.joincolumns.StringJoinColumn;


/**
 * JRF table for chm62edt_habitat_occurrence (red list of habitats).
 **/
public class Chm62edtHabitatOccurrenceDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatOccurrencePersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        this.setTableName("chm62edt_habitat_redlist_occurrence");
        this.setReadOnly(true);

        this.addColumnSpec(
                new IntegerColumnSpec("ID_HABITAT", "getIdHabitat",
                "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
//        this.addColumnSpec(
//                new IntegerColumnSpec("ID_OCCURRENCE", "getIdOccurrence",
//                        "setIdOccurrence", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new StringColumnSpec("EU28_type", "getEU28Type",
                "setEU28Type", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("present", "getPresent",
                "setPresent", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("current_area_value", "getCurrentAreaValue",
                "setCurrentAreaValue", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("trend_quantity", "getTrendQuantity",
                "setTrendQuantity", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("trend_quality", "getTrendQuality",
                "setTrendQuality", DEFAULT_TO_NULL));

        OuterJoinTable country = new OuterJoinTable("chm62edt_redlist_countries", "ID_COUNTRY",
                "ID_COUNTRY");
        country.addJoinColumn(
                new StringJoinColumn("COUNTRY_NAME", "setCountry"));
        this.addJoinTable(country);

        OuterJoinTable sea = new OuterJoinTable("chm62edt_redlist_seas", "ID_SEA",
                "ID_SEA");
        sea.addJoinColumn(
                new StringJoinColumn("SEA_NAME", "setSea"));
        this.addJoinTable(sea);

    }
}
