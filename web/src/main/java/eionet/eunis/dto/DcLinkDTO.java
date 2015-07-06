package eionet.eunis.dto;

import java.io.Serializable;

/**
 * Links for DC References
 */
public class DcLinkDTO implements Serializable, Comparable<DcLinkDTO> {
    /**
     * URL
     */
    private String link;
    /**
     * Link label
     */
    private String linkText;
    /**
     * Reference ID
     */
    private String idDc;

    public DcLinkDTO(String idDc, String link, String linkText) {
        this.link = link;
        this.linkText = linkText;
        this.idDc = idDc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getIdDc() {
        return idDc;
    }

    public void setIdDc(String idDc) {
        this.idDc = idDc;
    }

    @Override
    public int compareTo(DcLinkDTO o) {
        if(getLinkText() != null){
            return getLinkText().compareTo(o.getLinkText());
        } else {
            return getLink().compareTo(o.getLink());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcLinkDTO dcLinkDTO = (DcLinkDTO) o;

        if (link != null ? !link.equals(dcLinkDTO.link) : dcLinkDTO.link != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return link != null ? link.hashCode() : 0;
    }
}

