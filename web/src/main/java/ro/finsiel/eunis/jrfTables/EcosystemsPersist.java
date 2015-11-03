package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.domain.PersistentObject;

/**
 * Bean for Species and Habitats Ecosystems
 */
public class EcosystemsPersist extends PersistentObject {
    /**
     * Nature object id
     */
    private Integer idNatureObject;
    /**
     * Biogeoregion id
     */
    private Integer idBioGeoRegion;
    /**
     * Ecosystem code
     */
    private String ecoCode;
    /**
     * Association type (for species (Preferred, Suitable, Occasional); null for habitats)
     */
    private String typeAssoc;

    /**
     * Name of the ecosystem (from chm62edt_ecosystems)
     */
    private String ecoName;
    /**
     * MAES ecosystem or not
     */
    private String isMaes;

    public Integer getIdNatureObject() {
        return idNatureObject;
    }

    public void setIdNatureObject(Integer idNatureObject) {
        this.idNatureObject = idNatureObject;
    }

    public Integer getIdBioGeoRegion() {
        return idBioGeoRegion;
    }

    public void setIdBioGeoRegion(Integer idBioGeoRegion) {
        this.idBioGeoRegion = idBioGeoRegion;
    }

    public String getEcoCode() {
        return ecoCode;
    }

    public void setEcoCode(String ecoCode) {
        this.ecoCode = ecoCode;
    }

    public String getTypeAssoc() {
        return typeAssoc;
    }

    public void setTypeAssoc(String typeAssoc) {
        this.typeAssoc = typeAssoc;
    }

    public String getEcoName() {
        return ecoName;
    }

    public void setEcoName(String ecoName) {
        this.ecoName = ecoName;
    }

    public String getIsMaes() {
        return isMaes;
    }

    public void setIsMaes(String isMaes) {
        this.isMaes = isMaes;
    }

    /**
     * Equals is used to get rid of the name duplicates caused by biogeoregions (only uses the ecosystem code and id nature object)
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EcosystemsPersist that = (EcosystemsPersist) o;

        if (!idNatureObject.equals(that.idNatureObject)) return false;
        return ecoCode.equals(that.ecoCode);

    }

    @Override
    public int hashCode() {
        int result = idNatureObject.hashCode();
        result = 31 * result + ecoCode.hashCode();
        return result;
    }
}
