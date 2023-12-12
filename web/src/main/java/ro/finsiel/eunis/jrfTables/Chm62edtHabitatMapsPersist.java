/*
 * $Id
 */

package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.domain.PersistentObject;


/**
 * Habitat map existence
 * @version $Revision$ $Date$
 **/
public class Chm62edtHabitatMapsPersist extends PersistentObject {

    private static final long serialVersionUID = 1L;

    private String habitatCode;
    private String habitatName;
    private int distributionMap;
    private int suitabilityMap;
    private int probabilityMap;

    public String getHabitatCode() {
        return habitatCode;
    }

    public void setHabitatCode(String habitatCode) {
        this.habitatCode = habitatCode;
    }

    public String getHabitatName() {
        return habitatName;
    }

    public void setHabitatName(String habitatName) {
        this.habitatName = habitatName;
    }

    public boolean getDistributionMap() {
        return distributionMap == 1;
    }

    public void setDistributionMap(Integer distributionMap) {
        this.distributionMap = distributionMap;
    }

    public boolean getSuitabilityMap() {
        return suitabilityMap == 1;
    }

    public void setSuitabilityMap(Integer suitabilityMap) {
        this.suitabilityMap = suitabilityMap;
    }

    public boolean getProbabilityMap() {
        return probabilityMap == 1;
    }

    public void setProbabilityMap(Integer probabilityMap) {
        this.probabilityMap = probabilityMap;
    }
}
