package eionet.eunis.api;


import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * DTO class to represent one result of a lookup-species API invocation.
 * @see LookupSpeciesResult
 *
 * @author Aleksandr Ivanov
 * <a href="mailto:aleks21@gmail.com">contact</a>
 */
@Root
public class SpeciesDTO implements Serializable {

    /**
     * serial.
     */
    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "https://eunis.eea.europa.eu/species/";
	
    private int speciesId;
	
    @Element(required = false, name = "scientificName")
    private String scientificName;
	
    @Element(required = false, name = "author")
    private String author;
	
    public SpeciesDTO(int speciesId, String scientificName, String author) {
        this.speciesId = speciesId;
        this.scientificName = scientificName;
        this.author = author;
    }
	
    @Element(name = "url")
    public String getEunisUrl() {
        return PREFIX + speciesId;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
