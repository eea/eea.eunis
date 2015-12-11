/*
 * $Id
 */

package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.domain.PersistentObject;


/**
 *
 * @version $Revision$ $Date$
 **/
public class Chm62edtHabitatDescriptionPersist extends PersistentObject {

    /**
     * This is a database field.
     **/
    private String i_idHabitat = null;

    /**
     * This is a database field.
     **/
    private String i_description = null;

    /**
     * This is a database field.
     **/
    private String i_ownerText = null;

    private Integer idLanguage = null;

    private String languageName = null;

    private Integer idDc = null;

    private String species = null;

    public Chm62edtHabitatDescriptionPersist() {
        super();
    }

    public Integer getIdDc() {
        return idDc;
    }

    public void setIdDc(Integer idDc) {
        this.idDc = idDc;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Integer getIdLanguage() {
        return idLanguage;
    }

    public void setIdLanguage(Integer idLanguage) {
        this.idLanguage = idLanguage;
    }

    /**
     * Getter for a database field.
     **/
    public String getDescription() {
        if (null == i_description) {
            return "n/a";
        }
        return i_description;
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
    public String getOwnerText() {
        if (null == i_ownerText) {
            return "n/a";
        }
        return i_ownerText;
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
     * @param idHabitat
     **/
    public void setIdHabitat(String idHabitat) {
        i_idHabitat = idHabitat;
        // Changing a primary key so we force this to new.
        this.forceNewPersistentState();
    }

    /**
     * Setter for a database field.
     * @param ownerText
     **/
    public void setOwnerText(String ownerText) {
        i_ownerText = ownerText;
        this.markModifiedPersistentState();
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
