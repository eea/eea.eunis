package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.domain.PersistentObject;

import java.util.HashMap;
import java.util.Map;

public class Chm62edtHabitatRedlistPersist extends PersistentObject  {
    /**
     * create table chm62edt_habitat_redlist (
     *     id_habitat int(16) primary key,
     *     category_EU28 varchar(100),
     *     category_EU28PLUS varchar(100),
     *     synthesis varchar(2500),
     *     capacity_recover varchar(2500),
     *     EU28_trend_quantity varchar(100),
     *     EU28PLUS_trend_quantity varchar(100),
     *     EU28_trend_quality varchar(100),
     *     EU28PLUS_trend_quality varchar(100),
     *     conservation_management varchar(2500),
     *     map_description varchar(2500)
     *
     *     overall_criteria_EU28,
     * overall_criteria_EU28_plus,
     * confidence_assessment,
     * pressures_and_threats
     * occurrence_ext_EU28_EOO varchar(100);
     * occurrence_ext_EU28PLUS_EOO varchar(100);
     * occurrence_ext_EU28_AOO varchar(100);
     * occurrence_ext_EU28PLUS_AOO varchar(100);
     * occurrence_ext_EU28_area_km2 varchar(100);
     * occurrence_ext_EU28PLUS_area_km2 varchar(100);
     * occurrence_ext_EU28_comment varchar(250);
     * occurrence_ext_EU28PLUS_comment varchar(250); 
     * );
     */

    private static Map<String, String> categoryCodeMap;

    static {
        categoryCodeMap = new HashMap<>();
        categoryCodeMap.put("-", "O");
        categoryCodeMap.put("n/a", "O");
        categoryCodeMap.put("Endangered", "EN");
        categoryCodeMap.put("Vulnerable", "VU");
        categoryCodeMap.put("Least Concern", "LC");
        categoryCodeMap.put("Least Concerned", "LC");
        categoryCodeMap.put("Least Concen", "LC");
        categoryCodeMap.put("Data Deficient", "DD");
        categoryCodeMap.put("Critically Endangered", "CR");
        categoryCodeMap.put("Critically EndangeredR", "CR");
        categoryCodeMap.put("Near Threatened", "NT");
    }


    private Integer idHabitat;
    private String categoryEU28;
    private String categoryEU28Plus;
    private String synthesis;
    private String capacityRecover;
    private String EU28TrendQuality;
    private String EU28PlusTrendQuality;
    private String EU28TrendQuantity;
    private String EU28PlusTrendQuantity;
    private String conservationManagement;
    private String mapDescription;
    private String EU28OverallCriteria;
    private String EU28PlusOverallCriteria;
    private String confidence;
    private String pressuresAndThreats;
    private String occurrenceEU28EOO;
    private String occurrenceEU28AOO;
    private String occurrenceEU28Area;
    private String occurrenceEU28Comment;
    private String occurrenceEU28PlusEOO;
    private String occurrenceEU28PlusAOO;
    private String occurrenceEU28PlusArea;
    private String occurrenceEU28PlusComment;

    public Integer getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(Integer idHabitat) {
        this.idHabitat = idHabitat;
    }

    public String getCategoryEU28() {
        return categoryEU28;
    }

    public void setCategoryEU28(String categoryEU28) {
        this.categoryEU28 = categoryEU28;
    }

    public String getCategoryEU28Plus() {
        return categoryEU28Plus;
    }

    public void setCategoryEU28Plus(String categoryEU28Plus) {
        this.categoryEU28Plus = categoryEU28Plus;
    }

    public String getSynthesis() {
        return synthesis;
    }

    public void setSynthesis(String synthesis) {
        this.synthesis = synthesis;
    }

    public String getCapacityRecover() {
        return capacityRecover;
    }

    public void setCapacityRecover(String capacityRecover) {
        this.capacityRecover = capacityRecover;
    }

    public String getEU28TrendQuality() {
        if("-".equals(EU28TrendQuality)) {
            return "No occurrence";
        }
        return EU28TrendQuality;
    }

    public void setEU28TrendQuality(String EU28TrendQuality) {
        this.EU28TrendQuality = EU28TrendQuality;
    }

    public String getEU28PlusTrendQuality() {
        if ("-".equals(EU28PlusTrendQuality)) {
            return "No occurrence";
        }
        return EU28PlusTrendQuality;
    }

    public void setEU28PlusTrendQuality(String EU28PlusTrendQuality) {
        this.EU28PlusTrendQuality = EU28PlusTrendQuality;
    }

    public String getEU28TrendQuantity() {
        if ("-".equals(EU28TrendQuantity)) {
            return "No occurrence";
        }
        return EU28TrendQuantity;
    }

    public void setEU28TrendQuantity(String EU28TrendQuantity) {
        this.EU28TrendQuantity = EU28TrendQuantity;
    }

    public String getEU28PlusTrendQuantity() {
        if ("-".equals(EU28PlusTrendQuantity)) {
            return "No occurrence";
        }
        return EU28PlusTrendQuantity;
    }

    public void setEU28PlusTrendQuantity(String EU28PlusTrendQuantity) {
        this.EU28PlusTrendQuantity = EU28PlusTrendQuantity;
    }

    public String getConservationManagement() {
        return conservationManagement;
    }

    public void setConservationManagement(String conservationManagement) {
        this.conservationManagement = conservationManagement;
    }

    public String getMapDescription() {
        return mapDescription;
    }

    public void setMapDescription(String mapDescription) {
        this.mapDescription = mapDescription;
    }

    public String getEU28OverallCriteria() {
        return EU28OverallCriteria;
    }

    public void setEU28OverallCriteria(String EU28OverallCriteria) {
        this.EU28OverallCriteria = EU28OverallCriteria;
    }

    public String getEU28PlusOverallCriteria() {
        return EU28PlusOverallCriteria;
    }

    public void setEU28PlusOverallCriteria(String EU28PlusOverallCriteria) {
        this.EU28PlusOverallCriteria = EU28PlusOverallCriteria;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getPressuresAndThreats() {
        return pressuresAndThreats;
    }

    public void setPressuresAndThreats(String pressuresAndThreats) {
        this.pressuresAndThreats = pressuresAndThreats;
    }

    public String getOccurrenceEU28EOO() {
        return occurrenceEU28EOO;
    }

    public void setOccurrenceEU28EOO(String occurrenceEU28EOO) {
        this.occurrenceEU28EOO = occurrenceEU28EOO;
    }

    public String getOccurrenceEU28AOO() {
        return occurrenceEU28AOO;
    }

    public void setOccurrenceEU28AOO(String occurrenceEU28AOO) {
        this.occurrenceEU28AOO = occurrenceEU28AOO;
    }

    public String getOccurrenceEU28Area() {
        return occurrenceEU28Area;
    }

    public void setOccurrenceEU28Area(String occurrenceEU28Area) {
        this.occurrenceEU28Area = occurrenceEU28Area;
    }

    public String getOccurrenceEU28Comment() {
        return occurrenceEU28Comment;
    }

    public void setOccurrenceEU28Comment(String occurrenceEU28Comment) {
        this.occurrenceEU28Comment = occurrenceEU28Comment;
    }

    public String getOccurrenceEU28PlusEOO() {
        return occurrenceEU28PlusEOO;
    }

    public void setOccurrenceEU28PlusEOO(String occurrenceEU28PlusEOO) {
        this.occurrenceEU28PlusEOO = occurrenceEU28PlusEOO;
    }

    public String getOccurrenceEU28PlusAOO() {
        return occurrenceEU28PlusAOO;
    }

    public void setOccurrenceEU28PlusAOO(String occurrenceEU28PlusAOO) {
        this.occurrenceEU28PlusAOO = occurrenceEU28PlusAOO;
    }

    public String getOccurrenceEU28PlusArea() {
        return occurrenceEU28PlusArea;
    }

    public void setOccurrenceEU28PlusArea(String occurrenceEU28PlusArea) {
        this.occurrenceEU28PlusArea = occurrenceEU28PlusArea;
    }

    public String getOccurrenceEU28PlusComment() {
        return occurrenceEU28PlusComment;
    }

    public void setOccurrenceEU28PlusComment(String occurrenceEU28PlusComment) {
        this.occurrenceEU28PlusComment = occurrenceEU28PlusComment;
    }

    public String getCategoryCodeEU28(){
        return categoryCodeMap.get(this.categoryEU28);
    }

    public String getCategoryCodeEU28Plus(){
        return categoryCodeMap.get(this.categoryEU28Plus);
    }
}
