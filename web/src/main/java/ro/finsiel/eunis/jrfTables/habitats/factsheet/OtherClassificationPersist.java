/**
 * Date: May 5, 2003
 * Time: 9:02:52 AM
 */
package ro.finsiel.eunis.jrfTables.habitats.factsheet;

import net.sf.jrf.domain.PersistentObject;

public class OtherClassificationPersist extends PersistentObject {

  /**
   * This is a database field.
   **/
  private String i_idHabitat = null;
  /**
   * This is a database field.
   **/
  private Integer i_idClassCode = null;
  /**
   * This is a database field.
   **/
  private String i_title = null;
  /**
   * This is a database field.
   **/
  private String i_relationType = null;
  /**
   * This is a database field.
   **/
  private String i_code = null;
  /**
   * This is a database field.
   **/
//  private Short i_relationExist = null;

  private String name = null;
  private Integer sortOrder = null;
  private Integer idDc = null;

  private String comment;

  public OtherClassificationPersist() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  /**
   * Getter for a database field.
   **/
  public String getCode() {
    return i_code;
  }

  /**
   * Getter for a database field.
   **/
  public Integer getIdClassCode() {
    return i_idClassCode;
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
//  public Short getRelationExist() { return i_relationExist; }

  /**
   * Getter for a database field.
   **/
  public String getRelationType() {
    return i_relationType;
  }

  /**
   * Getter for a database field.
   **/
  public String getTitle() {
    return i_title;
  }

  /**
   * Setter for a database field.
   * @param code
   **/
  public void setCode(String code) {
    i_code = code;
    this.markModifiedPersistentState();
  }

  /**
   * Setter for a database field.
   * @param idClassCode
   **/
  public void setIdClassCode(Integer idClassCode) {
    i_idClassCode = idClassCode;
    this.markModifiedPersistentState();
  }

  /**
   * Setter for a database field.
   * @param idHabitat
   **/
  public void setIdHabitat(String idHabitat) {
    i_idHabitat = idHabitat;
    this.markModifiedPersistentState();
  }

  /**
   * Setter for a database field.
   * @param relationExist
   **/
//  public void setRelationExist(Short relationExist) {
//    i_relationExist = relationExist;
//    this.markModifiedPersistentState();
//  }

  /**
   * Setter for a database field.
   * @param relationType
   **/
  public void setRelationType(String relationType) {
    i_relationType = relationType;
    this.markModifiedPersistentState();
  }

  /**
   * Setter for a database field.
   * @param title
   **/
  public void setTitle(String title) {
    i_title = title;
    this.markModifiedPersistentState();
  }

    /**
     * Return the reference to dc_index
     * @return
     */
    public Integer getIdDc() {
        return idDc;
    }

    /**
     * Set the reference to dc_index
     * @param idDc
     */
    public void setIdDc(Integer idDc) {
        this.idDc = idDc;
    }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
