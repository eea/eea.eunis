package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.domain.PersistentObject;

public class Chm62edtHabitatOccurrencePersist extends PersistentObject  {
    /**
     create table chm62edt_habitat_redlist_occurrence (
     id_occurrence int(16) primary key,
     id_habitat int(16),
     EU28_type varchar(10),
     id_country int(11),
     present varchar(100),
     current_area_value int(16),
     trend_quantity varchar(100),
     trend_quality varchar(100)
     );
     */

    private Integer idHabitat;
//    private Integer idOccurrence;
    private String EU28Type;
    private String country;
    private String present;
    private String currentAreaValue;
    private String trendQuantity;
    private String trendQuality;
    private String sea;

    public Integer getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(Integer idHabitat) {
        this.idHabitat = idHabitat;
    }

//    public Integer getIdOccurrence() {
//        return idOccurrence;
//    }
//
//    public void setIdOccurrence(Integer idOccurrence) {
//        this.idOccurrence = idOccurrence;
//    }

    public String getEU28Type() {
        return EU28Type;
    }

    public void setEU28Type(String EU28Type) {
        this.EU28Type = EU28Type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getCurrentAreaValue() {
        return currentAreaValue;
    }

    public void setCurrentAreaValue(String currentAreaValue) {
        this.currentAreaValue = currentAreaValue;
    }

    public String getTrendQuantity() {
        return trendQuantity;
    }

    public void setTrendQuantity(String trendQuantity) {
        this.trendQuantity = trendQuantity;
    }

    public String getTrendQuality() {
        return trendQuality;
    }

    public void setTrendQuality(String trendQuality) {
        this.trendQuality = trendQuality;
    }

    public String getSea() {
        return sea;
    }

    public void setSea(String sea) {
        this.sea = sea;
    }
}
