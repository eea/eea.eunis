package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.domain.PersistentObject;

public class Chm62edtHabitatRedlistConservationPersist extends PersistentObject  {

    private Integer idMeasure;
    private Integer idHabitat;
    private String mainCategory;
    private String subcategory;


    public Integer getIdMeasure() {
        return idMeasure;
    }

    public void setIdMeasure(Integer idMeasure) {
        this.idMeasure = idMeasure;
    }

    public Integer getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(Integer idHabitat) {
        this.idHabitat = idHabitat;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
}
