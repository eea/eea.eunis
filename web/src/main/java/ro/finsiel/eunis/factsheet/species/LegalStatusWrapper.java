package ro.finsiel.eunis.factsheet.species;

import eionet.eunis.dto.DcLinkDTO;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesPersist;
import ro.finsiel.eunis.utilities.EunisUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for legal information about species, used in species factsheet.
 *
 * @author finsiel
 */
public class LegalStatusWrapper implements Comparable<LegalStatusWrapper>{
    private String legalText = "n/a";
    private String url = "n/a";
    private String detailedReference = "n/a";
    private String area = "n/a";
    private String reference = "";
    private String comments = "";
    private Integer idDc;
    private String formattedUrl;
    private String description = "";
    private String nameInDocument;
    private List<Chm62edtSpeciesPersist> speciesInDocument;

    private String parentUrl = "";
    private String parentName = "";
    private String parentAlternative;

    private int idReportAttributes = -1;

    private List<DcLinkDTO> moreInfo = new ArrayList<DcLinkDTO>();



    /**
     * Create a new LegalStatusWrapper object.
     */
    public LegalStatusWrapper() {
    }

    /**
     * Getter for comments property.
     *
     * @return comments.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Setter for comments property.
     *
     * @param comments
     *            New value
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Getter for legalText property.
     *
     * @return legalText.
     */
    public String getLegalText() {
        return legalText;
    }

    /**
     * Setter for legalText property.
     *
     * @param legalText
     *            New value
     */
    public void setLegalText(String legalText) {
        this.legalText = legalText;
    }

    /**
     * Getter for url property.
     *
     * @return url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter for url property.
     *
     * @param url
     *            url.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for reference property.
     *
     * @return reference.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Setter for reference property.
     *
     * @param reference
     *            reference.
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Getter for detailedReference property.
     *
     * @return detailedReference.
     */
    public String getDetailedReference() {
        return detailedReference;
    }

    /**
     * Setter for detailedReference property.
     *
     * @param detailedReference
     *            detailedReference.
     */
    public void setDetailedReference(String detailedReference) {
        this.detailedReference = detailedReference;
    }

    /**
     * Getter for area property.
     *
     * @return area.
     */
    public String getArea() {
        return area;
    }

    /**
     * Setter for area property.
     *
     * @param area
     *            area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    public Integer getIdDc() {
        return idDc;
    }

    public void setIdDc(Integer idDc) {
        this.idDc = idDc;
    }
    
    public String getFormattedUrl() {
        return formattedUrl;
    }

    public void setFormattedUrl(String formattedUrl) {
        this.formattedUrl = formattedUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean status = false;

        if ((obj instanceof LegalStatusWrapper) && (this instanceof LegalStatusWrapper)) {
            LegalStatusWrapper object = (LegalStatusWrapper) obj;
            if ((isIntegerEquals(object.idDc, this.idDc) && isStringsEquals(object.area, this.area)
                    && isStringsEquals(object.comments, this.comments)
                    && isStringsEquals(object.detailedReference, this.detailedReference)
                    && isStringsEquals(object.legalText, this.legalText)
                    && isStringsEquals(object.reference, this.reference) && isStringsEquals(object.url, this.url))) {

                status = true;
            }
        }
        return status;

    }

    private static boolean isStringsEquals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if ((str1 == null && str2 != null) || (str1 != null && str2 == null)) {
            return false;
        } else {
            return str1.equals(str2);
        }

    }

    private static boolean isIntegerEquals(Integer i1, Integer i2) {
        if (i1 == null && i2 == null) {
            return true;
        } else if ((i1 == null && i2 != null) || (i1 != null && i2 == null)) {
            return false;
        } else {
            return i1.equals(i2);
        }

    }

    /**
     * Description field (dc_attributes name="description")
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * URL of the parent reference
     */
    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /**
     * Name of the parent reference
     */
    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * Alternative name of the parent reference
     * @param parentAlternative
     */
    public void setParentAlternative(String parentAlternative) {
        this.parentAlternative = parentAlternative;
    }

    /**
     * Alternative name of the parent reference
     */
    public String getParentAlternative() {
        return parentAlternative;
    }

    public int getIdReportAttributes() {
        return idReportAttributes;
    }

    public void setIdReportAttributes(int idReportAttributes) {
        this.idReportAttributes = idReportAttributes;
    }

    public void addMoreInfo(DcLinkDTO value) {
        moreInfo.add(value);
    }

    public List<DcLinkDTO> getMoreInfo() {
        return moreInfo;
    }

    public String getNameInDocument() {
        return nameInDocument;
    }

    public void setNameInDocument(String nameInDocument) {
        this.nameInDocument = nameInDocument;
    }

    public List<Chm62edtSpeciesPersist> getSpeciesInDocument() {
        return speciesInDocument;
    }

    public void setSpeciesInDocument(List<Chm62edtSpeciesPersist> speciesInDocument) {
        this.speciesInDocument = speciesInDocument;
    }

    /**
     * Implements the ordering of the legal status based on
     * https://taskman.eionet.europa.eu/issues/30232
     * @param o The object to compare
     * @return
     */
    @Override
    public int compareTo(LegalStatusWrapper o) {
        if(o != null){
            Integer i1 = EunisUtil.legalStatusOrder.get(this.getIdDc());
            Integer i2 = EunisUtil.legalStatusOrder.get(o.getIdDc());
            if(i1!=null && i2 != null)
                return i1 - i2;
            else {
                return 0;
            }
        } else {
            return 1;
        }
    }

}
