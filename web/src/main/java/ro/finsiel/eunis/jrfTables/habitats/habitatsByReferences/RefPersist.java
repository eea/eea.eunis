package ro.finsiel.eunis.jrfTables.habitats.habitatsByReferences;

/**
 * Date: Apr 24, 2003
 * Time: 10:57:59 AM
 */

import java.util.Date;

import net.sf.jrf.domain.PersistentObject;

public class RefPersist extends PersistentObject {


    /**
     * This is a database field.
     **/
    private Integer i_idDc = null;

    private String i_comment = null;

    private String i_source = null;

    private String i_editor = null;

    private String i_url = null;


    private String idHabitat = null;
    private String title = null;
    private String alternative = null;
    private String publisher = null;
    private String created = null;
    private Short haveSource = null;
    private Short haveOtherReferences = null;
    private String scName = null;
    private String eunisCode = null;
    private String annex1Code = null;
    private Integer level = null;
    private String description = null;
    private String code2000 = null;
    private String habitatType = null;


    public RefPersist() {
        super();
    }


    public Short getHaveSource() {
        return haveSource;
    }

    public void setHaveSource(Short haveSource) {
        this.haveSource = haveSource;
    }

    public String getEunisCode() {
        return eunisCode;
    }

    public void setEunisCode(String haveSource) {
        this.eunisCode = haveSource;
    }

    public String getAnnex1Code() {
        return annex1Code;
    }

    public void setAnnex1Code(String haveSource) {
        this.annex1Code = haveSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String haveSource) {
        this.description = haveSource;
    }

    public Integer getLevel() {
        if (level == null) return new Integer(0);
        return level;
    }

    public void setLevel(Integer haveSource) {
        this.level = haveSource;
    }

    public Short getHaveOtherReferences() {
        return haveOtherReferences;
    }

    public void setHaveOtherReferences(Short haveOtherReferences) {
        this.haveOtherReferences = haveOtherReferences;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    public String getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(String idHabitat) {
        this.idHabitat = idHabitat;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        if(created != null && created.length() > 4) {
            created = created.substring(0, 4);
        }
        this.created = created;
    }

    public String getComment() {
        return i_comment;
    }

    public void setComment(String comment) {
        i_comment = comment;
        this.markModifiedPersistentState();
    }


    /**
     * Getter for a database field.
     **/
    public String getEditor() {
        return i_editor;
    }


    /**
     * Getter for a database field.
     **/
    public Integer getIdDc() {
        return i_idDc;
    }

    /**
     * Getter for a database field.
     **/
    public String getSource() {
        return i_source;
    }

    /**
     * Getter for a database field.
     **/
    public String getUrl() {
        return i_url;
    }


    /**
     * Setter for a database field.
     * @param editor
     **/
    public void setEditor(String editor) {
        this.i_editor = editor;

    }


    /**
     * Setter for a database field.
     * @param idDc
     **/
    public void setIdDc(Integer idDc) {
        i_idDc = idDc;
        // Changing a primary key so we force this to new.
        this.forceNewPersistentState();
    }


    /**
     * Setter for a database field.
     * @param source
     **/
    public void setSource(String source) {
        this.i_source = source;

    }

    /**
     * Setter for a database field.
     * @param url
     **/
    public void setUrl(String url) {
        this.i_url = url;

    }

    public String getCode2000() {
        return code2000;
    }

    public void setCode2000(String code2000) {
        this.code2000 = code2000;
    }

    public String getHabitatType() {
        return habitatType;
    }

    public void setHabitatType(String habitatType) {
        this.habitatType = habitatType;
    }
}

