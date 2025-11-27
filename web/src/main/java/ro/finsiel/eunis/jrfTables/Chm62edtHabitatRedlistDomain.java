package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.ShortColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;


/**
 * JRF table for chm62edt_habitat_redlist (red list of habitats).
 **/
public class Chm62edtHabitatRedlistDomain extends AbstractDomain {

    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtHabitatRedlistPersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        // These setters could be used to override the default.
        // this.setDatabasePolicy(new null());
        // this.setJDBCHelper(JDBCHelperFactory.create());
        this.setTableName("chm62edt_habitat_redlist");
        this.setReadOnly(true);

        this.addColumnSpec(
                new IntegerColumnSpec("ID_HABITAT", "getIdHabitat",
                "setIdHabitat", DEFAULT_TO_ZERO, NATURAL_PRIMARY_KEY));
        this.addColumnSpec(
                new StringColumnSpec("category_EU28", "getCategoryEU28",
                "setCategoryEU28", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("category_EU28PLUS", "getCategoryEU28Plus",
                        "setCategoryEU28Plus", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("synthesis", "getSynthesis",
                        "setSynthesis", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("capacity_recover", "getCapacityRecover",
                        "setCapacityRecover", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("EU28_trend_quantity", "getEU28TrendQuantity",
                        "setEU28TrendQuantity", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("EU28PLUS_trend_quantity", "getEU28PlusTrendQuantity",
                        "setEU28PlusTrendQuantity", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("EU28_trend_quality", "getEU28TrendQuality",
                        "setEU28TrendQuality", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("EU28PLUS_trend_quality", "getEU28PlusTrendQuality",
                        "setEU28PlusTrendQuality", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("conservation_management", "getConservationManagement",
                        "setConservationManagement", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("map_description", "getMapDescription",
                        "setMapDescription", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("overall_criteria_EU28", "getEU28OverallCriteria",
                        "setEU28OverallCriteria", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("overall_criteria_EU28_plus", "getEU28PlusOverallCriteria",
                        "setEU28PlusOverallCriteria", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("overall_criteria_EU28_plus", "getEU28PlusOverallCriteria",
                        "setEU28PlusOverallCriteria", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("confidence_assessment", "getConfidence",
                        "setConfidence", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("pressures_and_threats", "getPressuresAndThreats",
                        "setPressuresAndThreats", DEFAULT_TO_NULL));



        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28_EOO", "getOccurrenceEU28EOO",
                        "setOccurrenceEU28EOO", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28PLUS_EOO", "getOccurrenceEU28PlusEOO",
                        "setOccurrenceEU28PlusEOO", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28_AOO", "getOccurrenceEU28AOO",
                        "setOccurrenceEU28AOO", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28PLUS_AOO", "getOccurrenceEU28PlusAOO",
                        "setOccurrenceEU28PlusAOO", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28_area_km2", "getOccurrenceEU28Area",
                        "setOccurrenceEU28Area", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28PLUS_area_km2", "getOccurrenceEU28PlusArea",
                        "setOccurrenceEU28PlusArea", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28_comment", "getOccurrenceEU28Comment",
                        "setOccurrenceEU28Comment", DEFAULT_TO_NULL));
        this.addColumnSpec(
                new StringColumnSpec("occurrence_ext_EU28PLUS_comment", "getOccurrenceEU28PlusComment",
                        "setOccurrenceEU28PlusComment", DEFAULT_TO_NULL));
    }
}
