/*
 * $Id
 */

package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.domain.PersistentObject;


/**
 *
 * @version $Revision$ $Date$
 **/
public class Chm62edtHabitatPersist extends PersistentObject {
    // Sort this type of objects after their EUNIS CODE
    public static final int SORT_EUNIS_CODE = 0;
    public static final int SORT_SCIENTIFIC_NAME = 1;
    public static final int SORT_HABITAT_LEVEL = 2;
    public static final int SORT_HABITAT_NAME = 3;

    // Currently criteria used for sorting
    private int sort_criteria = SORT_EUNIS_CODE;

    /**
     * This is a database field.
     **/
    private String i_idHabitat = null;

    /**
     * This is a database field.
     **/
    private Integer i_idNatureObject = null;

    /**
     * This is a database field.
     **/
    private String i_scientificName = null;

    /**
     * This is a database field.
     **/
    private String i_description = null;

    /**
     * This is a database field.
     **/
    private String i_code2000 = null;

    /**
     * This is a database field.
     **/
    private String i_codeAnnex1 = null;

    /**
     * This is a database field.
     **/
    private Short i_priority = null;

    /**
     * This is a database field.
     **/
    private String i_eunisHabitatCode = null;

    /**
     * This is a database field.
     **/
    private String i_classRef = null;

    /**
     * This is a database field.
     **/
    private String i_codePart2 = null;

    /**
     * This is a database field.
     **/
    private Integer i_habLevel = null;

    private String geographicalDistribution = null;

    private String originallyPublishedCode = null;

    private String habitatType = null;

    private Integer idHabitatParent;

    private String eeaCode;

    public Chm62edtHabitatPersist() {
        super();
    }

    public String getOriginallyPublishedCode() {
        return originallyPublishedCode;
    }

    public void setOriginallyPublishedCode(String originallyPublishedCode) {
        this.originallyPublishedCode = originallyPublishedCode;
    }

    public String getGeographicalDistribution() {
        return geographicalDistribution;
    }

    public void setGeographicalDistribution(String geographicalDistribution) {
        this.geographicalDistribution = geographicalDistribution;
    }

    /**
     * Getter for a database field.
     **/
    public String getClassRef() {
        return i_classRef;
    }

    /**
     * Getter for a database field.
     **/
    public String getCode2000() {
        return i_code2000;
    }

    /**
     * Getter for a database field.
     **/
    public String getCodeAnnex1() {
        return i_codeAnnex1;
    }

    /**
     * Getter for a database field.
     **/
    public String getCodePart2() {
        return i_codePart2;
    }

    /**
     * Getter for a database field.
     **/
    public String getDescription() {
        return i_description;
    }

    /**
     * Getter for a database field.
     **/
    public String getEunisHabitatCode() {
        return i_eunisHabitatCode;
    }

    /**
     * Getter for a database field.
     **/
    public Integer getHabLevel() {
        if (null == i_habLevel) {
            return new Integer(0);
        }
        return i_habLevel;
    }

    /**
     * Getter for a database field.
     **/
    public String getIdHabitat() {
        return i_idHabitat;
    }

    /**
     * Getter for a database field.
     **/
    public Integer getIdNatureObject() {
        return i_idNatureObject;
    }

    /**
     * Getter for a database field.
     **/
    public Short getPriority() {
        return i_priority;
    }

    /**
     * Getter for a database field.
     **/
    public String getScientificName() {
        return i_scientificName;
    }

    /**
     * Setter for a database field.
     * @param classRef
     **/
    public void setClassRef(String classRef) {
        i_classRef = classRef;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param code2000
     **/
    public void setCode2000(String code2000) {
        i_code2000 = code2000;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param codeAnnex1
     **/
    public void setCodeAnnex1(String codeAnnex1) {
        i_codeAnnex1 = codeAnnex1;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param codePart2
     **/
    public void setCodePart2(String codePart2) {
        i_codePart2 = codePart2;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param description
     **/
    public void setDescription(String description) {
        i_description = description;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param eunisHabitatCode
     **/
    public void setEunisHabitatCode(String eunisHabitatCode) {
        i_eunisHabitatCode = eunisHabitatCode;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param habLevel
     **/
    public void setHabLevel(Integer habLevel) {
        i_habLevel = habLevel;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param idHabitat
     **/
    public void setIdHabitat(String idHabitat) {
        i_idHabitat = idHabitat;
        // Changing a primary key so we force this to new.
        this.forceNewPersistentState();
    }

    /**
     * Setter for a database field.
     * @param idNatureObject
     **/
    public void setIdNatureObject(Integer idNatureObject) {
        i_idNatureObject = idNatureObject;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param priority
     **/
    public void setPriority(Short priority) {
        i_priority = priority;
        this.markModifiedPersistentState();
    }

    /**
     * Setter for a database field.
     * @param scientificName
     **/
    public void setScientificName(String scientificName) {
        i_scientificName = scientificName;
        this.markModifiedPersistentState();
    }

    /** Getter for property sort_criteria.
     * @return Value of property sort_criteria.
     *
     */
    public int getSort_criteria() {
        return sort_criteria;
    }

    /** Setter for property sort_criteria.
     * @param sort_criteria New value of property sort_criteria.
     *
     */
    public void setSort_criteria(int sort_criteria) {
        this.sort_criteria = sort_criteria;
    }

    /** This method returns the string represented by the object, depending on the
     * sort criteria set. For example setSort_criteria(SORT_EUNIS_CODE) will return
     * getEunisHabitatCode() when doing obj.getSortCriteria().
     * @return A string identifying the object.
     */
    public String getSortCriteria() {
        switch (sort_criteria) {
        case SORT_EUNIS_CODE:
            return getEunisHabitatCode();

        case SORT_SCIENTIFIC_NAME:
            return getScientificName();

        case SORT_HABITAT_LEVEL:
            return getHabLevel().toString();

        case SORT_HABITAT_NAME:
            return getDescription();

        default:
            return getEunisHabitatCode();
        }
    }

    public String getHabitatType() {
        return habitatType;
    }

    public void setHabitatType(String habitatType) {
        this.habitatType = habitatType;
    }

    public Integer getIdHabitatParent() {
        return idHabitatParent;
    }

    public void setIdHabitatParent(Integer idHabitatParent) {
        this.idHabitatParent = idHabitatParent;
    }

    public String getEeaCode() {
        return eeaCode;
    }

    public void setEeaCode(String eeaCode) {
        this.eeaCode = eeaCode;
    }
}
