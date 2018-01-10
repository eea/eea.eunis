package ro.finsiel.eunis.jrfTables.species.habitats;


import net.sf.jrf.domain.PersistentObject;


/**
 * Date: Sep 23, 2003
 * Time: 12:42:15 PM
 */
public class HabitatsNatureObjectReportTypeSpeciesPersist extends PersistentObject {

    private Integer idNatureObjectHabitat = null;
    private Integer idGeoscope = null;
    private Integer idReportAttributes = null;
    private String eunisHabitatCode = null;
    private String code2000 = null;
    private String habitatScientificName = null;
    private String idHabitat = null;
    private Integer idSpecies = null;
    private Integer idSpeciesLink = null;
    private String speciesScientificName = null;
    private Integer IdReportType = null;
    private String groupName = null;
    private Integer idNatureObjectSpecies = null;
    private String value = null;

    public HabitatsNatureObjectReportTypeSpeciesPersist() {
        super();
    }

    public Integer getIdNatureObjectHabitat() {
        return idNatureObjectHabitat;
    }

    public void setIdNatureObjectHabitat(Integer idNatureObjectHabitat) {
        this.idNatureObjectHabitat = idNatureObjectHabitat;
    }

    public Integer getIdGeoscope() {
        return idGeoscope;
    }

    public void setIdGeoscope(Integer idGeoscope) {
        this.idGeoscope = idGeoscope;
    }

    public Integer getIdReportAttributes() {
        return idReportAttributes;
    }

    public void setIdReportAttributes(Integer idReportAttributes) {
        this.idReportAttributes = idReportAttributes;
    }

    public String getEunisHabitatCode() {
        return eunisHabitatCode;
    }

    public void setEunisHabitatCode(String eunisHabitatCode) {
        this.eunisHabitatCode = eunisHabitatCode;
    }

    public String getCode2000() {
        return code2000;
    }

    public void setCode2000(String code2000) {
        this.code2000 = code2000;
    }

    public String getHabitatScientificName() {
        return habitatScientificName;
    }

    public void setHabitatScientificName(String habitatScientificName) {
        this.habitatScientificName = habitatScientificName;
    }

    public String getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(String idHabitat) {
        this.idHabitat = idHabitat;
    }

    public Integer getIdSpecies() {
        return idSpecies;
    }

    public void setIdSpecies(Integer idSpecies) {
        this.idSpecies = idSpecies;
    }

    public Integer getIdSpeciesLink() {
        return idSpeciesLink;
    }

    public void setIdSpeciesLink(Integer idSpeciesLink) {
        this.idSpeciesLink = idSpeciesLink;
    }

    public String getSpeciesScientificName() {
        return speciesScientificName;
    }

    public void setSpeciesScientificName(String speciesScientificName) {
        this.speciesScientificName = speciesScientificName;
    }

    public Integer getIdReportType() {
        return IdReportType;
    }

    public void setIdReportType(Integer idReportType) {
        IdReportType = idReportType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getIdNatureObjectSpecies() {
        return idNatureObjectSpecies;
    }

    public void setIdNatureObjectSpecies(Integer idNatureObjectSpecies) {
        this.idNatureObjectSpecies = idNatureObjectSpecies;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
