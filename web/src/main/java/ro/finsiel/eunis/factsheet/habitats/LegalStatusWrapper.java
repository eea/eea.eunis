package ro.finsiel.eunis.factsheet.habitats;

import eionet.eunis.dto.DcLinkDTO;
import ro.finsiel.eunis.jrfTables.habitats.factsheet.HabitatLegalPersist;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.EunisUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for the Legal status, used for Habitats Legal display
 */
public class LegalStatusWrapper implements Comparable<LegalStatusWrapper>{

    /**
     * Mapping for habitat relation types to descriptions
     */
    public static final Map<String, String> relationTypeMap = new HashMap<String, String>();

    static {
        relationTypeMap.put("=", "Same");
        relationTypeMap.put(">", "Wider");
        relationTypeMap.put("<", "Narrower");
        relationTypeMap.put("#", "Overlap");
        relationTypeMap.put("?", "Not determined");
    }

    private HabitatLegalPersist legalPersist;
    private String annexTitle;
    private String annexLink;
    private String parentTitle;
    private String parentLink;
    private String parentAlternative;

    private List<DcLinkDTO> moreInfo = new ArrayList<DcLinkDTO>();
    private String replacedByTitle;
    private String replacedBy;

    public LegalStatusWrapper(HabitatLegalPersist legalPersist) {
        this.legalPersist = legalPersist;
    }

    public HabitatLegalPersist getLegalPersist() {
        return legalPersist;
    }

    public String getAnnexTitle() {
        return annexTitle;
    }

    public void setAnnexTitle(String annexTitle) {
        this.annexTitle = annexTitle;
    }

    public String getAnnexLink() {
        return annexLink;
    }

    public void setAnnexLink(String annexLink) {
        this.annexLink = annexLink;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public String getParentLink() {
        return parentLink;
    }

    public void setParentLink(String parentLink) {
        this.parentLink = parentLink;
    }

    public String getParentAlternative() {
        return parentAlternative;
    }

    public void setParentAlternative(String parentAlternative) {
        this.parentAlternative = parentAlternative;
    }

    /**
     * The meaning of the relation type
     * @return
     */
    public String getRelationTypeString(){
        if(legalPersist.getRelationType() == null)
            return null;
        return relationTypeMap.get(legalPersist.getRelationType());
    }

    public void addMoreInfo(DcLinkDTO value) {
        moreInfo.add(value);
    }

    public List<DcLinkDTO> getMoreInfo() {
        return moreInfo;
    }

    public void setReplacedByTitle(String replacedByTitle) {
        this.replacedByTitle = replacedByTitle;
    }

    public String getReplacedByTitle() {
        return replacedByTitle;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    public String getReplacedBy() {
        return replacedBy;
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
            try {
                Integer i1 = EunisUtil.legalStatusOrder.get(this.getLegalPersist().getIdDc());
                Integer i2 = EunisUtil.legalStatusOrder.get(o.getLegalPersist().getIdDc());
                return i1 - i2;
            } catch(Exception e){
                return 0;
            }
        } else {
            return 1;
        }
    }
}
